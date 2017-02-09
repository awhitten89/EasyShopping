package alanstudio.com.easyshop.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.services.ShoppingItemServices;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChangeItemNameDialogFragment extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.dialog_change_item_name_editText)
    EditText newItemName;

    public static final String SHOPPING_ITEM_EXTRA_INFO =  "SHOPPING_ITEM_EXTRA_INFO";

    private String mShoppingListId;
    private String mItemId;
    private String mCurrentItemName;

    public static ChangeItemNameDialogFragment newInstance(ArrayList<String> shoppingItemInfo) {

        Bundle arguments = new Bundle();
        arguments.putStringArrayList(SHOPPING_ITEM_EXTRA_INFO, shoppingItemInfo);
        ChangeItemNameDialogFragment dialogFragment = new ChangeItemNameDialogFragment();
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShoppingListId = getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO).get(0);
        mItemId = getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO).get(1);
        mCurrentItemName = getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO).get(2);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_item_name, null);
        ButterKnife.bind(this, rootView);
        newItemName.setText(getArguments().getStringArrayList(SHOPPING_ITEM_EXTRA_INFO).get(2));

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Change Name", null)
                .setNegativeButton("Cancel", null)
                .setTitle("Change Shopping Item Name")
                .show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(this);

        return alertDialog;
    }

    @Override
    public void onClick(View view) {

        bus.post(new ShoppingItemServices.ChangeItemNameRequest(newItemName.getText().toString(),mShoppingListId,mItemId,userEmail));
    }

    @Subscribe
    public void ChangeNameRequest(ShoppingItemServices.ChangeItemNameResponse response) {

        if (!response.didSucceed()){
            newItemName.setError(response.getPropertyErrors("newitemname"));
        } else {
            dismiss();
        }
    }
}
