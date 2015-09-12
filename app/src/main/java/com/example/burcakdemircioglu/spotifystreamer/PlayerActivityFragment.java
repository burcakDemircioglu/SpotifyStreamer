package com.example.burcakdemircioglu.spotifystreamer;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;


public class PlayerActivityFragment extends DialogFragment {
    String mArtistName;
    String mAlbumName;
    String mTrackName;
    String mImageUrl;
    String mPreviewUrl;
    String mTrackDuration;
    Boolean mTwoPane;
    MediaPlayer mMediaPlayer;
    WifiManager.WifiLock mWifiLock;

    public PlayerActivityFragment() {
    }

    public PlayerActivityFragment newInstance(Track track, ArrayList<Track> trackArrayList, boolean twoPane) {
        PlayerActivityFragment fragment = new PlayerActivityFragment();
        Bundle args = new Bundle();
        args.putSerializable("track", track);
        args.putSerializable("trackArrayList", trackArrayList);
        args.putBoolean("twoPane", twoPane);
        fragment.setArguments(args);
        return fragment;
    }

    public String formatTrackDuration(String duration) {
        int formatted = Integer.parseInt(duration);//msec
        formatted = formatted / 1000;//sec
        String formattedDuration;
        int remainder;
        int quotient;
        if (formatted > 60) {
            remainder = formatted % 60;
            formatted = formatted - remainder;
            quotient = formatted / 60;

            formattedDuration = quotient + ":";
            if (remainder > 9)
                formattedDuration += remainder;
            else
                formattedDuration += "0" + remainder;
        } else {
            if (formatted > 9)
                formattedDuration = "0:" + formatted;
            else
                formattedDuration = "0:0" + formatted;


        }
        return formattedDuration;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_player, container, false);
        Intent intent = getActivity().getIntent();
        final TextView artistNameView = ((TextView) rootView.findViewById(R.id.player_artistName_textView));
        final TextView albumNameView = ((TextView) rootView.findViewById(R.id.player_albumName_textView));
        final TextView trackNameView = ((TextView) rootView.findViewById(R.id.player_trackName_textView));
        final TextView durationView = ((TextView) rootView.findViewById(R.id.player_trackDuration_textView));
        final ImageView imageView = (ImageView) rootView.findViewById(R.id.player_albumArtwork);
        final SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.player_seekbar);
        final TextView currentTime = (TextView) rootView.findViewById(R.id.player_trackTime_textView);
        final ImageButton buttonNext = (ImageButton) rootView.findViewById(R.id.player_next_button);
        final ImageButton buttonPrevious = (ImageButton) rootView.findViewById(R.id.player_previous_button);
        final ImageButton buttonPause = (ImageButton) rootView.findViewById(R.id.player_pause_button);
        final ImageButton buttonPlay = (ImageButton) rootView.findViewById(R.id.player_play_button);
        final Track track;
        final ArrayList<Track> trackArrayList;

        if (intent.getExtras() != null) {
            track = (Track) intent.getExtras().getSerializable("track");
            mTwoPane = intent.getExtras().getBoolean("twoPane");
            trackArrayList = (ArrayList<Track>) intent.getExtras().getSerializable("trackArrayList");
        } else {
            track = (Track) getArguments().getSerializable("track");
            mTwoPane = getArguments().getBoolean("twoPane");
            trackArrayList = (ArrayList<Track>) getArguments().getSerializable("trackArrayList");
        }

        int positionTemp = 0;
        for (int i = 0; i < trackArrayList.size(); ++i) {
            if (trackArrayList.get(i).getTrackName().equals(track.getTrackName())) positionTemp = i;
        }
        final int position = positionTemp;
        mArtistName = track.getArtistName();
        mAlbumName = track.getAlbumName();
        mTrackName = track.getTrackName();
        mImageUrl = track.getImageUrl();
        mPreviewUrl = track.getPreviewUrl();
        mTrackDuration = track.getTrackDuration();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setWakeMode(getActivity().getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        try {
            mMediaPlayer.setDataSource(mPreviewUrl);

            mWifiLock = ((WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE))
                    .createWifiLock(WifiManager.WIFI_MODE_FULL, "mylock");
            mWifiLock.acquire();

            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
            mMediaPlayer.start();
        } catch (IOException e) {
            Log.e("mMediaPlayer error", e.toString());
        }

        final int fullTime = mMediaPlayer.getDuration();
        mTrackDuration = formatTrackDuration(String.valueOf(mMediaPlayer.getDuration()));

        artistNameView.setText(mArtistName);
        albumNameView.setText(mAlbumName);
        trackNameView.setText(mTrackName);
        durationView.setText(mTrackDuration);
        if (mImageUrl != null) {
            Picasso.with(getActivity()).load(mImageUrl).into(imageView);
        }

        seekBar.setMax(fullTime / 1000);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress * 1000);
                    currentTime.setText(formatTrackDuration(Integer.toString(mMediaPlayer.getCurrentPosition())));
                }
            }
        });
        if (position == 0) {
            buttonPrevious.setVisibility(View.INVISIBLE);
        } else if (position == 9) {
            buttonNext.setVisibility(View.INVISIBLE);
        }
        buttonPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMediaPlayer.isPlaying())
                    mMediaPlayer.pause();
                buttonPause.setVisibility(View.GONE);
                buttonPlay.setVisibility(View.VISIBLE);
            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMediaPlayer.start();
                buttonPlay.setVisibility(View.GONE);
                buttonPause.setVisibility(View.VISIBLE);
            }
        });

        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 9) {
                    mMediaPlayer.stop();
                    FragmentTransaction fragmentManager = getActivity().getFragmentManager().beginTransaction();

                    if (mTwoPane) {
                        PlayerActivityFragment newFragment = new PlayerActivityFragment().newInstance(trackArrayList.get(position + 1), trackArrayList, mTwoPane);
                        newFragment.show(fragmentManager, "dialog");
                        PlayerActivityFragment.this.dismiss();
                    } else {
                        Intent intent = new Intent(getActivity(), PlayerActivity.class)
                                .putExtra("track", trackArrayList.get(position + 1))
                                .putExtra("trackArrayList", trackArrayList)
                                .putExtra("twoPane", mTwoPane);
                        startActivity(intent);
                    }
                }
            }
        });

        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position != 0) {
                    mMediaPlayer.stop();
                    FragmentTransaction fragmentManager = getActivity().getFragmentManager().beginTransaction();

                    if (mTwoPane) {
                        PlayerActivityFragment newFragment = new PlayerActivityFragment().newInstance(trackArrayList.get(position - 1), trackArrayList, mTwoPane);
                        newFragment.show(fragmentManager, "dialog");
                        PlayerActivityFragment.this.dismiss();


                    } else {
                        Intent intent = new Intent(getActivity(), PlayerActivity.class)
                                .putExtra("track", trackArrayList.get(position - 1))
                                .putExtra("trackArrayList", trackArrayList)
                                .putExtra("twoPane", mTwoPane);
                        startActivity(intent);
                    }
                }
            }
        });

        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(final MediaPlayer mp) {

                mp.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(final MediaPlayer mp) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                int currentPosition = 0;
                                int fullTimeInSec = fullTime / 1000;
                                while ((mp != null) && (currentPosition < fullTimeInSec - 1)) {
                                    try {
                                        Thread.sleep(1000);
                                        currentPosition = mp.getCurrentPosition() / 1000;

                                    } catch (InterruptedException e) {
                                        return;
                                    } catch (Exception e) {
                                        return;
                                    }
                                    seekBar.setProgress(currentPosition);
                                    if (mMediaPlayer.isPlaying()) {
                                        currentTime.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                currentTime.setText(formatTrackDuration(Integer.toString(mp.getCurrentPosition())));
                                            }
                                        });
                                    }
                                }
                            }
                        }).start();
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int currentPosition = 0;
                        int fullTimeInSec = fullTime / 1000;
                        while ((mp != null) && (currentPosition < fullTimeInSec - 1)) {
                            try {
                                Thread.sleep(1000);
                                currentPosition = mp.getCurrentPosition() / 1000;

                            } catch (InterruptedException e) {
                                return;
                            } catch (Exception e) {
                                return;
                            }
                            seekBar.setProgress(currentPosition);
                            if (mMediaPlayer.isPlaying()) {
                                currentTime.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        currentTime.setText(formatTrackDuration(Integer.toString(mp.getCurrentPosition())));
                                    }
                                });
                            }
                        }
                    }
                }).start();

            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekBar.setProgress(0);
                mMediaPlayer.seekTo(0);
                currentTime.setText(formatTrackDuration(Integer.toString(0)));
                buttonPause.post(new Runnable() {
                    @Override
                    public void run() {
                        buttonPause.setVisibility(View.GONE);
                    }
                });
                buttonPlay.post(new Runnable() {
                    @Override
                    public void run() {
                        buttonPlay.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        //        String songName;
//// assign the song name to songName
//        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
//                new Intent(getApplicationContext(), MainActivity.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        Notification notification = new Notification();
//        notification.tickerText = text;
//        notification.icon = R.drawable.play0;
//        notification.flags |= Notification.FLAG_ONGOING_EVENT;
//        notification.setLatestEventInfo(getApplicationContext(), "MusicPlayerSample",
//                "Playing: " + songName, pi);
//        startForeground(NOTIFICATION_ID, notification);
//        stopForeground(true);
/*AudioFocus yapmadin*/


        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mWifiLock.release();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
