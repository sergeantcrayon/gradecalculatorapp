package com.airthcompany.gradecalculator;

import android.widget.Button;

/**
 * Created by Airth on 1/11/2015.
 */
public class RowData {

    private int id;
    private String name;
    private int score;
    private int weight;
    private Button button;

    public RowData(int id, String name, int score, int weight){
        this.id = id;
        this.name = name;
        this.score = score;
        this.weight = weight;
    }

    // Getters

    public int getId(){  return id;    }
    public String getName(){  return name;  }
    public int getScore(){ return score; }
    public int getWeight(){ return weight; }


    // Setters/Changers

    public void setId(int id){ this.id = id; }
    public void setName(String name){ this.name = name; }
    public void setScore(int score){ this.score = score; }
    public void setWeight(int weight){ this.weight = weight; }
    public void setButton(Button button){ this.button = button; }


}
