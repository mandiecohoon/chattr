/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattr;

import entities.ChattrRoom;
import entities.ChattrMessages;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
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

    List<ChattrRoom> roomList;
    List<ChattrMessages> messageList;
   
    @Inject
    UserTransaction transaction;
    
    // Get room list
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
    
    // Get messages list
    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") int id) {
        JsonArrayBuilder json = Json.createArrayBuilder();
        Query q = em.createQuery("ChattrMessages.findAllById");
        messageList = q.getResultList();
        for (ChattrMessages m : messageList) {
            json.add(m.toJSON());
        }
        return Response.ok(json.build().toString()).build();
    }
    
    // Post a room
    @POST
    @Consumes("application/json")
    public Response add(JsonObject json) {
        Response result;
        try {
            transaction.begin();
            ChattrRoom r = new ChattrRoom(json);
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
    public Response add(JsonObject json, @PathParam("id") int id) {
        Response result;
        try {
            transaction.begin();
                ChattrMessages m = new ChattrMessages(json);
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
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") int id) {
        Response result;
        try {
            transaction.begin();
            ChattrRoom r = (ChattrRoom) em.createNamedQuery("ChattrRoom.findByRoomId")
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
