package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class MachineController implements Initializable {
    @FXML
    private TableView<String> tableViewMachines;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
        try {
            ResultSet mr = SqlLiteDb.getAllMachines();
            ObservableList<String> data = FXCollections.observableArrayList();
            while (mr.next()) {
                data.add(mr.getString("name"));
            }

            tableViewMachines.setItems(data);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}