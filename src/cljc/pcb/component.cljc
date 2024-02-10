(ns pcb.component
  "A basic component"
  (:require
   [clojure.spec.alpha :as s]))



;; SPEC

;; FIXME: wanted to use `::this` instead of `:component` but apparently spec doesn't like fully qualified keywords in that context
(s/def ::type #{:component})

(s/def ::relative-x number?)
(s/def ::relative-y number?)
(s/def ::relative-coords (s/keys :req-un [::relative-x ::relative-y]))

(s/def ::label string?)

(s/def ::leg (s/keys :req-un [::relative-coords] :req-opt [::label]))

(s/def ::legs (s/coll-of ::leg))

(s/def ::this (s/keys :req-un [::type ::legs] :req-opt [::label]))



;; PREDICATES

(defn instanceof?
  "Return a truthy value if O is a component."
  [o & {:keys [strict verbose]}]
  (if strict
    (if verbose
      (s/explain ::this o)
      (s/valid? ::this o))
    (= (::type ::this))))
