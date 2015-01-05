package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
public class TheoremListController implements Initializable {
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

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
        tableViewTheorems.setItems(getTheoremData());
        tableViewTheorems.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE);
        tableViewTheorems.setEditable(true);

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

    public ObservableList<TheoremTable> getTheoremData() {
        try {
            ResultSet mr = SqlLiteDb.getAllTheorems();
            while (mr.next()) {
                TheoremTable t = new TheoremTable(mr.getString("name"),
                        mr.getString("prover"), mr.getInt("execution"),
                        mr.getInt("provable"), mr.getString("family"));
                tData.add(t);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tData;
    }

    @FXML
    protected void handleCompare(ActionEvent event) {

        Alert alert = new Alert(AlertType.INFORMATION);
        String message = "";
        alert.setTitle("Theorem");
        alert.setHeaderText("Theorem selected");

        ObservableList<TheoremTable> selectedItems = tableViewTheorems
                .getSelectionModel().getSelectedItems();
        if (selectedItems.size() == 0) {
            message = "No theorem selected to compare.";
        }

        for (TheoremTable t : selectedItems) {
            System.out.println(t.getName());
            message += t.getName() + ":" + t.getExecution() + "(ms) \n";
        }
        alert.setContentText(message);

        alert.showAndWait();

    }
}