package com.example.pete.musicalstructureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.pete.musicalstructureapp.MainActivity.distinctByKey;
import static com.example.pete.musicalstructureapp.MainActivity.getSongsDatabase;

public class ArtistsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_artists_list);

        // Retrieve the songs database
        ArrayList<Song> songs = getSongsDatabase(this);

        // There should only be one entry displayed per artist, so we need to obtain a list of distinct artist names from our master songs list
        // Use Java 8 Lambda expression for this purpose
        List<Song> filteredSongs = songs.stream()
                .filter(distinctByKey(Song::getArtistName))
                .collect(Collectors.toList());

        // Display the list of artists
        SongAdapter_Artists songAdapter_artists = new SongAdapter_Artists(this, filteredSongs);
        GridView gridView_artists_list = findViewById(R.id.gridView_artists_list);
        gridView_artists_list.setAdapter(songAdapter_artists);

        // Set the item click listener(s) for the artist views
        gridView_artists_list.setOnItemClickListener((parent, view, position, id) -> {
            // Go to the albums activity, passing the artist name (to filter by) as an intent extra
            Intent gotoAlbumsActivity = new Intent(getApplicationContext(), AlbumsActivity.class);

            String artistName = ((TextView) view.findViewById(R.id.item_artist_thumbnail_artist_name)).getText().toString();

            gotoAlbumsActivity.putExtra("artistName_filter", artistName);

            startActivity(gotoAlbumsActivity);
        });
    }
}
