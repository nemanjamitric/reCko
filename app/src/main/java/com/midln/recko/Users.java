package com.midln.recko;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Users implements Serializable {
public List<User> Users;
public Users(){
    this.Users = new ArrayList<User>();
}
}
