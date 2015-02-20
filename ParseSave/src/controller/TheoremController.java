package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.StringConverter;
import model.Machine;
import parser.TheoremParser;
import dbconnection.SqlLiteDb;

public class TheoremController extends BaseController {
    @FXML
    private Text                    theoremInfo;

    @FXML
    private ComboBox<Machine>       machineCombo;
    private ObservableList<Machine> machineBoxData   = FXCollections
                                                             .observableArrayList();

    @FXML
    Button                          uploadButton;

    @FXML
    ProgressBar                     loadingIndicator = new ProgressBar(0);
    Button                          button           = new Button(
                                                             "Click me to start loading");
    final ListView<String>          listView         = new ListView<String>();
    List<File>                      listItems;

    @FXML
    protected void uploadFileAction(ActionEvent actionEvent)
            throws SQLException {
        Window chooserStage = machineCombo.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file with theorem to load");
        fileChooser.setInitialDirectory(new File("ParseSave/time-example"));

        List<File> files = fileChooser.showOpenMultipleDialog(chooserStage);
        loadingIndicator.setVisible(true);

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() {
                Integer machine_id = machineCombo.getValue().id;
                long filesCount = files.size();
                for (File file : files) {
                    try {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                theoremInfo.setText("Loading file "
                                        + file.getName());
                            }
                        });

                        TheoremParser.processFile(file, machine_id);

                    } catch (IOException e) {
                        this.cancel();
                        loadingIndicator.setVisible(false);
                        showErrorMessage(e.toString());
                    } catch (SQLException e) {
                        this.cancel();
                        loadingIndicator.setVisible(false);
                        showErrorMessage(e.toString());
                    } catch (Exception e) {
                        this.cancel();
                        loadingIndicator.setVisible(false);
                        showErrorMessage(e.toString());

                    }
                    updateProgress((long) files.indexOf(file) + 1, filesCount);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                updateMessage("Done!");
                theoremInfo.setText("");
                showInfoMessage("Upload complete", "All files loaded.");
            }

            @Override
            protected void failed() {
                super.failed();
                loadingIndicator.setVisible(false);
                updateMessage("Failed!");
                showErrorMessage("Task to load files failed.");
            }
        };
        loadingIndicator.progressProperty().bind(task.progressProperty());

        if (!files.isEmpty()) {
            Thread th = new Thread(task);
            th.setDaemon(true);
            th.start();

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

        machineCombo.setConverter(new StringConverter<Machine>() {
            @Override
            public String toString(Machine machine) {
                if (machine == null) {
                    return null;
                } else {
                    return machine.name;
                }
            }

            @Override
            public Machine fromString(String personString) {
                return null;
            }
        });

        try {
            ResultSet resAllMachines = SqlLiteDb.getAllMachines();

            while (resAllMachines.next()) {
                machineBoxData.add(new Machine(resAllMachines.getInt("id"),
                        resAllMachines.getString("name"), resAllMachines
                                .getString("description")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        machineCombo.getItems().clear();
        machineCombo.setItems(machineBoxData);

    }
}
