/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userrecognitionB;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

/**
 * FXML Controller class
 *
 * @author Rama
 */
public class HomeController implements Initializable {

    @FXML
    private Button faceRecognition;
    @FXML
    private Button newUser;
    @FXML
    private Button visitDetails;
    @FXML
    private ImageView liveFeed;

    private VideoCapture capture;

    private ScheduledExecutorService timer;
   // int accessType;

    int count = 0;
   

    Mat frame = new Mat();
    MatOfByte mem = new MatOfByte();
    @FXML
    private Button BackBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        System.out.println("Hello");
        // set a fixed width for the frame
        //liveFeed.setFitWidth();
        // preserve image ratio
        //liveFeed.setPreserveRatio(true);
        liveFeed.setPreserveRatio(false);
        this.capture = new VideoCapture(0);
        if (this.capture.isOpened()) {

            // grab a frame every 33 ms (30 frames/sec)
            Runnable frameGrabber = new Runnable() {

                @Override
                public void run() {
                    Image imageToShow = grabFrame();
                    liveFeed.setImage(imageToShow);
                }
            };

            this.timer = Executors.newSingleThreadScheduledExecutor();
            this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
        }

    }

   

    @FXML
    private void recognizeFace(ActionEvent event) {
        this.capture.release();
        this.timer.shutdownNow();
        try
		{
                    //System.out.println(System.getProperty("java.library.path"));
		//System.loadLibrary(Core.NATIVE_LIBRARY_NAME);    
                    Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow(); 
			//Parent root = FXMLLoader.load(getClass().getResource("FaceRecognition.fxml"));
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FaceRecognition.fxml"));
			AnchorPane root = (AnchorPane) loader.load();
			// set a whitesmoke background
			//root.setStyle("-fx-background-color: whitesmoke;");
			// create and style a scene
			Scene scene = new Scene(root);//, 800, 600);
                        
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			// create the stage with the given title and the previously created
			// scene
			stage.setTitle("Face Recognition");
			stage.setScene(scene);
                        stage.centerOnScreen();
			// show the GUI
			stage.show();
			
			// init the controller
			FaceRecognitionController controller = loader.getController();
			controller.init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
    }

    @FXML
    private void openStudentRegistration(ActionEvent event) throws IOException {
        this.capture.release();   
        this.timer.shutdownNow();
        {
        Parent datePage = FXMLLoader.load(getClass().getResource("RegisterStudent.fxml"));
        Scene datePageScene = new Scene(datePage);
        Stage dateStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        dateStage.setTitle("Register a Student");
        dateStage.setScene(datePageScene);
        dateStage.centerOnScreen();
        dateStage.show();
    }
    }

    @FXML
    private void openVisitDetails(ActionEvent event) throws IOException {
       this.capture.release();
       this.timer.shutdownNow();
        Parent root = FXMLLoader.load(getClass().getResource("/projectreport/InputDateRange.fxml"));
        Scene scene = new Scene(root);
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Select a date range");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
    
    /**
     * Get a frame from the opened video stream (if any)
     *
     * @return the {@link Image} to show
     */
    private Image grabFrame() {
        //all this is doing is grabbing the image from the camera...taking the Mat, convert to image, return
        // init everything
        Image imageToShow = null;
        Mat frame = new Mat();

        // check if the capture is open
        if (this.capture.isOpened()) {
            try {
                // read the current frame
                this.capture.read(frame);

                // if the frame is not empty, process it
                if (!frame.empty()) {
                    // face detection
                    // this.detectAndDisplay(frame);

                    // convert the Mat object (OpenCV) to Image (JavaFX)
                    imageToShow = mat2Image(frame);
                }

            } catch (Exception e) {
                // log the (full) error
                System.err.println("ERROR: " + e);
            }
        }

        return imageToShow;
    }

    /**
     * Convert a Mat object (OpenCV) in the corresponding Image for JavaFX
     *
     * @param frame the {@link Mat} representing the current frame
     * @return the {@link Image} to show
     */
    private Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }
    
    public void setAccessType(int accessType)
    {
        
                    if (accessType == 1)
            {
                visitDetails.setDisable(false);
            }
            else 
            {
                visitDetails.setDisable(true);
            }
    }

    @FXML
    private void goBackAction(ActionEvent event) throws IOException {
        capture.release();
        Parent datePage = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene datePageScene = new Scene(datePage);
        Stage dateStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        dateStage.setTitle("Login Page");
        dateStage.setScene(datePageScene);
        dateStage.centerOnScreen();
        dateStage.show();
    }

}
