package com.example.burcakdemircioglu.spotifystreamer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ArtistList mArtistAdapter;
    ArtistsPager results;
    EditText mSearch;
    private ProgressDialog progress;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    ListView mListView;
    boolean mTwoPane;

    public MainActivityFragment() {
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        progress = new ProgressDialog(getActivity());
        progress.setTitle(R.string.loading);

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mArtistAdapter = new ArtistList(getActivity());


        mSearch = (EditText) rootView.findViewById(R.id.search);
        mSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    progress.show();
                    ArtistSearch artistSearch = new ArtistSearch();
                    artistSearch.execute();

                    ((TextView) getActivity().findViewById(R.id.give_input)).setVisibility(View.GONE);
                    if (getActivity().findViewById(R.id.tracks_container) != null)
                        mTwoPane = true;
                    else
                        mTwoPane = false;
                    mListView = (ListView) rootView.findViewById(R.id.listview_artists);
                    mListView.setAdapter(mArtistAdapter);

                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String artistName = results.artists.items.get(position).name;
                            String artistID = results.artists.items.get(position).id;
                            if (mTwoPane) {
                                FragmentManager fm = getFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();

                                Bundle args = new Bundle();
                                args.putString("artistName", artistName);
                                args.putString("artistID", artistID);
                                TracksActivityFragment fragment = new TracksActivityFragment();
                                fragment.setArguments(args);
                                ft.replace(R.id.tracks_container, fragment)
                                        .commit();
                            } else {

                                Intent intent = new Intent(getActivity(), TracksActivity.class)
                                        .putExtra(Intent.EXTRA_TEXT, artistName).putExtra(Intent.EXTRA_UID, artistID);
                                startActivity(intent);
                            }
                        }
                    });
                    if (mSearch != null) {
                        Log.v("EditText= ", mSearch.getText().toString());
                    }
                }
                return false;
            }
        });


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),
                null,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public class ArtistSearch extends AsyncTask<String, Void, ArtistsPager> {
        private final Context context = this.context;
        ImageView imageView;

        @Override
        protected ArtistsPager doInBackground(String... params) {

            if (mSearch != null) {
                ArrayList<String> artistList = new ArrayList<String>();

                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                try {
                    results = spotify.searchArtists(mSearch.getEditableText().toString());
                    for (Artist artist : results.artists.items) {
                        artistList.add(artist.name);
                        Log.v("spotifyResult= ", artist.name);
                        imageView = (ImageView) getActivity().findViewById(R.id.list_item_artist_image);
                    }
                    return results;
                } catch (Exception e) {
                    Log.v("Exception", e.toString());
                }
            }
            return null;
        }


        @Override
        protected void onPostExecute(ArtistsPager result) {
            progress.dismiss();
            if(results!=null) {
                if (results.artists.items.size() == 0) {
                    Toast toast = Toast.makeText(getActivity(), mSearch.getText() + getString(R.string.not_found), Toast.LENGTH_SHORT);
                    toast.show();
                    ((TextView) getActivity().findViewById(R.id.not_found)).setVisibility(View.VISIBLE);
                } else {
                    ((TextView) getActivity().findViewById(R.id.not_found)).setVisibility(View.GONE);
                }
                if (result != null) {
                    mArtistAdapter.clear();
                    for (Artist artist : result.artists.items) {
                        mArtistAdapter.add(artist);
                    }
                }
            }
        }
    }
}
