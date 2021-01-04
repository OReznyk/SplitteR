package com.splitter.Model;

import android.net.Uri;

import java.util.List;

public class Group {
    private String id;
    private String title;
    private String groupImg;
    private List<String> participants;
    private List<String> adminsIDs;
    private List<Uri> images;
    private List<Basket> baskets;

    public Group() {
    }

    public Group(String id, String title, String groupImg, List<String> participants, List<String> adminsIDs, List<Uri> images, List<Basket> baskets) {
        this.id = id;
        this.title = title;
        this.groupImg = groupImg;
        this.participants = participants;
        this.adminsIDs = adminsIDs;
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

    public String getGroupImg() {
        return groupImg;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public List<String> getAdminsIDs() {
        return adminsIDs;
    }

    public void setAdminsIDs(List<String> adminsIDs) {
        this.adminsIDs = adminsIDs;
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