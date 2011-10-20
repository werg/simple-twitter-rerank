(ns cabinit.run
  (:use ring.adapter.jetty)
  (:require [cabinit.web :as web]))

(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8000"))]
    (run-jetty #'web/app {:port port})))
