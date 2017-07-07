package com.example.mark.myapplication;
n
import android.content.res.AssetFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mp;
    private Button btn_play;
    private Button btn_pause;

    //synthesis variables
    private boolean pausereq = false;
    private boolean playbutton = false;
    private boolean pausebutton = false;

    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_play = (Button) findViewById(R.id.buttonstart);
        btn_pause = (Button) findViewById(R.id.buttonpause);

        mp = new MediaPlayer();
        mp.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                loadSong();
                mp.start();
            }
        });
        loadSong();
        Log.d("t",pausebutton+" "+pausereq+" "+playbutton);

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                playbutton = true;
                musicSynth(pausebutton, pausereq, playbutton);
                playbutton = false;
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // check for already playing
                pausebutton = true;
                musicSynth(pausebutton, pausereq, playbutton);
                pausebutton = false;
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        pausereq = true;
        musicSynth(pausebutton, pausereq, playbutton);
        pausereq = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        pausereq = false;
        musicSynth(pausebutton, pausereq, playbutton);
        pausereq = false;
    }

    //output variable - NB outputs must NOT be set elsewhere
    private boolean music;
    //internal state tracker, dont set this anywhere else either
    private int state = 0;

    //Synthesized 'FRP' program
    private void musicSynth(boolean pausebutton , boolean pausereq , boolean playbutton){
        if ( state==0 && ((((! isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton)))){
            music=(play());
            state=0;
        }
        if ( state==0 && (((((((! isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((! isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton)))) ||
                ((((isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton)))) ||
                ((((isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton)))) ||
                ((((isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))){
            music=(pause());
            state=0;
        }
        if ( state==0 && (((((((! isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((! isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))) ||
                ((((! isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))) ||
                ((((isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton)))) ||
                ((((! isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton)))){
            music=music;
            state=0;
        }

        if ( state==0 && ((((! isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton))) ||
                ((((isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton)))){
            music=(play());
            state=1;
        }
        if ( state==0 && ((((isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))){
            music=(pause());
            state=1;
        }

        if ( state==1 && ((((! isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton))) ||
                ((((isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton)))){
            music=(play());
            state=0;
        }
        if ( state==1 && ((((! isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton)))){
            music=(pause());
            state=0;
        }
        if ( state==1 && ((((((! isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton)))) ||
                ((((! isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))) ||
                ((((isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton)))){
            music=music;
            state=0;
        }

        if ( state==1 && ((((((! isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((isplaying()) && (isevent(playbutton))) && (! isevent(pausereq))) && (isevent(pausebutton)))) ||
                ((((isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))) ||
                ((((! isplaying()) && (! isevent(playbutton))) && (! isevent(pausereq))) && (! isevent(pausebutton)))){
            music=(play());
            state=1;
        }
        if ( state==1 && (((((isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton))) ||
                ((((! isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))) ||
                ((((isplaying()) && (! isevent(playbutton))) && (isevent(pausereq))) && (! isevent(pausebutton)))){
            music=(pause());
            state=1;
        }
        if ( state==1 && (((! isplaying()) && (isevent(playbutton))) && (isevent(pausereq))) && (isevent(pausebutton))){
            music=music;
            state=1;
        }



        Log.d("myout",pausebutton+" "+pausereq+" "+playbutton+" "+state);

    }

    private boolean isplaying() {
        return mp.isPlaying();
    }

    private boolean play(){
        mp.start();
        return true;
    }
    private boolean pause(){
        mp.pause();
        return false;
    }
    private boolean isevent(boolean x){
        return x;
    }


    public void loadSong() {
        try {
            AssetFileDescriptor afd = getAssets().openFd("song.mp3");

            mp.reset();
            mp.setDataSource(afd);
            mp.prepare();
            //mp.pause();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


