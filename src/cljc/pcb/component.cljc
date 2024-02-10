(ns pcb.component
  "A basic component"
  (:require
   [cljs.spec.alpha :as s]))



;; SPEC

(s/def ::relative-x number?)
(s/def ::relative-y number?)
(s/def ::relative-coords :req-un [::relative-x ::relative-y])

(s/def ::label string?)

(s/def ::leg (s/keys :req-un [::relative-coords] :req-opt [::label]))

(s/def ::legs (s/coll-of ::leg))

(s/def ::this (s/keys :req-un [::legs]))
