var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
  //  var socket = new SockJS('/gs-guide-websocket'); only should use SockJS if  some browsers don't have implemented websocket.
	  var socket = new WebSocket('ws://localhost:8080/gs-guide-websocket');  
	  stompClient = Stomp.over(socket);
	  
	  stompClient.connect('jalor', 'test', connectedCallback);
	  /*stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
        
        stompClient.subscribe('/user/exchange/amq.direct/', function(data){
            alert(data.body);
        });
    });*/
}

var connectedCallback = function (frame) {
    setConnected(true);
    console.log('Connected: ' + frame);
    stompClient.subscribe('/topic/greetings', function (greeting) {
        showGreeting(JSON.parse(greeting.body).content);
    });
    
    stompClient.subscribe('/user/queue/search', function(data){
        alert(data.body);
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    var headers = {'selector': "location = 'Europe'"};
    //stompClient.unsubscribe('/user/queue/search', headers);
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
    stompClient.send("/app/search", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

