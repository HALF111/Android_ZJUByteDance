package com.example.project_mini_tiktok.model;

import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("result")
    public VideoMessage message;
    @SerializedName("success")
    public boolean success;
    @SerializedName("url")
    public String url;
    @SerializedName("error")
    public String error;
}
