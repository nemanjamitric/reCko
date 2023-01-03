package com.midln.recko;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WordsObject implements Serializable {
    public int Version;
    public List<Word> Words;
    public WordsObject() {
        Words = new ArrayList<Word>();
    }
}

