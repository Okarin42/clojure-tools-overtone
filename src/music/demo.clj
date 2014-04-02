; The first noise - it's alive
(demo 10 (sin-osc))
;       ^- ugen (Unit generator) - process and controll signals

; let's make it louder and waver around
(demo 10 (* 0.1 (saw [100 90 110 70])))


;create different instaces of foo with frequenze of freq
(definst foo [freq 220] (saw freq))
;creates the SuperCollider Synthesizer definition 
;and loads it into the SuperCollider server. 
;It returns a function to start this synthesizer.

(foo)
(kill 42)
(foo 440)
(kill foo)


;play a sin-wave on the left and a saw wave on the right (it's stereo)
(defsynth mysynth [freq 440]
  (out 0 (sin-osc freq))
  (out 1 (saw freq)))

(mysynth)
(stop)


;modulates the amplitude of a saw-wave
(definst bar [amp 0.3 freq 440] (* amp (saw freq)))
(bar 1)
(ctl bar :amp 0.5) ;modify the amplitute of bar
(ctl bar :freq 220) ; modify the frequenzy of bar
(ctl bar :amp 2 :freq 1000) ; modify the frequenzy of bar
(kill bar)

;chain ugens together to create a tremolo effect
;:kr -> for control signals to save cpu-time
;:ar -> to run at sound-card rate
; FREE -> kills the synth after it is done
(definst trem ""
	[freq 420 depth 10 rate 6 length 3]
    (* 0.5
       (line:kr 0 1 length FREE)
       (saw (- freq (* depth (sin-osc rate))))
    )
)

(trem)
(trem 110 100 1 10)
(stop)

(definst beep [note 60]    ;midi->hz
  (let [sound-src (sin-osc (midicps note))
        env (env-gen (perc 0.01 1.0) :action FREE)]
    (* sound-src env)))
(beep)

(for [i (range 100)] (at (+ (now) (* i 50)) (beep i)))

