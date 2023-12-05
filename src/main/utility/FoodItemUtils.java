package main.utility;

import main.pojo.FoodItem;

import java.sql.*;
import java.util.ArrayList;

public class FoodItemUtils {
    Connection c;
    ArrayList<FoodItem> foodItems = new ArrayList<FoodItem>();

    public FoodItemUtils() {

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost/Zwigo", "postgres", "postgres");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFoodItems(PreparedStatement preparedStatement) {
        try {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                FoodItem item = new FoodItem();
                item.setItemId(rs.getString("item_id"));
                item.setItemName(rs.getString("item_name"));
                item.setItemType(rs.getString("item_type"));
                item.setIsAvailable(rs.getString("is_available"));
                item.setPrice(rs.getString("price"));
                item.setQuantity(rs.getString("quantity"));
                if(rs.getString("group_id")==null) {
                }
                else {
                    item.setGroupId(rs.getString("group_id"));
                }
                foodItems.add(item);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void CreateNewItem(FoodItem item) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = c.prepareStatement("insert into food_items(item_name,price,quantity,item_type,group_id) values(?,?,?,?,?) returning item_id,is_available");
            preparedStatement.setString(1,item.getItemName());
            preparedStatement.setInt(2, Integer.parseInt(item.getPrice()));
            preparedStatement.setInt(3, Integer.parseInt(item.getQuantity()));
            preparedStatement.setString(4,item.getItemType());
            if(item.getGroupId()!=null) {
                preparedStatement.setInt(5, Integer.parseInt(item.getGroupId()));
            }else{
                preparedStatement.setNull(5, Types.INTEGER);
            }
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            item.setItemId(rs.getString("item_id"));
            item.setIsAvailable(rs.getString("is_available"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateFoodItems(FoodItem item,int id){
        item.setItemId(String.valueOf(id));
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = c.prepareStatement("update food_items set item_name=?,price=?,quantity=?,item_type=?,is_available=?,group_id=? where item_id=?");
            preparedStatement.setString(1,item.getItemName());
            preparedStatement.setInt(2, Integer.parseInt(item.getPrice()));
            preparedStatement.setInt(3, Integer.parseInt(item.getQuantity()));
            preparedStatement.setString(4,item.getItemType());
            preparedStatement.setString(5, item.getIsAvailable());
            if(item.getGroupId()!=null) {
                preparedStatement.setInt(6, Integer.parseInt(item.getGroupId()));
            }else{
                preparedStatement.setNull(6, Types.INTEGER);
            }
            preparedStatement.setInt(7,id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<FoodItem> getFoodItems() {
        System.out.println("Food Items Count: " + foodItems.size());
        return foodItems;
    }

    public void clearFoodItems(){
        foodItems.clear();
    }

    public void deleteById(int id) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = c.prepareStatement("update food_items set is_deleted='Yes' where item_id=?");
            preparedStatement.setInt(1,id);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addItemToGroup(int itemId, String groupId) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = c.prepareStatement("update food_items set group_id=? where item_id=?");
            preparedStatement.setInt(1, Integer.parseInt(groupId));
            preparedStatement.setInt(2,itemId);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error in Group Association");
        }
    }
    public void removeItemFromGroup(int itemId) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = c.prepareStatement("update food_items set group_id=? where item_id=?");
            preparedStatement.setNull(1, Types.INTEGER);
            preparedStatement.setInt(2,itemId);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println("Error in Group Deassociation");
        }
    }
}

