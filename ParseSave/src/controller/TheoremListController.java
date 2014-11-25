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
import model.Theorem;
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class TheoremListController implements Initializable {
    @FXML
    private TableView<Theorem>      tableViewTheorems;

    private ObservableList<Theorem> theoremData = FXCollections
                                                        .observableArrayList();

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {

        try {
            ResultSet mr = SqlLiteDb.getAllTheorems();
            while (mr.next()) {
                theoremData.add(new Theorem(mr.getString("name"), mr
                        .getBoolean("provable"), mr.getBoolean("success"), mr
                        .getInt("execution_time"), mr.getInt("id")));
            }
            System.out.println("OBSERVABLE:" + theoremData);
            tableViewTheorems.setItems(theoremData);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}