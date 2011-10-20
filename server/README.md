# Cabinit

a basic Clojure REST API for a twitter application.

## Status

## To do
* In case you don't host the app at http://localhost/ you'll have to edit the test.config property
    oauth.redirect=/path/to/index.html
* Here's an idea for how to retrieve the oauth-token programmatically by including
  sandbar.stateful-session in your dependencies and calling
	(let [oauth (session-get :twitter-oauth)])
  but this hasn't been tested (not even sure whether oauth would include several values)
- Alternatively we could try using something like it's done in the example I based my oauth dance off of
  (it's from https://github.com/kornysietsma/twitter-example, they use a different twitter lib):
	  (GET "/auth/tweets.json" {oauth :twitter-oauth}
		(twitter/with-oauth (consumer) (:oauth_token oauth) (:oauth_token_secret oauth)
		  (json-response {:name (:screen_name oauth)
						  :tweets (map filtered-tweet (twitter/home-timeline))})))
