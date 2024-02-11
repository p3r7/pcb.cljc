(ns pcb.utils.core)


;; STRINGS

(defn parseInt [s]
  #?(:clj (Integer/parseInt s))
  #?(:cljs (js/parseInt s)))



;; COLLECTIONS - GENERIC

(defn maintain
  "Apply sequence processing FX (e.g. `map` or `filter`) with entry FN on each element of COLL while keeping the original collection type.

  Taken from book \"Clojure the Essential Reference\"."
  ([fx f coll]
   (into (empty coll) (fx f coll)))
  ([xform coll]
   (into (empty coll) xform coll)))

(defn map-vals
  "Returns a lazy hashmap consisting of the result of applying f to
    the value of each set in hashmap.
    Function f should accept one single argument."
  [f m]
  (persistent!
   (reduce-kv (fn [m k v] (assoc! m k (f v)))
              (transient (empty m)) m)))

(defn map-keys
  "Returns a lazy hashmap consisting of the result of applying f to
  the key of each set in hashmap.
  Function f should accept one single argument."
  [f m]
  (persistent!
   (reduce-kv (fn [m k v] (assoc! m (f k) v))
              (transient (empty m)) m)))


(defn continuous-partition [c]
  (let [first-partition (partition 2 c)
        second-partition (partition-all 2 (next c))]
    (->> (interleave first-partition second-partition)
         (remove #(= 1 (count %))))))



;; COLLECTIONS - PREDICATES

(defn- entry-member-of-map? [entry coll]
  (let [[k v] entry]
    (and (contains? coll k)
         (= v (get coll k)))))

(defn member?
  "Returns a truthy value if V is found in collection COLL."
  [v coll]

  (when-not (coll? coll)
    (throw (ex-info "Argument `coll` is not a collection" {:ex-type :unexpected-type})))

  (cond
    (set? coll) (coll v)                ; sets can be used as fn

    (map? coll)
    (cond
      (and (vector? v)
           (= 2 (count v)))
      (entry-member-of-map? v coll)

      (and (map? v)
           (= 1 (count v)))
      (entry-member-of-map? (first v) coll)

      :default (throw (ex-info "Argument `coll` is a map, expecting `v` to be a vector of size 2 or map os size 1"
                               {:ex-type :unexpected-type,
                                :v v :coll coll})))

    :default (some #{v} coll)))



;; COLLECTIONS - FILTER

(defn keep-first [pred coll]
  (some (fn [x] (when (pred x) x)) coll))

(defn keep-in-coll
  "Return new collection of same type as COLL with only elements satisfying PREDICATE."
  [coll predicate]
  (when (not (coll? coll))
    (throw (ex-info "Argument `coll` is not a collection"
                    {:ex-type :unexpected-type,
                     :coll coll})))
  (maintain filter predicate coll))

(defn keep-vals-in-coll
  "Return new collection of same type as COLL with only elements whose values satisfy PREDICATE."
  [coll predicate]
  (when (not (coll? coll))
    (throw (ex-info "Argument `coll` is not a collection"
                    {:ex-type :unexpected-type,
                     :coll coll})))
  (let [predicate (if (map? coll)
                    (comp predicate val)
                    predicate)]
    (maintain filter predicate coll)))

(defn remove-in-coll
  "Return new collection of same type as COLL with elements satisfying PREDICATE removed."
  [coll predicate]
  (keep-in-coll coll (complement predicate)))

(defn remove-vals-in-coll
  "Return new collection of same type as COLL with elements whose values satisfy PREDICATE removed."
  [coll predicate]
  (keep-vals-in-coll coll (complement predicate)))

(defn remove-nils [coll]
  (remove-vals-in-coll coll nil?))

(defn remove-empty [coll]
  ;; coll
  (remove-vals-in-coll coll #(and (coll? %)
                                  (empty? %))))
