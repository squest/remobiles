(defproject alfa "0.1.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [zenedu/zenpack "0.1.11"]
                 [clj-time "0.9.0"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [org.clojure/clojurescript "0.0-2511"]
                 [reagent "0.4.3"]
                 [cljs-ajax "0.3.3"]
                 [com.taoensso/carmine "2.9.0"]
                 [jayq "2.5.2"]
                 [couchbase-clj "0.2.0"]]
  
  :cljsbuild {:builds
              [{:source-paths ["src_cljs"],
                :compiler {:pretty-print false,
                           :closure-warnings {:non-standard-jsdoc :off},
                           :output-to "resources/zenvocab/www/app.js",
                           :output-wrapper false,
                           :optimizations :simple}}]}
  
  :url "http://example.com/FIXME"
  :main alfa.core
  :jvm-opts ["-server"]
  :plugins [[lein-ring "0.8.13"]
            [codox "0.8.10"]
            [lein-expectations "0.0.8"]
            [lein-environ "1.0.0"]
            [lein-cljsbuild "1.0.3"]
            [lein-autoexpect "1.4.2"]]
  :description "FIXME: write description"
  :min-lein-version "2.0.0")
