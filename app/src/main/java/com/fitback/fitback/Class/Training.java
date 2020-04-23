package com.fitback.fitback.Class;

public class Training {
    private String name;
    private String calory;
    private String distance;
    private String duration;
    private String urlMap;
    private String date;
    private String feeling;
    private String painLocationBefore;
    private String painLevelBefore;
    private String painLocationAfter;
    private boolean isSession;
    private String sessionName;
    private String painLevelAfter;
    private String remarks;
    private String selected_activity;


    public Training(String name, String selected_activity, String calory, String distance, String duration, String urlMap, String date, String feeling, String painLevelAfter, String painLevelBefore, String painLocationAfter, String painLocationBefore, String remarks, boolean isSession, String sessionName) {
        this.name = name;
        this.selected_activity = selected_activity;
        this.calory = calory;
        this.distance = distance;
        this.duration = duration;
        this.urlMap = urlMap;
        this.date = date;
        this.feeling = feeling;
        this.painLevelAfter = painLevelAfter;
        this.painLevelBefore = painLevelBefore;
        this.painLocationAfter = painLocationAfter;
        this.painLocationBefore = painLocationBefore;
        this.remarks = remarks;
        this.isSession = isSession;
        this.sessionName = sessionName;
    }

    public Training() {
    }


    @Override
    public String toString() {
        return "Training{" +
                "name='" + name + '\'' +
                ", calory='" + calory + '\'' +
                ", distance='" + distance + '\'' +
                ", duration='" + duration + '\'' +
                ", urlMap='" + urlMap + '\'' +
                ", date='" + date + '\'' +
                ", feeling='" + feeling + '\'' +
                ", painLocationBefore='" + painLocationBefore + '\'' +
                ", painLevelBefore='" + painLevelBefore + '\'' +
                ", painLocationAfter='" + painLocationAfter + '\'' +
                ", painLevelAfter='" + painLevelAfter + '\'' +
                ", remarks='" + remarks + '\'' +
                ", isSession='" + isSession + '\'' +
                ", sessioName='" + sessionName + '\'' +
                '}';
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalory() {
        return calory;
    }

    public void setCalory(String calory) {
        this.calory = calory;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrlMap() {
        return urlMap;
    }

    public void setUrlMap(String urlMap) {
        this.urlMap = urlMap;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getPainLocationBefore() {
        return painLocationBefore;
    }

    public void setPainLocationBefore(String painLocationBefore) {
        this.painLocationBefore = painLocationBefore;
    }

    public String getPainLevelBefore() {
        return painLevelBefore;
    }

    public void setPainLevelBefore(String painLevelBefore) {
        this.painLevelBefore = painLevelBefore;
    }

    public String getPainLocationAfter() {
        return painLocationAfter;
    }

    public void setPainLocationAfter(String painLocationAfter) {
        this.painLocationAfter = painLocationAfter;
    }

    public String getPainLevelAfter() {
        return painLevelAfter;
    }

    public void setPainLevelAfter(String painLevelAfter) {
        this.painLevelAfter = painLevelAfter;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }


    public String getSelected_activity() {
        return selected_activity;
    }

    public void setSelected_activity(String selected_activity) {
        this.selected_activity = selected_activity;
    }

    public boolean isSession() {
        return isSession;
    }

    public void setSession(boolean session) {
        isSession = session;
    }


}
