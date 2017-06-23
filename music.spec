// a music player that stops when the app is paused

G (
     // when the app is paused, no user interaction
      (isEvent(pauseReq) -> ! isEvent(playButton))
   && (isEvent(pauseReq) -> ! isEvent(pauseButton))

     // pause and play cannot be pressed togehter
   && (! (isEvent(playButton) && isEvent(pauseButton)))

//   && [[music <- play()]] ->  X (  isPlaying() W [[music <- pause]])
//   && [[music <- pause()]] -> X (! isPlaying() W [[music <- play]])
)

->

G (
     // allow the user to play and pause music
     (isEvent(playButton) -> [[ music <- play() ]])
   && (isEvent(pauseButton) -> [[ music <- pause() ]])

     // music can only paused by the user or a pauseReq
   && ([[ music <- pause() ]] -> (isEvent(pauseReq) || isEvent(pauseButton)))

     // music can only be played, if not paused
   && ([[ music <- play() ]] -> ! isEvent(pauseReq))

     // if the user paused the music, only the user can play it again
   && (([[ music <- pause() ]] && isEvent(pauseButton)) -> (! [[ music <- play() ]] W isEvent(playButton)))

     // if playing, stop music on pause and resume playing afterwards
   && ( ( [[ music <- play() ]] && (! [[ music <- pause() ]] U isEvent(pauseReq)) )
//   && ( isPlaying() && isEvent(pauseReq)

       ->

       ( ! isEvent(pauseReq) W (isEvent(pauseReq)
           && [[ music <- pause() ]]
           && (isEvent(pauseReq) W (! isEvent(pauseReq)
             && (! isEvent(pauseButton) -> [[ music <- play() ]]))))
       )
     )
)
