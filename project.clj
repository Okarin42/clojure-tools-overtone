(defproject music "0.1.0-SNAPSHOT"
  :description "music written in overtone"
  :url "non yet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [ [org.clojure/clojure "1.5.1"]
                  [overtone "0.9.1"] ]
  :main ^:skip-aot music.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})

