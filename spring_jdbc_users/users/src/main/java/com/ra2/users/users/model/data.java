package com.ra2.users.users.model;

import java.util.List;

public class Data {
    private int count;
    private String control;
    private List<Users> users;
    
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
    
    public String getControl() { return control; }
    public void setControl(String control) { this.control = control; }
    
    public List<Users> getUsers() { return users; }
    public void setUsers(List<Users> users) { this.users = users; }
}