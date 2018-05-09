package com.example.pete.musicalstructureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.pete.musicalstructureapp.MainActivity.distinctByKey;
import static com.example.pete.musicalstructureapp.MainActivity.getSongsDatabase;

/*
* Albums Activity
* + populates layout_album_thumbnails
* + filters songs database to only show unique album titles
* TODO: can optionally receive an intent specifying which Artist Name to filter by
* +: each item_album_thumbnail has a click event leading to AlbumDetailsActivity
* */
public class AlbumsActivity extends AppCompatActivity {

    private String artistName_filter = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_album_thumbnails);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Receive artistName_filter from ArtistsActivity, if any
        Intent intent = getIntent();
        artistName_filter = intent.getStringExtra("artistName_filter");
        if (artistName_filter == null) {
            artistName_filter= "";
        }

        // Retrieve the songs database
        ArrayList<Song> songs = getSongsDatabase(this);

        // There should only be one album displayed per song, so we need to obtain a list of distinct album titles from our master songs list
        // Use Java 8 Lambda expression for this purpose
        List<Song> filteredSongs = songs.stream()
                .filter(distinctByKey(Song::getAlbumTitle))
                .collect(Collectors.toList());

        // Also filter by artist, if there is any artistName_filter
        if (!artistName_filter.equals("")) {
            filteredSongs = filteredSongs.stream()
                    .filter(s -> s.getArtistName().equals(artistName_filter))
                    .collect(Collectors.toList());
        }

        // Display the list of albums
        SongAdapter_Albums songAdapter_albums = new SongAdapter_Albums(this, filteredSongs);
        GridView gridView_album_list = findViewById(R.id.gridView_album_list);
        gridView_album_list.setAdapter(songAdapter_albums);

        // Set the item click listener(s) for the album views
        gridView_album_list.setOnItemClickListener((parent, view, position, id) -> {
            // Go to the album details activity, passing the album name as an intent extra
            Intent gotoAlbumDetailsActivity = new Intent(getApplicationContext(), AlbumDetailsActivity.class);

            // Sending both the album title and artist name
            // in case there is an album with the same title by a different artist
            String albumTitle = ((TextView) view.findViewById(R.id.item_album_thumbnail_title)).getText().toString();
            String artistName = ((TextView) view.findViewById(R.id.item_album_thumbnail_artist)).getText().toString();

            gotoAlbumDetailsActivity.putExtra("albumTitle", albumTitle);
            gotoAlbumDetailsActivity.putExtra("artistName", artistName);

            startActivity(gotoAlbumDetailsActivity);
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
