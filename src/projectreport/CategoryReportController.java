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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import projectdatabase.StudentDB;

/**
 * FXML Controller class
 *
 * @author liuziqi
 */
public class CategoryReportController implements Initializable {

    
    String fromDate; 
    String toDate; 
    private ObservableList<visitByCategory> vbc;
    @FXML
    private TextField from;
    @FXML
    private TextField to;
    @FXML
    private TableColumn<visitByCategory, String> Reason;
    @FXML
    private TableColumn<visitByCategory, String> Name;
    //private TableColumn<visitByCategory, String> Date;
    @FXML
    private TableView<visitByCategory> Table;
    @FXML
    private TableColumn<visitByCategory, String> vDate;
    
    ObservableList options = FXCollections.observableArrayList();
    @FXML
    private ComboBox<String> reasonList;
    
    private ArrayList<visitByCategory> vbcList;
    
    private ObservableList<visitByCategory> vbc2;
    

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
    public void setTable(String fromDate, String toDate) throws ParseException, SQLException{
        vbc = FXCollections.observableArrayList();
        
        from.setText(fromDate);
        to.setText(toDate);

        Date startDate=new SimpleDateFormat("yyyy/MM/dd").parse(fromDate); 
        Date endDate=new SimpleDateFormat("yyyy/MM/dd").parse(toDate); 
        
        //System.out.println(startDate);
        //System.out.println(endDate);
        
        StudentDB stb = new StudentDB();
        vbcList = stb.VisitsByCategory(startDate, endDate);
        stb.close();
        for(int i=0;i<vbcList.size();i++){
            vbc.add(vbcList.get(i));
            //System.out.println(vbcList.get(i).getDate());
        }
        
        Reason.setCellValueFactory(new PropertyValueFactory<>("description"));
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        vDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        
        
        Table.setItems(null);
        Table.setItems(vbc);
        
    }

    public void fillComboBox() throws SQLException {
        StudentDB stb = new StudentDB();
        ArrayList<String> reasonOption = stb.getReasonList();

        for (int i = 0; i < reasonOption.size(); i++) {
            options.add(reasonOption.get(i));
        }
        reasonList.getItems().addAll(options);   
        stb.close();
    }

    @FXML
    private void selectReason(ActionEvent event) {
        String description = reasonList.getValue();
        vbc2 = FXCollections.observableArrayList();
        //StudentDB stb = new StudentDB();
        //vbcList = stb.VisitsByCategory(startDate, endDate);
        
        for(int i=0;i<vbcList.size();i++){
            if(description.equals(vbcList.get(i).getDescription())){
            vbc2.add(vbcList.get(i));}
            //System.out.println(vbcList.get(i).getDate());
        }
        
        Reason.setCellValueFactory(new PropertyValueFactory<>("description"));
        Name.setCellValueFactory(new PropertyValueFactory<>("name"));
        vDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        
        
        Table.setItems(null);
        Table.setItems(vbc2);
        
    }

}
