(ns cabinit.util
    (:require
      [clj-json.core :as json])
)

;(defmacro dbg[x] `(let [x# ~x] (println "dbg:" '~x "=" x#) x#))


; json-response so it can be accessed by both web and oauth_dance without circular dependencies
(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(comment ; all sorts of random algos cut and pasted here
(defn filtered-tweet
  "filter out just the parts of the tweet wanted by the UI"
  [tweet]
  (let [{text :text {screen_name :screen_name} :user} tweet]
    {:screen_name screen_name :tweet text}))

      
  ;(GET "/auth/tweets.json" {oauth :twitter-oauth}
  ;  (twitter/with-oauth (consumer) (:oauth_token oauth) (:oauth_token_secret oauth)
  ;    (json-response {:name (:screen_name oauth)
  ;                    :tweets (map filtered-tweet (twitter/home-timeline))})))




(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "8080"))]
    (config)  ; force memoization of config variables - fail early!
    (run-jetty app {:port port})))


(defn json-response
  "return a JSON formatted Ring response"
  [data]
  (-> (response/response (json/generate-string data))
    (response/content-type "application/json"))))
