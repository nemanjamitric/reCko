package com.midln.recko;

import java.io.Serializable;

public class WordUser implements Serializable {
    public String WordSr;
    public int Time;
    public WordUser(String wordSr, int time){
        this.WordSr = wordSr;
        this.Time = time;
    }
}
