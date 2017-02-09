package alanstudio.com.easyshop.live;


import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import alanstudio.com.easyshop.entities.ShoppingListItem;
import alanstudio.com.easyshop.infastructure.EasyShoppingApplication;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.services.ShoppingItemServices;

public class LiveShoppingItemService extends BaseLiveServices {

    public LiveShoppingItemService(EasyShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void addItemToList(ShoppingItemServices.AddItemRequest request) {
        ShoppingItemServices.AddItemResponse response = new ShoppingItemServices.AddItemResponse();

        if (request.itemName.isEmpty()) {
            response.setPropertyErrors("itemname", "Shopping item must have a name");
        }

        if (response.didSucceed()){

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                    .child(Utils.FIREBASE_SHOPPING_ITEM_REFERENCE)
                    .child(request.shoppingListId).push();

            DatabaseReference listReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE + request.ownerEmail + "/" + request.shoppingListId
            );

            HashMap<String,Object> timeStampCreated = new HashMap<>();
            timeStampCreated.put("timeStamp", ServerValue.TIMESTAMP);

            ShoppingListItem shoppingListItem = new ShoppingListItem(reference.getKey(), request.itemName, request.ownerEmail,
                    "", false, timeStampCreated);

            reference.child("id").setValue(shoppingListItem.getId());
            reference.child("itemName").setValue(shoppingListItem.getItemName());
            reference.child("ownerEmail").setValue(shoppingListItem.getOwnerEmail());
            reference.child("boughtBy").setValue(shoppingListItem.getBoughtBy());
            reference.child("bought").setValue(shoppingListItem.isBought());
            reference.child("dateCreated").setValue(shoppingListItem.getDateCreated());

            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date", ServerValue.TIMESTAMP);

            //update a database using a map
            Map newListData = new HashMap();
            newListData.put("dateLastChanged", timeLastChanged);
            listReference.updateChildren(newListData);
        }

        bus.post(response);
    }

    @Subscribe
    public void ChangeItemName(ShoppingItemServices.ChangeItemNameRequest request) {
        ShoppingItemServices.ChangeItemNameResponse response = new ShoppingItemServices.ChangeItemNameResponse();

        if (request.newItemName.isEmpty()) {
            response.setPropertyErrors("newitemname", "Item must have a name");
        }

        if (response.didSucceed()) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId + "/" +
                            request.shoppingItemId
            );

            DatabaseReference listReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE + request.ownerEmail + "/" + request.shoppingListId
            );

            //update item name
            Map newItemdata = new HashMap();
            newItemdata.put("itemName", request.newItemName);
            reference.updateChildren(newItemdata);

            //update shopping list last update time
            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date", ServerValue.TIMESTAMP);
            Map newListData = new HashMap();
            newListData.put("dateLastChanged", timeLastChanged);
            listReference.updateChildren(newListData);

        }
        bus.post(response);
    }

    @Subscribe
    public void DeleteItem(ShoppingItemServices.DeleteItemRequest request) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId + "/"
                        + request.shoppingItemId
        );

        reference.removeValue();
    }

    @Subscribe
    public void ItemBoughtStatus(ShoppingItemServices.ItemBoughtStatusRequest request) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId + "/"
                        + request.item.getId()
        );

        if (!request.item.isBought()) {
            Map newItemdata = new HashMap();
            newItemdata.put("boughtBy", request.currentUserEmail);
            newItemdata.put("bought",true);
            reference.updateChildren(newItemdata);
        } else if (request.item.getBoughtBy().equals(request.currentUserEmail)) {
            Map newItemdata = new HashMap();
            newItemdata.put("boughtBy", "");
            newItemdata.put("bought",false);
            reference.updateChildren(newItemdata);
        }
    }
}
