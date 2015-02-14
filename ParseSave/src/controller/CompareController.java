package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.Machine;
import model.TheoremTable;
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class CompareController extends BaseController {
    @FXML
    TableColumn<TheoremTable, String>    itemNameCompareCol;
    @FXML
    TableColumn<TheoremTable, Integer>   itemExecutionCompareCol;
    @FXML
    TableColumn<TheoremTable, Boolean>   itemProvableCompareCol;
    @FXML
    TableColumn<TheoremTable, String>    itemProverCompareCol;

    @FXML
    TableView<TheoremTable>              tableViewComparedTheorem;

    @FXML
    ComboBox<Machine>                    cmbMachine;
    @FXML
    ListView<String>                     listTestset;

    private ObservableList<TheoremTable> tData          = FXCollections
                                                                .observableArrayList();
    private ObservableList<Machine>      machineBoxData = FXCollections
                                                                .observableArrayList();
    @FXML
    private BarChart<String, Number>     executionChart;

    ObservableList<String>               provers        = FXCollections
                                                                .observableArrayList();
    @FXML
    Button                               buttonSearch;

    private DoubleProperty               iniWidth;
    @FXML
    ScrollPane                           scrollPaneChart;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
        listTestset.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);
        listTestset.setItems(getAllProver());

        configureItems();

        populateMachineChoice();
        configureCharts();
        executionChart.addEventFilter(ScrollEvent.ANY, new ZoomHandler());

        iniWidth = new SimpleDoubleProperty();
        iniWidth.set(100.0d);

    }

    private void populateMachineChoice() {
        cmbMachine.getItems().clear();
        try {
            ResultSet resAllMachines = SqlLiteDb.getAllMachines();

            while (resAllMachines.next()) {
                machineBoxData.add(new Machine(resAllMachines.getInt("id"),
                        resAllMachines.getString("name"), resAllMachines
                                .getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cmbMachine.setItems(machineBoxData);
    }

    private ObservableList<String> getAllProver() {
        try {
            ResultSet mr = SqlLiteDb.getAllProvers();
            while (mr.next()) {
                provers.add(mr.getString("prover"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return provers;
    }

    private void configureCharts() {

    }

    private void configureItems() {
        itemNameCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "name"));
        itemExecutionCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Integer>(
                        "execution"));
        itemProverCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "prover"));
        itemProvableCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Boolean>(
                        "provable"));
        itemProvableCompareCol.setCellFactory(column -> {
            return new TableCell<TheoremTable, Boolean>() {
                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        VBox vb = new VBox();
                        vb.setAlignment(Pos.CENTER);
                        if (item == false) {
                            vb.getChildren().addAll(
                                    new ImageView(
                                            new Image("/images/false.png")));
                        } else {
                            vb.getChildren()
                                    .addAll(new ImageView(new Image(
                                            "/images/true.png")));

                        }
                        setGraphic(vb);
                    }
                }
            };
        });

        cmbMachine.setCellFactory((comboBox) -> {
            return new ListCell<Machine>() {
                @Override
                protected void updateItem(Machine item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.name);
                    }
                }
            };
        });

        cmbMachine.setOnAction((event) -> {
            Machine selectedMachine = cmbMachine.getSelectionModel()
                    .getSelectedItem();
            if (selectedMachine != null) {
                buttonSearch.setDisable(false);
            }

        });

        cmbMachine.setConverter(new StringConverter<Machine>() {
            @Override
            public String toString(Machine machine) {
                if (machine == null) {
                    return null;
                } else {
                    return machine.name;
                }
            }

            @Override
            public Machine fromString(String personString) {
                return null;
            }
        });
    }

    @FXML
    public void handleSearchAction() {
        setTableData();
        setChartData();
    }

    private void setTableData() {
        tData.clear();
        Integer machine_id = cmbMachine.getValue().id;
        try {
            ResultSet mr = SqlLiteDb.getTheoremsFromSearch(machine_id,
                    listTestset.getSelectionModel().getSelectedItems());

            if (!mr.next()) {
                showInfoMessage("Search", "No theorems found.");
            }

            while (mr.next()) {
                TheoremTable t = new TheoremTable(mr.getString("name"),
                        mr.getString("prover"), mr.getInt("execution"),
                        mr.getInt("provable"), mr.getString("family"),
                        mr.getString("machine"));
                tData.add(t);
            }
            tableViewComparedTheorem.setItems(tData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setChartData() {
        String curr_prover = "";

        XYChart.Series<String, Number> prover_serie = new XYChart.Series<String, Number>();

        Integer machine_id = cmbMachine.getValue().id;
        try {
            ResultSet mr = SqlLiteDb.getTheoremsFromSearch(machine_id,
                    listTestset.getSelectionModel().getSelectedItems());

            while (mr.next()) {
                if (mr.isFirst()) {
                    curr_prover = mr.getString("prover");
                    prover_serie.setName(curr_prover);
                }

                if (curr_prover.compareTo(mr.getString("prover")) != 0) {
                    curr_prover = mr.getString("prover");
                    executionChart.getData().add(prover_serie);
                    prover_serie = new XYChart.Series<String, Number>();
                    prover_serie.setName(curr_prover);
                }

                prover_serie.getData().add(
                        new Data<String, Number>(mr.getString("name"), mr
                                .getInt("execution")));

                if (!mr.next()) {
                    executionChart.getData().add(prover_serie);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static double getSceneShiftX(Node node) {
        double shift = 0;
        do {
            shift += node.getLayoutX();
            node = node.getParent();
        } while (node != null);
        return shift;
    }

    private static double getSceneShiftY(Node node) {
        double shift = 0;
        do {
            shift += node.getLayoutY();
            node = node.getParent();
        } while (node != null);
        return shift;
    }

    private class ZoomHandler implements EventHandler<ScrollEvent> {
        @Override
        public void handle(ScrollEvent scrollEvent) {
            NumberAxis yAxis = (NumberAxis) executionChart.getYAxis();

            if (scrollEvent.isControlDown()) {
                boolean widthChange = false;
                double changeWidth = 0;
                double newTickUnit = 0;

                DoubleProperty x = new SimpleDoubleProperty();
                x.set(scrollEvent.getX());

                yAxis = (NumberAxis) executionChart.getYAxis();

                if (scrollEvent.getDeltaY() > 0) {
                    if (executionChart.prefWidthProperty()
                            .greaterThanOrEqualTo(iniWidth.multiply(2)).get()) {
                        newTickUnit = yAxis.tickLengthProperty().get() / 2;
                        widthChange = true;
                    }
                    changeWidth = 1.1d;
                } else {
                    changeWidth = ((double) 10 / 11);
                    if (executionChart.prefWidthProperty()
                            .lessThanOrEqualTo(iniWidth).get()) {
                        newTickUnit = yAxis.tickLengthProperty().get() * 2;
                        widthChange = true;
                    }
                }

                if (executionChart.getPrefWidth() * changeWidth > executionChart
                        .getMinWidth()) {
                    Timeline zoomTimeline = new Timeline();
                    KeyValue kv = new KeyValue(
                            executionChart.prefWidthProperty(),
                            executionChart.getPrefWidth() * changeWidth);
                    KeyFrame kf = new KeyFrame(Duration.millis(100), kv);
                    zoomTimeline.getKeyFrames().add(kf);
                    zoomTimeline.play();

                }

                if (widthChange) {
                    yAxis.tickLengthProperty().set(newTickUnit);
                }

                if (widthChange) {
                    if (scrollEvent.getDeltaY() > 0) {
                        iniWidth.set(iniWidth.multiply(2).get());
                    } else {
                        iniWidth.set(iniWidth.divide(2).get());
                    }
                }

            }
        }
    }

}