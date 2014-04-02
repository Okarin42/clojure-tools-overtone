; created by Jens Mehler
; this is rewritten from my own approach of creating a guitar-synth
; which was never commited because I found this

;(stop) kills the guitar
;guitar is predefined with 6 strings
(def tg (guitar))

;effects for the guitar
;        ~'dur       {:default 10.0  :min 1.0 :max 100.0}
;        ~'decay     {:default 30    :min 1   :max 100} ;; pluck decay
;        ~'coef      {:default 0.3   :min -1  :max 1}   ;; pluck coef
;        ~'noise-amp {:default 0.8   :min 0.0 :max 1.0}
;        ~'pre-amp   {:default 6.0   :min 0.0 :max 10.0}
;        ~'amp       {:default 1.0   :min 0.0 :max 10.0}
;        ;; by default, no distortion, no reverb, no low-pass
;        ~'distort   {:default 0.0   :min 0.0 :max 0.9999999999}
;        ~'rvb-mix   {:default 0.0   :min 0.0 :max 1.0}
;        ~'rvb-room  {:default 0.0   :min 0.0 :max 1.0}
;        ~'rvb-damp  {:default 0.0   :min 0.0 :max 1.0}
;        ~'lp-freq   {:default 20000 :min 100 :max 20000}
;        ~'lp-rq     {:default 1.0   :min 0.1 :max 10.0}
;        ~'pan       {:default 0.0   :min -1  :max 1}
;        ~'out-bus   {:default 0     :min 0   :max 100}]

(ctl tg 
  :dur 10 
  :decay 100 
  :coef 0.3 
  :noise-amp 1 
  :pre-amp 10 
  :distort 0.9 
  :rvb-mix 1 
  :rvb-room 1
  :rvb-damp 1
  :lp-freq 4000
  :lq-rq 10
)

(guitar-strum tg [-1 3 2 0 1 0] :down 0.01) ; C
(stop)

(def g (guitar))

; default effect settings for the guitar-synth :)
(ctl g :pre-amp 6.0 :amp 1.0 :distort 0)

; chord strumming
; with a vektor of frets for [E A D G B e]
; n number of fret : 0 -> open
; -1 -> don't play / mute
(guitar-strum g [-1 3 2 0 1 0] :down 0.1) ; C

;or as a chord - those are predefined
(guitar-strum g :C :up 0.1)

; chord strumming
;(guitar-strum the-guitar the-chord move timing)
; put in something else than 0.01 - take 1
(guitar-strum g :C :down 1)
(guitar-strum g :C :up 1)

; chord progressions
; let's play them in a row and see what happens

; a simple helper function - pretty uncool
(defn play [chord]
  (guitar-strum g chord :down 0.01))

(play :Eb)

; we need some chords - writing those everytime is crazy - take a map
(def powerchord
{
  :A [-1 0 0 -1 -1 -1]
  :C [-1 3 5 -1 -1 -1]
  :D [-1 5 7 -1 -1 -1]
  :Eb [-1 6 8 -1 -1 -1]
  :P [-1 -1 -1 -1 -1 -1] ;a pause 
  })

; let's define some chords to play in a row
(def melody [
  :P
  :A
  :C
  :D
  :P
  :A
  :C
  :Eb
  :D
  :P
  :A
  :C
  :D
  :P
  :C
  :A
  :P ])

