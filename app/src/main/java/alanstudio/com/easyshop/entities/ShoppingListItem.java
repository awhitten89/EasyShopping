package alanstudio.com.easyshop.entities;

import java.util.HashMap;

public class ShoppingListItem {

    private String id;
    private String itemName;
    private String ownerEmail;
    private String boughtBy;
    private boolean bought;
    private HashMap<String, Object> dateCreated;

    public ShoppingListItem() {
    }

    public ShoppingListItem(String id, String itemName, String ownerEmail, String boughtBy, boolean bought, HashMap<String, Object> dateCreated) {
        this.id = id;
        this.itemName = itemName;
        this.ownerEmail = ownerEmail;
        this.boughtBy = boughtBy;
        this.bought = bought;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getBoughtBy() {
        return boughtBy;
    }

    public boolean isBought() {
        return bought;
    }

    public HashMap<String, Object> getDateCreated() {
        return dateCreated;
    }
}



