
/**
 * @author eleonora
 * 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
	Process p;
	
	@Override
	public void start(Stage primaryStage) {
		SqlLiteDb.openDb("ParseSave/db/theorem.sqlite3");
		try {
			//BorderPane root = new BorderPane();
			// this thread will read from process without blocking an application
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //try-with-resources from jdk7, change it back if you use older jdk
                        try (BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                            String line;

                            while ((line = bri.readLine()) != null) {
                                log(line);
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
            
	        
	        FileChooser fileChooser = new FileChooser();        
	        
	        
	        Button openButton = new Button("Seleziona un file...");
	        openButton.setOnAction(
	                new EventHandler<ActionEvent>() {
	                    @Override
	                    public void handle(final ActionEvent e) {
	                        File file = fileChooser.showOpenDialog(primaryStage);
	                        if (file != null) {
	                        	System.out.println("Apro file" +file);
	                        	BaseParser.parseStandardFile(file);
	                        	ResultSet res = SqlLiteDb.getTheoremWithMaxExecution();
	                        	String max_result = "";
								try {
									max_result = res.getString("name") + "TIME:"+ res.getInt("max_execution") + " ms";
								} catch (SQLException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
	                        	log.appendText(max_result);
	                        }
	                    }
	                });
	        
	        log = new TextArea();
	        
	        Label title = new Label();
	        StackPane.setAlignment(title, Pos.BOTTOM_CENTER);
	        
	        StackPane root = new StackPane();

	       
	        root.getChildren().add(openButton);
	        //root.getChildren().add(log);
	        
	        GridPane grid = new GridPane();
	        grid.setAlignment(Pos.CENTER);
	        grid.setHgap(10);
	        grid.setVgap(10);
	        grid.setPadding(new Insets(25, 25, 25, 25));
	        
	        grid.add(openButton, 0, 1);
	        grid.add(log, 0, 2);
	        Scene scene = new Scene(grid,400,400);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setTitle("Upload theorem file");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	 public static void log(final String st) {
		 	System.out.println("LOGGO");
	        // we can access fx objects only from fx thread
	        // so we need to wrap log access into Platform#runLater
	        Platform.runLater(new Runnable() {

	            @Override
	            public void run() {
	                log.setText(st + "\n" + log.getText());
	            }
	        });
	    }
}

