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
     (holds nil agent) ;This might fuck up because nil is a thing. Does it need to be !not
     (at floor agent)
     (on floor box)
     (at floor platform1)
     (at floor platform2)
     })

(ops-search world-state-1 '((holds box agent)) ops)

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
     :txt (pick-up object at ?location)
    }
    drop
    {:pre
          ((at floor agent)
            (holds ?obj agent)
            (:not (holds nil agent)))
     :del ((holds ?obj agent))
     :add ((holds nil agent)
            (on floor ?obj))
     :txt (drop ?obj on floor)
  }
    climb-on
    {:pre
          ((next-to ?location agent)
            (climable ?location))
     :del ((next-to ?location agent)
            (at floor agent))
     :add ((at ?location agent))
     :txt (climb-on agent on top of ?location)
   }
    climb-off
    {:pre
          (
            (on ?platform agent))
     :del ((on ?platform agent))
     :add ((on floor agent)
            (next-to ?platform ?agent))
     :txt (climb-off ?platform onto ?floor)
   }
    pick-off
    {:pre
          ((agent ?agent)
            (on ?platform ?agent)
            (on ?platform ?obj)
            (manipulable ?obj)
            (holds nil ?agent))
     :del ((holds nil ?agent)
            (on ?platform ?obj))
     :add ((holds ?obj ?agent)
            (:not (on ?platform ?obj)))
     :txt (pick-off ?obj from ?platform)
     }
    drop-on
    {:pre
          (
            (on ?platform ?agent)
            (isa platform ?platform)
            (:not (on ?platform ?obj))
            (hold ?obj ?agent))
     :del ((:not (on ?platform ?obj))
            (hold ?obj ?agent))
     :add ((holds nil ?agent)
            (on ?platform ?obj))}
    })

;(defn apply-op
;  [state {:keys [pre add del]}]
;  (mfind* [pre state]
;          (union (mout add)
;                 (difference state (mout del))
;                 )))
