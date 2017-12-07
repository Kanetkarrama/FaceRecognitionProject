/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userrecognitionB;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;
import org.opencv.videoio.VideoCapture;



/**
 *
 * @author Sammuel Hobbs
 */
public class FaceRecogniser {
    public static HashMap<Integer, String> names = new HashMap<Integer, String>();
    private ImageView originalFrame;

    
    public ImageView getOriginalFrame() {
        return originalFrame;
    }

    public void setOriginalFrame(ImageView originalFrame) {
        this.originalFrame = originalFrame;
    }
    
       private int absoluteFaceSize;
       // a timer for acquiring the video stream
    private ScheduledExecutorService timer;
    // the OpenCV object that performs the video capture
    private VideoCapture capture;
    // a flag to change the button behavior
    private boolean cameraActive;
    public int index = 0;
    public int ind = 0;
    private CascadeClassifier faceCascade;
    private int fSize = 100;
    // New user Name for a training data
     public String newname;

   
     // Random number of a training set
    public int random = (int) (Math.random() * 2000 + 3);
    
   // public String newUserName ="";
    
     public String getNewname() {
        return newname;
    }

    public void setNewname(String newname) {
        this.newname = newname;
    }
    
    private static int count =3;

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        FaceRecogniser.count = count;
    }
    
    
//---------------------------------------------------------------
//---------------------------------------------------------------
    
    public void init() {
        this.capture = new VideoCapture();
        this.faceCascade = new CascadeClassifier();
        //this is an integer, not sure why set to 0
        this.absoluteFaceSize = 0;

        String classifierPath = "resources/lbpcascades/lbpcascade_frontalface.xml";
        this.faceCascade.load(classifierPath);
        
        // disable 'new user' functionality
        // this is the button
      //  this.newUserNameSubmit.setDisable(true);

        // receives the text name
      //  this.newUserName.setDisable(true);
        // Takes some time thus use only when training set
        // was updated 
        trainModel();
    }
    
    public void trainModel(){
        
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
        //FaceRecognizer faceRecognizer = Face.createFisherFaceRecognizer(0,1500);
        org.opencv.face.FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        //FaceRecognizer faceRecognizer = Face.createEigenFaceRecognizer(0,50);
        faceRecognizer.train(images, labels);
        faceRecognizer.save("traineddata");
    }
    public void startCamera() {
        // set a fixed width for the frame
        originalFrame.setFitWidth(600);
        // preserve image ratio
        originalFrame.setPreserveRatio(true);
       

        if (!this.cameraActive) {

            // start the video capture
            this.capture.open(0);

            // is the video stream available?
            if (this.capture.isOpened()) {
                this.cameraActive = true;

                // grab a frame every 33 ms (30 frames/sec)
                Runnable frameGrabber = new Runnable() {

                    @Override
                    public void run() {
                        Image imageToShow = grabFrame();
                        originalFrame.setImage(imageToShow);
                    }
                };

                this.timer = Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber, 0, 33, TimeUnit.MILLISECONDS);

                // update the button content
               //**** this.cameraButton.setText("Stop Camera");
            } else {
                // log the error
                System.err.println("Failed to open the camera connection...");
            }
        } else {
            // the camera is not active at this point
            this.cameraActive = false;
            // update again the button content
           //**** this.cameraButton.setText("Start Camera");
            // enable classifiers checkboxes
//            this.haarClassifier.setDisable(false);
//            this.lbpClassifier.setDisable(false);
            // enable 'New user' checkbox
//            this.newUser.setDisable(false);

            // stop the timer
            try {
                this.timer.shutdown();
                this.timer.awaitTermination(33, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                // log the exception
                System.err.println("Exception in stopping the frame capture, trying to release the camera now... " + e);
            }

            // release the camera
            this.capture.release();
            // clean the frame
            this.originalFrame.setImage(null);

            // Clear the parameters for new user data collection
            index = 0;
            newname = "";
        }
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
/**
     * Method for face detection and tracking
     *
     * @param frame it looks for faces in this frame
     */
    private void detectAndDisplay(Mat frame) throws MalformedURLException, Exception {
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
            if (Math.round(height * 0.20f) > 0) {
                this.absoluteFaceSize = Math.round(height * 0.20f);
            }
        }

        // detect faces
        this.faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE,
                new Size(this.absoluteFaceSize, this.absoluteFaceSize), new Size());

        // each rectangle in faces is a face: draw them!
        Rect[] facesArray = faces.toArray();
        
        count++;
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

            // Crop the detected faces
            Rect rectCrop = new Rect(facesArray[i].x, facesArray[i].y,facesArray[i].width, facesArray[i].height );
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
            
            
            // The criteria of newUser.isSelected doesn't really matter in the new code, isEmpty is important (AndrewID)
            // all this does is takes pictures if the action was performed, nothing else
            //if ((newUser.isSelected() && !newname.isEmpty()))
            if (!newname.isEmpty()) {

                if (index < 50) {
                    Imgcodecs.imwrite("resourcesB/trainingset/combined/"
                            + random + "-" + newname + "_" + (index++) + ".png", resizeImage);
                }
            }
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
            String box_text = "New User = " + newname + " Picture Count = " + index;
            
              
            // Calculate the position for annotated text (make sure we don't
            // put illegal values in there):
            double pos_x = Math.max(facesArray[i].tl().x - 10, 0);
            double pos_y = Math.max(facesArray[i].tl().y - 10, 0);
            // And now put it into the image:
            Imgproc.putText(frame, box_text, new Point(pos_x, pos_y),
                    Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(0, 255, 0, 2.0));
      
            if (index>50){
            this.capture.release();
            this.timer.shutdownNow();
        }
            
        }
        
    }
    /**
     * Method for face recognition
     *
     * grabs the detected face and matches it with the training set. If
     * recognized the name of the person is printed below the face rectangle
     *
     * @return
     */
    private double[] faceRecognition(Mat currentFace) {

        // predict the label
        int[] predLabel = new int[1];
        double[] confidence = new double[1];
        int result = -1;

        org.opencv.face.FaceRecognizer faceRecognizer = Face.createLBPHFaceRecognizer();
        faceRecognizer.load("traineddata");
        faceRecognizer.predict(currentFace, predLabel, confidence);
        	result = faceRecognizer.predict_label(currentFace);
 
             //played with this and no extra help   

//   result = predLabel[0];

                       if(confidence[0] < 3000){
                    return new double[] {result,confidence[0]};
                }
                else{
                    return new double[] {-1,confidence[0]};
                }
        	
         
       // return new double[]{result, confidence[0]};
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
 //newUserName is the text field, equivalent to andrewId
 
 /* protected void newUserNameSubmitted() {
        if ((newUserName.getText() != null && !newUserName.getText().isEmpty())) {
            newname = newUserName.getText();
            //collectTrainingData(name);
            System.out.println("BUTTON HAS BEEN PRESSED");
            newUserName.clear();
        }
    }*/
 
}
