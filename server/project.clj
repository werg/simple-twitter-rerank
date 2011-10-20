(defproject cabinit "0.0.1"
  :description "REST datastore interface."
  :dependencies
    [[org.clojure/clojure "1.2.1"]
     [org.clojure/clojure-contrib "1.2.0"]
     [ring/ring-jetty-adapter "0.3.10"]
     [ring/ring-core "0.3.8"]
     [ring-json-params "0.1.3"]
     [compojure "0.6.4"]
     [clj-oauth "1.2.10-SNAPSHOT"]
     [sandbar "0.4.0-SNAPSHOT"]
     [org.slf4j/slf4j-api "1.6.1"]
     [org.slf4j/slf4j-log4j12 "1.6.1"]
     [log4j/log4j "1.2.16"]
     [twitter-api "0.5.0"]
     [clj-time "0.3.1"]]
  :dev-dependencies
    [[lein-run "1.0.0-SNAPSHOT"]
     [midje "1.2.0"]
     [ring/ring-devel "0.3.8"]])
