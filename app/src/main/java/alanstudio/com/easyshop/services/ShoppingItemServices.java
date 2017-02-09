package alanstudio.com.easyshop.services;


import alanstudio.com.easyshop.entities.ShoppingListItem;
import alanstudio.com.easyshop.infastructure.ServiceResponse;

public class ShoppingItemServices {

    public ShoppingItemServices() {
    }

    public static class AddItemRequest {

        public String itemName;
        public String ownerEmail;
        public String shoppingListId;


        public AddItemRequest(String itemName, String ownerEmail, String shoppingListId) {
            this.itemName = itemName;
            this.ownerEmail = ownerEmail;
            this.shoppingListId = shoppingListId;
        }
    }

    public static class AddItemResponse extends ServiceResponse {

    }

    public static class ChangeItemNameRequest {

        public String newItemName;
        public String shoppingListId;
        public String shoppingItemId;
        public String ownerEmail;

        public ChangeItemNameRequest(String newItemName, String shoppingListId, String shoppingItemId, String ownerEmail) {
            this.newItemName = newItemName;
            this.shoppingListId = shoppingListId;
            this.shoppingItemId = shoppingItemId;
            this.ownerEmail = ownerEmail;
        }
    }

    public static class ChangeItemNameResponse extends ServiceResponse {

    }

    public static class DeleteItemRequest {

        public String shoppingListId;
        public String shoppingItemId;

        public DeleteItemRequest(String shoppingListId, String shoppingItemId) {
            this.shoppingListId = shoppingListId;
            this.shoppingItemId = shoppingItemId;
        }
    }

    public static class ItemBoughtStatusRequest {

        public ShoppingListItem item;
        public String currentUserEmail;
        public String shoppingListId;

        public ItemBoughtStatusRequest(ShoppingListItem item, String currentUserEmail, String shoppingListId) {
            this.item = item;
            this.currentUserEmail = currentUserEmail;
            this.shoppingListId = shoppingListId;
        }
    }
}
