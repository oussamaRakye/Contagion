
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;


class DiseaseChart extends LineChart{

    //Lines in the line chart
    private XYChart.Series seriesInfected;
    private XYChart.Series seriesRecovered;
    private XYChart.Series seriesDeaths;

    //Index of the x-axis in the line chart
    private int index = 1;

    DiseaseChart(Axis axis, Axis axis2) {
        super(axis, axis2);

        setTitle("Cases");
        setCreateSymbols(false);

        //defining a series
        seriesInfected = new XYChart.Series();
        seriesInfected.setName("Infected");

        seriesInfected.getData().add(new XYChart.Data(0, 0)); //Set (0, 0) as initial value
        getData().add(seriesInfected);

        //defining a series
        seriesRecovered = new XYChart.Series();
        seriesRecovered.setName("Recovered");

        seriesRecovered.getData().add(new XYChart.Data(0, 0)); //Set (0, 0) as initial value
        getData().add(seriesRecovered);

        //defining a series
        seriesDeaths = new XYChart.Series();
        seriesDeaths.setName("Deaths");

        seriesDeaths.getData().add(new XYChart.Data(0, 0));  ///Set (0, 0) as initial value
        getData().add(seriesDeaths);
        changeColors();

    }

    /**
     * Set the line of deaths to be black
     * The legend does not get updated
     */
    private void changeColors() {
        seriesDeaths.getNode().setStyle("-fx-stroke: #000000; -fx-text-fill: #000000");
        seriesRecovered.getNode().setStyle("-fx-stroke: #14f000; -fx-text-fill: #14f000");
        seriesInfected.getNode().setStyle("-fx-stroke: #f50600; -fx-text-fill: #f50600");
    }

    /**
     * This method updates the values in the chart
     * Gets the state of each person individually, then it adds
     * them in the chart.
     */
    public void updateValues(int infected,int recovered,int dead) {

        seriesDeaths.getData().add(new XYChart.Data(index, dead));
        seriesInfected.getData().add(new XYChart.Data(index, infected));
        seriesRecovered.getData().add(new XYChart.Data(index, recovered));

        //Increases the index of the x-axis
        index++;
    }
}
