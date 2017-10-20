// a music player that stops when the app is paused

G (
     // leaving the app disallows user interaction until resuming
     (leaveApp(sys) -> (((! playButton(sys)) && (! pauseButton(sys))) W resumeApp(sys)))

     // pause and play cannot be pressed together
   && (! (playButton(sys) && pauseButton(sys)))

     // an app cannot immediately resume after when left
   && (! (leaveApp(sys) && resumeApp(sys)))

     // musicPlaying(mpIn) changes according to playing or pausing the mpIn
   && ([[mpOut <- play(tr, trackPos(mpIn)) ]]  -> X (  musicPlaying(mpIn) W [[mpOut <- pause(mpIn)]]))
   && ([[mpOut <- pause(mpIn)]] -> X (! musicPlaying(mpIn) W [[ mpOut <- play(tr, trackPos(mpIn)) ]]))
)

->

G (
     // allow the user to play and pause mpIn
     (playButton(sys) -> [[ mpOut <- play(tr, trackPos(mpIn)) ]])
   && (pauseButton(sys) -> [[ mpOut <- pause(mpIn) ]])

     // mpIn can only be paused by the user or if we leave the app
   && ([[ mpOut <- pause(mpIn) ]] -> (leaveApp(sys) || pauseButton(sys)))

     // mpIn can only be played, if not paused
   && (leaveApp(sys) -> ((! [[ mpOut <- play(tr, trackPos(mpIn)) ]]) W resumeApp(sys)))

     // if the user paused the mpIn, only the user can play it again
   && (pauseButton(sys) -> (! [[ mpOut <- play(tr, trackPos(mpIn)) ]] W playButton(sys)))

     // if playing, stop mpIn on pause and resume playing afterwards
   && ( musicPlaying(mpIn) && leaveApp(sys) -> [[ mpOut <- pause(mpIn) ]])

     // if playing, stop mpIn on pause and resume playing afterwards
   && ( musicPlaying(mpIn) && leaveApp(sys) ->
         (! resumeApp(sys) W (resumeApp(sys) &&
              (pauseButton(sys) || [[ mpOut <- play(tr, trackPos(mpIn)) ]]))))
)
