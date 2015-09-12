package com.example.burcakdemircioglu.spotifystreamer;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TracksActivityFragment extends Fragment {
    //implements LoaderManager.LoaderCallbacks<Cursor>{
    private String artistName;
    private String artistID;
    private Uri mUri;
    private TrackList mTrackAdapter;
    ArrayList<Track> result = new ArrayList<Track>();
    private ProgressDialog progress;
    private static final String LOG_TAG = TracksActivityFragment.class.getSimpleName();
    private ImageView mIconView;
    private TextView mSongNameView;
    private TextView mAlbumNameView;
    boolean mTwoPane;
    ListView mListView;
    static final String DETAIL_URI = "URI";
    private static String[] TRACK_COLUMNS = {
            "song_name",
            "album_name",
            "image_id"
    };

    public TracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tracks, container, false);
        progress = new ProgressDialog(getActivity());
        progress.setTitle(R.string.loading);

//        mIconView=(ImageView)rootView.findViewById(R.id.list_item_track_image);
//        mSongNameView=(TextView)rootView.findViewById(R.id.list_item_track_textview);

        if (getActivity().findViewById(R.id.listview_artists) != null)
            mTwoPane = true;
        else
            mTwoPane = false;

        Log.v("tracksFragment mTwoPane", String.valueOf(mTwoPane));
        if (mTwoPane) {
            Bundle arguments = getArguments();
            if (arguments != null) {
                artistName = arguments.getString("artistName");
                artistID = arguments.getString("artistID");
            }
        } else {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra((Intent.EXTRA_TEXT)) && intent.hasExtra((Intent.EXTRA_UID))) {
                artistName = intent.getStringExtra(Intent.EXTRA_TEXT);
                artistID = intent.getStringExtra(Intent.EXTRA_UID);
                //((TextView)rootView.findViewById(R.id.song_text)).setText(artistID);
            }
        }

        ((ActionBarActivity) getActivity()).getSupportActionBar().setSubtitle(artistName);

        mTrackAdapter = new TrackList(getActivity());
        progress.show();
        TrackSearch trackSearch = new TrackSearch();
        trackSearch.execute();

        mListView = (ListView) rootView.findViewById(R.id.listview_tracks);
        mListView.setAdapter(mTrackAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction fragmentManager = getActivity().getFragmentManager().beginTransaction();
                Track track = mTrackAdapter.getItem(position);

                if (mTwoPane) {
                    PlayerActivityFragment newFragment = new PlayerActivityFragment().newInstance(track, result, mTwoPane);
                    newFragment.show(fragmentManager, "dialog");
                } else {
                    Intent intent = new Intent(getActivity(), PlayerActivity.class)
                            .putExtra("track", track)
                            .putExtra("trackArrayList", result)
                            .putExtra("twoPane", mTwoPane);
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }


    public class TrackSearch extends AsyncTask<String, Void, ArrayList<Track>> {
        private final Context context = this.context;
        ImageView imageView;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        @Override
        protected ArrayList<Track> doInBackground(String... params) {

            if (artistName != null) {
                // ((TextView) getActivity().findViewById(R.id.tracks_not_found)).setVisibility(View.GONE);

                ArrayList<String> artistList = new ArrayList<String>();
                try {
                    final String SPOTIFY_BASE_URL = "https://api.spotify.com/v1/artists/" + artistID + "/top-tracks?country=SE";
                    URL url = new URL(SPOTIFY_BASE_URL);

                    // Log.v(LOG_TAG, "Built URI "+builtUri.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        ((TextView) getActivity().findViewById(R.id.tracks_not_found)).setVisibility(View.VISIBLE);

                        return null;
                    }
                    String tracksJsonStr = buffer.toString();
                    try {

                        return getTrackDataFromJson(tracksJsonStr, 10);


                    } catch (JSONException e) {
                        Log.e("Exception", e.getMessage(), e);
                        e.printStackTrace();
                    }
                    Log.v("Top10Tracks JSON: ", tracksJsonStr);
                } catch (IOException e) {
                    Log.e("Connection", "Error", e);
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Track> result) {
            progress.dismiss();
            if (result != null) {
                if (!result.isEmpty()) {
                    ((TextView) getActivity().findViewById(R.id.tracks_not_found)).setVisibility(View.GONE);
                    Log.v("result", result.toString());

                    mTrackAdapter.clear();
                    for (Track track : result) {
                        mTrackAdapter.add(track);
                    }
                } else {
                    ((TextView) getActivity().findViewById(R.id.tracks_not_found)).setVisibility(View.VISIBLE);
                }
            }
        }


        private ArrayList<Track> getTrackDataFromJson(String topTracksJsonStr, int SongAmount)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "tracks";
            final String OWM_NAME = "name";
            final String OWM_ALBUM = "album";
            final String OWM_IMAGES = "images";
            final String OWM_IMAGEURL = "url";
            final String OWM_PREVIEWURL = "preview_url";
            final String OWM_DURATION = "duration_ms";

            JSONObject trackJson = new JSONObject(topTracksJsonStr);
            JSONArray trackArray = trackJson.getJSONArray(OWM_LIST);
            String trackName;
            String albumName;
            String imageURL = null;
            String previewURL;
            String duration;
            Track track;
            for (int i = 0; i < trackArray.length(); i++) {

                track = new Track();

                JSONObject trackInfo = trackArray.getJSONObject(i);
                trackName = trackInfo.getString(OWM_NAME);
                previewURL = trackInfo.getString(OWM_PREVIEWURL);
                JSONObject albumObject = trackInfo.getJSONObject(OWM_ALBUM);
                albumName = albumObject.getString(OWM_NAME);
                duration = trackInfo.getString(OWM_DURATION);
                JSONArray AlbumImagesArray = albumObject.getJSONArray(OWM_IMAGES);

                if (AlbumImagesArray.length() != 0) {
                    JSONObject AlbumImage = AlbumImagesArray.getJSONObject(0);
                    imageURL = AlbumImage.getString(OWM_IMAGEURL);
                }

                track.setArtistName(artistName);
                track.setAlbumName(albumName);

                track.setTrackName(trackName);
                track.setTrackDuration(duration);
                track.setPreviewUrl(previewURL);

                if (imageURL != null) {
                    track.setImageUrl(imageURL);

                }
                result.add(track);
            }

            return result;

        }
    }


}
