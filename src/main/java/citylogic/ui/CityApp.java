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
        grigliaLogica = new UrbanGrid(); 
        validatore = new BuilderValidator(); 
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

        // Blocco logico 1280x720 che unisce griglia e immagine
        StackPane areaGiocoBase = new StackPane();
        areaGiocoBase.setPrefSize(1280, 720);
        areaGiocoBase.setMinSize(1280, 720);
        areaGiocoBase.setMaxSize(1280, 720);

        try {
            Image sfondImg = new Image(getClass().getResourceAsStream("/immagini/sfondo_isola.png"));
            ImageView sfondoView = new ImageView(sfondImg);
            sfondoView.setFitWidth(1280);
            sfondoView.setFitHeight(720);
            areaGiocoBase.getChildren().add(sfondoView);
        } catch (Exception e) {
            areaGiocoBase.setStyle("-fx-background-color: #87CEEB;");
        }

        // Regolazione in pixel della griglia
        mappaVisiva.setTranslateY(40); 
        areaGiocoBase.getChildren().add(mappaVisiva);

        // --- LA SOLUZIONE DEFINITIVA ---
        // Pane normale (nessuna centratura automatica che sballa i bordi)
        Pane contenitoreScalabile = new Pane(areaGiocoBase);
        
        Scale scaleTransform = new Scale();
        // Fissiamo il punto di origine dell'ingrandimento in alto a sinistra!
        scaleTransform.setPivotX(0);
        scaleTransform.setPivotY(0);
        
        // Colleghiamo la scala alle dimensioni della finestra
        scaleTransform.xProperty().bind(root.widthProperty().divide(1280.0));
        scaleTransform.yProperty().bind(root.heightProperty().divide(720.0));
        
        areaGiocoBase.getTransforms().add(scaleTransform);
        // --- FINE ---

        AnchorPane.setTopAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setBottomAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setLeftAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setRightAnchor(contenitoreScalabile, 0.0);

        root.getChildren().addAll(contenitoreScalabile, barraSuperiore, barraLaterale, barraTempo);

        AnchorPane.setTopAnchor(barraSuperiore, 0.0);
        AnchorPane.setLeftAnchor(barraSuperiore, 0.0);
        AnchorPane.setRightAnchor(barraSuperiore, 0.0);

        AnchorPane.setTopAnchor(barraLaterale, 80.0); 
        AnchorPane.setLeftAnchor(barraLaterale, 20.0);

        AnchorPane.setBottomAnchor(barraTempo, 30.0);
        AnchorPane.setRightAnchor(barraTempo, 30.0);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("CityLogic Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}