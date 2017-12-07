package userrecognitionB;

//import userrecognition.*;
import java.io.IOException;
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

/**
 * The main class for a JavaFX application. It creates and handle the main
 * window with its resources (style, graphics, etc.).
 * 
 * This application handles a video stream and try to find any possible human
 * face in a frame. It can use the Haar or the LBP classifier.
 * 
 * @author Luigi De Russis / Igor @ HeroinSoul / Jim O'Connorhorrill @ cuerobotics
 * @version 1.3 (2016-08-04)
 * @since 1.0 (2014-01-10)
 * 
 */
public class MainB extends Application
{
	@Override
	public void start(Stage primaryStage) throws IOException
	{
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Login Screen");
        primaryStage.setScene(scene);
        primaryStage.show();
	}
	
	public static void main(String[] args)
	{
		// load the native OpenCV library
        System.out.println(System.getProperty("java.library.path"));
	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		launch(args);
	}
} 
