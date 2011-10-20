(ns cabinit.web
  (:use 
    compojure.core
    sandbar.stateful-session
    ring.middleware.json-params
    ring.middleware.stacktrace)
  (:require
    [clj-json.core :as json]
    [cabinit.user :as user]
    [cabinit.util :as util]
    [cabinit.oauth_dance :as oauth_dance])
  (:import 
    org.codehaus.jackson.JsonParseException
    clojure.contrib.condition.Condition))

(def error-codes
  {:invalid 400
   :not-found 404})

(defn wrap-error-handling [handler]
  (fn [req]
    (try
      (or (handler req)
          (util/json-response {"error" "resource not found"} 404))
      (catch JsonParseException e
        (util/json-response {"error" "malformed json"} 400))
      (catch Condition e
        (let [{:keys [type message]} (meta e)]
          (util/json-response {"error" message} (error-codes type)))))))

(defroutes handler
  
  (GET "/auth/homeline" {oa :twitter-oauth}
    (util/json-response (user/frt-filtered-homeline oa)))
    ;(util/json-response (user/homeline oa)))
    
  (GET "/api/user/:id" [id]
    {:status 200
     :headers {"Content-Type" "application/json"}
     :body (user/homeline id)})

   oauth_dance/oauth-response-route
   oauth_dance/oauth-status-route)

(def app
  (-> handler
    oauth_dance/wrap-oauth
    wrap-stateful-session
    wrap-json-params
    wrap-error-handling
    wrap-stacktrace))
