/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
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
    @NamedQuery(name = "Chattr.findAll", query = "SELECT r FROM Chattr r"),
    @NamedQuery(name = "Chattr.findByRoomId", query = "SELECT r FROM Chattr r WHERE r.roomId = :roomId"),
    @NamedQuery(name = "Chattr.findByName", query = "SELECT r FROM Chattr r WHERE r.roomName = :roomName")
})
public class ChattrEntities implements Serializable  {
    @Id
    private int roomId;
    private String roomName;
    private String description;
    
    public ChattrEntities() {
    }

    public ChattrEntities(int roomId, String roomName, String description) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.description = description;
    }
    
    public ChattrEntities(JsonObject json) {
        roomId = json.getInt("roomId");
        roomName = json.getString("roomName");
        description = json.getString("description");
    }
    
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getDescritpion() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
