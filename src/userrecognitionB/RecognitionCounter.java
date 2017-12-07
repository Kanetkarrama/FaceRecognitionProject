/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userrecognitionB;

/**
 *
 * @author Rama
 */
public class RecognitionCounter {
    private String name;
    private int count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    RecognitionCounter(String rName, int i) {
        this.name = rName;
        this.count = 0;

    }
}
