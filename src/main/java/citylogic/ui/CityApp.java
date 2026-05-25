package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.core.validation.BuilderValidator;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.image.Image;
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
        
        try {
            Image sfondImg = new Image(getClass().getResourceAsStream("/immagini/sfondo_isola.png"));
            BackgroundImage bg = new BackgroundImage(sfondImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true));
            root.setBackground(new Background(bg));
        } catch (Exception e) {
            root.setStyle("-fx-background-color: #87CEEB;");
        }

        TopBar barraSuperiore = new TopBar();
        barraSuperiore.aggiornaDati(statoCitta);
        barraSuperiore.setSimulationEngine(motore);
        
        MappaGriglia mappaVisiva = new MappaGriglia(grigliaLogica, validatore, statoCitta, barraSuperiore);
        SideBar barraLaterale = new SideBar(mappaVisiva);
        TimeBar barraTempo = new TimeBar(motore, barraSuperiore, mappaVisiva, statoCitta);

        // Aggiungiamo tutti gli elementi sovrapposti
        root.getChildren().addAll(mappaVisiva, barraSuperiore, barraLaterale, barraTempo);

        // La mappa occupa tutto lo schermo ed è sul fondo
        AnchorPane.setTopAnchor(mappaVisiva, 0.0);
        AnchorPane.setBottomAnchor(mappaVisiva, 0.0);
        AnchorPane.setLeftAnchor(mappaVisiva, 0.0);
        AnchorPane.setRightAnchor(mappaVisiva, 0.0);

        // Barra superiore trasparente ancorata in alto
        AnchorPane.setTopAnchor(barraSuperiore, 0.0);
        AnchorPane.setLeftAnchor(barraSuperiore, 0.0);
        AnchorPane.setRightAnchor(barraSuperiore, 0.0);

        // Menu laterale ancorato a sinistra sotto la TopBar
        AnchorPane.setTopAnchor(barraLaterale, 80.0); 
        AnchorPane.setLeftAnchor(barraLaterale, 20.0);

        // Controlli del tempo ancorati in basso a destra
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