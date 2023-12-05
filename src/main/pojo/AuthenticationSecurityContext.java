package main.pojo;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class AuthenticationSecurityContext implements SecurityContext {
    private User user;

    public AuthenticationSecurityContext(User user) {
        this.user = user;
    }

    @Override
    public Principal getUserPrincipal() {
        return new UserPrincipal(user.getUsername());
    }

    @Override
    public boolean isUserInRole(String role) {
        String userRole = user.getRole();
        return userRole != null && userRole.equals(role);
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public String getAuthenticationScheme() {
        return SecurityContext.BASIC_AUTH;
    }
}

 class UserPrincipal implements Principal{
     String username="";
     public UserPrincipal(String username){
       this.username=username ;
     }
    @Override
    public String getName() {
        return username;
    }
}
