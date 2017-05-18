(require '[cgsx.tools.matcher :refer :all])
(require '[clojure.set :refer :all])
(require '[cgsx.tools.opertator_search :refer :all])
;(require '[cgsx.tools.opertator_search.states :refer :all])
;(require '[operator-search.planner :refer :all])

(def world-state-1
  '#{(Agent agent)
     (isa object platform)
     (isa object box)
     (manipulable box)
     (isa location platform1)
     (isa location platform2)
     (isa location floor)
     (isa location box)
     (climbable platform)
     (climbable box)
     (holds nil agent) ;Change this to box for different searches
     (at floor agent)
     (on floor box)
     (at floor platform1)
     (at floor platform2)
     })

;(ops-search world-state-1 '((holds box agent)) ops)   Testings stuff

(def ops
  '{move
    {:pre
          ((at ?location1 agent)
            (isa location ?location2)
            (at floor ?location2))
     :del ((at ?location1 agent))
     :add ((at ?location2 agent)
            (next-to ?location2 agent))
     :txt (move agent from ?location1 next to ?location2)
     }
    pickup
    {:pre
          ( (holds nil agent)
            (at ?location agent)
            (on ?location ?obj)
            (manipulable ?obj)
            )
     :del ((on ?location ?obj)
            (holds nil agent))
     :add ((holds ?obj agent))
     :txt (pick-up ?obj at )
    }
    drop ;Drops onto floor only
    {:pre
          ((at floor agent)
            (holds ?obj agent)
            (:not (holds nil agent)))
     :del ((holds ?obj agent))
     :add ((holds nil agent)
            (on floor ?obj))
     :txt (drop ?obj on ?location)
  }
    climb-on
    {:pre
          ((next-to ?location agent) ;Need to figure out the syntax to get an agent to climb on
            (climable ?location))    ;First needs to move then be next-to
     :del ((next-to ?location agent) ;From there the climb on can be used so in the search does it need to be (on ?location agent)
            (at floor agent))
     :add ((on ?location agent))
     :txt (climb-on agent on top of ?location)
   }
    climb-off
    {:pre
          (
            (on ?location agent))
     :del ((on ?location agent))
     :add ((on floor agent)
            (next-to ?location agent)) ;Platform
     :txt (climb-off ?location onto ?floor)
   }
    pick-off ;Drops onto platform only
    {:pre
          ((on ?platform agent)
            (on ?platform ?obj)
            (manipulable ?obj)
            (holds nil agent))
     :del ((holds nil agent)
            (on ?platform ?obj))
     :add ((holds ?obj ?agent)
            (:not (on ?platform ?obj))) ;Should be platform or location?
     :txt (pick-off ?obj from ?platform)
     }
    drop-on ;This one needs looking at again
    {:pre
          (
            (on ?location agent)
            (isa platform ?platform)
            (:not (on ?platform ?obj))
            (hold ?obj ?agent))
     :del ((:not (on ?platform ?obj))
            (hold ?obj ?agent))
     :add ((holds nil ?agent)
            (on ?platform ?obj))}
    })


