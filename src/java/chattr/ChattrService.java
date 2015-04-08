/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattr;

import entities.ChattrRoom;
import entities.ChattrMessages;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.UserTransaction;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Amanda Cohoon - c0628569
 */

@Path("/room")
@RequestScoped
public class ChattrService {
    
    @PersistenceContext(unitName="ChattrPU")
    EntityManager em;

    List<ChattrRoom> roomList;
    List<ChattrMessages> messageList;
   
    @Inject
    UserTransaction transaction;
    
    @GET
    public Response getAll() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        Query q = em.createQuery("ChattrRoom.findAll");
        roomList = q.getResultList();
        for (ChattrRoom r : roomList) {
            json.add(r.toJSON());
        }
        return Response.ok(json.build().toString()).build();
    }
    
    
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        Query q = em.createQuery("SELECT r FROM Chattr r WHERE r.roomId = :roomId");
        q.setParameter("roomId", id);
        ChattrRoom r = (ChattrRoom) q.getSingleResult();
        String out = r.toJSON().toString();
        return Response.ok(out).build();
    }
    @GET
    @Path("{roomId}")
    @Produces("application/json")
    public Response doGet(@PathParam("roomId") int id) {
        return Response.ok(getMessageList("SELECT * FROM message WHERE roomId = ?", String.valueOf(id)), MediaType.APPLICATION_JSON).build();
        
    }
    
    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response result;
        try {
            transaction.begin();
            Chattr r = new Chattr(json);
            em.persist(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).build();
        }
        return result;
    }
    @POST
    @Consumes("application/json")
    public Response doPost(String data) throws SQLException {
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonObject json = reader.readObject();
        Connection conn = Credentials.getConnection();
        
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO `room`(`roomId`, `roomName`, `description`) "
               + "VALUES ("
               +"null, '"
               + json.getString("roomName") + "', '"
               + json.getString("description") +"'"
               +");"
        );
        try {
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            return Response.status(500).entity("500: Error creating chat room.").build();
        }
        
        PreparedStatement pstmtID = conn.prepareStatement("SELECT `roomId` FROM room ORDER BY `roomId` DESC LIMIT 1");
        ResultSet rs = pstmtID.executeQuery();
        rs.next();
        String id = String.valueOf(rs.getInt("roomId"));
        
        return Response.ok(getMessageList("SELECT * FROM product WHERE roomId = ?", id), MediaType.APPLICATION_JSON).build();
    }
    
    @PUT
    @Consumes("application/json")
    public Response edit(JsonObject json) {
        Response result;
        try {
            transaction.begin();
            Chattr r = (Chattr) em.createNamedQuery("Chattr.findByRoomId")
                    .setParameter("roomId", json.getInt("roomId"))
                    .getSingleResult();
            r.setRoomName(json.getString("roomName"));
            r.setDescription(json.getString("description"));
            em.persist(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).entity(ex.getMessage()).build();
        }
        return result;
    }
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response doPut(@PathParam("id") int id, String data) throws SQLException {
        JsonReader reader = Json.createReader(new StringReader(data));
        JsonObject json = reader.readObject();
        Connection conn = Credentials.getConnection();
        
        PreparedStatement pstmt = conn.prepareStatement("UPDATE `room` SET `name`='"
                +json.getString("roomName")+"',`description`='"
                +json.getString("description")+"'"
                + "WHERE `roomId`="
                +id
        );
        try {
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            return Response.status(500).entity("500: Error updating chat room info").build();
        }
        
        return Response.ok(getMessageList("SELECT * FROM room WHERE roomId = " + id), MediaType.APPLICATION_JSON).build();
    }
    
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        Response result;
        try {
            transaction.begin();
            Chattr r = (Chattr) em.createNamedQuery("Chattr.findByRoomId")
                    .setParameter("roomId", id).getSingleResult();
            em.remove(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).entity(ex.getMessage()).build();
        }
        return result;
    }
}
