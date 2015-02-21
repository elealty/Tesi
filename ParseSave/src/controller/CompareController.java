package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.Machine;
import model.Prover;
import model.SummaryTable;
import model.TheoremTable;

import org.controlsfx.dialog.ProgressDialog;

import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class CompareController extends BaseController {
    private static final String                RANDOMXX           = "RANDOMXX";
    private static final String                SYJ                = "SYJ";
    @FXML
    private TableView<TheoremTable>            tableViewComparedTheorem;
    @FXML
    private TableColumn<TheoremTable, String>  itemNameCompareCol;
    @FXML
    private TableColumn<TheoremTable, Integer> itemExecutionCompareCol;
    @FXML
    private TableColumn<TheoremTable, Boolean> itemProvableCompareCol;
    @FXML
    private TableColumn<TheoremTable, String>  itemProverCompareCol;
    @FXML
    private TableColumn<TheoremTable, String>  itemTestsetCompareCol;

    @FXML
    private ComboBox<Machine>                  cmbMachine;
    @FXML
    private ComboBox<String>                   cmbTestset;
    @FXML
    private ListView<Prover>                   listProvers;

    private ObservableList<TheoremTable>       tData              = FXCollections
                                                                          .observableArrayList();
    private ObservableList<Machine>            machineBoxData     = FXCollections
                                                                          .observableArrayList();
    private ObservableList<String>             testsetsData       = FXCollections
                                                                          .observableArrayList();
    private ObservableList<Prover>             provers            = FXCollections
                                                                          .observableArrayList();
    private ObservableList<SummaryTable>       sumData            = FXCollections
                                                                          .observableArrayList();

    @FXML
    Button                                     buttonSearch;

    private DoubleProperty                     iniWidth;
    @FXML
    ScrollPane                                 scrollPaneChart;
    @FXML
    private BarChart<String, Number>           executionChart;

    @FXML
    private CheckBox                           chkAllProvers;

    @FXML
    private TableView<SummaryTable>            tableViewSummary;

    @FXML
    private TableColumn<SummaryTable, String>  itemProverSum;
    @FXML
    private TableColumn<SummaryTable, String>  itemFamilySum;
    @FXML
    private TableColumn<SummaryTable, String>  itemMachineSum;
    @FXML
    private TableColumn<SummaryTable, Integer> itemTotalSum;
    @FXML
    private TableColumn<SummaryTable, Integer> itemProvableSum;
    @FXML
    private TableColumn<SummaryTable, Integer> itemExecutionSum;
    @FXML
    Service<Void>                              service;

    ProgressDialog                             progDiag;

    @FXML
    ProgressBar                                searchingIndicator = new ProgressBar(
                                                                          0);
    @FXML
    Tab                                        tabChart;
    @FXML
    TabPane                                    tabCompare;
    @FXML
    VBox                                       randSummary;
    @FXML
    VBox                                       SYJSummary;
    @FXML
    StackPane                                  stackPaneChart;

    LineChart<Number, Number>                  lineRandChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configuteTableView();

        configureMachineChoices();

        configureListProver();

        configureTestsetChoice();
        configureSummaryTableView();

        populateMachineChoice();
        populateTestsetChoice();
        configureExecutionCharts();
        configureRandXXCharts();

        iniWidth = new SimpleDoubleProperty();
        iniWidth.set(100.0d);

        chkAllProvers.selectedProperty().addListener(
                new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> ov,
                            Boolean old_val, Boolean new_val) {
                        provers.forEach((prover) -> prover.setSelected(new_val));
                    }
                });
        searchingIndicator.setVisible(false);
        tabCompare.getSelectionModel().selectedIndexProperty()
                .addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> ov,
                            Number oldValue, Number newValue) {
                        if ((Integer) newValue == 1) {
                            setChartData();
                        }
                        if ((Integer) newValue == 2) {
                            setSummaryData();
                        }
                    }
                });
    }

    private void configureRandXXCharts() {
        // TODO Auto-generated method stub

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
            showWarningMessage("Error loading Machine data", e.toString());
        }
        cmbMachine.setItems(machineBoxData);
    }

    private void populateTestsetChoice() {
        cmbTestset.getItems().clear();
        try {
            ResultSet resAllTestset = SqlLiteDb.getAllTestsets();

            while (resAllTestset.next()) {
                testsetsData.add(resAllTestset.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cmbTestset.setItems(testsetsData);
    }

    private void configureExecutionCharts() {
        executionChart.addEventFilter(ScrollEvent.ANY, new ZoomHandler());
    }

    private void configureTestsetChoice() {
        cmbTestset.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                provers.clear();
                chkAllProvers.setSelected(false);
                loadAllProvers(newValue);
                Machine selectedMachine = cmbMachine.getSelectionModel()
                        .getSelectedItem();
                if (selectedMachine != null && newValue != null) {
                    buttonSearch.setDisable(false);
                } else {
                    buttonSearch.setDisable(true);
                }

                switch (newValue) {
                case SYJ:
                    stackPaneChart.getChildren().get(0).toFront();
                    break;
                case RANDOMXX:
                    stackPaneChart.getChildren().get(1).toFront();
                    break;
                default:
                    stackPaneChart.getChildren().get(0).toBack();
                    stackPaneChart.getChildren().get(1).toBack();
                    break;
                }
            }
        });

        cmbTestset.setOnAction((event) -> {
            String selectedTestset = cmbTestset.getSelectionModel()
                    .getSelectedItem();
            if (selectedTestset != null) {
                buttonSearch.setDisable(false);
            }

        });
    }

    private void configureListProver() {
        listProvers.getSelectionModel()
                .setSelectionMode(SelectionMode.MULTIPLE);

        Callback<Prover, ObservableValue<Boolean>> getProperty = new Callback<Prover, ObservableValue<Boolean>>() {
            @Override
            public BooleanProperty call(Prover prov) {
                return prov.selectedProperty();
            }
        };
        Callback<ListView<Prover>, ListCell<Prover>> forListView = CheckBoxListCell
                .forListView(getProperty);
        listProvers.setCellFactory(forListView);
        listProvers.setItems(provers);
    }

    private void configureMachineChoices() {
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
            String testsetVal = cmbTestset.getValue();
            // chkAllProvers.setSelected(false);
                if (selectedMachine != null && testsetVal != null) {
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

    private void configuteTableView() {

        itemTestsetCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "testset"));
        itemNameCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "name"));
        itemExecutionCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Integer>(
                        "execution"));
        itemProverCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "prover"));
        itemProverCompareCol.setCellFactory(column -> {
            return new TableCell<TheoremTable, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setAlignment(Pos.CENTER);
                        setText(item.toString());
                        switch (item) {
                        case "NBU":
                            setTextFill(Color.DARKCYAN);
                            break;
                        case "FCUBE":
                            setTextFill(Color.AQUAMARINE);
                            break;
                        case "JNBU_DE_GCCOVER":
                            setTextFill(Color.CADETBLUE);
                            break;
                        case "JNBU_GO":
                            setTextFill(Color.VIOLET);
                            break;
                        case "JNBU_GO_MIN":
                            setTextFill(Color.TOMATO);
                            break;
                        case "PNBU_DEC":
                            setTextFill(Color.YELLOW);
                            break;
                        case "PNBU_DEM":
                            setTextFill(Color.BROWN);
                            break;
                        default:
                            setTextFill(Color.YELLOWGREEN);
                        }
                    }
                }
            };
        });
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
    }

    private void configureSummaryTableView() {
        itemFamilySum
                .setCellValueFactory(new PropertyValueFactory<SummaryTable, String>(
                        "family"));
        itemProverSum
                .setCellValueFactory(new PropertyValueFactory<SummaryTable, String>(
                        "prover"));
        itemProverSum.setCellFactory(column -> {
            return new TableCell<SummaryTable, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setAlignment(Pos.CENTER);
                        setText(item.toString());
                        switch (item) {
                        case "NBU":
                            setTextFill(Color.DARKCYAN);
                            break;
                        case "FCUBE":
                            setTextFill(Color.AQUAMARINE);
                            break;
                        case "JNBU_DE_GCCOVER":
                            setTextFill(Color.CADETBLUE);
                            break;
                        case "JNBU_GO":
                            setTextFill(Color.VIOLET);
                            break;
                        case "JNBU_GO_MIN":
                            setTextFill(Color.TOMATO);
                            break;
                        case "PNBU_DEC":
                            setTextFill(Color.DARKGRAY);
                            break;
                        case "PNBU_DEM":
                            setTextFill(Color.BROWN);
                            break;
                        default:
                            setTextFill(Color.YELLOWGREEN);
                        }
                    }
                }
            };
        });
        itemProvableSum
                .setCellValueFactory(new PropertyValueFactory<SummaryTable, Integer>(
                        "totalProvable"));
        itemTotalSum
                .setCellValueFactory(new PropertyValueFactory<SummaryTable, Integer>(
                        "total"));
        itemExecutionSum
                .setCellValueFactory(new PropertyValueFactory<SummaryTable, Integer>(
                        "totalExecution"));
    }

    private void loadAllProvers(String testset) {
        try {
            ResultSet mr = SqlLiteDb.getAllTestsetProvers(testset);
            while (mr.next()) {
                provers.add(new Prover(false, mr.getString("prover")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> getSelectedProver() {
        List<String> selected = new ArrayList<String>();
        provers.forEach((prover) -> {
            if (prover.getSelected() == true)
                selected.add(selected.size(), prover.getName());
        });

        return selected;

    }

    @FXML
    public void handleSearchAction() {
        searchingIndicator.setVisible(true);
        tableViewSummary.getItems().clear();
        tData.clear();
        System.out.println("handleSearch" + tData);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                setTableData();
                updateProgress(this.getProgress(), this.getTotalWork());
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                searchingIndicator.setVisible(false);
                showInfoMessage("Search", "Search complete.");
            }

            @Override
            protected void failed() {
                super.failed();
                searchingIndicator.setVisible(false);
                updateMessage("Failed!");
            }
        };
        searchingIndicator.progressProperty().bind(task.progressProperty());

        if (getSelectedProver().isEmpty()) {
            showInfoMessage("Selection empty", "Select almost one Prover.");
        } else {
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();

        }

    }

    private void setTableData() {
        System.out.println("setTableAndChartData");
        tableViewSummary.getItems().clear();
        tData.clear();

        Integer machine_id = cmbMachine.getValue().id;
        String testset = cmbTestset.getValue();
        List<String> selectedProvers = getSelectedProver();

        try {
            ResultSet mr = SqlLiteDb.getTheoremsFromSearch(machine_id, testset,
                    selectedProvers);

            if (!mr.next()) {
                showInfoMessage("Search", "No theorems found.");
            }

            while (mr.next()) {
                TheoremTable t = new TheoremTable(mr.getString("name"),
                        mr.getString("prover"), mr.getString("testset"),
                        mr.getInt("execution"), mr.getInt("provable"),
                        mr.getString("family"), mr.getString("machine"));
                tData.add(t);
            }
            tableViewComparedTheorem.setItems(tData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setChartData() {
        System.out.println("setChartData");
        tData.clear();
        String curr_prover = "";
        XYChart.Series<String, Number> prover_serie = new XYChart.Series<String, Number>();

        Integer machine_id = cmbMachine.getValue().id;
        String testset = cmbTestset.getValue();
        List<String> selectedProvers = getSelectedProver();

        try {
            ResultSet mr = SqlLiteDb.getTheoremsFromSearch(machine_id, testset,
                    selectedProvers);

            if (!mr.next()) {
                showInfoMessage("Search", "No theorems found.");
                return;
            }
            while (mr.next()) {
                if (curr_prover.compareTo("") == 0) {
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
            tableViewComparedTheorem.setItems(tData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    private void setSummaryData() {
        Integer machine_id = cmbMachine.getValue().id;
        String testset = cmbTestset.getValue();
        List<String> selectedProvers = getSelectedProver();

        try {
            ResultSet mr = SqlLiteDb.getTotals(machine_id, testset,
                    selectedProvers);

            while (mr.next()) {
                SummaryTable s = new SummaryTable(mr.getString("prover"),
                        mr.getString("family"), mr.getString("testset"),
                        mr.getInt("total_provable"), mr.getInt("total"),
                        mr.getInt("execution_sum"), "");
                sumData.add(s);

            }
            tableViewSummary.setItems(sumData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setRandSummaryData() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Month");

        lineRandChart = new LineChart<Number, Number>(xAxis, yAxis);

        lineRandChart.setTitle("Stock Monitoring, 2010");

        // XYChart.Series series = new XYChart.Series();
        XYChart.Series<Number, Number> series = new XYChart.Series<Number, Number>();

        series.setName("My portfolio");

        series.getData().add(new Data<Number, Number>(1, 23));
        series.getData().add(new Data<Number, Number>(2, 14));
        series.getData().add(new Data<Number, Number>(3, 15));
        series.getData().add(new Data<Number, Number>(4, 24));
        series.getData().add(new Data<Number, Number>(5, 34));
        series.getData().add(new Data<Number, Number>(6, 36));
        series.getData().add(new Data<Number, Number>(7, 22));
        series.getData().add(new Data<Number, Number>(8, 45));
        series.getData().add(new Data<Number, Number>(9, 43));
        series.getData().add(new Data<Number, Number>(10, 17));
        series.getData().add(new Data<Number, Number>(11, 29));
        series.getData().add(new Data<Number, Number>(12, 25));

        lineRandChart.getData().add(series);
    }

}