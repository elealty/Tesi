package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import dbconnection.SqlLiteDb;

public class MainController extends BaseController {
    @FXML
    private MenuBar    appMenuBar;

    @FXML
    private AnchorPane mainBorder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            handleCompareTheoremAction(new ActionEvent());
        } catch (IOException e) {
            showErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void handleMenuCloseAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleMachineListAction(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/ListMachines.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.getChildren().clear();
            mainBorder.getChildren().add(cmdPane);
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }

    }

    @FXML
    private void handleTheoremAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/TheoremOverview.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.getChildren().clear();
            mainBorder.getChildren().add(cmdPane);
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    @FXML
    protected void handleTheoremListAction(ActionEvent event)
            throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/ListTheorems.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.getChildren().clear();
            mainBorder.getChildren().add(cmdPane);
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    @FXML
    private void handleCompareTheoremAction(ActionEvent event)
            throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/CompareTheorems.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.getChildren().clear();
            mainBorder.getChildren().add(cmdPane);
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }

    }

    @FXML
    private void handleCleanTheoremAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Confirm operation");
        alert.setContentText("This operation will delete all Theorem data: are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                SqlLiteDb.deleteAllTheorems();
                showInfoMessage("Clear all theorem", "All theorem deleted.");
            } catch (SQLException e) {
                showErrorMessage(e.getMessage());
            }
        }
    }

    @FXML
    private void handleCleanMachineAction(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Confirm operation");
        alert.setContentText("This operation will delete all Machine data: are you sure?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            try {
                SqlLiteDb.deleteAllMachines();
            } catch (SQLException e) {
                showErrorMessage(e.getMessage());
            }
        }
    }

}