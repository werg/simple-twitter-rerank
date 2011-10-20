# Simple Twitter client with re-ranking

This is a simple example web-app twitter client. 
It does login via OAuth, retreives the user's timeline and re-ranks it
by a weighted sum of scoring functions (a stupid example is provided).

The code comes in two parts, one client an one server, the client is
written in pure CoffeeScript with Backbone.js, a rather simple affair.
The REST-backend is written in Clojure/Ring/Compojure, using adamwynne's
[twitter-api](https://github.com/adamwynne/twitter-api) library.

## Dependencies

For the server make sure you have installed

* Install nginx on your system via package manager (or set up your own reverse proxy)
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



## Requirements & Installation

you need node.js, npm and CoffeeScript

After installing npm, to get CoffeeScript you enter:

	npm install -g coffee-script


### Test the app
Find stuff at [http://localhost/index.html](http://localhost/index.html) in case you set
`/path/to/client/directory` to lead directly to where `index.html` resides

## Set up the reverse proxy

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

## License

Copyright (c) 2011 Priska Herger and Gabriel Pickard

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
