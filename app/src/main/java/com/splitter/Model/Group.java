package com.splitter.Model;

import android.net.Uri;

import java.util.HashMap;
import java.util.List;

public class Group {
    private String id;
    private String title, description;
    private String groupImg;
    private HashMap<String, String> participants;
    private List<Uri> images;
    private List<Basket> baskets;

    public Group() {
    }

    public Group(String id, String title, String description, String groupImg, HashMap<String, String> participants, List<Uri> images, List<Basket> baskets) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.groupImg = groupImg;
        this.participants = participants;
        this.images = images;
        this.baskets = baskets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public HashMap<String, String> getParticipants() {
        return participants;
    }

    public void setParticipants(HashMap<String, String> participants) {
        this.participants = participants;
    }

    public List<Uri> getImages() {
        return images;
    }

    public void setImages(List<Uri> images) {
        this.images = images;
    }

    public List<Basket> getBaskets() {
        return baskets;
    }

    public void setBaskets(List<Basket> baskets) {
        this.baskets = baskets;
    }
}