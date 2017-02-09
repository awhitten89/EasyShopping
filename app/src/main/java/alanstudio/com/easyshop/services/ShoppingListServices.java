package alanstudio.com.easyshop.services;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.infastructure.ServiceResponse;

public class ShoppingListServices {


    private ShoppingListServices() {
    }

    public static class AddShoppingListRequest {

        public String shoppingListName;
        public String ownerName;
        public String ownerEmail;

        public AddShoppingListRequest(String shoppingListName, String ownerName, String ownerEmail) {
            this.shoppingListName = shoppingListName;
            this.ownerName = ownerName;
            this.ownerEmail = ownerEmail;
        }
    }

    public static class AddShoppingListResponse extends ServiceResponse {

    }

    public static class DeleteShoppingListRequest {

        public String ownerEmail;
        public String shoppingListId;

        public DeleteShoppingListRequest(String ownerEmail, String shoppingListId) {
            this.ownerEmail = ownerEmail;
            this.shoppingListId = shoppingListId;
        }
    }

    public static class ChangeListNameRequest {

        public String newShoppingListName;
        public String shoppingListId;
        public String shoppingListOwnerEmail;

        public ChangeListNameRequest(String newShoppingListName, String shoppingListId, String shoppingListOwnerEmail) {
            this.newShoppingListName = newShoppingListName;
            this.shoppingListId = shoppingListId;
            this.shoppingListOwnerEmail = shoppingListOwnerEmail;
        }
    }

    public static class ChangeListNameResponse extends ServiceResponse {

    }

    public static class GetCurrentShoppingListRequest {

        public DatabaseReference reference;

        public GetCurrentShoppingListRequest(DatabaseReference reference) {
            this.reference = reference;
        }
    }

    public static class GetCurrentShoppingListResponse {
        public ShoppingList shoppingList;
        public ValueEventListener valueEventListener;
    }

    public static class UpdateListTimeLastChangedRequest {

        public DatabaseReference reference;

        public UpdateListTimeLastChangedRequest(DatabaseReference reference) {
            this.reference = reference;
        }
    }
}

