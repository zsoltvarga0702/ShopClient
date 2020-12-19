package com.example.shopclient.Models;

public class SelectedItem {
    public int id;
    public int user_id ;
    public String name;
    public String email;
    public String arrival;
    public String address ;
    public String phone ;
    public String description ;
    public String title ;
    public String price ;
    public String img;
    public String img1;
    public String img2;
    public String img3;
    public String img4;
    public String img5;

    public SelectedItem(int id, int user_id, String name, String email, String arrival, String address, String phone, String description, String title, String price, String img, String img1, String img2, String img3, String img4, String img5) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.arrival = arrival;
        this.address = address;
        this.phone = phone;
        this.description = description;
        this.title = title;
        this.price = price;
        this.img = img;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.img5 = img5;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public SelectedItem(){

    }
    @Override
    public String toString() {
        return "user_id: " + user_id + '\n'+
                "address: " + address + '\n' +
                "phone: " + phone + '\n' +
                "description: " + description + '\n' +
                "title: " + title + '\n' +
                "price: " + price + '\n' +
                "img: " + img + '\n'+'\n'+'\n';
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
