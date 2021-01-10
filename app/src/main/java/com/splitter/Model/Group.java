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
    private HashMap<String, String> basketsIDs;

    public Group() {
    }

    public Group(String id, String title, String description, String groupImg, HashMap<String, String> participants, List<Uri> images, HashMap<String, String> baskets) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.groupImg = groupImg;
        this.participants = participants;
        this.images = images;
        this.basketsIDs = baskets;
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

    public HashMap<String, String> getBasketsIDs() {
        return basketsIDs;
    }

    public void setBasketsIDs(HashMap<String, String> basketsIDs) {
        this.basketsIDs = basketsIDs;
    }
}