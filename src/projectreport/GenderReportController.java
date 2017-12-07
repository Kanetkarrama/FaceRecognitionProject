package projectreport;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import projectdatabase.StudentDB;

/**
 * FXML Controller class
 *
 * @author liuziqi
 */
public class GenderReportController implements Initializable {

    @FXML
    private PieChart pieChart;
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private TextField male;
    @FXML
    private TextField female;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
/*
    private void backButtonAction(ActionEvent event) throws IOException {
        Parent addInfoPage = FXMLLoader.load(getClass().getResource("InputDateRange.fxml"));
                    
        Scene addInfoScene = new Scene(addInfoPage);
        
        Stage addInfoStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        addInfoStage.setTitle("Input Date Range");
        addInfoStage.setScene(addInfoScene);
        addInfoStage.show(); 
    }
    */
    public void displayDateChart(String fromDate, String toDate) throws ParseException, SQLException{
        from.setText(fromDate);
        to.setText(toDate);

        Date startDate=new SimpleDateFormat("yyyy/MM/dd").parse(fromDate); 
        Date endDate=new SimpleDateFormat("yyyy/MM/dd").parse(toDate); 
        
        //System.out.println(startDate);
        //System.out.println(endDate);
        
        StudentDB stb = new StudentDB();
        stb.countVisitGender(startDate, endDate);
        
        int[] count = stb.countVisitGender(startDate, endDate);
        male.setText(count[1]+"");
        female.setText(count[0]+"");
        stb.close();

        ObservableList<PieChart.Data> pieChartDate = FXCollections.observableArrayList(
                new PieChart.Data("Female", count[0]),
                new PieChart.Data("Male", count[1]));
        pieChart.setData(pieChartDate);
    }
    

}
