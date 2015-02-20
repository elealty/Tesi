package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.TheoremTable;
import dbconnection.SqlLiteDb;

public class AllTheoremController extends BaseController {
    @FXML
    private MenuBar                      appMenuBar;

    @FXML
    private AnchorPane                   mainBorder;

    @FXML
    private TextField                    filterField;
    @FXML
    TableView<TheoremTable>              tableViewTheorems;
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
    TableColumn<TheoremTable, String>    itemTestsetCol;

    private ObservableList<TheoremTable> masterData = FXCollections
                                                            .observableArrayList();

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. Initializes the table columns and
     * sets up sorting and filtering.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureColumns();
        getTheoremData();

        FilteredList<TheoremTable> filteredData = new FilteredList<>(
                masterData, p -> true);

        filterField.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    filteredData.setPredicate(theorem -> {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        String lowerCaseFilter = newValue.toLowerCase();

                        if (theorem.getName().toLowerCase()
                                .indexOf(lowerCaseFilter) != -1) {
                            return true;
                        }
                        if (theorem.getProver().toLowerCase()
                                .indexOf(lowerCaseFilter) != -1) {
                            return true;
                        }
                        if (theorem.getMachine().toLowerCase()
                                .indexOf(lowerCaseFilter) != -1) {
                            return true;
                        }
                        return false;
                    });
                });

        SortedList<TheoremTable> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(
                tableViewTheorems.comparatorProperty());

        tableViewTheorems.setItems(sortedData);
    }

    private void configureColumns() {
        itemFamilyCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "family"));
        itemProverCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "prover"));
        itemTestsetCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "testset"));
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
                        if (item.compareTo("NBU") == 0) {
                            setTextFill(Color.DARKCYAN);
                        } else {
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
    }

    public void getTheoremData() {
        try {
            ResultSet mr = SqlLiteDb.getAllTheorems();
            while (mr.next()) {
                TheoremTable t = new TheoremTable(mr.getString("name"),
                        mr.getString("prover"), mr.getString("testset"),
                        mr.getInt("execution"), mr.getInt("provable"),
                        mr.getString("family"), mr.getString("machine"));
                masterData.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}