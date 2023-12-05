package main.filter;

import main.annotation.ResourceAuthenticator;
import main.pojo.*;

import javax.annotation.*;
import javax.ws.rs.*;
import javax.ws.rs.container.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.sql.*;
import java.util.Base64;

@Provider
@ResourceAuthenticator
@Priority(Priorities.AUTHENTICATION)

public class Authenticate implements ContainerRequestFilter {
    private static final String AUTHENTICATION_SCHEME = "Basic";

    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        String authorizationHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if(authorizationHeader==null||!authorizationHeader.startsWith(AUTHENTICATION_SCHEME+" ")){
            abortWithUnauthorized(ctx);
            return;
        }
        String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        String credentialObj = new String(Base64.getDecoder().decode(token));
        String[] credential=credentialObj.split(":");
        try {
           User user= validateToken(credential[0],credential[1]);
           if(user!=null){
               ctx.setSecurityContext(new AuthenticationSecurityContext(user));
           }
           else{
               abortWithUnauthorized(ctx);
           }
        } catch (Exception e) {
            abortWithUnauthorized(ctx);
        }
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
    }

    private User validateToken(String username, String password) throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
            Connection c = DriverManager.getConnection("jdbc:postgresql://localhost/Zwigo", "****", "*****");
            PreparedStatement p=c.prepareStatement("select * from token_based_authentication where username=? and password=?");
            p.setString(1,username);
            p.setString(2,password);
            ResultSet rs=p.executeQuery();
            rs.next();
            User user=new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(rs.getString("role"));
            return user;

        } catch (Exception e) {
           return null;
        }
    }
}
