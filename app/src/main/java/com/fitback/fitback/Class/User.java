package com.fitback.fitback.Class;

public class User {
    private String lastname;
    private String firstname;
    private String email;
    private Profile profile;

    public User(String lastname, String firstname, String email) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.profile = new Profile(0, 0, 0, 0, 0, 0, "null", "null");
    }

    public User(String lastname, String firstname, String email, Profile profile) {
        this(lastname, firstname, email);
        this.profile = profile;
    }


    public User() {

    }

    @Override
    public String toString() {
        return "User{" +
                "lastname='" + lastname + '\'' +
                ", firstname='" + firstname + '\'' +
                ", email='" + email + '\'' +
                ", profile=" + profile +
                '}';
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
