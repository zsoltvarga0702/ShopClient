package com.example.shopclient.Models;

public class User {
    public int id;
    public String name;
    public String email;
    public String password;

    public User(int id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }
    @Override
    public String toString() {
        return "id: " + id + '\n'+
                "name: " + name + '\n' +
                "email: " + email + '\n' +
                "password: " + password + '\n'+'\n'+'\n';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
