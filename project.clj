(defproject pcb "0.1.0-SNAPSHOT"
  :description "PCB design using clojure"
  :url "https://github.com/p3r7/pcb.cljc"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :repositories
  {"clojars" {:url "https://clojars.org/repo"
              :sign-releases false}}

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/clojurescript "1.11.132"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]

                 ;; formats
                 [metosin/jsonista "0.3.8"]

                 [thheller/shadow-cljs "2.27.3"]]

  :plugins   [
              ;; nREPL / Emacs
              [refactor-nrepl "3.9.1"]
              [cider/cider-nrepl "0.45.0"]]

  :source-paths ["src/cljc"
                 "src/clj"
                 "src/cljs"
                 "src/js"]

  :main ^:skip-aot pcb.core
  :target-path "target/%s"
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
