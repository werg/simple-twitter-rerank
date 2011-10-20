class @App extends Backbone.Router
	initialize:	->
		
	display_timeline: =>
		@tl = new Timeline []
		@tlv	= new TimelineView {model: @tl}
		# fetches the timeline from REST
		@cmptlv = new CompareTLView {model: @tl}
		
		fetchparams = 
			success: => 
				@tlv.render()
				@cmptlv.render()
				
		@tl.fetch fetchparams
		
		
		
	check_status: (success) ->
		result = $.getJSON "/auth/status.json"
		result.done (data) =>
			success()
			#@render_view(@outputElement,"tweets", {name: data.name})
		result.fail (data) =>
			@auth_error(data)

	auth_error: (data) ->
		if data.status == 401
			payload = JSON.parse(data.responseText)
			unless payload? and payload.authUrl?
				$('#container').prepend ich.errorview {message: "Auth error, got 401 but something else went wrong", payload: payload, response: data}
			else
				$('#container').prepend ich.noauthview payload
		else
			$('#container').prepend ich.errorview {message: "Auth error, did not get 401", payload: payload, response: data}
		
		
@app = new App()
		
$(document).ready ->
	# todo: make this cleaner so that display_timeline isn't hidden in check_status
	app.check_status(app.display_timeline)
	#app.display_timeline()
	#app.tl.addTweets [x1]
