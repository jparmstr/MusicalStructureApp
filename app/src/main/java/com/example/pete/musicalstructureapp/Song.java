package com.example.pete.musicalstructureapp;

import android.content.Context;

import java.time.Duration;
import java.util.Locale;

class Song {

    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final String TIME_FORMAT_STRING = "%02d:%02d";

    private String artistName;
    private String albumTitle;
    private int trackNumber;
    private String songTitle;
    private Duration songLength;
    private int year;

    // TODO: Duration for songLength is actually kind of useless
    // We could either store the songTitle as long (total number of milliseconds)
    //  or as a String "01:23" and get the total millis from a public-facing method upon request
    // The second approach might be more efficient because we load the whole database
    //  in most Activities and don't need more than a String to represent the track time
    //  except when a specific song is currently playing

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

    /*
    * Returns the songLength as a Duration
    * */
    public Duration getSongDuration(){
        return songLength;
    }

    /*
    * Returns the songLength Duration as a String in the format mm:ss
    * */
    public String getSongLength() {
        String result;

        long thisSongDurationSeconds = thisSongDurationMilliseconds() / MILLISECONDS_IN_SECOND;
        long minutes = thisSongDurationSeconds / MINUTES_IN_HOUR;
        result = String
                .format(Locale.getDefault(), TIME_FORMAT_STRING, minutes % MINUTES_IN_HOUR, thisSongDurationSeconds % SECONDS_IN_MINUTE);

        return result;
    }

    /*
     * @param songLength is provided in the String format
     * This method automatically converts from String to Duration
     * */
    private void setSongLength(String songLength) {
        // Convert the songLength String into something parseable by Duration.parse()
        // See: https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-
        // Expects the songLength string to be in the format mm:ss
        CharSequence charSequence = songLength;
        charSequence = "PT" + charSequence;
        charSequence = charSequence.toString().replace(":", "M");
        charSequence = charSequence + "S";

        this.songLength = Duration.parse(charSequence);
    }

    private long thisSongDurationMilliseconds() {
        return getSongDuration().getSeconds() * MILLISECONDS_IN_SECOND;
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