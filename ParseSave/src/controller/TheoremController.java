package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Machine;
import parser.TheoremParser;
import dbconnection.SqlLiteDb;

public class TheoremController implements Initializable {
    @FXML
    private Text                    theoremInfo;

    @FXML
    private ComboBox<Machine>       machineCombo;
    private ObservableList<Machine> machineBoxData = FXCollections
                                                           .observableArrayList();

    @FXML
    Button                          uploadButton;

    @FXML
    protected void uploadFileAction(ActionEvent actionEvent)
            throws SQLException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file with theorem to load");
        File file = fileChooser
                .showOpenDialog(new Stage(StageStyle.TRANSPARENT));

        if (file != null) {
            try {
                Integer machine_id = machineCombo.getValue().id;
                TheoremParser.processFile(file, machine_id);
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Theorem");
            alert.setHeaderText("Theorem file uploaded.");
            theoremInfo.setText("Theorem file uploaded.");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        machineCombo.setCellFactory((comboBox) -> {
            return new ListCell<Machine>() {
                @Override
                protected void updateItem(Machine item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.name);
                    }
                }
            };
        });
        machineCombo.setOnAction((event) -> {
            uploadButton.setDisable(true);
            Machine selectedMachine = machineCombo.getSelectionModel()
                    .getSelectedItem();
            if (selectedMachine != null) {
                uploadButton.setDisable(false);
            }
        });

        try {
            ResultSet resAllMachines = SqlLiteDb.getAllMachines();

            while (resAllMachines.next()) {
                machineBoxData.add(new Machine(resAllMachines.getInt("id"),
                        resAllMachines.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        machineCombo.getItems().clear();
        machineCombo.setItems(machineBoxData);

    }
}
