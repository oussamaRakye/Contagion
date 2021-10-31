import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * This class creates the pane where simulation will be hold
 *
 * @author Oussama Rakye
 * @author Sumeet Saini 
 * @version 2.00
 */

public class MyWorld extends Application {

    //Chart to display disease information
    private static DiseaseChart diseaseChart;

    private static boolean running;

    //Size of the screen
    static final int PANE_WIDTH = 1500;
    static final int PANE_HEIGHT = 500;

    private Pane background;

    private CollisionChecker collisionChecker;


    @Override
    public void start(Stage stage) {
        running = true;

        diseaseChart = createChart();

        background = new Pane();
        VBox vBox = new VBox();

        stage.setTitle("Coronavirus");
        background.setPrefSize(PANE_WIDTH, PANE_HEIGHT);


        vBox.getChildren().addAll(buttons(), background, diseaseChart);
        Scene scene = new Scene(vBox);

        stage.setScene(scene);
        stage.show();

        //Add all persons in the simulation
        background.getChildren().addAll(populate(Constants.POPULATION));

        collisionChecker = new CollisionChecker();
        collisionChecker.start();
        collisionChecker.suspend();
    }

    private HBox buttons() {
        HBox hBox = new HBox();

        Button playB = new Button("Play");
        Button stopB = new Button("Stop");

        Slider populationS = new Slider();
        populationS.setPrefWidth(500);
        populationS.setMin(20);
        populationS.setMax(10000);
        populationS.setValue(Constants.POPULATION);

        Label populationL = new Label("Population: " + Constants.POPULATION);

        populationS.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                    Person.resetValues();
                    background.getChildren().addAll(populate(newValue.intValue()));
                    populationL.setText("Population: " + newValue.intValue());
                });


        playB.setOnAction(event -> {
            collisionChecker.resume();
            Person.resumeAll();
            populationS.setDisable(true);
        });

        stopB.setOnAction(event -> {
            collisionChecker.suspend();
            Person.pauseAll();
            populationS.setDisable(false);
        });

        hBox.getChildren().addAll(playB, stopB, populationS, populationL);

        return hBox;
    }

    static void updateValues(){
        diseaseChart.updateValues(Person.getInfected(), Person.getRecovered(), Person.getDead());
    }


    /**
     * Populate the world based on the screen size
     * and defined population
     *
     * @return populationList a list of the population
     */
    private ArrayList<Person> populate(int population) {
        background.getChildren().clear();
        double radius = Math.sqrt((PANE_WIDTH*PANE_HEIGHT*Constants.PANE_OCCUPATION)/(population*3.14));
        //List of all persons
        ArrayList<Person> populationList = new ArrayList<>();
        for (int i = 0; i < population; i++) {
            populationList.add(new Person(background, radius));
        }

        //Infects the 1st guy, the bat eater
        populationList.get(0).infect(true);

        Person.pauseAll();
        return populationList;
    }

    /**
     * This method creates the Chart that will be displayed in the window
     * @return DiseaseChart the chart of disease information
     */
    private static DiseaseChart createChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of people");
        xAxis.setLabel("Time");

        return new DiseaseChart(xAxis, yAxis);

    }

    public static boolean getRunning(){
        return running;
    }
}


