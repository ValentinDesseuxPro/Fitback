package com.fitback.fitback.Class;

public class Profile {
    private double totalDistance;
    private double totalCalories;
    private double IMC;
    private double weight;
    private double height;
    private int age;
    private String sexe;
    private String profileImgUrl;


    public Profile(double totalDistance, double totalCalories, double IMC, double weight, double height, int age, String sexe, String profileImgUrl) {
        this.totalDistance = totalDistance;
        this.totalCalories = totalCalories;
        this.IMC = IMC;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.sexe = sexe;
        this.profileImgUrl = profileImgUrl;
    }

    public Profile() {
    }


    public double calculIMC(double taille, double poids) {
        return Math.round((poids / (taille * taille)) * 100) / 100;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "totalDistance=" + totalDistance +
                ", totalCalories=" + totalCalories +
                ", IMC=" + IMC +
                ", weight=" + weight +
                ", height=" + height +
                ", age=" + age +
                ", sexe='" + sexe + '\'' +
                ", profileImgUrl='" + profileImgUrl + '\'' +
                '}';
    }

    public String getProfileImgUrl() {
        return profileImgUrl;
    }

    public void setProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getIMC() {
        return IMC;
    }

    public void setIMC(double IMC) {
        this.IMC = IMC;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}
