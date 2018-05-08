package com.example.pete.musicalstructureapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

class SongAdapter_Artists extends ArrayAdapter<Song> {

    /*
     * This is a custom constructor which relies on
     * the Overridden getView method instead of passing a valid ResourceID
     * */
    SongAdapter_Artists(Activity context, List<Song> songs) {
        super(context, 0, songs);
    }

    /*
     * This Overrides the getView method of ArrayAdapter
     * It handles the creation of each album (item_artist_thumbnail.xml)
     * */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_artist_thumbnail, parent, false);
        }

        // Get the {@link Song} object located at this position in the list
        Song currentSong = getItem(position);

        // Set the artist image
        // TODO: create new image resources for artist images?
        ImageView item_artist_thumbnail_imageView = listItemView.findViewById(R.id.item_artist_thumbnail_imageView);
        if (currentSong != null) {
            int albumCoverResourceID = currentSong.getAlbumCover(getContext(), "thumb");
            item_artist_thumbnail_imageView.setImageResource(albumCoverResourceID);
        }

        // Set the artist name
        TextView item_artist_thumbnail_artist_name = listItemView.findViewById(R.id.item_artist_thumbnail_artist_name);
        if (currentSong != null) {
            item_artist_thumbnail_artist_name.setText(currentSong.getArtistName());
        }

        // Return the whole list item layout (containing 2 TextViews and an ImageView) so that it can be shown in the ListView
        return listItemView;
    }
}
