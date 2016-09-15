package com.nnsookwon.bpm_counter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements android.widget.MediaController.MediaPlayerControl {

    private EditText et_bpm;
    private MediaPlayer mediaPlayer;
    private android.widget.MediaController mediaController;
    private double bpm;
    private BeatsPerMinuteAnalyzer bpmAnalyzer;
    private Uri uriSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            10);
                }
            }
        }

        et_bpm = (EditText) findViewById(R.id.main_activity_et_bpm);
        bpmAnalyzer = new BeatsPerMinuteAnalyzer();
        bpmAnalyzer.setBPM(60);
        setETbpm(bpmAnalyzer.getBPM());
        mediaController = new android.widget.MediaController(MainActivity.this, false) {
            @Override
            public void hide() {
            }      // Prevent hiding of controls.
        };
        mediaController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekTo(getCurrentPosition() + 10000);
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekTo(getCurrentPosition() - 10000);
            }
        });

        mediaController.setMediaPlayer(this);
        mediaController.setEnabled(true);
        mediaController.setAnchorView(findViewById(R.id.screen));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.menu_choose_song:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 10);
                break;
        }
        return true;
    }

    public void increment(View v) {
        setETbpm(++bpm);
    }

    public void decrement(View v) {
        setETbpm(--bpm);
    }

    private void setETbpm(double nBPM) {
        bpm = nBPM;
        et_bpm.setText("" + nBPM);
    }

    public void tapToBeat(View v) {
        bpmAnalyzer.recordTime();
    }

    public void reset(View v) {
        bpmAnalyzer.reset();
    }

    public void analyze(View v) {
        bpmAnalyzer.analyze();
        setETbpm(((int) (bpmAnalyzer.getBPM() * 10)) / 10.0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == 10) {
            uriSong = data.getData();
            try {
                if (mediaPlayer != null)
                    mediaPlayer.release();
                mediaPlayer = MediaPlayer.create(MainActivity.this, uriSong);
                mediaController.show(0);
                mediaController.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null) {
            mediaController.setVisibility(View.INVISIBLE);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

  /*  @Override
    protected void onStart(){
        super.onStart();
        if (mediaPlayer!=null){
            try {
                mediaPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/




    @Override
    public void start() {
        mediaPlayer.start();
    }

    @Override
    public void pause() {
        mediaPlayer.pause();
    }

    @Override
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    @Override
    public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override
    public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

}
