/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chattr;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import javax.ws.rs.GET;
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
public class ChattrService {
    
    @GET
    @Path("{roomId}")
    @Produces("application/json")
    public Response doGet(@PathParam("roomId") int id) {
        return Response.ok(getResults("SELECT * FROM product WHERE roomId = ?", String.valueOf(id)), MediaType.APPLICATION_JSON).build();
    }
    
    private String getResults(String query, String... params) {
        String result = "";
        
        try (Connection conn = Credentials.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(query);
            
            for (int i = 1; i <= params.length; i++) {
                pstmt.setString(i, params[i - 1]);
            }
            
            ResultSet rs = pstmt.executeQuery();
            StringWriter out = new StringWriter();
            JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
            JsonGenerator gen = factory.createGenerator(out);
            
            gen.writeStartArray();
            while (rs.next()) {
                gen.writeStartObject()
                    .write("productId", rs.getInt("productID"))
                    .write("name", rs.getString("name"))
                    .write("description", rs.getString("description"))
                    .write("quantity", rs.getInt("quantity"))
                    .writeEnd();
            }
            gen.writeEnd();
            gen.close();
            result = out.toString();
            
        } catch (SQLException ex) {
            Logger.getLogger(ChattrService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
