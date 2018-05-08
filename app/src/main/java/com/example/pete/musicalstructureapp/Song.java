package com.example.pete.musicalstructureapp;

import android.content.Context;

class Song {

    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int SECONDS_IN_MINUTE = 60;

    private String artistName;
    private String albumTitle;
    private int trackNumber;
    private String songTitle;
    private String songLength;
    private int year;

    public Song(String artistName, String albumTitle, int trackNumber, String songTitle, String songLength, int year) {
        setArtistName(artistName);
        setAlbumTitle(albumTitle);
        setTrackNumber(trackNumber);
        setSongTitle(songTitle);
        setSongLength(songLength);
        setYear(year);
    }

    /*
    * Get the album cover drawable resource based on artistName + albumTitle
    * The calling Activity should be able to use ImageView.setImageResource on the return value
    * @param size "full", "thumb", or "icon"
    * */
    public int getAlbumCover(Context context, String size){
        // I've manually saved images of the album covers and named the files according to the format below
        // I've also had to conform to the android resource naming conventions of only a-z, 0-9, and underscores
        String modifiedArtistName = artistName.toLowerCase();
        modifiedArtistName = modifiedArtistName.replace(" ", "_");
        modifiedArtistName = modifiedArtistName.replaceAll("[^a-z0-9_]", "");

        String modifiedAlbumTitle = albumTitle.toLowerCase();
        modifiedAlbumTitle = modifiedAlbumTitle.replace(" ", "_");
        modifiedAlbumTitle = modifiedAlbumTitle.replaceAll("[^a-z0-9_]", "");

        String imageResourceName = size + "_" + modifiedArtistName + "_" + modifiedAlbumTitle;

        return context.getResources().getIdentifier(imageResourceName, "drawable", context.getPackageName());
    }

    public int getTrackNumber() {
        return trackNumber;
    }

    private void setTrackNumber(int trackNumber) {
        this.trackNumber = trackNumber;
    }

    public String getSongLength() {
        return songLength;
    }

    private void setSongLength(String songLength) {
        this.songLength = songLength;
    }

    /*
    * Returns the total number of milliseconds
    * This is used by the play timer in Now Playing
    * */
    public long getLengthMilliseconds() {
        return getLengthSeconds() * MILLISECONDS_IN_SECOND;
    }

    /*
    * Returns the total number of seconds, ie minutes are converted to seconds too
    * */
    private int getLengthSeconds() {
        String length[] = songLength.split(":");
        int minutes = Integer.valueOf(length[0]);
        int seconds = Integer.valueOf(length[1]);

        return (minutes * SECONDS_IN_MINUTE) + seconds;
    }

    public String getSongTitle() {
        return songTitle;
    }

    private void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtistName() {
        return artistName;
    }

    private void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    private void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public int getYear() {
        return year;
    }

    private void setYear(int year) {
        this.year = year;
    }
}