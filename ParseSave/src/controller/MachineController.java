package controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import model.MachineTable;
import dbconnection.SqlLiteDb;

/**
 * @author eleonora
 */
public class MachineController extends BaseController {
    @FXML
    private TableView<MachineTable>      tableViewMachines;
    private ObservableList<MachineTable> mData = FXCollections
                                                       .observableArrayList();

    @FXML
    TableColumn<MachineTable, String>    itemNameCol;
    @FXML
    TableColumn<MachineTable, String>    itemDescriptionCol;

    @FXML
    private Button                       btnSave;

    @FXML
    private TextField                    machineName;
    @FXML
    private TextField                    machineDescription;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle arg1) {
        tableViewMachines.setItems(getMachinesData());
        tableViewMachines.setEditable(true);
        itemNameCol
                .setCellValueFactory(new PropertyValueFactory<MachineTable, String>(
                        "name"));
        itemNameCol.setEditable(true);

        itemNameCol.setCellFactory(TextFieldTableCell
                .<MachineTable> forTableColumn());
        itemNameCol
                .setOnEditCommit(new EventHandler<CellEditEvent<MachineTable, String>>() {
                    @Override
                    public void handle(CellEditEvent<MachineTable, String> t) {
                        Integer mId = t.getTableView().getItems()
                                .get(t.getTablePosition().getRow()).getId();
                        try {
                            SqlLiteDb.updateMachineName(mId, t.getNewValue());
                            showInfoMessage("Update", "Machine name updated.");
                        } catch (SQLException e) {
                            showErrorMessage(e.getMessage());
                        }
                    }
                });

        itemDescriptionCol
                .setCellValueFactory(new PropertyValueFactory<MachineTable, String>(
                        "description"));
        itemDescriptionCol.setCellFactory(TextFieldTableCell
                .<MachineTable> forTableColumn());
        itemDescriptionCol
                .setOnEditCommit(new EventHandler<CellEditEvent<MachineTable, String>>() {
                    @Override
                    public void handle(CellEditEvent<MachineTable, String> t) {
                        Integer mId = t.getTableView().getItems()
                                .get(t.getTablePosition().getRow()).getId();
                        try {
                            SqlLiteDb.updateMachineDescription(mId,
                                    t.getNewValue());
                            showInfoMessage("Update",
                                    "Machine description updated.");
                        } catch (SQLException e) {
                            showErrorMessage(e.getMessage());
                        }
                    }
                });

    }

    // private void setMachineDetail(MachineTable machine) {
    // machineName.setText(machine.getName());
    // machineDescription.setText(machine.getDescription());
    // }

    public ObservableList<MachineTable> getMachinesData() {
        mData.clear();
        try {
            ResultSet mr = SqlLiteDb.getAllMachines();
            while (mr.next()) {
                MachineTable m = new MachineTable(mr.getInt("id"),
                        mr.getString("name"), mr.getString("description"));

                mData.add(m);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mData;
    }

    @FXML
    protected void handleSaveAction(ActionEvent event) {
        String name = machineName.getText().trim();
        if (name.isEmpty() == true) {
            showWarningMessage("New machine", "No machine name inserted.");
            machineName.requestFocus();
        } else {
            try {
                SqlLiteDb.insertMachineRow(name, machineDescription.getText());
                getMachinesData();
                cleanMachineData();
                showInfoMessage("New machine", "Machine inserted");
            } catch (SQLException e) {
                showErrorMessage(e.getMessage());
                machineName.requestFocus();
            }
        }
    }

    private void cleanMachineData() {
        machineName.setText(null);
        machineDescription.setText(null);
    }
}
