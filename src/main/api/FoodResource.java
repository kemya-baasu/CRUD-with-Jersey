package main.api;

import main.pojo.FoodItem;
import main.service.FoodItemsService;
import main.annotation.ResourceAuthenticator;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("api/food-items")
@RolesAllowed("Admin")
public class FoodResource {
    FoodItemsService utility= FoodItemsService.getInstance();
    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @ResourceAuthenticator
    public List<FoodItem> getFoodItems(@Context SecurityContext securityContext,@Context UriInfo uriInfo) {
        System.out.println("Username: "+securityContext.getUserPrincipal().getName());
        return utility.getFoodItemsWithFilter(uriInfo);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response addItem(List<FoodItem> items){
        utility.addFoodItem(items);
        return Response.status(201).build();
    }

    @Path("/{id}")
    @PUT
    @Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response updateItem(@PathParam("id") int id, FoodItem item){
        utility.updateFoodItem(item,id);
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @Path("/{id}")
    @DELETE
    public Response deleteItem(@PathParam("id") int id){
        utility.deleteItemById(id);
        return Response.status(Response.Status.OK).build();
    }

    @Path("/{id}/group/associate")
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public Response associateGroup(@PathParam("id") int itemId,String groupId){
        System.out.println("GroupId---"+groupId);
        utility.associateGroupToItem(itemId,groupId);
        return Response.status(Response.Status.OK).build();
    }

    @Path("/{id}/group/deassociate")
    @PUT
    public Response deassociateGroup(@PathParam("id") int itemId){
        utility.deassociateGroupToItem(itemId);
        return Response.status(Response.Status.OK).build();
    }
}
