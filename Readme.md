# Simple Twitter client with re-ranking

This is a simple example web-app twitter client. 
It does login via OAuth, retreives the user's timeline and re-ranks it
by applying a weighted sum of scoring functions (a stupid example is provided).

## Code overview

The code comes in two parts, client and server, the client is
written in pure CoffeeScript with Backbone.js.
The REST-backend is written in Clojure/Ring/Compojure, using adamwynne's
[twitter-api](https://github.com/adamwynne/twitter-api) library.

We used Henrik Joreteg's ICanHaz library for client-side templating with mustache.js.
In hindsight we probably should have used Handlebars since we needed helper functions,
but it still is a solid library.

We wrote this to do re-filtering of the twitter stream, which obviously is not ideal
to do upon each request, but for simplicity's sake we did not include the database backend for now.
To figure out how to add your own scoring functions check out the example filter definition
`follow-retweet-filter` in `server/src/cabinit/user.clj` which is a map from scoring functions
to their weight.

	(def follow-retweet-filter 
		{ #(/ 1.0 (+ 0.1 (featx/user-prop % :followers_count)))                                          0.4
		  #(double (/ (featx/tweet-prop % :retweet_count) (+ 0.1 (featx/user-prop % :followers_count)))) 0.6})


You then need to wrap it in a function, and add it to the `defroutes` in `server/src/cabinit/web.clj`.


## Dependencies

For the server make sure you have installed

* Install nginx on your system via package manager (or set up your own preferred reverse proxy)
* Get [Leiningen](https://github.com/technomancy/leiningen)

To build the client side coffeescript you need to install [Node.js](http://nodejs.org/) and [npm](http://npmjs.org/)
Then run

	npm install -g coffee-script

## Getting started

### Twitter credentials
Go to [Twitter](https://dev.twitter.com/apps/new) And set up a new app, be sure to set
`http://127.0.0.1/twitter_oauth_response` (and not `localhost`) as your callback.

Next copy your consumer key and secret, and edit them into your server's config file `cabinit.config`:

	cp server/resources/sample.config server/resources/cabinit.config

### Nginx
See below for how to configure nginx

If it isn't running, start nginx

on Ubuntu:

	sudo service nginx start

on Snow Leopard:

	sudo nginx


### Compile your client-side code

	cd client/
	make


### Start the server

	cd ../server/
	
    lein deps
	lein run c


### Test the app
Find stuff at [http://localhost/index.html](http://localhost/index.html) in case you set
`/path/to/client/directory` to lead directly to where `index.html` resides

## Set up the reverse proxy
If you want to serve your app as a static html-file, you need a reverse
proxy to get around the cross-site access restriction when calling the
API.

* Find the nginx config file 
  On Ubuntu it's in `/etc/nginx/sites-enabled/default`
  On Mac Snow Leopard it's in `/usr/local/etc/nginx/nginx.conf`
* To map the /api route to http://localhost:8000/ edit your config file to display something like this


		server {
			listen       80;
			server_name  localhost;
			access_log  /var/log/nginx/localhost.access.log;

			location / {
				root /path/to/client/directory;
				autoindex on;
				allow 127.0.0.1;
				index index.html index.htm;
			}
			
			location /api {
				proxy_pass http://localhost:8000;
			}
			
			location /auth {
				proxy_pass http://localhost:8000;
				allow 127.0.0.1;
			}
			location /twitter_oauth_response {
				proxy_pass http://localhost:8000;
				allow 127.0.0.1;
			}
		}


## Acknowledgements / further reading
Thanks to 
[https://github.com/kornysietsma/twitter-example](https://github.com/kornysietsma/twitter-example)
for most of the oauth dance bits.
Also to [https://github.com/adamwynne/twitter-api](https://github.com/adamwynne/twitter-api)
for much of the rest.

## Session & OAuth

### Reverse proxy

If you want to serve your app as a static html-file, you need a reverse
proxy to get around the cross-site access restriction when calling the
API. We use nginx to do this, we serve up `/` on the server (i.e.
`http://localhost/`) as a static file, using nginx directly. we then
allocate all the paths we want to serve by our api and send them to 
the port we are serving it from (in this case 8000).
## License

Copyright (c) 2011 Priska Herger and Gabriel Pickard

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
