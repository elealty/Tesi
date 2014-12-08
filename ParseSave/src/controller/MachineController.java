package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.MachineTable;
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class MachineController implements Initializable {
    @FXML
    private TableView<MachineTable>      tableViewMachines;
    private ObservableList<MachineTable> mData = FXCollections
                                                       .observableArrayList();

    @FXML
    TableColumn<MachineTable, Integer>   itemIdCol;
    @FXML
    TableColumn<MachineTable, String>    itemNameCol;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
        tableViewMachines.setItems(getMachinesData());

        itemIdCol
                .setCellValueFactory(new PropertyValueFactory<MachineTable, Integer>(
                        "id"));
        itemNameCol
                .setCellValueFactory(new PropertyValueFactory<MachineTable, String>(
                        "name"));

    }

    public ObservableList<MachineTable> getMachinesData() {
        try {
            ResultSet mr = SqlLiteDb.getAllMachines();
            while (mr.next()) {
                MachineTable m = new MachineTable(mr.getInt("id"),
                        mr.getString("name"));

                mData.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mData;
    }
}