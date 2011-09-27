uuid = require 'node-uuid'

miaGame = require './miaGame'
dice = require './dice'

class InactiveState
	handleMessage: (messageCommand, messageArgs) ->
		this

class WaitingForJoinState
	constructor: (@token, @callback, @nextState) ->
	handleMessage: (messageCommand, messageArgs) ->
		actualToken = messageArgs[0]
		if messageCommand == 'JOIN' and actualToken == @token
			@nextState new InactiveState
			@callback true

class WaitingForTurnState
	constructor: (@token, @callback, @nextState) ->
	handleMessage: (command, args) ->
		token = args[0]
		if (token == @token)
			switch command
				when 'ROLL'
					@nextState new InactiveState
					@callback miaGame.Messages.ROLL
				when 'SEE'
					@nextState new InactiveState
					@callback miaGame.Messages.SEE

class WaitingForAnnounceState
	constructor: (@token, @callback, @nextState) ->
	handleMessage: (command, args) ->
		announcedDice = dice.parse args[0]
		token = args[1]
		if command == 'ANNOUNCE' and token == @token and announcedDice
			@nextState new InactiveState
			@callback announcedDice

class RemotePlayer
	constructor: (@name, @sendMessageCallback) ->
		@currentState = new InactiveState

	registered: ->
		@sendMessage 'REGISTERED;0'

	changeState: (newState) =>
		@currentState = newState

	willJoinRound: (callback) ->
		token = @generateToken()
		@changeState new WaitingForJoinState(token, callback, @changeState)
		@sendMessage "ROUND STARTING;#{token}"

	yourTurn: (callback) ->
		token = @generateToken()
		@changeState new WaitingForTurnState(token, callback, @changeState)
		@sendMessage "YOUR TURN;#{token}"

	yourRoll: (dice, callback) ->
		token = @generateToken()
		@changeState new WaitingForAnnounceState(token, callback, @changeState)
		@sendMessage "ROLLED;#{dice};#{token}"

	roundCanceled: (reason) ->
		@changeState new InactiveState
		@sendMessage "ROUND CANCELED;#{reason}"

	roundStarted: ->
		@sendMessage "ROUND STARTED;testClient:0" #TODO correct players/scores

	announcedDiceBy: (dice, player) ->
		@sendMessage "ANNOUNCED;#{player.name};#{dice}"

	playerLost: (player) ->

	handleMessage: (messageCommand, messageArgs) ->
		@currentState.handleMessage messageCommand, messageArgs

	sendMessage: (message) ->
		@sendMessageCallback message

	generateToken: ->
		uuid()

exports.create = (name, sendMessageCallback) ->
	new RemotePlayer(name, sendMessageCallback)

