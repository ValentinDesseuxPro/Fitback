package com.fitback.fitback.Class;

import java.util.ArrayList;
import java.util.List;

public class Activity {
    private String name;
    private String icon;
    private List<String> session;

    public Activity(String name, String icon, String map_needed) {
        this.name = name;
        this.icon = icon;
        this.map_needed = map_needed;
    }

    public Activity() {

    }

    @Override
    public String toString() {
        return "Activity{" +
                "name='" + name + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }

    public List<String> getSession() {
        return session;
    }

    public void setSession(List<String> session) {
        this.session = session;
    }

    public String getMap_needed() {
        return map_needed;
    }

    public void setMap_needed(String map_needed) {
        this.map_needed = map_needed;
    }

    private String map_needed;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
