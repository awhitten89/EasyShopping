package alanstudio.com.easyshop.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.services.ShoppingItemServices;

public class DeleteItemDialogFragment extends BaseDialog implements View.OnClickListener {

    public static final String EXTRA_SHOPPING_LIST_ID = "EXTRA_SHOPPING_LIST_ID";
    public static final String EXTRA_SHOPPING_ITEM_ID = "EXTRA_SHOPPING_ITEM_ID";
    public static final String EXTRA_BOOLEAN = "EXTRA_BOOLEAN";

    private String mShoppingListId;
    private String mShoppingItemId;
    private boolean mIsClicked;

    public static DeleteItemDialogFragment newInstance(String shoppingListId, String shoppingItemId, boolean isClicked) {

        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SHOPPING_LIST_ID, shoppingListId);
        arguments.putString(EXTRA_SHOPPING_ITEM_ID, shoppingItemId);
        arguments.putBoolean(EXTRA_BOOLEAN, isClicked);

        DeleteItemDialogFragment dialogFragment = new DeleteItemDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mShoppingListId = getArguments().getString(EXTRA_SHOPPING_LIST_ID);
        mShoppingItemId = getArguments().getString(EXTRA_SHOPPING_ITEM_ID);
        mIsClicked = getArguments().getBoolean(EXTRA_BOOLEAN);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_item,null))
                .setPositiveButton("Confirm",null)
                .setNegativeButton("Cancel",null)
                .setTitle("Delete List Item")
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);
        return alertDialog;
    }

    @Override
    public void onClick(View view) {

        if (mIsClicked) {
            dismiss();
            bus.post(new ShoppingItemServices.DeleteItemRequest(mShoppingListId, mShoppingItemId));
        }
    }
}
