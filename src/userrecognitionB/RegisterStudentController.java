/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userrecognitionB;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.opencv.objdetect.CascadeClassifier;
import projectdatabase.StudentDB;
import userrecognitionB.FaceRecogniser;


/**
 * FXML Controller class
 *
 * @author Sammuel Hobbs
 */

public class RegisterStudentController implements Initializable {

    @FXML
    private ImageView studentImage;
    @FXML
    private TextField email;
    @FXML
    private TextField address;
    @FXML
    private TextField phone;
    @FXML
    private TextField studentName;
    @FXML
    private TextField supervisor;
    @FXML
    private ComboBox<String> reasonList;
    @FXML
    private TextArea announcements;
    
    //cascade classifier
    private CascadeClassifier faceCascade;
    private int absoluteFaceSize;
    //video capture
    private VideoCapture capture;
    private static int count=1;
    
    FaceRecogniser fR = new FaceRecogniser();
    @FXML
    private Button btInsert;

    @FXML
    private ImageView recognitionCamera;
    @FXML
    private Button btRegisterStudent;
    @FXML
    private Button btTakePicture;
    @FXML
    private ComboBox<String> comboBoxProg;
    @FXML
    private ComboBox<String> comboBoxConc;
    @FXML
    private ComboBox<String> comboBoxSem;
    @FXML
    private ComboBox<String> comboBoxGen;
    @FXML
    private Button btGoMenu;
    @FXML
    private TextField andrewID;
    @FXML
    private Label errorMessage;
    private ObservableList<String> program  = FXCollections.observableArrayList("MSIT","Global-MISM","Global-MSPPM","MSIT-12 Months", "PPM", "MSPPM","MISM");
    private ObservableList<String> concentration  = FXCollections.observableArrayList("BIDA","MISM","PPM", "InfoSec");
    private ObservableList<String> semester  = FXCollections.observableArrayList("Sem-1","Sem-2","Sem-3", "Sem-4");
    private ObservableList<String> gender = FXCollections.observableArrayList("Male","Female");
    
    private Alert alert = new Alert(Alert.AlertType.ERROR);
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
errorMessage.setVisible(false);
 StudentDB stb = new StudentDB();
        ObservableList reasonListDB;
        try {
            reasonListDB = FXCollections.observableArrayList(stb.getReasonDescription());
            reasonList.setItems(reasonListDB);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        comboBoxProg.setItems(program);
        comboBoxConc.setItems(concentration);
        comboBoxSem.setItems(semester);
        comboBoxGen.setItems(gender);
        try {
            // TODO
            stb.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegisterStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    @FXML
    private void takePictureofStudent(ActionEvent event) {
        String name = andrewID.getText();
            System.out.println(name+"was taken from the field");
            if(name.isEmpty())
            errorMessage.setVisible(true);
      //   for (int i =0; i<10; i++)
            else{
                errorMessage.setVisible(false);
            VideoCapture camera = new VideoCapture(0);
    	if(!camera.isOpened()){
    		System.out.println("Error with camera");
    	}
    	else {
    		Mat frame = new Mat();
    	    while(true){
    	    	if (camera.read(frame)){
                    
    	    		System.out.println("Frame Obtained");
    	    		System.out.println("Captured Frame Width " + 
    	    		frame.width() + " Height " + frame.height());
    	    		Imgcodecs.imwrite("resourcesB/profileImages/"+name +".PNG", frame);                        
    	    		//"resourcesC/profileImages/"+
                        System.out.println(name+"was recorded");
    	    		break;
    	    	}
    	    }	
    	}
    	camera.release();
            
        // name is a local variable when I used the " String name = andrewId.getText();"
        // where "andrewId" is the JavaFX text field 
        //studentImage is the JavaFX image object on the interface page
        File file = new File("resourcesB/profileImages/"+name +".PNG");
        try {
                BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                studentImage.setImage(image);
            } catch (IOException ex) {
                Logger.getLogger(FaceRecognitionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        // Image im = new Image(getClass().getResourceAsStream(name +".jpg"));
         //"resourcesC/profileImages/"+
         //ImageView imV = new ImageView(im);
        // imageFrame.setImage(im);
        }
    }

    private void HandleButtonAction(ActionEvent event) throws IOException {
       Parent datePage = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene datePageScene = new Scene(datePage);
        Stage dateStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        dateStage.setTitle("Home Page");
        dateStage.setScene(datePageScene);
        dateStage.centerOnScreen();
        dateStage.show();
    }

    @FXML
    private void registerStudent(ActionEvent event) {
         String sam = andrewID.getText();
        System.out.println(sam);
        if(andrewID.getText().isEmpty())
            errorMessage.setVisible(true);
        else
        {
        errorMessage.setVisible(false);
        fR.setOriginalFrame(recognitionCamera);
        fR.setNewname(andrewID.getText());
        fR.init();
        fR.startCamera();
        }
        
    }

    @FXML
    private void InsertData(ActionEvent event) {
        boolean insertFlag = false;
         StudentDB stb1 = new StudentDB();
        try {
            insertFlag = stb1.insertNewStudent2(andrewID.getText(), studentName.getText(), email.getText(), address.getText(), phone.getText()
                    , comboBoxGen.getValue(), comboBoxProg.getValue(), comboBoxConc.getValue(), comboBoxSem.getValue() ,supervisor.getText());
            Date today = new Date();
            stb1.insertNewVisit(andrewID.getText(), reasonList.getValue(), today);
            stb1.close();
        } catch (SQLException ex) {
            Logger.getLogger(RegisterStudentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (!insertFlag)
        {
            alert.setTitle("Error");
            alert.setContentText("Andrew ID already present in the database");
            alert.showAndWait();
        }
        
    }

    @FXML
    private void goBack(ActionEvent event) throws IOException{
        Parent datePage = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene datePageScene = new Scene(datePage);
        Stage dateStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        dateStage.setTitle("Home Page");
        dateStage.setScene(datePageScene);
        dateStage.show();
    }


    
    
}
