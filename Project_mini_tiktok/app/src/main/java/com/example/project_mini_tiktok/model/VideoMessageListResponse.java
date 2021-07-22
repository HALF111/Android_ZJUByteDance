package com.example.project_mini_tiktok.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoMessageListResponse {
    @SerializedName("feeds")
    public List<VideoMessage> feeds;
    @SerializedName("success")
    public boolean success;
}
