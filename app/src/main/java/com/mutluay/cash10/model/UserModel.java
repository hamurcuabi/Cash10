package com.mutluay.cash10.model;

public class UserModel {
    private String docId;
    private String name;
    private String email;
    private String phone;
    private String photoUrl;
    private String password;
    private int point;
    private int star;
    private int heart;
    private boolean islogin;
    private boolean isAdmin;
    private String playerId;
    private String ref_code;
    private boolean used_ref;
    public UserModel() {
    }

    public int getHeart() {
        return heart;
    }

    public void setHeart(int heart) {
        this.heart = heart;
    }

    public boolean isIslogin() {
        return islogin;
    }

    public void setIslogin(boolean islogin) {
        this.islogin = islogin;
    }

    public boolean isUsed_ref() {
        return used_ref;
    }

    public void setUsed_ref(boolean used_ref) {
        this.used_ref = used_ref;
    }

    public String getRef_code() {
        return ref_code;
    }

    public void setRef_code(String ref_code) {
        this.ref_code = ref_code;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDocId() {

        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
