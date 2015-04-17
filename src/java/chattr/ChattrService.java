/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattr;

import entities.Message;
import entities.Room;
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
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
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
    public Response getAllRooms() {
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
    public Response getAllMessagesInRoom(@PathParam("id") int id) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        Query q = em.createNamedQuery("ChattrMessages.findByAllId")
                .setParameter("roomId", id);
        messageList = q.getResultList();
        for (Message m : messageList) {
            json.add(m.toJSON());
        }
        return Response.ok(json.build().toString()).build();
    }
    
    // Get one message by its id
    @GET
    @Path("{roomId}/{messageId}")
    public Response getMessageById(@PathParam("roomId") int roomId, @PathParam("messageId") int messageId) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        Query q = em.createNamedQuery("ChattrMessages.findById")
                .setParameter("messageId", messageId);
        messageList = q.getResultList();
        for (Message m : messageList) {
            json.add(m.toJSON());
        }
        return Response.ok(json.build().toString()).build();
    }
    
    // Post a room
    @POST
    @Consumes("application/json")
    public Response addRoom(JsonObject json) throws SQLException {
        Response result;
        
        // Find the Id of the last inserted item and adds one to create the next Id
        Query q = em.createNamedQuery("ChattrRoom.findOne");
        List<Room> autoIdList =  q.setMaxResults(1).getResultList();
        int autoId = 0;
        for (Room r : autoIdList) {
            autoId = r.getRoomId();
        }
        autoId++;
        
        // Creates a new json object with the correct Id
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
    public Response addMessage(JsonObject json, @PathParam("id") int id) throws SQLException {
        Response result;
        
        // Find the Id of the last inserted item and adds one to create the next Id
        Query q = em.createNamedQuery("ChattrMessages.findOne");
        List<Message> autoIdList =  q.setMaxResults(1).getResultList();
        int autoId = 0;
        for (Message m : autoIdList) {
            autoId = m.getMessageId();
        }
        autoId++;
        
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
    public Response editRoom(JsonObject json) {
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
    
    // Update a message by its id
    @PUT
    @Path("{roomId}/{messageId}")
    @Consumes("application/json")
    public Response editMessage(JsonObject json, @PathParam("roomId") int roomId, @PathParam("messageId") int messageId) {
        Response result;
        try {
            transaction.begin();
            Message m = (Message) em.createNamedQuery("ChattrMessages.findById")
                    .setParameter("messageId", messageId)
                    .getSingleResult();
            m.setMessage(json.getString("message"));
            m.setRoomId(roomId);
            em.persist(m);
            transaction.commit();
            result = Response.ok().build();
        } catch (IllegalStateException | SecurityException | HeuristicMixedException | HeuristicRollbackException | NotSupportedException | RollbackException | SystemException ex) {
            result = Response.status(500).entity(ex.getMessage()).build();
        }
        return result;
    }
    
    
    // Delete a room - with all its messages
    @DELETE
    @Path("{id}")
    public Response deleteRoom(@PathParam("id") int id) {
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
    
    // Delete a message by its id
    @DELETE
    @Path("{roomId}/{messageId}")
    public Response deleteMessage(@PathParam("roomId") int roomId, @PathParam("messageId") int messageId) {
        Response result;
        try {
            transaction.begin();
            Message m = (Message) em.createNamedQuery("ChattrMessages.findById")
                    .setParameter("messageId", messageId).getSingleResult();
            em.remove(m);
            transaction.commit();
            result = Response.ok().build();
        } catch (Exception ex) {
            result = Response.status(500).entity(ex.getMessage()).build();
        }
        return result;
    }
    
    //Attempt to add 2 way messaging
    /*
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
    */
}
