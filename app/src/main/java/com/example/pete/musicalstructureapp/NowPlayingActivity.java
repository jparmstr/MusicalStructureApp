package com.example.pete.musicalstructureapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.example.pete.musicalstructureapp.MainActivity.getSongsDatabase;

public class NowPlayingActivity extends AppCompatActivity {

    //region Constants and Instance Variables

    // Constants
    private static final int OPAQUE = 255;
    private static final int TRANSPARENT = 100;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final String TIME_FORMAT_STRING = "%02d:%02d";

    // Song and Queue
    private Song thisSong;
    private List<Song> thisAlbum;

    // Timer / song progress variables
    private boolean playing = false;
    private long timer_startTime = 0;
    private long pauseTimer_startTime = 0;

    // View references (layout_now_playing)
    private ImageView now_playing_album_cover;
    private ProgressBar now_playing_progress_bar;
    private TextView now_playing_song_current_length;
    private TextView now_playing_song_total_length;
    private TextView now_playing_song_title;
    private TextView now_playing_artist_name;
    private TextView now_playing_album_title;
    private Button now_playing_previous_button;
    private Button now_playing_play_button;
    private Button now_playing_next_button;

    //endregion Constants and Instance Variables

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_now_playing);

        // Receive the artistName, songTitle intent extras
        Intent intent = getIntent();
        String artistName = intent.getStringExtra("artistName");
        String songTitle = intent.getStringExtra("songTitle");

        onCreate_sub(artistName, songTitle);

        transport_play();
    }

    /*
     * This method finishes setting up the Activity
     * It can be called from either onCreate or onRestore
     * */
    private void onCreate_sub(String artistName, String songTitle) {
        getViewReferences();

        // Retrieve the songs database
        ArrayList<Song> songs = getSongsDatabase(this);

        // Find the song passed via Intent
        // (Assumes that artist name + song title can uniquely identify a song)
        thisSong = songs.stream()
                .filter(s -> s.getArtistName().equals(artistName) && s.getSongTitle().equals(songTitle))
                .collect(Collectors.toList()).get(0);

        // Find all songs on the same album
        // (simulating queue-like functionality to demonstrate previous/next buttons)
        thisAlbum = songs.stream()
                .filter(a -> a.getAlbumTitle().equals(thisSong.getAlbumTitle()))
                .collect(Collectors.toList());

        // Sort the album by track number
        thisAlbum.sort(Comparator.comparing(Song::getTrackNumber));

        // Set the user interface based on thisSong's properties
        setViewsForCurrentSong();

        // Add onClickListeners to the buttons
        setOnClickListeners();

        setStyle_ofTransportControls();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("artistName", thisSong.getArtistName());
        savedInstanceState.putString("songTitle", thisSong.getSongTitle());
        savedInstanceState.putBoolean("playing", playing);
        savedInstanceState.putLong("timer_startTime", timer_startTime);
        savedInstanceState.putLong("pauseTimer_startTime", pauseTimer_startTime);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String artistName = savedInstanceState.getString("artistName");
        String songTitle = savedInstanceState.getString("songTitle");

        playing = savedInstanceState.getBoolean("playing");
        timer_startTime = savedInstanceState.getLong("timer_startTime");
        pauseTimer_startTime = savedInstanceState.getLong("pauseTimer_startTime");

        // The timer will un-pause itself if we don't do this
        if (!playing) {
            // We don't use timerPause or timerResume here because they mess with the pause timer
            timerHandler.removeCallbacks(timerRunnable);
            now_playing_play_button.setForeground(getDrawable(R.drawable.ic_play_arrow_50dp));
            // Make sure the pause timer is running
            pauseTimerHandler.postDelayed(pauseTimerRunnable, 0);
        }

        onCreate_sub(artistName, songTitle);
    }

    //region Transport Control-related methods

    /*
     * Handle Previous, Play/Pause, and Next button clicks
     * */
    private void handleTransportControls(View view) {
        String viewName = view.getResources().getResourceName(view.getId());

        if (viewName.contains("now_playing_previous_button")) {
            transport_previousSong();
        } else if (viewName.contains("now_playing_play_button")) {
            // Play or pause the current track
            if (playing) {
                transport_pause();
            } else {
                transport_resume();
            }
        } else if (viewName.contains("now_playing_next_button")) {
            transport_nextSong();
        } else {
            displayToast("unknown view name: " + viewName);
        }
    }

    /*
     * Sets the style of the Previous / Next buttons
     * to indicate whether clicking them will have any effect
     * */
    private void setStyle_ofTransportControls() {
        // Set the enabled style of the next button
        if (thisSong.getTrackNumber() != 1) {
            setStyle_ofPreviousButton(true);
        } else {
            // Disable the previous button if it will be unavailable on the next click
            setStyle_ofPreviousButton(false);
        }

        // Set the enabled style of the next button
        if (thisSong.getTrackNumber() != thisAlbum.size()) {
            setStyle_ofNextButton(true);
        } else {
            // Disable the next button if it will be unavailable on the next click
            setStyle_ofNextButton(false);
        }
    }

    private void setStyle_ofPreviousButton(Boolean enabled) {
        if (enabled) {
            now_playing_previous_button.getBackground().setAlpha(OPAQUE);
        } else {
            now_playing_previous_button.getBackground().setAlpha(TRANSPARENT);
        }
    }

    private void setStyle_ofNextButton(Boolean enabled) {
        if (enabled) {
            now_playing_next_button.getBackground().setAlpha(OPAQUE);
        } else {
            now_playing_next_button.getBackground().setAlpha(TRANSPARENT);
        }
    }

    private void transport_nextSong() {
        // Play the next track on the album
        if (thisSong.getTrackNumber() != thisAlbum.size()) {
            thisSong = thisAlbum.get(thisSong.getTrackNumber());
            timerStop();
            setViewsForCurrentSong();
            setStyle_ofTransportControls();
            transport_play();
        }
    }

    private void transport_previousSong() {
        // Play the previous track on the album
        if (thisSong.getTrackNumber() != 1) {
            // The list positions are unintuitive in this method because
            // the thisAlbum List is 0-based but the track numbers are 1-based
            thisSong = thisAlbum.get(thisSong.getTrackNumber() - 2);
            timerStop();
            setViewsForCurrentSong();
            setStyle_ofTransportControls();
            transport_play();
        }
    }

    /*
     * Click the play button
     * This plays the current song from the beginning
     * */
    private void transport_play() {
        timerReset();
        playing = true;
        now_playing_play_button.setForeground(getDrawable(R.drawable.ic_pause_50dp));
    }

    /*
     * Click the pause button
     * This pauses the timer and starts the "pause timer"
     * */
    private void transport_pause() {
        timerPause();
        playing = false;
        now_playing_play_button.setForeground(getDrawable(R.drawable.ic_play_arrow_50dp));
    }

    /*
     * Click the play button (resume playing)
     * This stops the "pause timer" and adds the time elapsed while paused
     * to the original song timer
     * */
    private void transport_resume() {
        timerResume();
        playing = true;
        now_playing_play_button.setForeground(getDrawable(R.drawable.ic_pause_50dp));
    }

    //endregion Transport Control-related methods

    //region Timer-Related Methods

    private final Handler timerHandler = new Handler();
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            timer_countUp();

            timerHandler.postDelayed(this, 10);
        }
    };

    private final Handler pauseTimerHandler = new Handler();
    private final Runnable pauseTimerRunnable = new Runnable() {
        @Override
        public void run() {
            pauseTimerHandler.postDelayed(this, 10);
        }
    };

    private long pauseTimer_pauseTime() {
        return System.currentTimeMillis() - pauseTimer_startTime;
    }

    @Override
    public void onPause() {
        super.onPause();
        timerStop();
    }

    // Start and reset the timer
    private void timerReset() {
        playing = true;
        now_playing_progress_bar.setMax((int) thisSong.getLengthMilliseconds());
        timer_startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    /*
     * Resume the timer without resetting it
     * Stop the pause timer
     * Add the time elapsed while paused to the original timer start time
     * */
    private void timerResume() {
        // Stop the pause timer
        pauseTimerHandler.removeCallbacks(pauseTimerRunnable);

        // Add the time elapsed while paused to the original timer start time
        timer_startTime += pauseTimer_pauseTime();

        // Resume the timer without resetting it
        timerHandler.postDelayed(timerRunnable, 0);
    }

    // Pause the timer (start the pause timer)
    private void timerPause() {
        timerStop();

        // Start the pause timer
        pauseTimer_startTime = System.currentTimeMillis();
        pauseTimerHandler.postDelayed(pauseTimerRunnable, 0);
    }

    // Stop the timer
    private void timerStop() {
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void timer_countUp() {
        long milliseconds_elapsed = System.currentTimeMillis() - timer_startTime;
        int seconds_elapsed = (int) (milliseconds_elapsed / MILLISECONDS_IN_SECOND);
        int minutes_elapsed = seconds_elapsed / SECONDS_IN_MINUTE;
        long milliseconds_remaining = thisSong.getLengthMilliseconds() - milliseconds_elapsed;

        if (milliseconds_remaining > 0) {
            // Update progress bar
            now_playing_progress_bar.setProgress((int) milliseconds_elapsed);

            // Update the current time elapsed
            now_playing_song_current_length.setText(String
                    .format(Locale.getDefault(), TIME_FORMAT_STRING, minutes_elapsed % MINUTES_IN_HOUR, seconds_elapsed % SECONDS_IN_MINUTE));
        } else {
            // Play next song
            transport_nextSong();

            // Update the progress bar
            now_playing_progress_bar.setProgress(0);

            // Update the current time elapsed
            now_playing_song_current_length.setText(String
                    .format(Locale.getDefault(), TIME_FORMAT_STRING, 0, 0));
        }
    }

    //endregion Timer-Related Methods

    /*
     * Given a Song, set the Views (user interface) with its properties
     * @param song The Song that's currently playing: set user interface for this song.
     * */
    private void setViewsForCurrentSong() {
        int albumCoverResourceID = thisSong.getAlbumCover(this, "full");
        now_playing_album_cover.setImageResource(albumCoverResourceID);
        now_playing_song_title.setText(thisSong.getSongTitle());
        now_playing_artist_name.setText(thisSong.getArtistName());
        now_playing_album_title.setText(thisSong.getAlbumTitle());

        // Update the current time elapsed
        // It doesn't matter if this is called with bad data on Activity startup because it will be immediately fixed by timer_countUp
        // However we do need this for when the song is paused and then the screen is rotated
        if (!playing) {
            long milliseconds_elapsed = System.currentTimeMillis() - (timer_startTime + pauseTimer_pauseTime());
            int seconds_elapsed = (int) (milliseconds_elapsed / MILLISECONDS_IN_SECOND);
            int minutes_elapsed = seconds_elapsed / SECONDS_IN_MINUTE;

            now_playing_song_current_length.setText(String
                    .format(Locale.getDefault(), TIME_FORMAT_STRING, minutes_elapsed % MINUTES_IN_HOUR, seconds_elapsed % SECONDS_IN_MINUTE));
        }

        // Update the total song length
        long thisSongDurationSeconds = thisSong.getLengthMilliseconds() / MILLISECONDS_IN_SECOND;
        long minutes = thisSongDurationSeconds / MINUTES_IN_HOUR;
        now_playing_song_total_length.setText(String
                .format(Locale.getDefault(), TIME_FORMAT_STRING, minutes % MINUTES_IN_HOUR, thisSongDurationSeconds % SECONDS_IN_MINUTE));
    }

    /*
     * Get references to the Views in layout_now_playing
     * which will be saved as instance variables of this Activity
     * */
    private void getViewReferences() {
        now_playing_album_cover = findViewById(R.id.now_playing_album_cover);
        now_playing_progress_bar = findViewById(R.id.now_playing_progress_bar);
        now_playing_song_current_length = findViewById(R.id.now_playing_song_current_length);
        now_playing_song_total_length = findViewById(R.id.now_playing_song_total_length);
        now_playing_song_title = findViewById(R.id.now_playing_song_title);
        now_playing_artist_name = findViewById(R.id.now_playing_artist_name);
        now_playing_album_title = findViewById(R.id.now_playing_album_title);
        now_playing_previous_button = findViewById(R.id.now_playing_previous_button);
        now_playing_play_button = findViewById(R.id.now_playing_play_button);
        now_playing_next_button = findViewById(R.id.now_playing_next_button);
    }

    /*
     * Set onClickListeners for the transport control buttons
     * This is done programatically instead of through XML
     * to comply with the Udacity rubric for Project 4
     * */
    private void setOnClickListeners() {
        now_playing_previous_button.setOnClickListener(this::handleTransportControls);

        now_playing_play_button.setOnClickListener(this::handleTransportControls);

        now_playing_next_button.setOnClickListener(this::handleTransportControls);
    }

    // Display Toast notification
    private void displayToast(String textToShow) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, textToShow, duration);
        toast.show();
    }
}