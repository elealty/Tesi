package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
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
    TableColumn<TheoremTable, Boolean>   checked;
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
        checked.setEditable(true);
        checked.setCellFactory(new Callback<TableColumn<TheoremTable, Boolean>, TableCell<TheoremTable, Boolean>>() {
            @Override
            public TableCell<TheoremTable, Boolean> call(
                    TableColumn<TheoremTable, Boolean> arg0) {
                return new CheckBoxTableCell<TheoremTable, Boolean>();
            }
        });
        itemFamilyCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "family"));
        itemProverCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "prover"));
        itemNameCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "name"));
        itemExecutionCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Integer>(
                        "execution"));
        itemProvableCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Boolean>(
                        "provable"));

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
}