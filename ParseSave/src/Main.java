/**
 * @author eleonora
 */

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Main extends Application {

    static TextArea log = null;
    Process         p;

    private Button uploadButton(Stage stage) {
        FileChooser fileChooser = new FileChooser();

        Button openButton = new Button("Seleziona un file...");
        openButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent e) {
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    System.out.println("Apro file" + file);
                    BaseParser.parseStandardFile(file);
                    ResultSet res = SqlLiteDb.getTheoremWithMaxExecution();
                    String max_result = "Tempo massimo teorema \n";
                    try {
                        max_result += res.getString("name") + "TIME:"
                                + res.getInt("max_execution") + " ms";
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                    log.appendText(max_result);
                }
            }
        });
        return openButton;
    }

    private void initDb() {
        SqlLiteDb.openDb("ParseSave/db/theorem.sqlite3");
    }

    @Override
    public void start(Stage primaryStage) {
        initDb();

        log = new TextArea();

        Label title = new Label();
        StackPane.setAlignment(title, Pos.BOTTOM_CENTER);

        StackPane root = new StackPane();
        Button uploadBtn = uploadButton(primaryStage);
        root.getChildren().add(uploadBtn);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        grid.add(uploadBtn, 0, 1);
        grid.add(log, 0, 2);

        Scene scene = new Scene(grid, 400, 400);
        // creare e associare un css
        // scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        primaryStage.setTitle("Upload theorem file");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void log(final String st) {
        // first prototype, log info
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                log.setText(st + "\n" + log.getText());
            }
        });
    }
}
