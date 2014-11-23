/**
 * @author eleonora
 */

import java.io.IOException;
import java.util.HashMap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import dbconnection.SqlLiteDb;

public class Main extends Application {

    static TextArea                      log     = null;
    Process                              p;

    public static HashMap<String, Scene> screens = new HashMap<String, Scene>();

    public static void main(String[] args) {
        initDb();
        launch(args);
    }

    private static void initDb() {
        SqlLiteDb.openDb();
    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "view/RootLayout.fxml"));
            Parent machineScreen = FXMLLoader.load(getClass().getResource(
                    "view/AddMachine.fxml"));
            screens.put("root", new Scene(root));
            screens.put("machine", new Scene(machineScreen));

            stage.setTitle("Theorems");
            stage.setScene(screens.get("root"));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
