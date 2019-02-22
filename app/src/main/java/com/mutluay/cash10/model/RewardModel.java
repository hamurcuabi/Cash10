package com.mutluay.cash10.model;

public class RewardModel {
    private String name;
    private String howmany;
    private int image;
    private int money;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public RewardModel() {
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHowmany() {
        return howmany;
    }

    public void setHowmany(String howmany) {
        this.howmany = howmany;
    }
}
