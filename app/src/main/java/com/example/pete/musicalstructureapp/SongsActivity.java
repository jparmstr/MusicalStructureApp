package com.example.pete.musicalstructureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

import static com.example.pete.musicalstructureapp.MainActivity.getSongsDatabase;

public class SongsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_songs_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Retrieve the songs database
        ArrayList<Song> songs = getSongsDatabase(this);

        // Sort the songs by song title
        songs.sort(Comparator.comparing(Song::getSongTitle));

        // Display the list of songs
        SongAdapter_Songs songAdapter_songs = new SongAdapter_Songs(this, songs);
        ListView listView_songs_list = findViewById(R.id.listView_songs_list);
        listView_songs_list.setAdapter(songAdapter_songs);

        // Set the item click listener(s) for the song list items
        listView_songs_list.setOnItemClickListener((parent, view, position, id) -> {
            // Go to the Now Playing activity, passing this song as the song to play
            Intent gotoNowPlayingActivity = new Intent(getApplicationContext(), NowPlayingActivity.class);

            String artistName = ((TextView) view.findViewById(R.id.item_song_thumbnail_artist_name)).getText().toString();
            String songTitle = ((TextView) view.findViewById(R.id.item_song_thumbnail_song_title)).getText().toString();

            // I'm assuming that each song can be uniquely identified by artist: song title but that might not be the case
            // If I were making this app truly robust I could create unique IDs for each song
            gotoNowPlayingActivity.putExtra("artistName", artistName);
            gotoNowPlayingActivity.putExtra("songTitle", songTitle);

            startActivity(gotoNowPlayingActivity);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}