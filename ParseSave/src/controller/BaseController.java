package controller;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableView;
import model.SummaryTable;

abstract class BaseController implements Initializable {

    protected void showMessage(AlertType type, String title, String header,
            String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.show();
    }

    protected void showErrorMessage(String message) {
        showMessage(AlertType.ERROR, "Error", "Error during operation", message);
    }

    protected void showInfoMessage(String header, String message) {
        showMessage(AlertType.INFORMATION, "Info", header, message);
    }

    protected void showWarningMessage(String header, String message) {
        showMessage(AlertType.WARNING, "Warning", header, message);
    }

    public Boolean call(TableView<SummaryTable> param) {
        return null;
    }

}
