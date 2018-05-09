package com.example.pete.musicalstructureapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.pete.musicalstructureapp.MainActivity.getSongsDatabase;

public class AlbumDetailsActivity extends AppCompatActivity {

    private String albumTitle = "";
    private String artistName = "";

    // View references (layout_album_details)
    private LinearLayout album_details_songs_linearLayout;
    private ImageView album_details_album_cover;
    private TextView album_details_album_title;
    private TextView album_details_artist_name;
    private TextView album_details_year;
    private TextView album_details_song_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_album_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Receive the albumTitle, artistName intent extras
        Intent intent = getIntent();
        albumTitle = intent.getStringExtra("albumTitle");
        artistName = intent.getStringExtra("artistName");

        getViewReferences();

        // Retrieve the songs database
        ArrayList<Song> songs = getSongsDatabase(this);

        // Find all songs with this album name (using Java 8 lambda expression)
        List<Song> filteredSongs = songs.stream()
                .filter(a -> a.getAlbumTitle().equals(albumTitle) && a.getArtistName().equals(artistName))
                .collect(Collectors.toList());

        // Set values of static Views (album title, artist name, etc)
        int albumCoverResourceID = filteredSongs.get(0).getAlbumCover(this, "full");

        album_details_album_cover.setImageResource(albumCoverResourceID);
        album_details_album_title.setText(albumTitle);
        album_details_artist_name.setText(artistName);
        album_details_year.setText(String.valueOf(filteredSongs.get(0).getYear()));
        String song_count_text = String.format(getResources().getQuantityString(R.plurals.song_count_text, filteredSongs.size())
                ,filteredSongs.size());
        album_details_song_count.setText(song_count_text);

        // Create list item style Views for each Song in filteredSongs, adding them to album_details_songs_linearLayout
        for (Song s : filteredSongs) {
            View songInfoView = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_song_information, album_details_songs_linearLayout,
                            false);

            // Get view references in item_song_information
            TextView item_song_information_track_number = songInfoView.findViewById(R.id.item_song_information_track_number);
            TextView item_song_information_song_title = songInfoView.findViewById(R.id.item_song_information_song_title);
            TextView item_song_information_track_length = songInfoView.findViewById(R.id.item_song_information_track_length);

            // Set view properties
            item_song_information_track_number.setText(String.valueOf(s.getTrackNumber()));
            item_song_information_song_title.setText(s.getSongTitle());
            item_song_information_track_length.setText(s.getSongLength());

            // Set the on click listener for this songInfoView
            songInfoView.setOnClickListener(this::songOnClickHandler);

            album_details_songs_linearLayout.addView(songInfoView);
        }
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

    /*
     * Defines what happens when a song is clicked in the album details activity
     * (go to the Now Playing activity)
     * */
    private void songOnClickHandler(View v) {
        // Go to the Now Playing activity, passing this song as the song to play
        Intent gotoNowPlayingActivity = new Intent(getApplicationContext(), NowPlayingActivity.class);

        String songTitle = ((TextView) v.findViewById(R.id.item_song_information_song_title)).getText().toString();

        // See the Songs Activity onItemClick method for more notes about this approach
        gotoNowPlayingActivity.putExtra("artistName", artistName);
        gotoNowPlayingActivity.putExtra("songTitle", songTitle);

        startActivity(gotoNowPlayingActivity);
    }

    /*
     * Get references to the Views in layout_album_details
     * which will be saved as instance variables of this Activity
     * */
    private void getViewReferences() {
        album_details_songs_linearLayout = findViewById(R.id.album_details_songs_linearLayout);
        album_details_album_cover = findViewById(R.id.album_details_album_cover);
        album_details_album_title = findViewById(R.id.album_details_album_title);
        album_details_artist_name = findViewById(R.id.album_details_artist_name);
        album_details_year = findViewById(R.id.album_details_year);
        album_details_song_count = findViewById(R.id.album_details_song_count);
    }
}