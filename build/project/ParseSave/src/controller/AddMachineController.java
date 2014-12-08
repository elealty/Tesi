package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import dbconnection.SqlLiteDb;

public class AddMachineController implements Initializable {

    @FXML
    private Button    btnSave;

    @FXML
    private TextField machineName;

    @FXML
    private Text      actionInfo;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {

    }

    @FXML
    protected void handleSaveAction(ActionEvent event) {
        try {
            SqlLiteDb.insertMachineRow(machineName.getText());
            actionInfo.setText("SALVATO");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}