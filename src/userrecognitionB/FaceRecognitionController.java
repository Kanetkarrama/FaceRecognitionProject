package userrecognitionB;

//import userrecognition.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.Face;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import org.opencv.face.BasicFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.face.FaceRecognizer;

import projectdatabase.*;

/**
 * FXML Controller class
 *
 * @author Rama
 */
public class FaceRecognitionController implements Initializable {

    @FXML
    private ImageView recognitionCamera;
    @FXML
    private ImageView studentImage;
    @FXML
    private TextField email;
    @FXML
    private TextField address;
    @FXML
    private TextField phone;
    @FXML
    private TextField gender;
    @FXML
    private TextField studentName;
    @FXML
    private TextField program;
    @FXML
    private TextField concentration;
    @FXML
    private TextField semester;
    @FXML
    private TextField supervisor;
    @FXML
    private TextField visitCount;
    @FXML
    private TextField visitReason;
    @FXML
    private TextField visitDate;
    @FXML
    private ComboBox<String> reasonList;
    @FXML
    private TextArea announcements;
    @FXML
    private Button btDetails;
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;

    // face cascade classifier
    private CascadeClassifier faceCascade;
    private StudentDB stb = new StudentDB();
    private int absoluteFaceSize;
    private int fSize =100;
    public int index = 0;
    public int ind = 0;

    // New user Name for a training data
    public String newname;

    // Names of the people from the training set
    public HashMap<Integer, String> names = new HashMap<Integer, String>();
    // a timer for acquiring the video stream
    int a = 0, b = 0, c = 0, d = 0, e = 0, f = 0, g = 0;
    static ArrayList<RecognitionCounter> arrL = new ArrayList<RecognitionCounter>();
    @FXML
    private Button btStartCamera;
    private String recognisedName;
    @FXML
    private Button backButton;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void init() {
        
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        this.faceCascade.load("resources/lbpcascades/lbpcascade_frontalface.xml");
        this.absoluteFaceSize = 0;

        trainModel();
        // btStopCamera.disarm();
        //startCamera();

        // disable 'new user' functionality
        //this.newUserNameSubmit.setDisable(true);
        //this.newUserName.setDisable(true);
        // Takes some time thus use only when training set
        // was updated 
        //trainModel();
    }

