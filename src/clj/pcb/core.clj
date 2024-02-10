(ns pcb.core
  (:gen-class)
  (:require
   [pcb.component :as component]))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (println "Hello, World!")

  (let [c {:type :component
           :label "R1"
           :legs [{:relative-coords {:relative-x 1
                                     :relative-y 1}
                   :label "GND"
                   }]}]
    (when (component/instanceof? c :strict true :verbose true)
      (println "Valid component!")
      )
    )
  )
