package com.midln.recko;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public String UserName;
    public int XP;
    public List<WordUser> WordsForUser;
    public User(String userName, int xp){
        this.UserName = userName;
        this.XP = xp;
        this.WordsForUser = new ArrayList<WordUser>();
    }
}
