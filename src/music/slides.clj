; I wanted to do drums but then I got a better idea
; let's build a slide-guitar
; well just a function that enables any guitar to do a slide on a string
; that is currently not implemented in the stringed-synth

; ====== taken from overtone.synth.stringed 
; we need those here because they are non-public and we keep it this way

(defn- fret-to-note
  "given a fret-offset, add to the base note index with special
  handling for -1"
  [base-note offset]
  (if (>= offset 0)
    (+ base-note offset)
    offset))

(defn- mkarg
  "useful for making arguments for the instruments strings"
  [s i]
  (keyword (format "%s-%d" s i))); creates a string (think about printf)

; ======

(def sg (guitar))

(ctl sg :pre-amp 5.0 :distort 0.96
     :lp-freq 5000 :lp-rq 0.25
     :rvb-mix 0.5 :rvb-room 0.7 :rvb-damp 0.4)

; part of this is in pick-string - we can build on that 
(defn set-fret [the-inst string-index fret]
  "Sets fret for the-inst on string-index"
  (let [the-note (fret-to-note (nth guitar-string-notes string-index) fret)]
    (if (= the-note -1) ;mute it
      (ctl the-inst (mkarg "gate" string-index) 0)
    )
    (if (>= the-note 0) ; set other note on string-index
      (ctl the-inst (mkarg "note" string-index) the-note) 
    )
  )
)

;tests
(guitar-pick sg 0 1)
(set-fret sg 0 2) ; one slide up
(set-fret sg 0 3) ; another slide up

(set-fret sg 0 -1) ; mute

;slide it baby
(let [time (now)]
  (at time (guitar-pick sg 0 1))
  (at (+ 200 time) (set-fret sg 0 2))
  (at (+ 250 time) (set-fret sg 0 3))
  (at (+ 300 time) (set-fret sg 0 4))
  (at (+ 400 time) (set-fret sg 0 5))
)

;a functions for sliding a string from fret 1 to 5
(defn slide-string-1-5
  "slides the-string of the-inst from fret 1 to fret 5.
  if keep is set the last note will be fret 5
  otherwise the string gets muted"
  [the-inst the-string keep-note]
  (let [time (now)]
    (at time (guitar-pick the-inst the-string 1))
    (at (+ 100 time) (set-fret the-inst the-string 2))
    (at (+ 200 time) (set-fret the-inst the-string 3))
    (at (+ 300 time) (set-fret the-inst the-string 4))
    (at (+ 400 time) (set-fret the-inst the-string 5))
    (if (zero? keep-note)
      (at (+ 500 time) (set-fret the-inst the-string -1))
    )
  )
)
(doc slide-string-1-5)
(slide-string-1-5 sg 0 1)
(guitar-pick sg 0 2)

;let's make it varible start and end frets
;maybe we can use that for some kind of timing
(let [time (now) the-inst sg the-string 2 start-fret 3 end-fret 9]
  (at time (guitar-pick the-inst the-string start-fret))
  (doseq [fret (range start-fret (inc end-fret)) ]
    (at (+ (* fret 30) time) (set-fret the-inst the-string fret))
  )
)
;turns out we can use it for some timing - let's put in a function

(defn slide-string-now
  "slides the-string of the-inst from fret start-fret to fret end-fret.
  Every note inbetween sounds for duration time.
  if keep is set the last note will be fret end-fret
  otherwise the string gets muted"
  [the-inst the-string start-fret end-fret duration keep-note]
  (let [time (now)]
    (at time (guitar-pick the-inst the-string start-fret))
    (doseq [fret (range start-fret (inc end-fret)) ]
      (if (and (= end-fret fret) (zero? keep-note) ) 
        (at (+ (* fret duration) time) (set-fret the-inst the-string -1))
        (at (+ (* fret duration) time) (set-fret the-inst the-string fret))
      )
    )
  )
)

(doc slide-string-now)
(slide-string-now sg 2 3 9 50 0)
;the timing is not absolutly correct but we can build up on this :)
;first let's be able to set a starting time

(defn slide-string
  "slides the-string of the-inst from fret start-fret to fret end-fret.
  Every note inbetween sounds for duration time.
  if keep is set the last note will be fret end-fret
  otherwise the string gets muted"
  [the-inst the-string start-fret end-fret start duration keep-note]
  (at start (guitar-pick the-inst the-string start-fret))
  (doseq [fret (range start-fret (inc end-fret)) ]
    (if (and (= end-fret fret) (zero? keep-note) ) 
      (at (+ (* fret duration) start) (set-fret the-inst the-string -1))
      (at (+ (* fret duration) start) (set-fret the-inst the-string fret))
    )
  )
)
;logic problem - if we don't keep the last note alive it doesn't get played
(doc slide-string)

(slide-string sg 2 3 9 (now) 50 0)

(slide-string sg 2 3 9 (now) 50 1)
(guitar-pick sg 2 8)

;all this can do is move up the neck but we want to move down to :)
;the correct timing will come later - let's look at down-slides now

;some tests
(let [start-fret 2 end-fret 13]
  (if (< end-fret start-fret)
    (println "backwars")
    (println "forwards")
  )
)

;another funny test
(let [start-fret 2 end-fret 13 i (atom 1)]
  (doseq 
    [fret (if (< end-fret start-fret) 
            (reverse (range end-fret (inc start-fret)))
            (range start-fret (inc end-fret))
          )
    ]
      (println "pick " fret "at " @i )
      (swap! i inc)

  )
)

(defn slide-string
  "slides the-string of the-inst from fret start-fret to fret end-fret.
  Every note inbetween sounds for duration time.
  if keep is set the last note will be fret end-fret
  otherwise the string gets muted"
  [the-inst the-string start-fret end-fret start duration keep-note]
  (at start (guitar-pick the-inst the-string start-fret))
  (let [i (atom 1)] ; used to calculate the offset between the sub-slides
    (doseq 
      [fret (if (< end-fret start-fret) 
              (reverse (range end-fret (dec start-fret)))
              (range start-fret (inc end-fret))
            )
      ]
      (at (+ (* @i duration) start) (set-fret the-inst the-string fret))
      (if (and (= end-fret fret) (zero? keep-note) ) 
          (at (+ (* (inc @i)  duration) start) (set-fret the-inst the-string -1))
      )
      (swap! i inc)
    )
  )
)
(doc slide-string)

(ctl sg :pre-amp 5.0 :distort 0.96
     :lp-freq 5000 :lp-rq 0.25
     :rvb-mix 0.5 :rvb-room 0.7 :rvb-damp 0.4)

(ctl sg :pre-amp 5.0 :amp 6 :distort 0.99
     :lp-freq 5000 :lp-rq 0.25
     :rvb-mix 0.5 :rvb-room 0.7 :rvb-damp 0.4)

(slide-string sg 2 17 7 (now) 80 0)
(slide-string sg 2 9 3 (now) 50 1)
(guitar-pick sg 2 5)
(stop)

(defn bend-string
  "Bend a string of an inst either full (1) or half(0)"
  [the-inst the-string the-fret bend time]
  (if (zero? bend)
    (let [start the-fret end (+ the-fret 1)]
      (slide-string the-inst the-string start end (now) time 0)
    )
    (let [start the-fret end (+ the-fret 2)]
      (slide-string the-inst the-string start end (now) time 0)
    )
  )
)
(bend-string sg 4 12 0 50)




