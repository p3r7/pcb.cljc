(ns pcb.kicad-symbol)

(declare parse)


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
     :props (map parse props)}))

(defn parse-symbol-lib [o]
  (let [[_kind & symbols] o]
    {:version (parse (some #(when (= (first %) 'version) %) symbols))
     :generator (parse (some #(when (= (first %) 'generator) %) symbols))
     :symbols (map parse symbols)}))

(defn parse [o]
  (case (first o)
    kicad_symbol_lib (parse-symbol-lib o)
    symbol (parse-symbol o)
    property (parse-symbol-prop o)
    (version generator) (second o)
    nil)
  )

(defn parse-at-filepath [fp]
  (->> (clojure.core/slurp fp)
       clojure.edn/read-string
       parse))
