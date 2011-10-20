# The views contained herein mainly serve to provide and manipulate the 
# DOM based upon backbone models, their contents and events.

class @TweetView extends Backbone.View
	initialize: =>
		@model.view = this
		@rendered = false

	render: =>
		# creates a jQuery wrapped DOM element
		if not @rendered
			@el = ich.tweetview flatten_json @model.toJSON(), ['user']
			@$(".status").linkify()
			@rendered = true
		return @el
	
	
	remove: =>
		@el.slideUp "slow", -> @el.remove()
	
class @TimelineView extends Backbone.View
	initialize: =>
		# views can bind to model events
		@model.bind 'add', @added
		
	render: =>
		@el = ich.tlview {}
		$('#main').append @el
		@model.each (twm) =>
			te = twm.view.render()
			@el.append te
		return @el

	added: (twi) =>
		# again ensure correct order in adding
		@el.prepend twi.view.render()

class @CompareTweetView extends Backbone.View
	render: =>
		@el = ich.cmptweetview flatten_json @model.toJSON(), ['user']
		@$(".status").linkify()
		return @el

class @CompareTLView extends TimelineView
	resorted: =>
		console.log @model
		@model.sortBy (tweet) =>
			#console.log tweet.get("created_at")
			0 - Date.parse tweet.get "created_at"
	
	render: =>
		@el = ich.comptlview {}
		$('#main').append @el
		_.each @resorted(), (twm) =>
			te = new CompareTweetView({model:twm}).render()
			@el.append te
			
		return @el
		
