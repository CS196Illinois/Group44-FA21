package com.example.musicplayer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
// tutorial: https://jerrybanfield.com/code-music-app-player-android-studio/ (video on site)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Note: minimum sdk is 16
    private static final String[]  PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static final int REQUEST_PERMISSIONS = 12345;

    private static final int PERMISSIONS_COUNT = 1;

    // permissions are automatically granted on some devices but not on others so need to check
    @SuppressLint("NewApi")
    private boolean arePermissionsDenied() {
        for (int i = 0; i < PERMISSIONS_COUNT; i++) {
            // if permission is not granted
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true; // indicate that permission is denied
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    @Override
    /**
     * get result of asking for permission (did user allow or deny)
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(arePermissionsDenied()) { // if user denied access to files
            // clear data and close app because file access permission is vital to app
            ((ActivityManager) (this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
            // recreate activity
            recreate();
        } else { // user grants permission
            onResume();
        }
    }

    // boolean to check if music player is initialized
    private boolean isMusicPlayerInit;

    //list of music files
    private List<String> musicFilesList;

    /**
     * add all mp3 files from a directory into musicFilesList
     * @param dirPath the directory to add mp3 files from
     */
    private void addMusicFilesFrom(String dirPath) {
        final File musicDir = new File(dirPath);
        if(!musicDir.exists()) { // if the music directory does not exist
            musicDir.mkdir(); // then it is empty. make the directory
            return;
        }
        // if directory does exist, list all mp3 files of that directory
        final File[] files = musicDir.listFiles();
        for (File file: files) {
            final String path = file.getAbsolutePath();
            if (path.endsWith(".mp3")) {
                musicFilesList.add(path);
            }

        }
    }

    /**
     * populate the list of music files
     */
    private void fillMusicList() {
        musicFilesList.clear();
        // add mp3 files from music directory
        addMusicFilesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MUSIC)));
        // add mp3 files from downloads directory
        addMusicFilesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
        )));
    }

    // declared here so it is accessible to any class
    private MediaPlayer mp;
    /**
     * Plays a song
     * @param path the file path for the song
     * @return the duration of the song as an int
     */
    private int playMusicFile(String path) {
        mp = new MediaPlayer(); // create new media player
        try { // try playing the music located at path
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
        } catch (Exception e) { // print the exception if there was a problem
            e.printStackTrace();
        }
        return mp.getDuration();
    }

    private int songPosition = 0;
    private boolean isSongPlaying;
    @Override
    protected void onResume() {
        super.onResume();

        // check if need to request permission
        // NOTE: M stands for the version Marshmallow
        // "if version is marshmallow or later and permission is not granted"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS); //request permissions
            return;
        }

        if(!isMusicPlayerInit) { // if music player is not initialized
            final ListView listView = findViewById(R.id.listView); // access listView from activity_main
            final TextAdapter textAdapter = new TextAdapter(); //create new text adapter for displaying items
            musicFilesList = new ArrayList<>(); //create list for music files
            //fill the list
            fillMusicList();
            textAdapter.setData(musicFilesList);
            listView.setAdapter(textAdapter);

            final View playbackControls = findViewById(R.id.playBackButtons); // show bottons
            // Pause button
            final Button pauseButton = findViewById(R.id.pauseButton);
            pauseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isSongPlaying) {
                        mp.pause();
                        pauseButton.setText("Play");
                    } else {
                        mp.start();
                        pauseButton.setText("Pause");
                    }
                    isSongPlaying = !isSongPlaying;
                }
            });

            // Loop button
            final Button loopButton = findViewById(R.id.loopButton);
            loopButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mp.isLooping()) {
                        mp.setLooping(false);
                        loopButton.setBackgroundColor(-65536);
                    } else {
                        mp.setLooping(true);
                        loopButton.setBackgroundColor(-16711936);
                    }
                }
            });

            // create a seekbar for when the music is playing
            final SeekBar seekBar = findViewById(R.id.seekBar);
            //listener for the seekbar; updates every time the progress moves
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int songProgress;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        songProgress = progress; // update position to position indicated by user
                        Log.i("Progress Changed", ""+songProgress); // log
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    songPosition = songProgress;
                    mp.seekTo(songPosition); // play song from updated position (allows skipping)
                    Log.i("seekBar seekTo", "seeked to "+songProgress+" secs"); // log
                }
            });
            final TextView songPositionTextView = findViewById(R.id.currentPosition);
            final TextView songDurationTextView = findViewById(R.id.songDuration);
            // play music file when list item is clicked
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //find position of file to get
                    final String musicFilePath = musicFilesList.get(position);
                    // play music & song's duration in seconds
                    final int songDuration = playMusicFile(musicFilePath) / 1000;
                    seekBar.setMax(songDuration);
                    //show the seek bar
                    seekBar.setVisibility(View.VISIBLE); // Set button as visible
                    playbackControls.setVisibility(View.VISIBLE);
                    // set text next to seekbar to show the duration of the song in mins and secs
                    String c = ":";
                    if (songDuration%60 < 10) {
                        c = ":0";
                    }
                    isSongPlaying = true; // for pause button
                    songDurationTextView.setText(String.valueOf(songDuration/60)+c+String.valueOf(songDuration%60));
                    // Threads allow the computer to do things in parallel
                    new Thread() {
                        public void run() {
                            songPosition = 0;
                            while (songPosition < songDuration) {
                                try {
                                    Thread.sleep(1000); // wait 1 second before updating again
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                // check that it is playing
                                if (isSongPlaying) {
                                    // update song position by 1 second
                                    songPosition++;
                                    // update seek bar to match song position
                                    // cannot change ui if on background thread, so use this to run on ui thread:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // move seekBar to updated position
                                            seekBar.setProgress(songPosition);
                                            // update the text next to seekbar to show current position in mins and secs
                                            String colon = ":";
                                            if (songPosition%60 < 10) {
                                                colon = ":0";
                                            }
                                            songPositionTextView.setText(String.valueOf(songPosition/60)+colon+String.valueOf(songPosition%60));
                                        }
                                    });
                                }
                                if (songDuration == songPosition && mp.isLooping()) { // restart song
                                    songPosition = 0;
                                }
                            }
                        }
                    }.start();
                }
            });
            isMusicPlayerInit = true; // initialize it
        }
    }
    /**
     * adapter for list view. shows text items in application
     */
    class TextAdapter extends BaseAdapter {
        private List<String> data = new ArrayList<>();

        void setData(List<String> mData) {
            data.clear();
            data.addAll(mData);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public String getItem(int position) {
            return null; // placeholder return null for now
        }
        @Override
        public long getItemId(int position) {
            return 0; // placeholder return 0 for now
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                //get view and inflate it
                convertView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.item, parent, false);
                convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.myItem)));
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final String item = data.get(position);
            // gets the name of the file, cut from the files full path
            holder.info.setText(item.substring(item.lastIndexOf('/') + 1));
            return convertView;
        }

        class ViewHolder {
            TextView info;
            ViewHolder(TextView mInfo) {
                info = mInfo;
            }
        }
    }
}