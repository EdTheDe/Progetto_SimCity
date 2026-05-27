package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.core.validation.BuilderValidator;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class CityApp extends Application {
    private StatoCitta statoCitta;
    private UrbanGrid grigliaLogica;
    private BuilderValidator validatore;
    private SimulationEngine motore;

    @Override
    public void start(Stage primaryStage) {
        statoCitta = new StatoCitta();
        // Griglia rettangolare ottimizzata per l'area verde dell'isola
        grigliaLogica = new UrbanGrid(24, 18);
        validatore = new BuilderValidator(grigliaLogica);
        motore = new SimulationEngine(statoCitta, grigliaLogica);

        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #1a252f;");

        TopBar barraSuperiore = new TopBar();
        barraSuperiore.aggiornaDati(statoCitta);
        barraSuperiore.setSimulationEngine(motore);

        MappaGriglia mappaVisiva = new MappaGriglia(grigliaLogica, validatore, statoCitta, barraSuperiore);
        barraSuperiore.setRiferimenti(grigliaLogica, statoCitta, mappaVisiva);

        SideBar barraLaterale = new SideBar(mappaVisiva);
        TimeBar barraTempo = new TimeBar(motore, barraSuperiore, mappaVisiva, statoCitta);

        // Blocco logico a risoluzione nativa 1280x720 per lo sfondo dell'isola
        StackPane areaGiocoBase = new StackPane();
        areaGiocoBase.setPrefSize(1280, 720);
        areaGiocoBase.setMinSize(1280, 720);
        areaGiocoBase.setMaxSize(1280, 720);

        try {
            Image sfondImg = new Image(getClass().getResourceAsStream("/immagini/sfondo_isola.jpeg"));
            ImageView sfondoView = new ImageView(sfondImg);
            sfondoView.setFitWidth(1280);
            sfondoView.setFitHeight(720);
            areaGiocoBase.getChildren().add(sfondoView);
        } catch (Exception e) {
            areaGiocoBase.setStyle("-fx-background-color: #87CEEB;");
        }

        // Spostamento verticale della mappa di 1 cella verso l'alto (da 40 a 10 pixel)
        mappaVisiva.setTranslateY(10);
        areaGiocoBase.getChildren().add(mappaVisiva);

        // --- SISTEMA RESPONSIVE PER MAPPA E INTERFACCIA UTENTE ---
        // Contenitore virtuale ad alta risoluzione fissa 1280x720
        AnchorPane areaGiocoCompleta = new AnchorPane();
        areaGiocoCompleta.setPrefSize(1280, 720);
        areaGiocoCompleta.setMinSize(1280, 720);
        areaGiocoCompleta.setMaxSize(1280, 720);

        // Inseriamo lo sfondo con la mappa agganciandoli su tutti i lati
        areaGiocoCompleta.getChildren().add(areaGiocoBase);
        AnchorPane.setTopAnchor(areaGiocoBase, 0.0);
        AnchorPane.setBottomAnchor(areaGiocoBase, 0.0);
        AnchorPane.setLeftAnchor(areaGiocoBase, 0.0);
        AnchorPane.setRightAnchor(areaGiocoBase, 0.0);

        // Inseriamo i menu dell'interfaccia nello stesso spazio virtuale scalabile
        areaGiocoCompleta.getChildren().addAll(barraSuperiore, barraLaterale, barraTempo);

        // Ancoraggio dei componenti UI alla risoluzione virtuale di riferimento
        AnchorPane.setTopAnchor(barraSuperiore, 0.0);
        AnchorPane.setLeftAnchor(barraSuperiore, 0.0);
        AnchorPane.setRightAnchor(barraSuperiore, 0.0);

        AnchorPane.setTopAnchor(barraLaterale, 80.0);
        AnchorPane.setLeftAnchor(barraLaterale, 20.0);

        AnchorPane.setBottomAnchor(barraTempo, 30.0);
        AnchorPane.setRightAnchor(barraTempo, 30.0);

        // Wrapper Pane per applicare la trasformazione di scala geometrica senza artefatti di layout
        Pane contenitoreScalabile = new Pane(areaGiocoCompleta);

        Scale scaleTransform = new Scale();
        scaleTransform.setPivotX(0);
        scaleTransform.setPivotY(0);

        // Vincoliamo i fattori di scala orizzontale e verticale alle dimensioni reali della finestra
        scaleTransform.xProperty().bind(root.widthProperty().divide(1280.0));
        scaleTransform.yProperty().bind(root.heightProperty().divide(720.0));

        areaGiocoCompleta.getTransforms().add(scaleTransform);

        // Estendiamo il contenitore scalabile per coprire l'intera finestra reale
        AnchorPane.setTopAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setBottomAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setLeftAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setRightAnchor(contenitoreScalabile, 0.0);

        root.getChildren().add(contenitoreScalabile);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("CityLogic Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}