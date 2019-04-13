package com.example.adminqism.Model;

public class Users
{
    private  String ism,phone,parol;

    public Users()
    {

    }

    public Users(String ism, String phone, String parol) {
        this.ism = ism;
        this.phone = phone;
        this.parol = parol;
    }

    public String getIsm() {
        return ism;
    }

    public void setIsm(String ism) {
        this.ism = ism;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getParol() {
        return parol;
    }

    public void setParol(String parol) {
        this.parol = parol;
    }
}
