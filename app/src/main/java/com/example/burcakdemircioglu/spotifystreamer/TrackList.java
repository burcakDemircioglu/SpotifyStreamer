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

/**
 * Created by burcakdemircioglu on 16/06/15.
 */
public class TrackList extends ArrayAdapter<Track> {//implements Parcelable{
    private LayoutInflater mLayoutInflater;
    private Track track;
    public TrackList(Context context) {
        super(context, 0);
        mLayoutInflater = LayoutInflater.from(context);
    }
//    public TrackList() {
//        super(null,0);
//    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_track, parent, false);
            holder = new ViewHolder();

            holder.image = (ImageView) view.findViewById(R.id.list_item_track_image);
            holder.trackName = (TextView) view.findViewById(R.id.list_item_track_textview);
            holder.albumName = (TextView) view.findViewById(R.id.list_item_album_textview);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        track = getItem(position);


        Log.v("trackName", track.getTrackName());
        String trackName = track.getTrackName();
        String imageURL = track.getImageUrl();
        String albumName=track.getAlbumName();
        //separated[1] = separated[1].trim(); to trim the spaces
        if (imageURL != null) {
            Picasso.with(getContext()).load(imageURL).into(holder.image);
        }
        holder.trackName.setText(trackName);
        holder.albumName.setText(albumName);
        return view;
    }

    class ViewHolder {
        ImageView image;
        TextView trackName;
        TextView albumName;
    }
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        // create a bundle for the key value pairs
//        Bundle bundle = new Bundle();
//
//        // insert the key value pairs to the bundle
//        bundle.putSerializable("track", track);
//
//        // write the key value pairs to the parcel
//        dest.writeBundle(bundle);
//    }
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//    public static final Parcelable.Creator<TrackList> CREATOR = new Creator<TrackList>() {
//
//        @Override
//        public TrackList createFromParcel(Parcel source) {
//            // read the bundle containing key value pairs from the parcel
//            Bundle bundle = source.readBundle();
//
//            // instantiate a person using values from the bundle
//            return new TrackList(getActivity());
//        }
//
//        @Override
//        public TrackList[] newArray(int size) {
//            return new TrackList[size];
//        }
//
//    };
}
