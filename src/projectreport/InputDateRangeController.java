/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectreport;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author liuziqi
 */
public class InputDateRangeController implements Initializable {

    @FXML
    private Button btCategory;
    @FXML
    private Button btGender;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    private Alert alert = new Alert(Alert.AlertType.ERROR);
    String pattern = "yyyy/MM/dd";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
    private String fromDate; //remeber to encapsulate/////******/////
    private String toDate; 
    @FXML
    private Button backBtn;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void GenderButtonAction(ActionEvent event) throws IOException, ParseException, SQLException {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        fromDate = toString(startDate);
        toDate = toString(endDate);    
                if (fromDate.isEmpty() || toDate.isEmpty())
        {
            alert.setTitle("Error");
            alert.setContentText("Please enter proper dates");
            alert.showAndWait();
        }
        else
        {
        
      
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("GenderReport.fxml"));
        
        Loader.load();
        
        GenderReportController gender = Loader.getController();
        gender.displayDateChart(fromDate, toDate);
        
        Parent p = Loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(p));
        stage.showAndWait();
        }

    }

    @FXML
    private void CategoryButtonAction(ActionEvent event) throws IOException, ParseException, SQLException {
        
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        fromDate = toString(startDate);
        toDate = toString(endDate);  
        if (fromDate.isEmpty() || toDate.isEmpty())
        {
            alert.setTitle("Error");
            alert.setContentText("Please enter proper dates");
            alert.showAndWait();
        }
        else
        {
        //System.out.println(fromDate);
        //System.out.println(toDate);
        
        FXMLLoader Loader = new FXMLLoader();
        Loader.setLocation(getClass().getResource("CategoryReport.fxml"));
        
        Loader.load();
        
        CategoryReportController category = Loader.getController();
        category.setTable(fromDate, toDate);
        category.fillComboBox();
        Parent p = Loader.getRoot();
        Stage stage = new Stage();
        stage.setScene(new Scene(p));
        stage.showAndWait();
        }
    }
    
    
    public String toString(LocalDate date) {
         if (date != null) {
             return dateFormatter.format(date);
         } else {
             return "";
         }
     }

    @FXML
    private void GobackAction(ActionEvent event) throws IOException  {
    
                Parent datePage = FXMLLoader.load(getClass().getResource("/userrecognitionB/Home.fxml"));
        Scene datePageScene = new Scene(datePage);
        Stage dateStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        dateStage.setTitle("Home Page");
        dateStage.setScene(datePageScene);
        dateStage.show();
    }
    
    
}
