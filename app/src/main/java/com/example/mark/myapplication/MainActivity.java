package com.example.mark.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;


public class MainActivity extends AppCompatActivity {
    public static final int MY_PERMISSION_REQUEST_CODE = 123;
    public Activity mActivity;
    private Context mContext;
    private Button btn_play;
    private Button btn_pause;
    private SeekBar seekBar;

    //synthesis variables
    private SysData sys = new SysData();
    private MPData mpIn = new MPData();
    private String tr = "";
    private String oldTrack = "";
    //output variable - NB outputs must NOT be set elsewhere
    private boolean mpOut;
    //internal state tracker, dont set this anywhere else either
    private int state = 0;



    /* TODO pretty sure I can remove this
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case MY_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Permission granted
                }else {
                    // Permission denied
                }
            }
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_play = (Button) findViewById(R.id.buttonstart);
        btn_pause = (Button) findViewById(R.id.buttonpause);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        Button btn_show_spec = (Button) findViewById(R.id.showSpec);


        mpIn.mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                loadSong("song.mp3");
                mpIn.mp.start();
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sys.playbutton = true;
                musicSynthIO(mpIn, sys, tr);
                sys.playbutton = false;
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                sys.pausebutton = true;
                musicSynthIO(mpIn, sys, tr);
                sys.pausebutton = false;
            }
        });

        btn_show_spec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), specView.class));
            }
        });

        // Get the application context
        mContext = getApplicationContext();
        mActivity = MainActivity.this;

        final MusicQuery mQuery = new MusicQuery(this);
        // Custom method to check permission at run time
        mQuery.checkPermission();
        mQuery.getMediaFileList(mContext);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.songGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //NB because loading songs is not in the spec, and would violate the spec that...
                // music can only be paused by the user or if we leave the app
                // updPause -> leaveApp(sys) || pauseButton(sys);
                // we need to manually handle this case, TODO add loading songs to the spec
                if (mpIn.mp.isPlaying()) {
                    play(mQuery.uriList.get(checkedId), mpIn);
                } else {
                    loadSong(mQuery.uriList.get(checkedId));
                }


            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        sys.pausereq = true;
        musicSynthIO(mpIn, sys, tr);
        sys.pausereq = true;//todo are these events or signals
    }

    @Override
    public void onResume() {
        super.onResume();
        sys.pausereq = false;
        musicSynthIO(mpIn, sys, tr);
        sys.pausereq = false;
    }

    public void loadSong(String uriLink) {
        try {
            Log.d("t", uriLink);
            //AssetFileDescriptor afd = getAssets().openFd(uriLink);
            //TODO only reset when new song, otherwise just change tracker positon
            mpIn.mp.reset();
            mpIn.mp.setDataSource(uriLink);
            mpIn.mp.prepare();
            //mp.pause();
            oldTrack=uriLink;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //predicate implementations
    private boolean leaveApp(SysData sys){return sys.pausereq;}
    private boolean resumeApp(SysData sys){return !sys.pausereq;}
    private boolean playButton(SysData sys){return sys.playbutton;}
    private boolean pauseButton(SysData sys){return sys.pausebutton;}
    private boolean musicPlaying(MPData mpIn) {
        return mpIn.mp.isPlaying();
    }

    //function implementations
    private boolean play(String tr, MPData mpIn){
        if (!tr.equals(oldTrack)) {
            loadSong(tr);

        }
        Log.d("t","trying to play at time "+mpIn.trackPos);
        Log.d("t", String.valueOf((((double)mpIn.trackPos)/100.0)*mpIn.mp.getDuration()));
        mpIn.mp.seekTo((int) (((double)mpIn.trackPos/100.0)*mpIn.mp.getDuration()));
        mpIn.mp.start();
        return true;
    }
    private boolean pause(MPData mpIn){
        Log.d("t","trying to pause");
        mpIn.mp.pause();
        return false;
    }
    private MPData trackPos(MPData mpIn){
        //get the track pos from the slider
        mpIn.trackPos = seekBar.getProgress();
        return mpIn;
    }


    private void musicSynthIO(MPData mpIn , SysData sys , String tr) {
        musicSynth(mpIn, sys, oldTrack);
    }


//TAMM converted to Android program - NB need to fix types manually

    //input variables (moved to top of file) - NB inputs must be set inside call points to music4Synth

    //Synthesized 'FRP' program
    private void musicSynth(MPData mpIn , SysData sys , String tr){
        if ( state==0 && (((((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))){
            mpOut=(play(tr,(trackPos(mpIn))));
            state=0;
        }
        else if ( state==0 && (((((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))){
            mpOut=(pause(mpIn));
            state=0;
        }
        else if ( state==0 && ((((((((((((((((((((((((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))){
            mpOut=mpOut;
            state=0;
        }

        else if ( state==0 && ((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys))){
            mpOut=(pause(mpIn));
            state=1;
        }

        else if ( state==1 && ((((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))){
            mpOut=(play(tr,(trackPos(mpIn))));
            state=0;
        }
        else if ( state==1 && (((((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))){
            mpOut=(pause(mpIn));
            state=0;
        }
        else if ( state==1 && (((((((((((((((((((((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (leaveApp(sys)))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))){
            mpOut=mpOut;
            state=0;
        }

        else if ( state==1 && ((((((resumeApp(sys)) && (playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))) ||
                (((((resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (musicPlaying(mpIn))) && (! leaveApp(sys)))){
            mpOut=(pause(mpIn));
            state=1;
        }
        else if ( state==1 && (((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (! leaveApp(sys))) ||
                (((((! resumeApp(sys)) && (! playButton(sys))) && (! pauseButton(sys))) && (! musicPlaying(mpIn))) && (leaveApp(sys)))){
            mpOut=mpOut;
            state=1;
        }





        //TODO update slider (should be in spec)
        Log.d("myout",resumeApp(sys)+" "+playButton(sys)+" "+pauseButton(sys)+" "+musicPlaying(mpIn)+" "+leaveApp(sys)+" "+state);

    }

    private class SysData {
        boolean pausereq = false;
        boolean resumereq = false;
        boolean playbutton = false;
        boolean pausebutton = false;
    }

    private class MPData {
        int trackPos = 0;
        MediaPlayer mp = new MediaPlayer();
    }


}


