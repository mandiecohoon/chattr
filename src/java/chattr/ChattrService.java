/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattr;

import entities.Message;
import entities.Room;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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

    List<Room> roomList;
    List<Message> messageList;
   
    @Inject
    UserTransaction transaction;
    
    // Get room list
    @GET
    public Response getAll() {
        JsonArrayBuilder json = Json.createArrayBuilder();
        Query q = em.createNamedQuery("ChattrRoom.findAll");
        roomList = q.getResultList();
        for (Room r : roomList) {
            json.add(r.toJSON());
        }
        return Response.ok(json.build().toString()).build();
    }
    
    // Get messages list
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        Query q = em.createNamedQuery("ChattrMessages.findByAllId")
                .setParameter("roomId", id);
        messageList = q.getResultList();
        for (Message m : messageList) {
            json.add(m.toJSON());
        }
        return Response.ok(json.build().toString()).build();
    }
    
    // Post a room
    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) throws SQLException {
        Response result;
        
        // Finds last inserted row and gets the value of the primary key
        PreparedStatement pstmtID = Credentials.getConnection().prepareStatement("SELECT `roomId` FROM room ORDER BY `roomId` DESC LIMIT 1");
        ResultSet rs = pstmtID.executeQuery();
        rs.next();
        int autoId = rs.getInt("roomId") + 1;
        // Creates a new object with the same data but with a correct ID
        JsonObject jsonData;
        JsonObjectBuilder jsonOB = Json.createObjectBuilder()
            .add("roomId", autoId)
            .add("roomName", json.getString("roomName"))
            .add("description", json.getString("description"));
        jsonData = jsonOB.build();
        
        try {
            transaction.begin();
            Room r = new Room(jsonData);
            em.persist(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception exRoom) {
            result = Response.status(500).build();
        }
        return result;
    }
    
    // Post a message
    @POST
    @Path("{id}")
    @Consumes("application/json")
    public Response add(JsonObject json, @PathParam("id") int id) throws SQLException {
        Response result;
        // Finds last inserted row and gets the value of the primary key
        PreparedStatement pstmtID = Credentials.getConnection().prepareStatement("SELECT `messageId` FROM message ORDER BY `messageId` DESC LIMIT 1");
        ResultSet rs = pstmtID.executeQuery();
        rs.next();
        int autoId = rs.getInt("messageId") + 1;
        // Creates a new object with the same data but with a correct ID
        JsonObject jsonData;
        JsonObjectBuilder jsonOB = Json.createObjectBuilder()
            .add("messageId", autoId)
            .add("message", json.getString("message"))
            .add("roomId", json.getInt("roomId"));
        jsonData = jsonOB.build();
        
        try {
            transaction.begin();
                Message m = new Message(jsonData);
                em.persist(m);
                transaction.commit();
                result = Response.ok().build();
        } catch (Exception exMessages) {
            result = Response.status(500).build();
        }
        return result;
    }
    
    // Update a room
    @PUT
    @Consumes("application/json")
    public Response edit(JsonObject json) {
        Response result;
        try {
            transaction.begin();
            Room r = (Room) em.createNamedQuery("ChattrRoom.findByRoomId")
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
    /*
    // Update a message
    @PUT
    @Path("{id}")
    @Consumes("application/json")
    public Response edit(JsonObject json, @PathParam("id") int id) {
        Response result;
        try {
            transaction.begin();
            ChattrRoom r = (ChattrRoom) em.createNamedQuery("ChattrRoom.findByRoomId")
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
    */
    
    // Delete a room - with all its messages
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        Response result;
        try {
            transaction.begin();
            Room r = (Room) em.createNamedQuery("ChattrRoom.findByRoomId")
                    .setParameter("roomId", id).getSingleResult();
            em.remove(r);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).entity(ex.getMessage()).build();
        }
        return result;
    }
    
    public static Response newp() {
        String url = "http://localhost:8080/chattr/rs/room";
        System.out.println(callURL(url));
        return Response.ok(callURL(url)).build();
    }
    
    public static String callURL(String myURL) {
        System.out.println("Requested URL:" + myURL);
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                                Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                            sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:"+ myURL, e);
        } 

        return sb.toString();
    }
}
