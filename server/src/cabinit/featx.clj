(ns cabinit.featx)

(defn tweet-prop [tweet propname]
  "Extract a property from a tweet."
  (let [{v propname} tweet] (if (nil? v) 0 (if (= "100+" v) 100 v))))
  
; FIXME these if nil? thingies seem rather defensive
(defn user-prop [tweet propname]
  "Extract a user property from a tweet."
  (let [{{v propname} :user} tweet] (if (nil? v) 0 v)))

; we use (sort-by extracted amalgamation of user props

(defn wapply-tuple [[f w] i]
  "Apply the [function:weight] tuple from our weighted-feats map (in a weighted manner)."
  (* (f i) w))

(defn weighted-apply [weighted-feats item]
  "Apply all feature functions in a weighted manner to the item."
  (reduce + 0.0 (map #(wapply-tuple % item) weighted-feats)))

(defn weighted-filter [weighted-feats]
  (partial weighted-apply weighted-feats))

(defn weighted-sort [timeline weighted-feats]
  "(Re-) sort a timeline based on weighted features (highest weights first)."
  (sort-by (weighted-filter weighted-feats) > timeline))
