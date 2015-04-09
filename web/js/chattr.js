/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
    getRoomList();
    setInvisible();
});

function getRoomList() {
    $.ajax({
       url: 'rs/room',
       method: 'GET',
       success: function(data) {
            tempData = JSON.parse(data);
            drawRoomList(tempData);
       }
    });
    $('#roomId').val(null);
    setInvisible();
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

function updateRoom(id, roomName, description) {
    $.ajax({
       url: 'rs/room',
       method: 'PUT',
       data: JSON.stringify({ 
           'roomId' : parseInt(id),
           'roomName' : roomName,
           'description' : description
        }),
        contentType: "application/json",
        success: function() {
            setTimeout(getMessages(id), 1000); 
            setRoomName(roomName);
            setRoomDescription(description);
        }
    });
    
}

function deleteRoom(id) {
    id = parseInt(id);
    $.ajax({
       url: 'rs/room/' + id,
       method: 'DELETE',
       success: function() {
           getRoomList();
       }
    });
}

function getMessages(id) {
    id = parseInt(id);
    $.ajax({
       url: 'rs/room/' + id,
       dataType: 'json',
       method: 'GET',
       success: function(data) {
           tempDataStr = JSON.stringify(data);
           tempDataPrse = JSON.parse(tempDataStr);
           drawMessageList(tempDataPrse);
           $('#roomId').val(id);
           $("#frame").scrollTop($("#frame")[0].scrollHeight);
       }
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
    row.append($("<li><a onclick='getMessages(" + rowData.roomId + "),setRoomName(\"" + rowData.roomName + "\"),setRoomDescription(\"" + rowData.description + "\")'>" + rowData.roomName + " –  " + rowData.description + "</a></li>"));
}

function drawMessageList(data) {
    $("#frame").empty();
    for (var i = 0; i < data.length; i++) {
        drawMessageListRow(data[i]);
    }
    setVisible();
}

function drawMessageListRow(rowData) {
    var row = $("<dl>");
    $("#frame").append(row);
    row.append($("<dt style=\"max-width:15px; margin-left:0px;\"><span class=\"glyphicon glyphicon-user\"></span></dt><dd style=\"margin-left: 30px;\">" + rowData.message + "</dd>"));
}

function setVisible() {
    var messageBoxInput = document.getElementById("message-box-input");
    messageBoxInput.type= "text";
    messageBoxInput.size= "75";
    messageBoxInput.placeholder="Message";
    $(".message-box-button").css("visibility", "visible");
    $(".backButton").css("visibility", "visible");
    $(".deleteRoom").css("visibility", "visible");
    $(".updateRoomButton").css("visibility", "visible");
}

function setInvisible() {
    var messageBoxInput = document.getElementById("message-box-input");
    messageBoxInput.type= "hidden";
    $(".message-box-button").css("visibility", "hidden");
    $(".backButton").css("visibility", "hidden");
    $(".deleteRoom").css("visibility", "hidden");
    $(".updateRoomButton").css("visibility", "hidden");
    $("#chatRoomTitle").empty();
}

function setRoomName(roomName) {
    $("#chatRoomTitle").empty();
    $("#chatRoomTitle").append(roomName);
    $("#editRoomName").val(roomName);
}

function setRoomDescription(description) {
    $("#editDescription").val(description);
}

function updateRoomDialog() {
    //$('#modal').modal('handleUpdate');
    //$('#modal').modal('show');
}