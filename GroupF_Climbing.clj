(require '[cgsx.tools.matcher :refer :all])
(require '[clojure.set :refer :all])
(require '[cgsx.tools.opertator_search :refer :all])
;(require '[cgsx.tools.opertator_search.states :refer :all])
;(require '[operator-search.planner :refer :all])

(def world-state-1                                          ;World State = agent and box both on floor
  '#{(Agent agent)
     (isa obj box)
     (manipulable box)
     (isa location platform1)
     (isa location platform2)
     (isa location floor)
     (climbable platform1)
     (climbable platform2)
     (holds nil agent)
     (at floor agent)
     (at floor platform1)
     (at floor platform2)
     (on floor box)
     })

(def world-state-2                                          ;World State = agent on platform2 box on platform1
  '#{(Agent agent)
     (isa obj box)
     (manipulable box)
     (isa location platform1)
     (isa location platform2)
     (isa location floor)
     (climbable platform1)
     (climbed platform2)
     (holds nil agent)
     (on-top platform2 agent)
     (at platform2 agent)
     (at floor platform1)
     (at floor platform2)
     (on platform1 box)
     })

;(ops-search world-state-1 '((holds box agent)) ops)            Get agent to hold box...
;(ops-search world-state-1 '((next-to platform1 agent)) ops)    Agent next-to p1...
;(ops-search world-state-1 '((next-to platform2 agent)) ops)    Agent next-to p2...
;(ops-search world-state-1 '((on-top platform1 agent)) ops)     Agent on p1...
;(ops-search world-state-1 '((on-top platform2 agent)) ops)     Agent on p2...
;(ops-search world-state-1 '((on platform1 box)) ops)           Box on p1...
;(ops-search world-state-1 '((on platform2 box)) ops)           Box on p2...
;(ops-search world-state-1 '((on floor box)) ops)               Box on floor...
;(ops-search world-state-1 '((at floor agent)) ops)             Agent on floor...

;(ops-search world-state-2 '((holds box agent)) ops)            Get agent to hold box...
;(ops-search world-state-2 '((next-to platform1 agent)) ops)    Agent next-to p1...
;(ops-search world-state-2 '((next-to platform2 agent)) ops)    Agent next-to p2...
;(ops-search world-state-2 '((on-top platform1 agent)) ops)     Agent on p1...
;(ops-search world-state-2 '((on-top platform2 agent)) ops)     Agent on p2...
;(ops-search world-state-2 '((on platform1 box)) ops)           Box on p1...
;(ops-search world-state-2 '((on platform2 box)) ops)           Box on p2...
;(ops-search world-state-2 '((on floor box)) ops)               Box on floor...
;(ops-search world-state-2 '((at floor agent)) ops)             Agent on floor...

(def ops
  '{move
    {:pre
          ((at floor agent)
        ;    (next-to ?location1 agent)
            (isa location ?location2)
            (at floor ?location2))
     :del ((next-to ?location1 agent))
     :add ((next-to ?location2 agent))
     :txt (Agent moved across floor to ?location2)
     }

    climb-on
    {:pre
          ((next-to ?location agent)                        ;Need to figure out the syntax to get an agent to climb on
            (climbable ?location)
            (at floor agent))                               ;First needs to move then be next-to
     :del ((next-to ?location agent)
            (climbable ?location)
            (at floor agent))                               ;From there the climb on can be used so in the search does it need to be (on ?location agent)
     :add ((on-top ?location agent)
            (climbed ?location))
     :txt (Agent climbed ?location)
     }

    climb-off
    {:pre
          ((on-top ?location agent)
            (climbed ?location))
     :del ((on-top ?location agent)
            (climbed ?location)
            (at ?location agent))
     :add ((at floor agent)
            (next-to ?location agent)
            (climbable ?location))                          ;Platform
     :txt (Agent climbed-off ?location onto floor)
     }

    pickup
    {:pre
          ((holds nil agent)
            (at ?location1 agent)
            (on ?location1 ?obj)
            (manipulable ?obj))
     :del ((on ?location1 ?obj)
            (holds nil agent))
     :add ((holds ?obj agent))
     :txt (Agent picked-up ?obj from ?location1)
     }
    pick-off                                                ;Drops onto platform only
    {:pre
          (
            (holds nil agent)
            (on-top ?location agent)
            ;(climbed ?location)
            (on ?location ?obj)
            (manipulable ?obj))
     :del (
            (holds nil agent)
            (on ?location ?obj))                            ;(:not (on ?location ?obj)) wat do
     :add (
            (holds ?obj agent))
     :txt (Agent picked-off ?obj from ?location)
     }
    drop                                                    ;Drops onto floor only
    {:pre
          (
            (at floor agent)
            (holds ?obj agent)
            (:not (holds nil agent)))
     :del (
            (holds ?obj agent))
     :add (
            (holds nil agent)
            (on floor ?obj))
     :txt (Agent dropped ?obj onto floor)
     }

    drop-on                                                 ;This one needs looking at again
    {:pre
          (
            (on-top ?location agent)                        ;Set its location to be the current location
            (holds ?obj agent)
            (:not (holds nil agent)))
     :del (
            ;(:not (on ?location ?obj))
            (holds ?obj agent))
     :add (
            (holds nil agent)
            (on ?location ?obj))
     :txt (Agent dropped ?obj onto ?location)
     }})