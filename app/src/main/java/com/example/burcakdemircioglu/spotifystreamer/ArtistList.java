package com.example.burcakdemircioglu.spotifystreamer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by burcakdemircioglu on 14/06/15.
 */
public class ArtistList extends ArrayAdapter<Artist> {
    private LayoutInflater mLayoutInflater;

    public ArtistList(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_artist, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) view.findViewById(R.id.list_item_artist_image);
            viewHolder.name = (TextView) view.findViewById(R.id.list_item_artist_textview);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final Artist artist = getItem(position);
        Log.v("artistName", artist.name);

        if (artist.images.size() != 0) {
            Picasso.with(getContext()).load(artist.images.get(0).url).into(viewHolder.image);
        }
        viewHolder.name.setText(artist.name);
        return view;
    }

    class ViewHolder {
        ImageView image;
        TextView name;
    }
}
