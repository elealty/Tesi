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
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import javafx.scene.control.Tooltip;
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
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class CompareController extends BaseController {
    private static final String                   RANDOMXX           = "RANDOMXX";
    private static final String                   SYJ                = "SYJ";
    @FXML
    private TableView<TheoremTable>               tableViewComparedTheorem;
    @FXML
    private TableColumn<TheoremTable, String>     itemNameCompareCol;
    @FXML
    private TableColumn<TheoremTable, Integer>    itemExecutionCompareCol;
    @FXML
    private TableColumn<TheoremTable, Boolean>    itemProvableCompareCol;
    @FXML
    private TableColumn<TheoremTable, String>     itemProverCompareCol;
    @FXML
    private TableColumn<TheoremTable, String>     itemTestsetCompareCol;

    @FXML
    private ComboBox<Machine>                     cmbMachine;
    @FXML
    private ComboBox<String>                      cmbTestset;
    @FXML
    private ListView<Prover>                      listProvers;

    private ObservableList<TheoremTable>          tData              = FXCollections
                                                                             .observableArrayList();

    private ObservableList<Machine>               machineBoxData     = FXCollections
                                                                             .observableArrayList();
    private ObservableList<String>                testsetsData       = FXCollections
                                                                             .observableArrayList();
    private ObservableList<Prover>                provers            = FXCollections
                                                                             .observableArrayList();
    private ObservableList<SummaryTable>          sumData            = FXCollections
                                                                             .observableArrayList();

    @FXML
    Button                                        buttonSearch;

    private DoubleProperty                        iniWidth;
    @FXML
    ScrollPane                                    scrollPaneChart;

    @FXML
    private CheckBox                              chkAllProvers;

    @FXML
    private TableView<SummaryTable>               tableViewSummary;
    @FXML
    private TableColumn<SummaryTable, String>     itemProverSum;
    @FXML
    private TableColumn<SummaryTable, String>     itemFamilySum;
    @FXML
    private TableColumn<SummaryTable, String>     itemMachineSum;
    @FXML
    private TableColumn<SummaryTable, Integer>    itemTotalSum;
    @FXML
    private TableColumn<SummaryTable, Integer>    itemProvableSum;
    @FXML
    private TableColumn<SummaryTable, Integer>    itemExecutionSum;

    @FXML
    ProgressBar                                   searchingIndicator = new ProgressBar(
                                                                             0);

    @FXML
    TabPane                                       tabCompare;
    @FXML
    Tab                                           tabListTheorem;
    @FXML
    Tab                                           tabChart;
    @FXML
    Tab                                           tabSummary;
    @FXML
    StackPane                                     stackPaneChart;

    @FXML
    private BarChart<String, Float>               executionChart;
    @FXML
    LineChart<String, Float>                      lineRandChart;

    private ObservableList<Series<String, Float>> chartData          = FXCollections
                                                                             .observableArrayList();
    @FXML
    Label                                         lblInfoSearch;
    @FXML
    VBox                                          vBoxChart;
    @FXML
    VBox                                          vBoxRandChart;

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
                            String testset = cmbTestset.getValue();

                            System.out.println("change tab:" + newValue);
                            if (testset.compareTo(SYJ) == 0) {
                                executionChart.getData().clear();
                                executionChart.getData().addAll(getChartData());
                                vBoxChart.toFront();
                                vBoxRandChart.toBack();
                            } else {
                                lineRandChart.getData().clear();
                                lineRandChart.getData().addAll(getChartData());
                                vBoxRandChart.toFront();
                                vBoxChart.toBack();
                            }

                        }
                        if ((Integer) newValue == 2) {
                            setSummaryData();
                        }
                    }
                });

    }

    private void configureRandXXCharts() {
        lineRandChart.setAnimated(true);
        lineRandChart.getYAxis().setAutoRanging(true);

        // lineRandChart.addEventFilter(ScrollEvent.ANY, new ZoomHandler());
        // // lineRandChart.setCreateSymbols(false);
        // ValueAxis<Float> yAxis = (ValueAxis<Float>) lineRandChart.getYAxis();
        //
        // // yAxis.setMinorTickCount(10);
        // // yAxis.setTickUnit(0.1);
        //
        // lineRandChart.getXAxis().setAutoRanging(true);
        // lineRandChart.getYAxis().setAutoRanging(true);
        // lineRandChart.getYAxis().setAnimated(true);
        // lineRandChart.setOnMouseClicked(mouseHandler);
        // lineRandChart.setOnScroll(scrollHandler);
        // resetButton.setOnAction(new EventHandler<ActionEvent>() {
        // @Override
        // public void handle(ActionEvent event) {
        // final NumberAxis xAxis = (NumberAxis)chart.getXAxis();
        // xAxis.setLowerBound(0);
        // xAxis.setUpperBound(1000);
        // final NumberAxis yAxis = (NumberAxis)chart.getYAxis();
        // yAxis.setLowerBound(0);
        // yAxis.setUpperBound(1000);
        //
        // zoomRect.setWidth(0);
        // zoomRect.setHeight(0);
        // }
        // });
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
        executionChart.getYAxis().setAutoRanging(false);
        executionChart.addEventFilter(ScrollEvent.ANY, new ZoomHandler());
    }

    private void configureTestsetChoice() {
        cmbTestset.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                    String oldValue, String newValue) {
                provers.clear();
                chkAllProvers.setSelected(false);

                Machine selectedMachine = cmbMachine.getSelectionModel()
                        .getSelectedItem();
                if (selectedMachine != null && newValue != null) {
                    buttonSearch.setDisable(false);
                    loadAllProvers(newValue, selectedMachine.id);
                } else {
                    buttonSearch.setDisable(true);
                }

                if (newValue.compareTo(SYJ) == 0) {
                    vBoxRandChart.toFront();
                    vBoxChart.toBack();
                } else {
                    vBoxRandChart.toBack();
                    vBoxChart.toFront();
                    // stackPaneChart.getChildren().get(1).toFront();
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
            chkAllProvers.setSelected(false);
            if (selectedMachine != null && testsetVal != null) {
                buttonSearch.setDisable(false);
                loadAllProvers(testsetVal, selectedMachine.id);
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
        // itemFamilySum.sortTypeProperty().addListener(
        // new ChangeListener<SortType>() {
        // @Override
        // public void changed(
        // ObservableValue<? extends SortType> paramObservableValue,
        // SortType param1, SortType param2) {
        // System.out.println("P1:" + param1 + " " + param2);
        // }
        // });
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
        itemProvableSum.setSortable(false);
        itemTotalSum
                .setCellValueFactory(new PropertyValueFactory<SummaryTable, Integer>(
                        "total"));
        itemTotalSum.setSortable(false);
        itemExecutionSum
                .setCellValueFactory(new PropertyValueFactory<SummaryTable, Integer>(
                        "totalExecution"));
        itemExecutionSum.setSortable(false);
        // sumData.comparatorProperty()
        // .bind(tableViewSummary.comparatorProperty());
        // tableViewSummary.sortPolicyProperty().set(new
        // Callback<TableView<SummaryTable>, Boolean>() {
        // @Override
        // public Boolean call(TableView<SummaryTable> param) {
        // Comparator<SummaryTable> comparator = new Comparator<SummaryTable>()
        // {
        // @Override
        // public int compare(SummaryTable r1, SummaryTable r2) {
        // int delay1 = r1.getTotal()
        // - r1.getTotalProvable();
        // int delay2 = r2.getTotal()
        // - r2.getTotalProvable();
        //
        // System.out.println("DEALYS:" + delay1 + " " + delay2);
        // System.out.println("ex :" + r1.getTotalExecution() + " " +
        // r2.getTotalExecution());
        // if (delay1 < delay2) {
        // return -1;
        // } else {
        // if (delay1 == delay2) {
        // if (r1.getTotalExecution() < r2.getTotalExecution()) {
        // return 1;
        // }
        // return -1;
        // }
        // }
        // }
        // }

        // tableViewSummary.getSortOrder().add(itemProverSum);
    }

    private void loadAllProvers(String testset, int machine_id) {
        provers.clear();
        try {
            ResultSet mr = SqlLiteDb.getAllTestsetProvers(testset, machine_id);
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
        tabCompare.getSelectionModel().select(tabListTheorem);

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                setTableData();
                setSummaryData();
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
        tData.clear();

        Integer machine_id = cmbMachine.getValue().id;
        String testset = cmbTestset.getValue();
        List<String> selectedProvers = getSelectedProver();

        try {
            ResultSet mr = SqlLiteDb.getTheoremsFromSearch(machine_id, testset,
                    selectedProvers);

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

    private ObservableList<Series<String, Float>> getChartData() {
        System.out.println("get chart data");
        chartData.clear();
        String curr_prover = "";
        XYChart.Series<String, Float> prover_serie = new XYChart.Series<String, Float>();

        Integer machine_id = cmbMachine.getValue().id;
        String testset = cmbTestset.getValue();
        List<String> selectedProvers = getSelectedProver();
        ResultSet mr = null;
        try {
            if (testset.compareTo(SYJ) == 0) {
                System.out.println("SYJ");
                mr = SqlLiteDb.getProvableTheoremsFromSearch(machine_id,
                        testset, selectedProvers);
            } else {
                System.out.println("RAND");
                mr = SqlLiteDb.getMediaTheoremsTimeoutFromSearch(machine_id,
                        testset, selectedProvers);
            }

            String cursor_prover = "";
            String cursor_name = "";
            Float cursor_execution = null;

            while (mr.next()) {
                cursor_prover = mr.getString("prover");
                cursor_execution = mr.getFloat("execution");
                cursor_name = mr.getString("name");

                if (curr_prover.compareTo("") == 0) {
                    curr_prover = cursor_prover;
                    prover_serie.setName(curr_prover);
                } else {
                    if (curr_prover.compareTo(cursor_prover) != 0) {

                        chartData.add(prover_serie);

                        curr_prover = cursor_prover;
                        prover_serie = new XYChart.Series<String, Float>();
                        prover_serie.setName(curr_prover);
                    }
                }
                Data<String, Float> item = new Data<String, Float>(cursor_name,
                        cursor_execution, new Tooltip(""
                                + cursor_execution.floatValue()));
                // Tooltip.install(item.getNode(), new Tooltip(""
                // + cursor_execution));
                prover_serie.getData().add(item);

            }
            chartData.add(prover_serie);
            System.out.println("CHART DATA:" + chartData.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chartData;
    }

    private void setSummaryData() {
        tableViewSummary.getItems().clear();

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
                        mr.getInt("execution_sum"));
                sumData.add(s);

            }
            tableViewSummary.setItems(sumData);

        } catch (SQLException e) {
            showErrorMessage("Error :" + e.getMessage());
        }
    }

    private class ZoomHandler implements EventHandler<ScrollEvent> {
        @Override
        public void handle(ScrollEvent scrollEvent) {
            Axis<Float> yAxis = executionChart.getYAxis();

            if (scrollEvent.isControlDown()) {
                boolean widthChange = false;
                double changeWidth = 0;
                double newTickUnit = 0;

                DoubleProperty x = new SimpleDoubleProperty();
                x.set(scrollEvent.getX());

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