; a riff everybody knows :)
;naive => very bad timing => sounds bad :(

; Take the first note and play it at the correct time 
; call yourself with rest of notes
(defn progression
  [time notes sep]
  (let [note (first notes)]
    (when note
      (at time (play (powerchord note)))) ;all as PowerChords from powerchord
    (let [next-time (+ time sep)]
      (apply-by next-time progression [next-time (rest notes) sep])))) ;try with apply-at (better timing)

(progression (now) melody 400)
;Well that sucks ... damn you chords ...

;why chords - let's try notes :)
;string picking
(guitar-pick g 0 0) ; E
(guitar-pick g 0 -1) ; mute it
(guitar-pick g 1 0) ; A
(guitar-pick g 2 0) ; D
(guitar-pick g 3 0) ; G
(guitar-pick g 4 0) ; B
(guitar-pick g 5 0) ; e

;endless fretboard => Fret 42 incoming
(guitar-pick g 0 42)

; a function to calculate the starting point of a note
(defn playat [time delta offset]
  (+ time (* offset delta))
)

(let[time (now) delta 250]
  (guitar-pick g 0 0 (playat time delta 0))
  (guitar-pick g 1 0 (playat time delta 1))
  (guitar-pick g 2 0 (playat time delta 2))
  (guitar-pick g 3 0 (playat time delta 3))
  (guitar-pick g 4 0 (playat time delta 4))
  (guitar-pick g 5 0 (playat time delta 5))
)

;equal strum would be
(guitar-strum g [0 0 0 0 0 0] :down 1.5)


;let's pick a part of a nice sounding song - something cool, something Slash :)
;something - easy to play 
;since we are dealing with Slash be aware it's usually half a step down
;(D# G# C# F# A# D#)
;but we play in standart tuning -> no worries here
;first approach (without muting and timing) -> crap
;second approach insert muting at right place (a guitar helps a lot) -> better
;final approac let's add the correct timing -> all hell broke lose
;it took a whole day to make this sound okay
;listen to intro - break it apart - listen to parts - setup - start all over

;!!! DON'T TOUCH THOSE TIMES !!!

(defn intro-a [time delta]
    (guitar-pick g 2 12 (playat time delta 0))
    (guitar-pick g 4 15 (playat time delta 1))
    (guitar-pick g 3 14 (playat time delta 2))
    (guitar-pick g 3 12 (playat time delta 3))

    (guitar-pick g 4 -1 (playat time delta 4))
    (guitar-pick g 2 -1 (playat time delta 4))
    (guitar-pick g 5 15 (playat time delta 4.1))
    
    (guitar-pick g 3 14 (playat time delta 5))
    (guitar-pick g 5 14 (playat time delta 6))
    (guitar-pick g 3 14 (playat time delta 7))  
    ;mute
    (guitar-pick g 5 -1 (playat time delta 8))
    (guitar-pick g 3 -1 (playat time delta 8))
)
(intro-a (now) 248)


(defn intro-b [time delta]
    (guitar-pick g 2 14 (playat time delta 0))
    (guitar-pick g 4 15 (playat time delta 1))
    (guitar-pick g 3 14 (playat time delta 2))
    (guitar-pick g 3 12 (playat time delta 3))

    (guitar-pick g 4 -1 (playat time delta 4))
    (guitar-pick g 2 -1 (playat time delta 4))
    (guitar-pick g 5 15 (playat time delta 4.1))
    
    (guitar-pick g 3 14 (playat time delta 5))
    (guitar-pick g 5 14 (playat time delta 6))
    (guitar-pick g 3 14 (playat time delta 7))  
    ;mute
    (guitar-pick g 5 -1 (playat time delta 8))
    (guitar-pick g 3 -1 (playat time delta 8))
)
(intro-b (now) 248)



(defn intro-c [time delta]
    (guitar-pick g 3 12 (playat time delta 0))
    (guitar-pick g 4 15 (playat time delta 1))
    (guitar-pick g 3 14 (playat time delta 2))
    (guitar-pick g 3 12 (playat time delta 3))

    (guitar-pick g 4 -1 (playat time delta 4))
    (guitar-pick g 2 -1 (playat time delta 4))
    (guitar-pick g 5 15 (playat time delta 4.1))
    
    (guitar-pick g 3 14 (playat time delta 5))
    (guitar-pick g 5 14 (playat time delta 6))
    (guitar-pick g 3 14 (playat time delta 7))  
    ;mute
    (guitar-pick g 5 -1 (playat time delta 8))
    (guitar-pick g 3 -1 (playat time delta 8))
)

(intro-c (now) 248)


(defn intro-d [time delta]
    (guitar-pick g 5 12 (playat time delta 0))
    (guitar-pick g 3 14 (playat time delta 1))
    (guitar-pick g 4 15 (playat time delta 2))
    (guitar-pick g 3 14 (playat time delta 3))
    (guitar-pick g 5 12 (playat time delta 4))
    (guitar-pick g 3 14 (playat time delta 5))
    (guitar-pick g 5 15 (playat time delta 6))
    (guitar-pick g 3 14 (playat time delta 7))
    (guitar-pick g 5 14 (playat time delta 8))
    (guitar-pick g 3 14 (playat time delta 9))
    (guitar-pick g 5 12 (playat time delta 10))
    (guitar-pick g 3 14 (playat time delta 11))
    (guitar-pick g 4 15 (playat time delta 12))

    (guitar-pick g 5 -1 (playat time delta 13))
    ;let it ring
)

(intro-a (now) 248) ;4x
(intro-b (now) 248) ;2x
(intro-c (now) 248) ;2x
(intro-d (now) 248) ;1x

;set the starting point of a note progression
(defn startat [sep offset delta]
  (let [t (now)]
    (+ t (* (* sep offset) delta))
  )
)

(defn intro-eg [delta]
      (intro-a (startat 0 0 delta) delta) ; start now 
      (intro-a (startat 8 1 delta) delta) ; start after ^ (1*8)
      (intro-a (startat 8 2 delta) delta) ; start after ^ (2*8)
      (intro-a (startat 8 3 delta) delta) ; .....
      (intro-b (startat 8 4 delta) delta)
      (intro-b (startat 8 5 delta) delta)
      (intro-c (startat 8 6 delta) delta)
      (intro-c (startat 8 7 delta) delta)
      (intro-d (startat 8 8 delta) delta) ; start after ^ (8*8)
)
; I found the correct delta by try and error - this has the same mistake as the metronome later one.
; this song is play at 124 BPM not 248 !

(recording-start "~/scom.wav")
(intro-eg 248)
(recording-stop)

; Listen to the original
; http://www.youtube.com/watch?v=bRfc_Y_AsLo

; back to chords
; we can do better for the chords - let's try something else
; with the approach from above
; after writing this I found the exact some thing in the clojure example ...
; damn .. let's leave it here

; We need a riff ... how about ... this one ?
;part 1
(defn begin []
  (let [t (now) dt 250]
    (guitar-strum g [-1 0 2 2 2 -1] :down 0.01 (+ t (* 0 dt)))
    (guitar-strum g [-1 0 2 2 2 -1] :up 0.01 (+ t (* 1 dt)))
    (guitar-strum g [-1 0 2 2 2 -1] :down 0.01 (+ t (* 2 dt) 50))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 3.5 dt)))
  )
)

