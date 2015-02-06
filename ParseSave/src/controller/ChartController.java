/**
 * 
 */
package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class ChartController extends BaseController {
    @FXML
    private BarChart<String, Integer>                       executionChart;
    @FXML
    private StackedBarChart<String, Integer>                maxTimesGraph;

    private ObservableList<XYChart.Series<String, Integer>> chartData    = FXCollections
                                                                                 .observableArrayList();

    private ObservableList<XYChart.Series<String, Integer>> maxChartData = FXCollections
                                                                                 .observableArrayList();
    final double                                            SCALE_DELTA  = 1.1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        executionChart.setData(getChartData());
        maxTimesGraph.setData(getMaxChartData());
    }

    private ObservableList<XYChart.Series<String, Integer>> getChartData() {
        Series<String, Integer> fcubeSeries = new Series<String, Integer>();
        fcubeSeries.setName("FCUBE");
        Series<String, Integer> nbuSeries = new Series<String, Integer>();
        nbuSeries.setName("NBU");
        try {
            ResultSet mr = SqlLiteDb.getProvableTheorems();

            while (mr.next()) {
                if (mr.getString("prover").compareTo("NBU") == 0) {
                    nbuSeries.getData().add(
                            new Data<String, Integer>(mr.getString("name"), mr
                                    .getInt("execution")));

                }
                if (mr.getString("prover").compareTo("FCUBE") == 0) {
                    fcubeSeries.getData().add(
                            new Data<String, Integer>(mr.getString("name"), mr
                                    .getInt("execution")));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        chartData.add(fcubeSeries);
        chartData.add(nbuSeries);
        return chartData;
    }

    private ObservableList<XYChart.Series<String, Integer>> getMaxChartData() {
        Series<String, Integer> maxSeries = new Series<String, Integer>();
        maxSeries.setName("Max times");
        try {
            ResultSet mr = SqlLiteDb.getTheoremsProverMaxTimes();

            while (mr.next()) {
                maxSeries.getData().add(
                        new Data<String, Integer>(mr.getString("prover"), mr
                                .getInt("max")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        maxChartData.add(maxSeries);
        System.out.println("maxchart:" + maxSeries);
        return maxChartData;
    }
}
