package com.midln.recko;

import java.io.Serializable;

public class Word implements Serializable {
    public String WordSr;
    public String WordEn;
    public int Level;

    public Word(String wordSr, String wordEn, int level){
        this.WordSr = wordSr;
        this.WordEn = wordEn;
        this.Level = level;
    }
}
