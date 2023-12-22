package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class RadioStatus {

    @SerializedName("current_track")
    private CurrentTrack currentTrack;

    public CurrentTrack getCurrentTrack() {
        return currentTrack;
    }

    public static class CurrentTrack {

        @SerializedName("title")
        private String title;

        @SerializedName("artwork_url_large")
        private String artworkUrlLarge;

        public String getTitle() {
            return title;
        }

        public String getArtworkUrlLarge() {
            return artworkUrlLarge;
        }
    }
}
