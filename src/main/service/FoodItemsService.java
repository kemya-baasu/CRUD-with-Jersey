package main.service;

import main.utility.FoodItemUtils;
import main.pojo.FoodItem;

import javax.ws.rs.core.*;
import java.sql.*;
import java.util.*;

public class FoodItemsService {
    Connection c;

    static FoodItemsService instance;
    FoodItemUtils foodItemUtils =new FoodItemUtils();
    public static FoodItemsService getInstance() {
        if (instance == null) {
            instance = new FoodItemsService();
        }
        return instance;
    }

    private FoodItemsService() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost/Zwigo", "postgres", "postgres");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<FoodItem> getFoodItemsWithFilter(@Context UriInfo uriInfo){
        MultivaluedMap<String,String> queryParams=uriInfo.getQueryParameters();
        List<String> keys=new ArrayList<>(Arrays.asList("name","price","quantity","type","availability","group"));
        HashMap<String,String> search=new HashMap<>();
        for (String paramName : keys) {
            if(queryParams.containsKey(paramName)) {
                List<String> values = queryParams.get(paramName);
                for (String s : values) {
                    System.out.println(s + "---" + paramName);
                    search.put(paramName, s);
                    break;
                }
            }
        }
        try {
           PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM food_items fi left join food_item_groups fig on fi.group_id =fig.group_id where is_deleted='No'  AND (? = '%' OR fig.group_name ILIKE ?) and item_name ilike ? and price::text ilike ? and quantity::text ilike ? and item_type ilike ? and is_available ilike ?  ORDER BY item_id ASC");
            preparedStatement.setString(1,search.containsKey("group")?"%"+search.get("group")+"%"  :"%");
            preparedStatement.setString(2,search.containsKey("group")?"%"+search.get("group")+"%"  :"%");
            preparedStatement.setString(3,search.containsKey("name")?"%"+search.get("name")+"%" :"%");
            preparedStatement.setString(4,search.containsKey("price")?"%"+search.get("price")+"%":"%");
            preparedStatement.setString(5,search.containsKey("quantity")?"%"+search.get("quantity")+"%":"%");
            preparedStatement.setString(6,search.containsKey("type")?search.get("type"):"%");
            preparedStatement.setString(7,search.containsKey("availability")?"%"+search.get("availability")+"%":"%");
            System.out.println(preparedStatement);
            foodItemUtils.clearFoodItems();
            foodItemUtils.setFoodItems(preparedStatement);
        } catch (Exception e) {
            System.out.println("Exception in getFoodItemList() of FoodItemsService Class");
        }
        return foodItemUtils.getFoodItems();
    }

    public void addFoodItem(List<FoodItem> items){
        for (FoodItem item:items) {
            foodItemUtils.CreateNewItem(item);
        }
    }
    public void updateFoodItem(FoodItem item,int id) {
        foodItemUtils.updateFoodItems(item,id);
    }

    public void deleteItemById(int id){
        foodItemUtils.deleteById(id);
    }

    public void associateGroupToItem(int itemId, String groupId) {
        foodItemUtils.addItemToGroup(itemId,groupId);
    }
    public void deassociateGroupToItem(int itemId) {
        foodItemUtils.removeItemFromGroup(itemId);
    }
}
