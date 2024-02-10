(ns pcb.core
  (:gen-class)
  (:require
   [pcb.component :as component]
   [pcb.kicad-symbol :as kicad-symbol]))



;; KiCad

(def kicad-symbol-ext ".kicad_sym")

(defn kicad-symbol-files [kicad-symbol-dirpath]
  (let [
        kicad-symbol-ext-matcher (.getPathMatcher
                                  (java.nio.file.FileSystems/getDefault)
                                  (str "glob:*" kicad-symbol-ext))
        kicad-symbol-dir (clojure.java.io/file kicad-symbol-dirpath)]
    (->> (file-seq kicad-symbol-dir)
         (filter #(.isFile %))
         (filter #(.matches kicad-symbol-ext-matcher (.getFileName (.toPath %)))))))




(defn -main
  "I don't do a whole lot ... yet."
  [& args]

  (println "Hello, World!")

  (let [kicad-symbol-files (kicad-symbol-files "/Applications/KiCad/KiCad.app/Contents/SharedSupport/symbols/")

        c {:type :component
           :label "R1"
           :legs [{:relative-coords {:relative-x 1
                                     :relative-y 1}
                   :label "GND"
                   }]}

        kicad-symbol-libs (map #(kicad-symbol/parse-at-filepath (.getAbsolutePath %)) kicad-symbol-files)
        kicad-symbols (apply concat (map #(:symbols %) kicad-symbol-libs))
        ]
    (when (component/instanceof? c :strict true :verbose true)
      (println "Valid component!")
      )

    ;; (prn (map #(.getAbsolutePath %) (take 10 kicad-symbol-files)))

    ;; (prn
    ;;  (->> (first kicad-symbol-files)
    ;;       .getAbsolutePath
    ;;       kicad-symbol/parse-at-filepath))

    (prn (some #(when (= (:label %) "LM13700") %) kicad-symbols))
    ;; (prn (first kicad-symbol-libs))
    )
  )
