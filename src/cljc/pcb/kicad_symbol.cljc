(ns pcb.kicad-symbol)


;; SPEC



;; PARSING

(defn parse-symbol-prop [p]
  (let [[_kind k v [_at x y z]] p]
    {:k k
     :v v
     :at [x y z]}
    ))

(defn parse-symbol [symbol]
  (let [[_kind label & rest] symbol
        props (filter #(= (first %) 'property) rest)]
    {:label label
     :props (map parse-symbol-prop props)}))

(defn parse [o]
  (let [[_kind [_ version] [_ generator] & symbols] o]
    {:version version
     :generator generator
     :symbols (map parse-symbol symbols)}))

(defn parse-at-filepath [fp]
  (->> (clojure.core/slurp fp)
       clojure.edn/read-string
       parse))
