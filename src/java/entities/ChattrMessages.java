/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Amanda Cohoon - c0628569
 */

@Entity
@NamedQueries({
    @NamedQuery(name = "ChattrMessages.findAll", query = "SELECT m FROM messages m"),
    @NamedQuery(name = "ChattrMessages.findByAllId", query = "SELECT m FROM messages m WHERE m.roomId = :roomId")
})
public class ChattrMessages implements Serializable {
    @Id
    private int messageId;
    private String message;
    private int roomId;

    public ChattrMessages() {
    }

    public ChattrMessages(int messageId, String message, int roomId) {
        this.messageId = messageId;
        this.message = message;
        this.roomId = roomId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
    
    public ChattrMessages(JsonObject json) {
        messageId = json.getInt("messageId");
        message = json.getString("message");
        roomId = json.getInt("roomId");
    }
    
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("messageId", messageId)
                .add("message", message)
                .add("roomId", roomId)
                .build();
    }
    
}
