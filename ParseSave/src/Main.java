/**
 * @author eleonora
 */

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import dbconnection.SqlLiteDb;

public class Main extends Application {

    static TextArea log = null;
    Process         p;

    // public static HashMap<String, Scene> screens = new HashMap<String,
    // Scene>();

    public static void main(String[] args) {
        initDb();
        launch(args);
    }

    private static void initDb() {
        try {
            SqlLiteDb.openDb();
            SqlLiteDb.checkDbTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(
                    "view/RootLayout.fxml"));
            Scene scene = new Scene(root);

            stage.setTitle("Theorems");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
