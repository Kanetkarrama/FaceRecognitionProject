/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectreport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author liuziqi
 */
public class visitByCategory {

    private StringProperty description;
    private StringProperty name;
    private StringProperty visitDate;

    public visitByCategory(String description, String name, String visitDate) {
        this.description = new SimpleStringProperty(description);
        this.name = new SimpleStringProperty(name);
        this.visitDate = new SimpleStringProperty(visitDate);
    }

    /*
    public StringProperty getDescription() {
        return description;
    }

    public void setDescription(StringProperty description) {
        this.description = description;
    }

    public StringProperty getName() {
        return name;
    }

    public void setName(StringProperty name) {
        this.name = name;
    }

    public StringProperty getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(StringProperty visitDate) {
        this.visitDate = visitDate;
    }
     */
    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty visitProperty() {
        return visitDate;
    }

    public void setName(String Name) {
        this.name.set(Name);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setDate(String visitDate) {
        this.visitDate.set(visitDate);
    }

    public String getName() {
        return name.get();
    }

    public String getVisitDate() {
        return visitDate.get();
    }

    public String getDescription() {
        return description.get();
    }

}
