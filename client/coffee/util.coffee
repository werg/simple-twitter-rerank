@flatten_json = (object, attributes) ->
	result = _.clone object
	_.each attributes, (attr) ->
		for own key, value of object[attr]
			result[attr + '_' + key] = value
	return result
	
@make_link = (string) ->
	string.replace /(https?:\/\/([-\w\.]+[-\w])+(:\d+)?(([\w/_\.-]*(\?\S+)?[^\.\s])?)?)/g, '<a href="$1">$1</a>'
	return string
	
@html_encode = (string) ->
	string.replace /[&<>"']/g, ($0) ->
		"&" + {"&":"amp", "<":"lt", ">":"gt", '"':"quot", "'":"#39"}[$0] + ";"
		