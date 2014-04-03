(def kick (sample (freesound-path 2086)))
(def one-twenty-bpm (metronome 120))

; this function will play our sound at whatever tempo we've set our metronome to 
(defn looper [nome sound]    
    (let [beat (nome)]
        (at (nome beat) (sound))
        (apply-at (nome (inc beat)) looper nome sound [])))

; turn on the metronome
(looper one-twenty-bpm kick) ; hmm every second ? ...
(one-twenty-bpm :bpm 60)
;something is off here - it ticks every two seconds ...
(stop)

; that above was made known to the overtone community - currently awaiting an answer
; back to the fun :)

; default effect settings for the guitar-synth :)
(ctl g :pre-amp 6.0 :amp 1.0 :distort 0)

;endless ticking metronome
(def metro (metronome 120))
(metro) ; return the current tick
(metro 600) ; returns the time the beat comes up - well that is handy :)

; previously we used some functions to calculate the starting-point
; let's get rid of that overhead and use a metronome
; to play a note at the right beat number

;uses guitar from guitar.clj
(defn intro-a [m cur-beat] ; hast 8 beats total
	(guitar-pick g 2 12 (m (+ 0 cur-beat)))
    (guitar-pick g 4 15 (m (+ 1 cur-beat)))
    (guitar-pick g 3 14 (m (+ 2 cur-beat)))
    (guitar-pick g 3 12 (m (+ 3 cur-beat)))

    (guitar-pick g 4 -1 (m (+ 4 cur-beat)))
    (guitar-pick g 2 -1 (m (+ 4 cur-beat)))
    (guitar-pick g 5 15 (m (+ 4.1 cur-beat)))
    
    (guitar-pick g 3 14 (m (+ 5 cur-beat)))
    (guitar-pick g 5 14 (m (+ 6 cur-beat)))
    (guitar-pick g 3 14 (m (+ 7 cur-beat)))  
    ;mute
    (guitar-pick g 5 -1 (m (+ 8 cur-beat)))
    (guitar-pick g 3 -1 (m (+ 8 cur-beat)))
)

(defn intro-b [m cur-beat] ; hast 8 beats total
    (guitar-pick g 2 14 (m (+ 0 cur-beat)))
    (guitar-pick g 4 15 (m (+ 1 cur-beat)))
    (guitar-pick g 3 14 (m (+ 2 cur-beat)))
    (guitar-pick g 3 12 (m (+ 3 cur-beat)))

    (guitar-pick g 4 -1 (m (+ 4 cur-beat)))
    (guitar-pick g 2 -1 (m (+ 4 cur-beat)))
    (guitar-pick g 5 15 (m (+ 4.1 cur-beat)))
    
    (guitar-pick g 3 14 (m (+ 5 cur-beat)))
    (guitar-pick g 5 14 (m (+ 6 cur-beat)))
    (guitar-pick g 3 14 (m (+ 7 cur-beat)))  
    ;mute
    (guitar-pick g 5 -1 (m (+ 8 cur-beat)))
    (guitar-pick g 3 -1 (m (+ 8 cur-beat)))
)

(intro-b metro (metro))

(defn intro-c [m cur-beat] ; hast 8 beats total
    (guitar-pick g 3 12 (m (+ 0 cur-beat)))
    (guitar-pick g 4 15 (m (+ 1 cur-beat)))
    (guitar-pick g 3 14 (m (+ 2 cur-beat)))
    (guitar-pick g 3 12 (m (+ 3 cur-beat)))

    (guitar-pick g 4 -1 (m (+ 4 cur-beat)))
    (guitar-pick g 2 -1 (m (+ 4 cur-beat)))
    (guitar-pick g 5 15 (m (+ 4.1 cur-beat)))
    
    (guitar-pick g 3 14 (m (+ 5 cur-beat)))
    (guitar-pick g 5 14 (m (+ 6 cur-beat)))
    (guitar-pick g 3 14 (m (+ 7 cur-beat))) 
    ;mute
    (guitar-pick g 5 -1 (m (+ 8 cur-beat)))
    (guitar-pick g 3 -1 (m (+ 8 cur-beat)))
)

(intro-c metro (metro))

(defn intro-d [m cur-beat] ; hast 12 beats total
    (guitar-pick g 5 12 (m (+ 0 cur-beat)))
    (guitar-pick g 3 14 (m (+ 1 cur-beat)))
    (guitar-pick g 4 15 (m (+ 2 cur-beat)))
    (guitar-pick g 3 14 (m (+ 3 cur-beat)))
    (guitar-pick g 5 12 (m (+ 4 cur-beat)))
    (guitar-pick g 3 14 (m (+ 5 cur-beat)))
    (guitar-pick g 5 15 (m (+ 6 cur-beat)))
    (guitar-pick g 3 14 (m (+ 7 cur-beat)))
    (guitar-pick g 5 14 (m (+ 8 cur-beat)))
    (guitar-pick g 3 14 (m (+ 9 cur-beat)))
    (guitar-pick g 5 12 (m (+ 10 cur-beat)))
    (guitar-pick g 3 14 (m (+ 11 cur-beat)))
    (guitar-pick g 4 15 (m (+ 12 cur-beat)))

    (guitar-pick g 5 -1 (m (+ 13 cur-beat)))
    ;let it ring
)
(intro-d metro (metro))

(defn intro-g [m cur-beat]
      (intro-a m cur-beat) ; start now
      (intro-a m (+ cur-beat 8)) ; start after ^ = 8 beats 
      (intro-a m (+ cur-beat (* 2 8))) ; start after ^ = (* 2 8) beats 
      (intro-a m (+ cur-beat (* 3 8))) ; ....
      (intro-b m (+ cur-beat (* 4 8)))
      (intro-b m (+ cur-beat (* 5 8)))
      (intro-c m (+ cur-beat (* 6 8)))
      (intro-c m (+ cur-beat (* 7 8)))
      (intro-d m (+ cur-beat (* 8 8)))
)
(metro :bpm 248) ; modify the metronome to be faster and compensates for the metronome error :)
; maybe it's coincidence that this is the same as the number for my own metronome
(intro-g metro (metro))
(stop)


