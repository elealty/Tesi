package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import parser.TheoremParser;
import dbconnection.SqlLiteDb;

public class TheoremController implements Initializable {
    @FXML
    private Text             theoremInfo;

    @FXML
    private ComboBox<String> machineCombo;

    @FXML
    protected void uploadFileAction(ActionEvent event) throws SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file with theorem to load");
        File file = fileChooser
                .showOpenDialog(new Stage(StageStyle.TRANSPARENT));

        if (file != null) {
            try {
                TheoremParser.processFile(file);
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Theorem");
            alert.setHeaderText("Theorem file uploaded.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        machineCombo.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>() {

                    @Override
                    public void changed(
                            ObservableValue<? extends String> selected,
                            String oldValue, String newValue) {
                        System.out.println("CAMBIO MACHINE: " + oldValue + " "
                                + newValue);

                    }
                });

        List<String> machineList = new ArrayList<String>();
        try {
            ResultSet resAllMachines = SqlLiteDb.getAllMachines();

            while (resAllMachines.next()) {
                machineList.add(resAllMachines.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ObservableList<String> obList = FXCollections
                .observableList(machineList);
        machineCombo.getItems().clear();
        machineCombo.setItems(obList);

    }
}
