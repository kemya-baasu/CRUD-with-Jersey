package main.api;

import main.pojo.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Base64;

@Path("/access-token")
public class Authentication {
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateToken(User credentials) {

        String username = credentials.getUsername();
        String password = credentials.getPassword();
        System.out.println(username+"--"+password);
        String token="";

        if(username.equals("1")&&password.equals("1")){
            String credential = username + ":" + password;
            token = Base64.getEncoder().encodeToString(credential.getBytes());
        }
        if(token.isEmpty()){
           return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        else {
          return Response.status(200).entity(token).build();
        }
    }
}