;part 2
(defn middle []
  (let [t (now) dt 250]
    (guitar-strum g [ 2 -1 0 2 3 -1] :down 0.01 (+ t (* 0 dt)))
    (guitar-strum g [ 2 -1 0 2 3 -1] :up 0.01 (+ t (* 1 dt)))
    (guitar-strum g [ 3 -1 0 0 3 -1] :down 0.01 (+ t (* 2 dt) 50))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 3.5 dt)))
  )
)

;part 3
(defn end []
  (let [t (now) dt 250]
    (guitar-strum g [ 2 -1 0 2 3 -1] :down 0.01 (+ t (* 0 dt)))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 1.5 dt)))
    (guitar-strum g [-1 0 2 2 2 -1] :down 0.01 (+ t (* 2 dt)))
    (guitar-strum g [-1 0 2 2 2 -1] :up 0.01 (+ t (* 3 dt)))
    (guitar-strum g [-1 -1 -1 -1 -1 -1] :down 0.01 (+ t (* 4.5 dt)))
  )
)

; get some crunchy sound
(ctl g :pre-amp 5.0 :distort 0.96
     :lp-freq 5000 :lp-rq 0.25
     :rvb-mix 0.5 :rvb-room 0.7 :rvb-damp 0.4)

(begin)
(middle)
(end)

(intro-eg 248) ; just because it's cool

; what I did - I basically programmed my own metronome :D well ...
