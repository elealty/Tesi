package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class MainController implements Initializable {
    @FXML
    private MenuBar    appMenuBar;

    @FXML
    private BorderPane mainBorder;

    @FXML
    private void handleMenuCloseAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleMachineAction(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/AddMachine.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.setCenter(cmdPane);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleMachineListAction(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/ListMachines.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.setCenter(cmdPane);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void handleTheoremAction(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/TheoremOverview.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.setCenter(cmdPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleTheoremListAction(ActionEvent event)
            throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/ListTheorems.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.setCenter(cmdPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCompareTheoremAction(ActionEvent event)
            throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/view/CompareCharts.fxml"));
        Pane cmdPane = (Pane) fxmlLoader.load();

        try {
            mainBorder.setCenter(cmdPane);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}