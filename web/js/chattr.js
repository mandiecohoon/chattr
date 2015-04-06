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
           $('#room').text(JSON.stringify(data));
       }
    });
}
function createRoom() {
    $.ajax({
       url: 'rs/room',
       method: 'POST',
       data: JSON.stringify({ 
           'roomId' : 4,
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