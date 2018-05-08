package com.example.pete.musicalstructureapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class MainActivity extends AppCompatActivity {

    // View References
    private Button main_menu_artists;
    private Button main_menu_albums;
    private Button main_menu_songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getViewReferences();

        setOnClickListeners();
    }

    /*
    This retrieves the database of Songs which is stored in JSON format
    The database is based on a CSV spreadsheet created in Excel
    The information was pretty much copy + pasted directly from Wikipedia into Excel
    The resulting CSV file was converted to JSON using this tool:
    http://www.convertcsv.com/csv-to-json.htm

    This data format is sufficient for demoing the user interface for purposes of this Project
    If this were a fully functional app it would build this information from MP3 metadata
    or retrieve it from a remote API.
    */
    public static ArrayList<Song> getSongsDatabase(Context context) {
        ArrayList<Song> result = new ArrayList<>();

        // Get the JSON resource as a String
        InputStream inputStream = context.getResources().openRawResource(R.raw.song_database);
        String jsonString = new Scanner(inputStream).useDelimiter("\\A").next();

        // Parse the JSON string
        JSONArray array;
        try {
            array = new JSONArray(jsonString);
            for (int i = 0; i < array.length(); i++) {
                JSONObject row = array.getJSONObject(i);
                String artistName = row.getString("Artist Name");
                String albumTitle = row.getString("Album Name");
                int trackNumber = row.getInt("Track Number");
                String songTitle = row.getString("Song Title");
                String songLength = row.getString("Song Length");
                int year = row.getInt("Year");

                Song song = new Song(artistName, albumTitle, trackNumber, songTitle, songLength, year);
                result.add(song);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void goToAlbumsActivity() {
        Intent intentAlbumsActivity = new Intent(getApplicationContext(), AlbumsActivity.class);
        startActivity(intentAlbumsActivity);
    }

    private void goToArtistsActivity() {
        Intent intentArtistsActivity = new Intent(getApplicationContext(), ArtistsActivity.class);
        startActivity(intentArtistsActivity);
    }

    private void goToSongsActivity() {
        Intent intentSongsActivity = new Intent(getApplicationContext(), SongsActivity.class);
        startActivity(intentSongsActivity);
    }

    /*
     * Set onClickListeners for the transport control buttons
     * This is done programatically instead of through XML
     * to comply with the Udacity rubric for Project 4
     * */
    private void setOnClickListeners() {
        main_menu_artists.setOnClickListener(v -> goToArtistsActivity());

        main_menu_albums.setOnClickListener(v -> goToAlbumsActivity());

        main_menu_songs.setOnClickListener(v -> goToSongsActivity());
    }

    /*
     * Get references to the Button Views on the main menu
     * */
    private void getViewReferences() {
        main_menu_artists = findViewById(R.id.main_menu_artists);
        main_menu_albums = findViewById(R.id.main_menu_albums);
        main_menu_songs = findViewById(R.id.main_menu_songs);
    }

    /*
     * distinctByKey method as discussed here:
     * @see <a href="https://stackoverflow.com/questions/23699371/java-8-distinct-by-property">Stack Overflow</a>
     * Allows the use of Java 8 Lambda expressions which consider a certain property of the given objects
     * */
    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}