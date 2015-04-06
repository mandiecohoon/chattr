/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function getRoomList() {
    $.ajax({
       url: 'room',
       method: 'GET',                   
       success: function(data) {
           $('#room').json(data);
       }
    });
}
function createRoom() {
    $.ajax({
       url: 'room',
       method: 'POST',
       data: { 
           'roomId' : 4,
           'roomName' : $('#roomName').val(),
           'description' : $('#description').val()
        },
       success: getRoomList
    });
}