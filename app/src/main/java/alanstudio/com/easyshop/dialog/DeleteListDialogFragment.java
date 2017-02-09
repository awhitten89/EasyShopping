package alanstudio.com.easyshop.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.activities.MainActivity;
import alanstudio.com.easyshop.entities.Friend;
import alanstudio.com.easyshop.entities.SharedList;
import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.entities.User;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.services.ShareListServices;
import alanstudio.com.easyshop.services.ShoppingItemServices;
import alanstudio.com.easyshop.services.ShoppingListServices;

public class DeleteListDialogFragment extends BaseDialog implements View.OnClickListener {

    public static final String EXTRA_SHOPPING_LIST_ID = "EXTRA_SHOPPING_LIST_ID";
    public static final String EXTRA_BOOLEAN = "EXTRA_BOOLEAN";

    private String mShoppingListId;
    private boolean mIsLongClicked;

    private ValueEventListener sharedListener;
    private SharedList sharedWith;
    private DatabaseReference sharedListReference;

    public static DeleteListDialogFragment newInstance(String shoppingListId, boolean isLongClicked) {

        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SHOPPING_LIST_ID, shoppingListId);
        arguments.putBoolean(EXTRA_BOOLEAN, isLongClicked);

        DeleteListDialogFragment dialogFragment = new DeleteListDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShoppingListId = getArguments().getString(EXTRA_SHOPPING_LIST_ID);
        mIsLongClicked = getArguments().getBoolean(EXTRA_BOOLEAN);

        //call to check if list has been shared with a friend
        sharedListReference = FirebaseDatabase.getInstance().getReferenceFromUrl(
                Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHARE_LIST_REF + mShoppingListId
        );
        bus.post(new ShareListServices.GetShareListRequest(sharedListReference));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_list, null))
                .setPositiveButton("Confirm", null)
                .setNegativeButton("Cancel",null)
                .setTitle("Delete Shopping List")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return dialog;
    }

    @Override
    public void onClick(View view) {

        if (mIsLongClicked) {
            dismiss();
            deleteSharedShoppingList(sharedWith.getSharedWith(),bus,mShoppingListId,userEmail);
            Toast.makeText(getActivity().getApplicationContext(),"List Deleted by " + Utils.decodeEmail(userEmail), Toast.LENGTH_LONG).show();
        } else {
            dismiss();
            getActivity().finish();
            deleteSharedShoppingList(sharedWith.getSharedWith(),bus,mShoppingListId,userEmail);
            Toast.makeText(getActivity().getApplicationContext(),"List Deleted by " + Utils.decodeEmail(userEmail), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void getSharedList(ShareListServices.GetShareListResponse response) {
        sharedListener = response.listener;

        if (response.sharedList != null) {
            sharedWith = response.sharedList;
        } else {
            sharedWith = new SharedList();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sharedListReference.removeEventListener(sharedListener);
    }

    public static void deleteSharedShoppingList(HashMap<String,User> sharedList, Bus bus, String shoppingListId,
                                                String ownerEmail) {
        
        if (sharedList != null && !sharedList.isEmpty()) {
            for (User user : sharedList.values()) {
                if (sharedList.containsKey(Utils.encodeEmail(user.getEmail()))){

                    DatabaseReference sharedListRef = FirebaseDatabase.getInstance().getReferenceFromUrl(
                            Utils.FIREBASE_BASE_URL + Utils.FIREBASE_SHOPPING_LIST_REFERENCE +
                                    Utils.encodeEmail(user.getEmail()) + "/" + shoppingListId
                    );
                    sharedListRef.removeValue();
                }
            }
        }
        bus.post(new ShoppingListServices.DeleteShoppingListRequest(ownerEmail, shoppingListId));
    }
}
