(ns pcb.utils.core)


;; SOME

(defn keep-first [pred coll]
  (some (fn [x] (when (pred x) x)) coll))
