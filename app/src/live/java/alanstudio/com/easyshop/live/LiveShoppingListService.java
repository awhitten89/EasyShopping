package alanstudio.com.easyshop.live;


import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.infastructure.EasyShoppingApplication;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.services.ShoppingListServices;

public class LiveShoppingListService extends BaseLiveServices{


    public LiveShoppingListService(EasyShoppingApplication application) {
        super(application);
    }

    @Subscribe
    public void AddShoppingList(ShoppingListServices.AddShoppingListRequest request) {
        ShoppingListServices.AddShoppingListResponse response = new ShoppingListServices.AddShoppingListResponse();

        if (request.shoppingListName.isEmpty()){
            response.setPropertyErrors("listname", "Shopping list must have a name");
        }

        if (response.didSucceed()){

            DatabaseReference reference = FirebaseDatabase.getInstance()
                    .getReference().child(Utils.FIREBASE_SHOPPING_LIST_REFERENCE).child(request.ownerEmail).push();
            HashMap<String,Object> timeStampCreated = new HashMap<>();
            timeStampCreated.put("timeStamp", ServerValue.TIMESTAMP);

            //create shopping list instance
            ShoppingList shoppingList = new ShoppingList(reference.getKey(),request.shoppingListName,
                    Utils.decodeEmail(request.ownerEmail),request.ownerName,timeStampCreated);

            //save instance to database
            reference.child("id").setValue(shoppingList.getId());
            reference.child("listName").setValue(shoppingList.getListName());
            reference.child("ownerEmail").setValue(shoppingList.getOwnerEmail());
            reference.child("ownerName").setValue(shoppingList.getOwnerName());
            reference.child("dateCreated").setValue(shoppingList.getDateCreated());
            reference.child("dateLastChanged").setValue(shoppingList.getDateLastChanged());

            Toast.makeText(application.getApplicationContext(), "List created", Toast.LENGTH_LONG).show();
        }

        bus.post(response);

    }

    @Subscribe
    public void DeleteShoppingList(ShoppingListServices.DeleteShoppingListRequest request) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE + request.ownerEmail
                        + "/" + request.shoppingListId
        );

        DatabaseReference itemRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_ITEM_REFERENCE + request.shoppingListId
        );

        DatabaseReference sharedWithRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHARE_LIST_REF + request.shoppingListId
        );

        reference.removeValue();
        itemRef.removeValue();
        sharedWithRef.removeValue();
    }

    @Subscribe
    public void ChangeListName(ShoppingListServices.ChangeListNameRequest request) {
        ShoppingListServices.ChangeListNameResponse response = new ShoppingListServices.ChangeListNameResponse();

        if (request.newShoppingListName.isEmpty()) {
            response.setPropertyErrors("listName", "Shopping List must have a name");
        }

        if (response.didSucceed()) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                    Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE + request.shoppingListOwnerEmail
                            + "/" + request.shoppingListId
            );
            HashMap<String,Object> timeLastChanged = new HashMap<>();
            timeLastChanged.put("date",ServerValue.TIMESTAMP);

            //update a database using a map
            Map newListData = new HashMap();
            newListData.put("listName", request.newShoppingListName);
            newListData.put("dateLastChanged", timeLastChanged);
            reference.updateChildren(newListData);
        }

        bus.post(response);
    }

    @Subscribe
    public void getCurrentShoppingList(final ShoppingListServices.GetCurrentShoppingListRequest request) {
        final ShoppingListServices.GetCurrentShoppingListResponse response = new ShoppingListServices.GetCurrentShoppingListResponse();

        response.valueEventListener = request.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                response.shoppingList = dataSnapshot.getValue(ShoppingList.class);
                if (response.shoppingList != null) {
                    bus.post(response);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(application.getApplicationContext(), "Firebase Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void UpdateListTimeLastChanged(ShoppingListServices.UpdateListTimeLastChangedRequest request){

        HashMap<String,Object> timeLastChanged = new HashMap<>();
        timeLastChanged.put("date",ServerValue.TIMESTAMP);

        //update a database using a map
        Map newListData = new HashMap();
        newListData.put("dateLastChanged", timeLastChanged);
        request.reference.updateChildren(newListData);

    }
}
