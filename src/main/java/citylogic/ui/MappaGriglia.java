package citylogic.ui;

import citylogic.core.validation.BuilderValidator;
import citylogic.domain.entities.*;
import citylogic.domain.map.Cell;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.HashMap;
import java.util.Map;

public class MappaGriglia extends GridPane {

    private final UrbanGrid grigliaLogica;
    private final BuilderValidator validatore;
    private final StatoCitta statoCitta;
    private final TopBar topBarRef;
    private static final double CELL_SIZE = 35.0; // Ottimizzato per l'erba
    
    // Ora tracciamo solo la stringa, non l'oggetto
    private String tipoEdificioSelezionato = null;

    private final Map<String, Image> imageCache = new HashMap<>();

    public MappaGriglia(UrbanGrid logica, BuilderValidator val, StatoCitta stato, TopBar topBar) {
        this.grigliaLogica = logica;
        this.validatore = val;
        this.statoCitta = stato;
        this.topBarRef = topBar;

        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: transparent;"); 
        

        // this.setTranslateY(40); rimosso per non sballare l'allineamento con l'erba
        
        rinfrescaMappaCompleta();
    }

    public void setTipoEdificioSelezionato(String tipo) {
        this.tipoEdificioSelezionato = tipo;
        rinfrescaMappaCompleta(); 
    }

    public void rinfrescaMappaCompleta() {
        this.getChildren().clear();

        for (int x = 0; x < grigliaLogica.getWidth(); x++) {
            for (int y = 0; y < grigliaLogica.getHeight(); y++) {
                
                Cell cellaLogica = grigliaLogica.getCell(x, y);
                StackPane cellaVisiva = new StackPane();
                cellaVisiva.setMinSize(CELL_SIZE, CELL_SIZE);
                cellaVisiva.setMaxSize(CELL_SIZE, CELL_SIZE);
                
                Rectangle overlayFiltro = new Rectangle(CELL_SIZE, CELL_SIZE);
                if (tipoEdificioSelezionato != null) {
                    overlayFiltro.setStroke(Color.web("#ffffff", 0.3));
                } else {
                    overlayFiltro.setStroke(Color.TRANSPARENT);
                }
                overlayFiltro.setFill(Color.TRANSPARENT);
                
                ImageView visualizzatore = new ImageView();
                
                if (cellaLogica.isOccupied()) {
                    UrbanEntity e = cellaLogica.getEntity();
                    visualizzatore.setImage(ottieniImmaginePerEntita(e, x, y));
                    
                    if (!(e instanceof Road)) {
                        visualizzatore.setFitWidth(CELL_SIZE * 1.8);
                        visualizzatore.setFitHeight(CELL_SIZE * 1.8);
                        visualizzatore.setTranslateY(-CELL_SIZE * 0.4);
                        visualizzatore.setPreserveRatio(true);
                    } else {
                        visualizzatore.setFitWidth(CELL_SIZE);
                        visualizzatore.setFitHeight(CELL_SIZE);
                        visualizzatore.setPreserveRatio(false);
                    }
                } else {
                    visualizzatore.setFitWidth(CELL_SIZE);
                    visualizzatore.setFitHeight(CELL_SIZE);
                }

                cellaVisiva.getChildren().addAll(visualizzatore, overlayFiltro);

                final int targetX = x;
                final int targetY = y;

                cellaVisiva.setOnMouseEntered(ev -> {
                    if (tipoEdificioSelezionato != null) {
                        // Creiamo un'entità test solo per verificare se lo spazio è valido
                        UrbanEntity entitaTest = UrbanEntityFactory.createEntity(tipoEdificioSelezionato);
                        try {
                            validatore.validaCostruzione(entitaTest, cellaLogica, statoCitta);
                            overlayFiltro.setFill(Color.web("#2ecc71", 0.6)); 
                        } catch (Exception ex) {
                            overlayFiltro.setFill(Color.web("#e74c3c", 0.6)); 
                        }
                    }
                });

                cellaVisiva.setOnMouseExited(ev -> overlayFiltro.setFill(Color.TRANSPARENT));

                cellaVisiva.setOnMouseClicked(ev -> {
                    if (ev.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                        if (tipoEdificioSelezionato != null) {
                            // Genera una nuova istanza pulita per il piazzamento effettivo
                            UrbanEntity nuovaEntita = UrbanEntityFactory.createEntity(tipoEdificioSelezionato);
                            try {
                                validatore.validaCostruzione(nuovaEntita, cellaLogica, statoCitta);
                                statoCitta.addFinanze(-nuovaEntita.getPlacementCost());
                                grigliaLogica.placeEntity(nuovaEntita, targetX, targetY);
                                
                                topBarRef.aggiornaDati(statoCitta);
                                rinfrescaMappaCompleta();
                                // La stringa non viene resettata a null. Il pennello rimane attivo.
                            } catch (Exception ex) {
                                // Rifiutato (cella occupata o fondi insufficienti)
                            }
                        } else if (cellaLogica.isOccupied()) {
                            mostraMenuContestuale(cellaLogica, cellaVisiva, targetX, targetY, ev.getScreenX(), ev.getScreenY());
                        }
                    }
                });

                this.add(cellaVisiva, x, y);
            }
        }
    }

