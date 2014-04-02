; taken from the overtone tutorial - basics are basics ...
; generates a saw-wave of 440Hz which get faded in and faded out.
(definst saw-wave [freq 440 attack 0.01 sustain 0.4 release 0.1 vol 0.4] 
  (* (env-gen (lin attack sustain release) 1 1 0 1 FREE)
     (saw freq)
     vol))

(saw-wave) ; our own sin-wave
(stop)
(saw-wave :attack 1 :release 1) ; strong fade in and fade out
(saw-wave :sustain 4) ; keep alive for a longer time

;; We can play notes using frequency in Hz
(saw-wave 440) ; This is A4
(saw-wave 523.25) ; This is C5
(saw-wave 261.63) ; This is C4

;; We can also play notes using MIDI note values
(saw-wave (midi->hz 69)) ; This is A4
(saw-wave (midi->hz 72)) ; this is C5
(saw-wave (midi->hz 60)) ; This is C4

;; We can play notes using standard music notes as well
(saw-wave (midi->hz (note :A4)))
(saw-wave (midi->hz (note :C5)))
(saw-wave (midi->hz (note :C4)))

;; Let's make it even easier
(defn play [music-note]
    (saw-wave (midi->hz (note music-note))))

;; Great!
(play :A4)
(play :C5)
(play :C4)

;; Let's play some chords
(defn play-chord [chord]
  (doseq [note chord] (play note)))

;; We can play many types of chords.
(chord :A4 :major) ; this creates the sequences of midi-notes to be played

;feed it to play-chord
(play-chord (chord :A4 :major))

;; We can play a chord progression on the synth
;; using times:
(defn chord-progression-time []
  (let [time (now)]
    (at time (play-chord (chord :C4 :major)))
    (at (+ 2000 time) (play-chord (chord :G3 :major)))
    (at (+ 3000 time) (play-chord (chord :F3 :sus4)))
    (at (+ 4300 time) (play-chord (chord :F3 :major)))
    (at (+ 5000 time) (play-chord (chord :G3 :major)))))

(chord-progression-time)

