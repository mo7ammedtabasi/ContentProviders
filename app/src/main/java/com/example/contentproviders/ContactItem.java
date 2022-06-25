package com.example.contentproviders;

public class ContactItem {
    private String id,name,number,email,photo,otherDetails;

    public ContactItem(String id, String name, String number, String email, String photo, String otherDetails) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
        this.photo = photo;
        this.otherDetails = otherDetails;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
    }
}