    private void mostraMenuContestuale(Cell cella, StackPane nodoVisivo, int x, int y, double screenX, double screenY) {
        UrbanEntity entita = cella.getEntity();
        ContextMenu menu = new ContextMenu();

        MenuItem btnMigliora = new MenuItem("Migliora (Livello " + entita.getDevelopmentLevel() + ")");
        if (entita instanceof Road) {
            btnMigliora.setDisable(true);
        }

        btnMigliora.setOnAction(e -> {
            double costoUpgrade = entita.getPlacementCost() * 0.5;
            if (statoCitta.getFinanze() >= costoUpgrade) {
                statoCitta.addFinanze(-costoUpgrade);
                entita.upgradeLevel();
                topBarRef.aggiornaDati(statoCitta);
                rinfrescaMappaCompleta(); 
            }
        });

        MenuItem btnDemolisci = new MenuItem("Demolisci");
        btnDemolisci.setOnAction(e -> {
            grigliaLogica.removeEntity(x, y);
            rinfrescaMappaCompleta(); 
        });

        menu.getItems().addAll(btnMigliora, btnDemolisci);
        menu.show(nodoVisivo, screenX, screenY);
    }

    private Image ottieniImmaginePerEntita(UrbanEntity e, int x, int y) {
        String nomeFile = "";
        int livello = e.getDevelopmentLevel();
        
        if (livello > 4) {
            livello = 4;
        }

        if (e instanceof Residential) {
            nomeFile = "house_" + livello + ".png";
        } else if (e instanceof Industrial) {
            nomeFile = "industry_" + livello + ".png";
        } else if (e instanceof Commercial) {
            nomeFile = "commercial_" + livello + ".png";
        } else if (e instanceof PowerPlant) {
            nomeFile = "powerplant_" + livello + ".png";
        } else if (e instanceof WaterPlant) {
            nomeFile = "waterplant_" + livello + ".png";
        } else if (e instanceof PoliceStation) {
            nomeFile = "police_" + livello + ".png";
        } else if (e instanceof School) {
            nomeFile = "school_" + livello + ".png";
        } else if (e instanceof Hospital) {
            nomeFile = "hospital_" + livello + ".png";
        } else if (e instanceof FireStation) {
            // Per FireStation usiamo un trucco: se livello 1 cerchiamo "firestation.png" come indicato nell'ls, oppure fallback
            nomeFile = (livello == 1) ? "firestation.png" : "firestation_" + livello + ".png";
        } else if (e instanceof Road) {
            boolean nord = y > 0 && grigliaLogica.getCell(x, y - 1).getEntity() instanceof Road;
            boolean sud = y < grigliaLogica.getHeight() - 1 && grigliaLogica.getCell(x, y + 1).getEntity() instanceof Road;
            boolean est = x < grigliaLogica.getWidth() - 1 && grigliaLogica.getCell(x + 1, y).getEntity() instanceof Road;
            boolean ovest = x > 0 && grigliaLogica.getCell(x - 1, y).getEntity() instanceof Road;

            if (nord && sud && est && ovest) {
                nomeFile = "road_cross.png";
            } else if (sud && est && ovest) {
                nomeFile = "road_t_s.png";
            } else if (nord && est && ovest) {
                nomeFile = "road_t_n.png";
            } else if (nord && sud && est) {
                nomeFile = "road_t_e.png";
            } else if (nord && sud && ovest) {
                nomeFile = "road_t_w.png";
            } else if (nord && est) {
                nomeFile = "road_curve_ne.png";
            } else if (nord && ovest) {
                nomeFile = "road_curve_nw.png";
            } else if (sud && est) {
                nomeFile = "road_curve_se.png";
            } else if (sud && ovest) {
                nomeFile = "road_curve_sw.png";
            } else if (nord || sud) {
                nomeFile = "road_v.png";
            } else {
                nomeFile = "road_h.png";
            }
        }

        if (imageCache.containsKey(nomeFile)) {
            return imageCache.get(nomeFile);
        }

       try {
            Image img = new Image(getClass().getResourceAsStream("/immagini/" + nomeFile), CELL_SIZE * 1.8, CELL_SIZE * 1.8, true, true);
            imageCache.put(nomeFile, img);
            return img;
        } catch (Exception ex) {
            return null; 
        }
    }
}