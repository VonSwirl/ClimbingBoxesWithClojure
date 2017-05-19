(require '[cgsx.tools.matcher :refer :all])
(require '[clojure.set :refer :all])
(require '[cgsx.tools.opertator_search :refer :all])
;(require '[cgsx.tools.opertator_search.states :refer :all])
;(require '[operator-search.planner :refer :all])

(def world-state-1 ;Agent is on the floor
  '#{(Agent agent)
     ;(isa object platform)
     (isa obj box)
     (manipulable box)
     (isa location platform1)
     (isa location platform2)
     (isa location floor)
     ;(isa location box) ;This can be commented out
     (climbable platform1)
     (climbable platform2)
     ;(climbable box) ;Take this out too
     (holds nil agent) ;Change this to box for different searches
     (at floor agent) ;Changed this from (at floor agent)
     (at floor platform1)
     (at floor platform2)
     (on floor box) ;Originally on floor box
     })

(def world-state-2 ;Agent on top of platform
  '#{(Agent agent)
     (isa obj box)
     (manipulable box)
     (isa location platform1)
     (isa location platform2)
     (isa location floor)
     (climbed platform1)
     (climbable platform2)
     (holds nil agent) ;Change this to box for different searches
     (on-top platform1 agent) ;Changed this from (at floor agent)
     (at floor platform1)
     (at floor platform2)
     (on-top platform1 box) ;Originally on floor box (on ?location box)
     })

;(ops-search world-state-1 '((holds box agent)) ops)            Get agent to hold box
;(ops-search world-state-1 '((at platform1 agent)) ops)         Move an agent to the platform
;(ops-search world-state-1 '((next-to platform1 agent)) ops)    Also moves
;(ops-search world-state-1 '((on-top platform1 agent)) ops)     Climbs ontop of platform
;(ops-search world-state-2 '((at floor agent)) ops)             Takes agent from platform onto floor (in world (on-top platform1 agent))
;(ops-search world-state-2 '((holds box agent)) ops)            Tries to take box off platform with agent stood on it

(def ops
  '{move
    {:pre
          ( (at ?location1 agent)
            (isa location ?location2)
            (at floor ?location2))
     :del ( (at ?location1 agent))
     :add ( (next-to ?location2 agent))
     :txt   (move agent from ?location1 next to ?location2)
     }
    pickup
    {:pre
          ( (holds nil agent)
            (at ?location agent)
            (on ?location ?obj)
            (manipulable ?obj))
     :del ( (on ?location ?obj)
            (holds nil agent))
     :add ( (holds ?obj agent))
     :txt   (pick-up ?obj at ?location)
    }
    drop ;Drops onto floor only
    {:pre
          ( (at floor agent)
            (holds ?obj agent)
            (:not (holds nil agent)))
     :del ( (holds ?obj agent))
     :add ( (holds nil agent)
            (on floor ?obj))
     :txt   (drop ?obj on ?location)
  }
    climb-on
    {:pre
          ( (next-to ?location agent)  ;Need to figure out the syntax to get an agent to climb on
            (climbable ?location))     ;First needs to move then be next-to
     :del ( (next-to ?location agent)) ;From there the climb on can be used so in the search does it need to be (on ?location agent)
     :add ( (on-top ?location agent)
            (climbed ?location))
     :txt   (climb-on agent on top of ?location)
   }
    climb-off
    {:pre
          ( (on-top ?location1 agent)
            (isa location ?location2)
            (climbed ?location))
     :del ( (on-top ?location1 agent)
            (climbed ?location))
     :add ( (at ?location2 agent)
            (next-to ?location1 agent)
            (climbable ?location)) ;Platform
     :txt (climb-off ?location1 to ?location2)
   }
    pick-off ;Drops onto platform only
    {:pre
          ( (holds nil agent)
            (on-top ?location agent)
            (climbed ?location)
            (on-top ?location ?obj)
            (manipulable ?obj)
            )
     :del ( (holds nil agent)
            (on-top ?location ?obj)) ;(:not (on ?location ?obj)) wat do
     :add ( (holds ?obj ?agent))
     :txt   (pick-off ?obj from ?location)
     }
    drop-on ;This one needs looking at again
    {:pre
          (
            (on ?location agent) ;Set its location to be the current location
            (isa platform ?platform)
            (:not (on ?platform ?obj))
            (hold ?obj ?agent))
     :del ((:not (on ?platform ?obj))
            (hold ?obj ?agent))
     :add ((holds nil ?agent)
            (on ?platform ?obj))}
    })


