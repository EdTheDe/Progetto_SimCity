package citylogic.ui;

import citylogic.core.validation.BuilderValidator;
import citylogic.domain.entities.*;
import citylogic.domain.map.Cell;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MappaGriglia extends GridPane {

    private final UrbanGrid grigliaLogica;
    private final BuilderValidator validatore;
    private final StatoCitta statoCitta;
    private final TopBar topBarRef;
    private static final double CELL_SIZE = 35.0; 
    private String tipoEdificioSelezionato = null;

    public MappaGriglia(UrbanGrid logica, BuilderValidator val, StatoCitta stato, TopBar topBar) {
        this.grigliaLogica = logica;
        this.validatore = val;
        this.statoCitta = stato;
        this.topBarRef = topBar;

        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: transparent;"); 
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
                if (tipoEdificioSelezionato != null) overlayFiltro.setStroke(Color.web("#ffffff", 0.3));
                else overlayFiltro.setStroke(Color.TRANSPARENT);
                
                overlayFiltro.setFill(Color.TRANSPARENT);
                ImageView visualizzatore = new ImageView();
                
                if (cellaLogica.isOccupied()) {
                    UrbanEntity e = cellaLogica.getEntity();
                    // DELEGA ALL'ASSET MANAGER
                    visualizzatore.setImage(AssetManager.ottieniImmagine(e, x, y, grigliaLogica));
                    
                    if (topBarRef != null && topBarRef.getSimulationEngine() != null) {
                        if (!e.isFunctioning() || !topBarRef.getSimulationEngine().checkCoverage(e)) visualizzatore.setOpacity(0.5);
                        else visualizzatore.setOpacity(1.0);
                    }
                    
                    if (!(e instanceof Road)) {
                        visualizzatore.setFitWidth(CELL_SIZE * 1.5);
                        visualizzatore.setFitHeight(CELL_SIZE * 1.5);
                        visualizzatore.setTranslateY(-CELL_SIZE * 0.25);
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
                            UrbanEntity nuovaEntita = UrbanEntityFactory.createEntity(tipoEdificioSelezionato);
                            try {
                                validatore.validaCostruzione(nuovaEntita, cellaLogica, statoCitta);
                                statoCitta.addFinanze(-nuovaEntita.getPlacementCost());
                                grigliaLogica.placeEntity(nuovaEntita, targetX, targetY);
                                topBarRef.aggiornaDati(statoCitta);
                                rinfrescaMappaCompleta();
                            } catch (Exception ex) {}
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
        
        javafx.scene.control.Label lblTitolo = new javafx.scene.control.Label(entita.getClass().getSimpleName());
        lblTitolo.setStyle("-fx-font-weight: bold; -fx-text-fill: black; -fx-font-size: 13px;");
        javafx.scene.control.CustomMenuItem btnTitolo = new javafx.scene.control.CustomMenuItem(lblTitolo);
        btnTitolo.setHideOnClick(false);

        double costoUpgrade = entita.getPlacementCost() * (entita.getDevelopmentLevel() * 0.75);
        MenuItem btnMigliora = new MenuItem("Migliora a Liv. " + (entita.getDevelopmentLevel() + 1) + " (Costo: " + (int)costoUpgrade + "$)");
        
        if (entita instanceof Road || entita.getDevelopmentLevel() >= 4 || statoCitta.getFinanze() < costoUpgrade) btnMigliora.setDisable(true);

        btnMigliora.setOnAction(e -> {
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

        menu.getItems().addAll(btnTitolo, btnMigliora, btnDemolisci);
        menu.show(nodoVisivo, screenX, screenY);
    }
}
