(ns pcb.kicad.sexp
  (:require
   [clojure.string :as string]
   [pcb.utils.core :refer [keep-first remove-empty]]))

(declare parse
         ;; symbol
         parse-symbol-lib
         parse-symbol
         parse-symbol-prop
         parse-kicad-symbol-prop
         parse-pin)



;; PARSING - GENERIC

(defn type? [o t]
  (= (first o) t))

(defn parse [[kind :as o] & {:keys [file-path]}]
  (case kind
    kicad_symbol_lib (parse-symbol-lib o file-path)
    symbol (parse-symbol o)
    property (parse-symbol-prop o)
    pin (parse-pin o)
    (version generator) (second o)
    nil))

(defn parse-at-filepath [fp]
  (-> (clojure.core/slurp fp)
      clojure.edn/read-string
      (parse :file-path fp)))



;; SYMBOL - PREDICATES

(defn kicad-symbol-prop-name? [s]
  (string/starts-with? s "ki_"))

(defn regular-symbol-prop? [o]
  (and (type? o 'property)
       (not (kicad-symbol-prop-name? (second o)))))

(defn kicad-symbol-prop? [o]
  (and (type? o 'property)
       (kicad-symbol-prop-name? (second o))))




;; SYMBOL - PARSING

(defn parse-symbol-lib [o file-path]
  (let [[_kind & props] o
        version (keep-first #(type? % 'version) props)
        generator (keep-first #(type? % 'generator)props)
        symbols (filter #(type? % 'symbol) props)]
    (remove-empty
     {:location file-path
      :version (parse version)
      :generator (parse generator)
      :symbols (map parse symbols)})))

(defn parse-symbol [symbol]
  (let [[_kind label & rest] symbol
        props (filter regular-symbol-prop? rest)
        kicad-props (filter kicad-symbol-prop? rest)
        pins (filter #(type? % 'pin) rest)
        sub-symbols (filter #(type? % 'symbol) rest)]
    (remove-empty
     {:label label
      :props (map parse props)
      :ki_props (into {} (map parse kicad-props))
      :sub-symbols (map parse sub-symbols)
      :pins (map parse pins)})))

(defn parse-symbol-prop [p]
  (let [[_kind k v [_at x y z]] p]
    (if (kicad-symbol-prop-name? k)
      (parse-kicad-symbol-prop p)
      ;; regular prop
      {:k k
       :v v
       :at [x y z]})))

(defn parse-kicad-symbol-prop [p]
  (let [[_kind k v] p
        v (case k
            ("ki_keywords" "ki_fp_filters") (string/split v #" ")
            v)]
    [k v]))

(defn parse-pin [[_kind dir footprint & rest :as o]]
  (remove-empty
   {:direction dir
    :footprint footprint
    :name (second (keep-first #(type? % 'name) rest))
    :number (second (keep-first #(type? % 'number) rest))}))



;; SYMBOL - CONVERSION

(defn symbol-lib->symbols [symbol-lib]
  (let [location (:location symbol-lib)
        symbols (:symbols symbol-lib)]
    (map #(assoc % :lib location) symbols)))

(defn symbol-libs->symbols [symbol-libs]
  (apply concat (map symbol-lib->symbols symbol-libs)))



;; SYMBOL - LOOKUP & PRN

(defn symbol-pins [symbol]
  (apply concat (:pins symbol) (symbol-pins :sub-symbols)))

(defn prn-symbol [symbol]
  (println (:label symbol))
  (when-let [datasheet-prop (keep-first #(= (:k %) "Datasheet") (:props symbol))]
    (println (:v datasheet-prop)))
  (when-let [tags (get-in symbol [:ki_props "ki_keywords"])]
    (println (str "Tags: " (string/join ", " tags))))
  ;; (prn (symbol-pins symbol))
  )
