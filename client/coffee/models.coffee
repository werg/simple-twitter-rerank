# basically these are wrapper classes for our datatypes and collections
# currently mainly the timeline collection does work, whereas Tweet is 
# basically just a container for a json structure (the one we get handed
# through from twitter)
class @Tweet extends Backbone.Model
	initialize: ->
		@view = new TweetView {model: this}
	#url: =>
	#	"api/tweet/" + @id

class @Timeline extends Backbone.Collection
	model: Tweet
	#initialize: (models, options) ->
		
	url: =>
		# provides a url from which to load the timeline
		# in case this.fetch() is called
		"/auth/homeline" # + (@id || '')
	
	addTweets: (li) =>
		# makes sure that tweets are added to the top of the timeline.
		# falls back upon this.add
		@add li, {at:0}
