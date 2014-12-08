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
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    TableColumn<TheoremTable, Integer>   itemIdCol;
    @FXML
    TableColumn<TheoremTable, String>    itemFamilyCol;
    @FXML
    TableColumn<TheoremTable, String>    itemNameCol;
    @FXML
    TableColumn<TheoremTable, Integer>   itemExecutionCol;
    @FXML
    TableColumn<TheoremTable, Boolean>   itemProvableCol;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
        tableViewTheorems.setItems(getTheoremData());

        itemIdCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Integer>(
                        "id"));
        itemFamilyCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "family"));
        itemNameCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, String>(
                        "name"));
        itemExecutionCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Integer>(
                        "execution"));
        itemProvableCol
                .setCellValueFactory(new PropertyValueFactory<TheoremTable, Boolean>(
                        "provable"));

        FilteredList<TheoremTable> filteredData = new FilteredList<>(tData,
                p -> true);
        System.out.println("filterdata:" + filteredData);
        // filterField.textProperty().addListener(
        // (observable, oldValue, newValue) -> {
        // filteredData.setPredicate(theorem -> {
        // if (newValue == null || newValue.isEmpty()) {
        // return true;
        // }
        // String lowerCaseFilter = newValue.toLowerCase();
        //
        // if (theorem.getName().toLowerCase()
        // .indexOf(lowerCaseFilter) != -1) {
        // return true; // Filter matches first name.
        // }
        // return false; // Does not match.
        // });
        // });
        //
        // filterField.textProperty().addListener(
        // (observable, oldValue, newValue) -> {
        // System.out.println("TextField Text Changed (newValue: "
        // + newValue + ")");
        // });
        SortedList<TheoremTable> sortedData = new SortedList<>(filteredData);
        System.out.println("sortedData:" + sortedData);
        sortedData.comparatorProperty().bind(
                tableViewTheorems.comparatorProperty());

        tableViewTheorems.setItems(sortedData);
    }

    public ObservableList<TheoremTable> getTheoremData() {
        try {
            ResultSet mr = SqlLiteDb.getAllTheorems();
            while (mr.next()) {
                TheoremTable t = new TheoremTable(mr.getInt("id"),
                        mr.getString("name"), mr.getInt("execution"),
                        mr.getInt("provable"), mr.getString("family"));
                tData.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tData;
    }
}