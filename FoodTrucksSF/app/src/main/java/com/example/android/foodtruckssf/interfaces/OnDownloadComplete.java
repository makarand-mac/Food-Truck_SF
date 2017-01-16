package com.example.android.foodtruckssf.interfaces;
// Created by Makarand Deshpande on 16-Jan-17.


public interface OnDownloadComplete {
    boolean OnDownloadSuccess(String Response);
    void OnDownloadFailed(Exception e);
}
