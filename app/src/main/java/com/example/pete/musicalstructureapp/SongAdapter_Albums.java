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

class SongAdapter_Albums extends ArrayAdapter<Song> {

    /*
     * This is a custom constructor which relies on
     * the Overridden getView method instead of passing a valid ResourceID
     * */
    SongAdapter_Albums(Activity context, List<Song> songs) {
        super(context, 0, songs);
    }

    /*
     * This Overrides the getView method of ArrayAdapter
     * It handles the creation of each album (item_album_thumbnail.xml)
     * */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_album_thumbnail, parent, false);
        }

        // Get the {@link Song} object located at this position in the list
        Song currentSong = getItem(position);

        // Set the album cover
        ImageView item_album_thumbnail_imageView = listItemView.findViewById(R.id.item_album_thumbnail_imageView);
        if (currentSong != null) {
            int albumCoverResourceID = currentSong.getAlbumCover(getContext(), "thumb");
            item_album_thumbnail_imageView.setImageResource(albumCoverResourceID);
        }

        // Set the album title
        TextView albumTitleTextView = listItemView.findViewById(R.id.item_album_thumbnail_title);
        if (currentSong != null) {
            albumTitleTextView.setText(currentSong.getAlbumTitle());
        }

        // Set the artist name
        TextView artistNameTextView = listItemView.findViewById(R.id.item_album_thumbnail_artist);
        if (currentSong != null) {
            artistNameTextView.setText(currentSong.getArtistName());
        }

        // Return the whole list item layout (containing 2 TextViews and an ImageView) so that it can be shown in the ListView
        return listItemView;
    }
}