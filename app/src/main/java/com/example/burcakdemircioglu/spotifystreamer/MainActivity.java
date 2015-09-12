package com.example.burcakdemircioglu.spotifystreamer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import kaaes.spotify.webapi.android.models.ArtistsPager;


public class MainActivity extends ActionBarActivity implements MainActivityFragment.Callback {
    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG="TLTAG";
    ArtistsPager results;
    @Override
    public void onItemSelected(Uri contentUri){

        if(mTwoPane){
            Bundle args=new Bundle();
            args.putParcelable(TracksActivityFragment.DETAIL_URI, contentUri);

            TracksActivityFragment fragment=new TracksActivityFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tracks_container,fragment, DETAILFRAGMENT_TAG)
                    .commit();
        }else{
           // String artistName = results.artists.items.get(position).name;
            //String artistID = results.artists.items.get(position).id;
            Intent intent=new Intent(this, TracksActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(findViewById(R.id.tracks_container)!=null){
            mTwoPane=true;
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tracks_container, new TracksActivityFragment(),DETAILFRAGMENT_TAG)
                        .commit();
            }
        }
        else{
            mTwoPane=false;
        }
        MainActivityFragment artistFragment=((MainActivityFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_artist));
        Log.v("mTwoPane", String.valueOf(mTwoPane));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
