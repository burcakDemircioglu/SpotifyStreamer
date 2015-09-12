package com.example.burcakdemircioglu.spotifystreamer;

import java.io.Serializable;

/**
 * Created by burcakdemircioglu on 11/08/15.
 */
public class Track implements Serializable {
    private String trackName;
    private String albumName;
    private String imageUrl;
    private String previewUrl;
    private String artistName;
    private String trackDuration;

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artisName) {
        this.artistName = artisName;
    }

    public String getTrackDuration() {
        return trackDuration;
    }

    public void setTrackDuration(String trackDuration) {
        this.trackDuration = trackDuration;
    }


}
