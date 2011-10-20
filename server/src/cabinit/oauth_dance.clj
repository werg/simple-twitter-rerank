(ns cabinit.oauth_dance
  (:use compojure.core
    ring.middleware.json-params
    ring.middleware.session
    sandbar.stateful-session
    clojure.contrib.except ; this might be in core by now??\
    [clojure.contrib.def :only [defn-memo]]) ; replace with incubator
  (:require
    [compojure.route :as route]
    [compojure.handler :as handler]
    [clj-json.core :as json]
    [oauth.client :as oauth]
    [cabinit.util :as util]
    [cabinit.user :as user]
    [clojure.java.io :as io]
    [ring.util.response :as response]))

(defn make-consumer
  "construct a Twitter consumer"
  [key secret]
  (oauth/make-consumer key secret
    "https://api.twitter.com/oauth/request_token"
    "https://api.twitter.com/oauth/access_token"
    "https://api.twitter.com/oauth/authorize"
    :hmac-sha1))

; FIXME
(def oauth-response-path "/twitter_oauth_response")

(defn-memo config
  "configuration constructed from environment variables - memoized!"
  []
  (let [env (System/getenv)
        key user/*app-consumer-key*
        secret user/*app-consumer-secret*
        ;host (get env "CALLBACK_HOST")
        ;port (get env "CALLBACK_PORT")
        host user/*host*
        callback (str "http://" host oauth-response-path)]
    (throw-if-not (and key secret host) "Not all required environment variables set")
    {:consumer (make-consumer key secret)
     :callback callback}))

(defn consumer
  "twitter consumer from global config"
  [] (:consumer (config)))

(defn oauth-response-callback
  "the url on our site that Twitter should redirect users back to"
  [] (:callback (config)))

(defn twitter-request-token
  "fetch request token from twitter to start the oauth authorization dance"
  []
  (oauth/request-token (consumer) (oauth-response-callback)))

(defn callback-uri
  "callback uri (on Twitter) to which we send a user to start authorization, once we have a request token"
  [request-token]
  (oauth/user-approval-uri (consumer)
    (:oauth_token request-token)))

(defn access-token-response
  "get access token from twitter once twitter has redirected back to the app"
  [request-token verifier]
  (oauth/access-token (consumer)
    request-token
    verifier))

(def oauth-response-route
  (GET oauth-response-path [oauth_token oauth_verifier]
    (let [request-token (session-get :request-token)
          resp (access-token-response request-token oauth_verifier)]
      (session-put! :twitter-oauth resp)
      (response/redirect user/*oauth-redirect-url*))))
      
(def oauth-status-route
  (GET "/auth/status.json" {oauth :twitter-oauth}
    (util/json-response
      {:name (:screen_name oauth)})))

(defn wrap-oauth
  "middelware wrapper for twitter oauth credential check
   only checks requests starting with '/auth'
   if signed in, stores credentials in :twitter-oauth and continues
   if not signed in, returns a 401 error, including the callback URL needed to sign in,
   so clients can prompt the user to start the OAuth dance!"
  [handler]
  (fn [request]
    (if (re-matches #"/auth/.*" (:uri request))
      (if-let [oauth (session-get :twitter-oauth)]
        (handler (assoc request :twitter-oauth oauth))
        (let [request-token (twitter-request-token)
              auth-url (callback-uri request-token)]
          (session-put! :request-token request-token)
          (-> (util/json-response {:authUrl auth-url})
            (response/status 401))))
      (handler request))))
