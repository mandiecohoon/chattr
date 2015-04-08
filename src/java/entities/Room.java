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
    @NamedQuery(name = "ChattrRoom.findAll", query = "SELECT r FROM Room r"),
    @NamedQuery(name = "ChattrRoom.findByRoomId", query = "SELECT r FROM Room r WHERE r.roomId = :roomId"),
    @NamedQuery(name = "ChattrRoom.findByName", query = "SELECT r FROM Room r WHERE r.roomName = :roomName")
})
public class Room implements Serializable  {
    @Id
    private int roomId;
    private String roomName;
    private String description;
    
    public Room() {
    }

    public Room(int roomId, String roomName, String description) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.description = description;
    }
    
    public Room(JsonObject json) {
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
    
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("roomId", roomId)
                .add("roomName", roomName)
                .add("description", description)
                .build();
    }
}
