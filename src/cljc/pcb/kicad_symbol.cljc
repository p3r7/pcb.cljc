(ns pcb.kicad-symbol
  (:require [clojure.string :as string]))

(declare parse)


;; SPEC



;; PARSING

(defn type? [o t]
  (= (first o) t))

(defn kicad-symbol-prop-name? [s]
  (string/starts-with? s "ki_"))

(defn regular-symbol-prop? [o]
  (and (type? o 'property)
       (not (kicad-symbol-prop-name? (second o)))))

(defn kicad-symbol-prop? [o]
  (and (type? o 'property)
       (kicad-symbol-prop-name? (second o))))

(defn parse-kicad-symbol-prop [p]
  (let [[_kind k v] p
        v (case k
            ("ki_keywords" "ki_fp_filters") (string/split v #" ")
            v)]
    [k v]
    ))

(defn parse-symbol-prop [p]
  (let [[_kind k v [_at x y z]] p]
    (if (kicad-symbol-prop-name? k)
      (parse-kicad-symbol-prop p)
      ;; regular prop
      {:k k
       :v v
       :at [x y z]})))

(defn parse-symbol [symbol]
  (let [[_kind label & rest] symbol
        props (filter regular-symbol-prop? rest)
        kicad-props (filter kicad-symbol-prop? rest)]
    {:label label
     :props (map parse props)
     :ki_props (into {} (map parse kicad-props))}))

(defn parse-symbol-lib [o]
  (let [[_kind & props] o
        version (some #(when (type? % 'version) %) props)
        generator (some #(when (type? % 'generator) %) props)
        symbols (filter #(type? % 'symbol) props)]
    {:version (parse version)
     :generator (parse generator)
     :symbols (map parse symbols)}))

(defn parse [[kind :as o]]
  (case kind
    kicad_symbol_lib (parse-symbol-lib o)
    symbol (parse-symbol o)
    property (parse-symbol-prop o)
    (version generator) (second o)
    nil))

(defn parse-at-filepath [fp]
  (->> (clojure.core/slurp fp)
       clojure.edn/read-string
       parse))
