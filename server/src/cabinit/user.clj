(ns cabinit.user
  (:use
    clojure.contrib.condition)
  (:require 
    [twitter.oauth :as oauth]
    [cabinit.featx :as featx]
    [twitter.api.restful :as rest])
  (:import 
    (java.util Properties))
  (:refer-clojure :exclude (list)))


;; load app consumer key and secret from config file ;;
(defn load-config-file
  [file-name]
  (let [file-reader (.. (Thread/currentThread)
                        (getContextClassLoader)
                        (getResourceAsStream file-name))
        props (Properties.)]
    (.load props file-reader)
    (into {} props)))

(def ^:dynamic *config* (load-config-file "cabinit.config"))

(defn assert-get
  [key-name]  
  (or (get *config* key-name) 
      (throw (Exception. (format "please define %s in the resources/cabinit.config file" key-name)))))

(def *app-consumer-key* (assert-get "app.consumer.key"))
(def *app-consumer-secret* (assert-get "app.consumer.secret"))
(def *oauth-redirect-url* (or (get *config* "oauth.redirect") "/"))
(def *host* (assert-get "host"))

;; make oauth credits with app consumer data and user access tokens
(defn make-creds [oa]
  (oauth/make-oauth-creds *app-consumer-key*
                          *app-consumer-secret*
                          (:oauth_token oa)
                          (:oauth_token_secret oa)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn homeline [oa]
  (:body (rest/home-timeline 
                :oauth-creds (make-creds oa)
                :params {:screen-name (:screen_name oa) :count 100})))

; the following i think would be great material to write a macro for filter definitions
; FIXME defensive divide-by-zero guard
(def follow-retweet-filter { #(/ 1.0 (+ 0.1 (featx/user-prop % :followers_count)))                                   0.4
                    #(double (/ (featx/tweet-prop % :retweet_count) (+ 0.1 (featx/user-prop % :followers_count))))   0.6})

  
(defn frt-filter [homeline]
  (featx/weighted-sort homeline  follow-retweet-filter))
  
(defn frt-filtered-homeline [oa]
  (frt-filter (homeline oa)))
