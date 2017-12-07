/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userrecognitionB;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import projectdatabase.StudentDB;

/**
 * FXML Controller class
 *
 * @author liuziqi
 */
public class LoginController implements Initializable {

    @FXML
    private Button ok;
    private Alert alert = new Alert(Alert.AlertType.ERROR);
    @FXML
    private TextField loginName;
    @FXML
    private TextField loginPassword;
    @FXML
    private ImageView loginImage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        File file = new File("LoginImage.PNG");
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            loginImage.setImage(image);
        } catch (IOException ex) {
            Logger.getLogger(FaceRecognitionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        // TODO
    }    

    @FXML
    private void loginAction(ActionEvent event) throws SQLException, IOException {
        String userName = loginName.getText();
        String psw = loginPassword.getText();
        System.out.println(userName + psw);
        StudentDB stb = new StudentDB();
        int isMatch;
        isMatch = stb.verifyLogin(userName, psw);
        System.out.println(isMatch);
        if(isMatch != 0 ){
            FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("Home.fxml"));
        
        Loader.load();
        
        HomeController accessType = Loader.getController();
        accessType.setAccessType(isMatch);
        Parent p = Loader.getRoot();
        Stage dateStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(p);
        dateStage.setScene(scene);
        dateStage.setTitle("Home page");
        dateStage.setScene(scene);
        dateStage.centerOnScreen();
        dateStage.show();
            
        }
       else
        {
            alert.setTitle("Error");
            alert.setContentText("Invalid Username or Password");
            alert.showAndWait();
            
        }
        stb.close();
        
    }

    
}
