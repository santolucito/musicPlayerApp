// a music player that stops when the app is paused

G (
     // leaving the app disallows user interaction until resuming
     (event(leaveApp) -> (((! event(playButton)) && (! event(pauseButton))) W event(resumeApp)))

     // pause and play cannot be pressed togehter
   && (! (event(playButton) && event(pauseButton)))

     // an app cannot immediately resume after when left
   && (! (event(leaveApp) && event(resumeApp)))

     // musicPlaying() changes accorinding to playing or pausing the music
   && ([[music <- play()]]  -> X (  musicPlaying() W [[music <- pause()]]))
   && ([[music <- pause()]] -> X (! musicPlaying() W [[music <- play()]]))
)

->

G (
     // allow the user to play and pause music
     (event(playButton) -> [[ music <- play() ]])
   && (event(pauseButton) -> [[ music <- pause() ]])

     // music can only be paused by the user or if we leave the app
   && ([[ music <- pause() ]] -> (event(leaveApp) || event(pauseButton)))

     // music can only be played, if not paused
   && (event(leaveApp) -> ((! [[ music <- play() ]]) W event(resumeApp)))

     // if the user paused the music, only the user can play it again
   && (event(pauseButton) -> (! [[ music <- play() ]] W event(playButton)))

     // if playing, stop music on pause and resume playing afterwards
   && ( musicPlaying() && event(leaveApp) -> [[ music <- pause() ]])

     // if playing, stop music on pause and resume playing afterwards
   && ( musicPlaying() && event(leaveApp) -> 
         (! event(resumeApp) W (event(resumeApp) &&
              (event(pauseButton) || [[ music <- play() ]]))))
)
