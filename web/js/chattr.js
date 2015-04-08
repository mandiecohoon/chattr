/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
    getRoomList();
    $(".message-box-button").css("visibility", "hidden");
});

function getRoomList() {
    $.ajax({
       url: 'rs/room',
       method: 'GET',
       success: function(data) {
            //$('#room').text(JSON.stringify(data));
            tempData = JSON.parse(data);
            drawRoomList(tempData);
       }
    });
}
function createRoom() {
    $.ajax({
       url: 'rs/room',
       method: 'POST',
       data: JSON.stringify({ 
           'roomId' : null,
           'roomName' : $('#roomName').val(),
           'description' : $('#description').val()
        }),
        contentType: "application/json",
       success: getRoomList
    });
}

function getMessages(id) {
    id = parseInt(id);
    $.ajax({
       url: 'rs/room/' + id,
       dataType: 'json',
       method: 'GET',
       success: function(data) {
           //$('#messages').text(JSON.stringify(data));
           tempDataStr = JSON.stringify(data);
           tempDataPrse = JSON.parse(tempDataStr);
           drawMessageList(tempDataPrse);
           $('#roomId').val(id);
       },
    });
}

function sendMessage(message, id) {
    $.ajax({
       url: 'rs/room/' + id,
       method: 'POST',
       data: JSON.stringify({ 
           'messageId' : null,
           'message' : message,
           'roomId' : parseInt(id)
        }),
        contentType: "application/json",
        success: function() {
            setTimeout(getMessages(id), 1000);
        }
    });
}

function drawRoomList(data) {
    $("#frame").empty();
    for (var i = 0; i < data.length; i++) {
        drawRoomListRow(data[i]);
    }
}

function drawRoomListRow(rowData) {
    var row = $("<span>");
    $("#frame").append(row);
    row.append($("<li><a onclick='getMessages(" + rowData.roomId + ")'>" + rowData.roomName + " –  " + rowData.description + "</a></li>"));
}

function drawMessageList(data) {
    $("#frame").empty();
    for (var i = 0; i < data.length; i++) {
        drawMessageListRow(data[i]);
    }
    var messageBoxInput = document.getElementById("message-box-input");
    messageBoxInput.type= "text";
    messageBoxInput.size= "75";
    messageBoxInput.placeholder="Message";
    
    $(".message-box-button").css("visibility", "visible");
}

function drawMessageListRow(rowData) {
    var row = $("<ul />");
    $("#frame").append(row);
    row.append($("<li>" + rowData.message + "</li>"));
}