    @FXML
    protected void startCamera() {
        // load the classifier(s)

        // now the video capture can start
        //this.cameraButton.setDisable(false);
        // preserve image ratio
        arrL = dbQuery();
        recognitionCamera.setPreserveRatio(true);
        // start the video capture
        this.capture.open(0);

        // is the video stream available?
        if (this.capture.isOpened()) {

            // grab a frame every 33 ms (30 frames/sec)
            timer = Executors.newSingleThreadScheduledExecutor();
            timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);
        }
    }
    Runnable frameGrabber = new Runnable() {

        @Override
        public void run() {
            Image imageToShow = grabFrame();
            recognitionCamera.setImage(imageToShow);
        }
    };

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
                    this.detectAndDisplay(frame);

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

    private void detectAndDisplay(Mat frame) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        // convert the frame in gray scale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        // equalize the frame histogram to improve the result
        Imgproc.equalizeHist(grayFrame, grayFrame);

        // compute minimum face size (45% of the frame height, in our case)
        // we changed this from .2 to .45
        if (this.absoluteFaceSize == 0) {
            int height = grayFrame.rows();
            if (Math.round(height * 0.2f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.2f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

            // Crop the detected faces
            Rect rectCrop = new Rect(facesArray[i].x, facesArray[i].y,facesArray[i].width, facesArray[i].height);
            Mat croppedImage = new Mat(frame, rectCrop);
            // Change to gray scale
            Imgproc.cvtColor(croppedImage, croppedImage, Imgproc.COLOR_BGR2GRAY);
            // Equalize histogram
            Imgproc.equalizeHist(croppedImage, croppedImage);
            // Resize the image to a default size
            Mat resizeImage = new Mat();
            Size size = new Size(fSize, fSize);
            Imgproc.resize(croppedImage, resizeImage, size);

            // check if 'New user' checkbox is selected
            // if yes start collecting training data (50 images is enough)
            //if ((newUser.isSelected() && !newname.isEmpty())) {
            //    if (index < 100) {
            //        Imgcodecs.imwrite("resourcesB/trainingset/combined/"
            //                + random + "-" + newname + "_" + (index++) + ".png", resizeImage);
            //    }
            //}
//			int prediction = faceRecognition(resizeImage);
            double[] returnedResults = faceRecognition(resizeImage);
            double prediction = returnedResults[0];
            double confidence = returnedResults[1];

//			System.out.println("PREDICTED LABEL IS: " + prediction);
            int label = (int) prediction;
            String name = "";
            if (names.containsKey(label)) {
                name = names.get(label);
            } else {
                name = "Unknown";
            }

            // Create the text we will annotate the box with:
//            String box_text = "Prediction = " + prediction + " Confidence = " + confidence;
// if confidence below certain level, display unknown
            //String box_text = "Prediction = " + name + " Confidence = " + confidence;
            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            double pos_x = Math.max(facesArray[i].tl().x - 10, 0);
            double pos_y = Math.max(facesArray[i].tl().y - 10, 0);
            // And now put it into the image:
            if(80>=confidence){
                String box_text = "Prediction = " + name;// + " Confidence = " + Math.round(confidence);
                Imgproc.putText(frame, box_text, new Point(pos_x, pos_y),
                    Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
            }else{
                 name = "Unknown";
                String box_text = "Prediction = " + name;// + " Confidence = " + Math.round(confidence);
               
                Imgproc.putText(frame, box_text, new Point(pos_x, pos_y),
                    Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
                
            }
            countFaces(name);
        }
    }

    private Image mat2Image(Mat frame) {
        // create a temporary buffer
        MatOfByte buffer = new MatOfByte();
        // encode the frame in the buffer, according to the PNG format
        Imgcodecs.imencode(".png", frame, buffer);
        // build and return an Image created from the image encoded in the
        // buffer
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }

    private void trainModel() {
        // Read the data from the training set
        File root = new File("resourcesB/trainingset/combined/");

        FilenameFilter imgFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".png");
            }
        };
        //root.listFiles(imgFilter) = Returns an array of abstract pathnames denoting the files and 
        //directories in the directory denoted by this abstract pathname that satisfy the specified filter. 
        //the pathnames in the returned array must satisfy the filter.
        File[] imageFiles = root.listFiles(imgFilter);

        List<Mat> images = new ArrayList<Mat>();

        System.out.println("THE NUMBER OF IMAGES READ IS: " + imageFiles.length);

        List<Integer> trainingLabels = new ArrayList<>();

        Mat labels = new Mat(imageFiles.length, 1, CvType.CV_32SC1);

        int counter = 0;

        for (File image : imageFiles) {
            // Parse the training set folder files
            Mat img = Imgcodecs.imread(image.getAbsolutePath());
            // Change to Grayscale and equalize the histogram
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(img, img);
            // Extract label from the file name
            int label = Integer.parseInt(image.getName().split("\\-")[0]);
            // Extract name from the file name and add it to names HashMap
            String labnname = image.getName().split("\\_")[0];
            String name = labnname.split("\\-")[1];
            names.put(label, name);
            // Add training set images to images Mat
            images.add(img);

            labels.put(counter, 0, label);
            counter++;
        }
        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();

        //FaceRecognizer faceRecognizer = Face.createEigenFaceRecognizer(0,50);
        faceRecognizer.train(images, labels);
        faceRecognizer.save("traineddata");
    }

    public void countFaces(String name) {
        System.out.println(name);
        String aId="";

        //when the name comes through, we iterate the names in arraylist to compare and we
        // +1 to the count
        for (int i = 0; i < arrL.size(); i++) {

            if (name.equalsIgnoreCase(arrL.get(i).getName())) {
                arrL.get(i).setCount(arrL.get(i).getCount() + 1);//setCount(getCount+1)
                //System.out.println(arrL.get(i).getName() + " has been identified "
                //        + arrL.get(i).getCount() + " times.");
            }

        }
        //we iterate through the arraylist and check if any have reached 5 
        for (int j = 0; j < arrL.size(); j++) {
            if (5 <= arrL.get(j).getCount()) {
               aId=arrL.get(j).getName();
               arrL.clear();
                try {
                    sendToDB(aId);
                } catch (SQLException ex) {
                    Logger.getLogger(FaceRecognitionController.class.getName()).log(Level.SEVERE, null, ex);
                }
               
               //System.out.println("query database");
                System.out.print(aId);
               // System.out.println(arrL.get(j).getName());

                //this is crucial to reset the count to 0 after/before the database is queried
                //I'm thinking that in the real system we actually clear all contents of the class
                //arraylist when the database is queried, and leave the queryDB method in init()
                // so that everytime we run the program it would take into account the updated names
                // that have been added to the database
               // for (int k = 0; k < arrL.size(); k++) {
               //     arrL.get(k).setCount(0);
               // }

            }
        }
    }

    private double[] faceRecognition(Mat currentFace) {

        // predict the label
        int[] predLabel = new int[1];
        double[] confidence = new double[1];
        int result = -1;

        FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load("traineddata");
        faceRecognizer.predict(currentFace, predLabel, confidence);
//        	result = faceRecognizer.predict_label(currentFace);
        result = predLabel[0];

        /*                if(confidence[0] < 3000){
                    return new double[] {result,confidence[0]};
                }
                else{
                    return new double[] {-1,confidence[0]};
                }
        	
         */
        return new double[]{result,confidence[0]};
    }

    public void sendToDB(String name) throws SQLException {
        recognisedName = name;
        ArrayList<String> result = new ArrayList<String>();
        ArrayList<String> visitResult = new ArrayList<String>();
        result = stb.getBasicInfo(name);
        System.out.println(stb.getBasicInfo(name));
        studentName.setText(result.get(0));
        email.setText(result.get(1));
        address.setText(result.get(2));
        phone.setText(result.get(3));
        gender.setText(result.get(4));
        program.setText(result.get(5));
        concentration.setText(result.get(6));
        semester.setText(result.get(7));
        supervisor.setText(result.get(8));
        visitCount.setText(stb.totalVisitCounts(name) + "");
        visitResult = stb.getLastVisitReason(name);
        visitReason.setText(visitResult.get(0));
        visitDate.setText(visitResult.get(1));
        announcements.setText(stb.getAnnouncement(result.get(5)));
        // name is a local variable when I used the " String name = andrewId.getText();"
        // where "andrewId" is the JavaFX text field 
        //studentImage is the JavaFX image object on the interface page
        File file = new File("resourcesB/profileImages/" + name + ".PNG");
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            studentImage.setImage(image);
        } catch (IOException ex) {
            Logger.getLogger(FaceRecognitionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        ObservableList reasonListDB;
        try {
            reasonListDB = FXCollections.observableArrayList(stb.getReasonDescription());
            reasonList.setItems(reasonListDB);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("Studnent ID: " + name + " was recognized " + "5" + " times.");
        this.capture.release();
        timer.shutdownNow();
        
    }

    @FXML
    private void goMenu(ActionEvent event) throws IOException, SQLException {
        if (!recognisedName.endsWith("Unknown")) {
            Date today = new Date();
            stb.insertNewVisit(recognisedName, reasonList.getValue(), today);
        }

    }

    public static ArrayList dbQuery() {
        String str, rName;
        //System.out.println(cFile.exists() + ": cFile still good");

        //filereader method is copied and manipulated to work
        try {
            StudentDB distinctAndrewID = new StudentDB();
            ArrayList<String> andrewID;// = new ArrayList<String>();
            andrewID = distinctAndrewID.getAndrewIDlist();
            for (int i = 0; i< andrewID.size(); i++) {

               // str = scanR.nextLine();
                //String[] parts = str.split("\t");
                //rName = parts[0];
                // rAdrss = parts[1];

                // the default is set to 0, the constructor is 0 - probably duplicated effort
                RecognitionCounter db = new RecognitionCounter(andrewID.get(i), 0);
                arrL.add(i, db);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
         catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return arrL;

    }

    @FXML
    private void goBackAction(ActionEvent event) throws IOException {
        Parent datePage = FXMLLoader.load(getClass().getResource("Home.fxml"));
        Scene datePageScene = new Scene(datePage);
        Stage dateStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        dateStage.setTitle("Home Page");
        dateStage.setScene(datePageScene);
        dateStage.centerOnScreen();
        dateStage.show();
    }

}
