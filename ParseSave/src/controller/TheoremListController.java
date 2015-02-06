package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.TheoremTable;
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class TheoremListController extends BaseController {
    @FXML
    private TableView<TheoremTable>      tableViewTheorems;

    private ObservableList<TheoremTable> tData = FXCollections
                                                       .observableArrayList();
    @FXML
    private TextField                    filterField;

    @FXML
    TableColumn<TheoremTable, String>    itemFamilyCol;
    @FXML
    TableColumn<TheoremTable, String>    itemProverCol;
    @FXML
    TableColumn<TheoremTable, String>    itemNameCol;
    @FXML
    TableColumn<TheoremTable, Integer>   itemExecutionCol;
    @FXML
    TableColumn<TheoremTable, Boolean>   itemProvableCol;
    @FXML
    TableColumn<TheoremTable, String>    itemMachineCol;

    @FXML
    Button                               compareBtn;

    @FXML
    private TableView<TheoremTable>      tViewCompare;

    @FXML
    TableColumn<TheoremTable, String>    itemFamilyCompareCol;
    @FXML
    TableColumn<TheoremTable, String>    itemProverCompareCol;
    @FXML
    TableColumn<TheoremTable, String>    itemNameCompareCol;
    @FXML
    TableColumn<TheoremTable, Integer>   itemExecutionCompareCol;
    @FXML
    TableColumn<TheoremTable, Boolean>   itemProvableCompareCol;
    @FXML
    TableColumn<TheoremTable, String>    itemMachineCompareCol;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
        tableViewTheorems.setItems(getTheoremData());
        tableViewTheorems.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE);
        tableViewTheorems.setEditable(true);

        configureColumns();
    }

    private void configureColumns() {
        itemFamilyCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "family"));
        itemProverCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "prover"));
        itemProverCol.setCellFactory(column -> {
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
                        case "FCUBE":
                            setTextFill(Color.AQUAMARINE);
                        case "PNBU_GOGENBOTE":
                            setTextFill(Color.CHOCOLATE);
                        case "JNBU_DE":
                            setTextFill(Color.CRIMSON);
                        case "JNBU_DE_MIN":
                            setTextFill(Color.FORESTGREEN);
                        default:
                            setTextFill(Color.CORAL);
                        }
                    }
                }
            };
        });
        itemNameCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "name"));
        itemExecutionCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Integer>(
                        "execution"));
        itemMachineCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "machine"));

        itemProvableCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Boolean>(
                        "provable"));
        itemProvableCol.setCellFactory(column -> {
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

        // COMPARE TABLEVIEW
        itemFamilyCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "family"));
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
                        if (item.compareTo("NBU") == 0) {
                            setTextFill(Color.DARKCYAN);
                        } else {
                            setTextFill(Color.CORAL);
                        }
                    }
                }
            };
        });
        itemNameCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "name"));
        itemExecutionCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Integer>(
                        "execution"));
        itemMachineCompareCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "machine"));

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

    public ObservableList<TheoremTable> getTheoremData() {
        try {
            ResultSet mr = SqlLiteDb.getAllTheorems();
            while (mr.next()) {
                TheoremTable t = new TheoremTable(mr.getString("name"),
                        mr.getString("prover"), mr.getInt("execution"),
                        mr.getInt("provable"), mr.getString("family"),
                        mr.getString("machine"));
                tData.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tData;
    }

    @FXML
    protected void handleCompare(ActionEvent event) {

        String message;

        ObservableList<TheoremTable> selectedItems = tableViewTheorems
                .getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0) {
            message = "No theorem selected to compare.";
            showMessage(message);
        } else {
            System.out.println(selectedItems);
            tViewCompare.setItems(selectedItems);
        }

        /*
         * for (TheoremTable t : selectedItems) {
         * System.out.println(t.getName()); message += t.getName() + ":" +
         * t.getExecution() + "(ms) \n"; }
         */

    }

    private void showMessage(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Theorem");
        alert.setHeaderText("Theorem selected");
        alert.setContentText(message);
        alert.showAndWait();
    }
}