package com.fitback.fitback.Class;

import java.util.List;

public class Session {
    private String name;
    private String activity;
    private List<String> session;

    public Session(String name, String activity, List<String> session) {
        this.name = name;
        this.activity = activity;
        this.session = session;
    }

    public Session() {

    }

    @Override
    public String toString() {
        return "Session{" +
                "name='" + name + '\'' +
                ", activity='" + activity + '\'' +
                ", session=" + session +
                '}';
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSession() {
        return session;
    }

    public void setSession(List<String> session) {
        this.session = session;
    }
}
