/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
    getRoomList();
});

function getRoomList() {
    $.ajax({
       url: 'rs/room',
       method: 'GET',
       success: function(data) {
            //$('#room').text(JSON.stringify(data));
            tmp1 = JSON.parse(data);
            drawTable(tmp1);
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
    getRoomList();
}

function getMessages(id) {
    $.ajax({
       url: 'rs/room/' + id,
       dataType: 'json',
       method: 'GET',
       success: function(data) {
           $('#messages').text(JSON.stringify(data));
       },
    });
}

function drawTable(data) {
    $("#room").empty();
    for (var i = 0; i < data.length; i++) {
        drawRow(data[i]);
    }
}

function drawRow(rowData) {
    var row = $("<ul />")
    $("#room").append(row);
    row.append($("<li><a onclick='getMessages(" + rowData.roomId + ")'>" + rowData.roomName + " –  " + rowData.description + "</a></li>"));
}