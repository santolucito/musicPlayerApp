//TAMM converted to Android program - NB need to fix types manually

//input variables - NB inputs must be set inside call points to musicSynth
private boolean pausebutton = False;
private boolean pausereq = False;
private boolean playbutton = False;
//output variable - NB outputs must NOT be set elsewhere
private boolean music;
//internal state tracker, dont set this anywhere else either
private int state = 0;

//Synthesized 'FRP' program
private void musicSynth(boolean pausebutton , boolean pausereq , boolean playbutton){
    if ( state==0 && ((((isevent(playbutton)) || (! isevent(pausereq))) || (! isevent(pausebutton))) && (((isevent(playbutton)) || (isevent(pausereq))) || (! isevent(pausebutton)))) && (((isevent(playbutton)) || (! isevent(pausereq))) || (isevent(pausebutton)))){ 
        music=(play());
        state=0;
    }
    if ( state==0 && ((! isevent(playbutton)) && (isevent(pausereq))) && (! isevent(pausebutton))){ 
        music=(pause());
        state=0;
    }
    if ( state==0 && ((! isevent(playbutton)) && (isevent(pausereq))) && (isevent(pausebutton))){ 
        music=music;
        state=0;
    }

    if ( state==0 && ((! isevent(playbutton)) && (! isevent(pausereq))) && (isevent(pausebutton))){ 
        music=(pause());
        state=1;
    }

    if ( state==1 && ((isevent(playbutton)) && (! isevent(pausereq))) && (! isevent(pausebutton))){ 
        music=(play());
        state=0;
    }
    if ( state==1 && ((isevent(playbutton)) && (isevent(pausereq))) && (isevent(pausebutton))){ 
        music=(pause());
        state=0;
    }

    if ( state==1 && ((! isevent(playbutton)) && (isevent(pausereq))) && (isevent(pausebutton))){ 
        music=(play());
        state=1;
    }
    if ( state==1 && ((((! isevent(playbutton)) && (! isevent(pausereq))) && (isevent(pausebutton))) || (((! isevent(playbutton)) && (isevent(pausereq))) && (! isevent(pausebutton)))) || (((isevent(playbutton)) && (isevent(pausereq))) && (! isevent(pausebutton)))){ 
        music=(pause());
        state=1;
    }
    if ( state==1 && (((isevent(playbutton)) && (! isevent(pausereq))) && (isevent(pausebutton))) || (((! isevent(playbutton)) && (! isevent(pausereq))) && (! isevent(pausebutton)))){ 
        music=music;
        state=1;
    }


}
