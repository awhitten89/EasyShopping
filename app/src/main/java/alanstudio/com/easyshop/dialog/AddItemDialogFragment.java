package alanstudio.com.easyshop.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.services.ShoppingItemServices;
import alanstudio.com.easyshop.services.ShoppingListServices;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddItemDialogFragment extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.dialog_item_add_editText)
    EditText newItemName;

    private static final String LISTID = "LISTID";

    private String mShoppingList;

    public static AddItemDialogFragment newInstance(String listId) {

        Bundle arguments = new Bundle();
        arguments.putString(LISTID, listId);
        AddItemDialogFragment dialogFragment = new AddItemDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShoppingList = getArguments().getString(LISTID);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_shopping_item, null);
        ButterKnife.bind(this,rootView);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Confirm",null)
                .setNegativeButton("Cancel",null)
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return alertDialog;
    }

    @Override
    public void onClick(View view) {

        bus.post(new ShoppingItemServices.AddItemRequest(newItemName.getText().toString(),userEmail, mShoppingList));
    }

    @Subscribe
    public void AddShoppingItem(ShoppingItemServices.AddItemResponse response){
        if (!response.didSucceed()){
            newItemName.setError(response.getPropertyErrors("itemname"));
        } else {
            dismiss();
        }
    }
}
