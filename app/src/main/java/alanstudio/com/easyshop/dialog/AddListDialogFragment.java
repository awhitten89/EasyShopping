package alanstudio.com.easyshop.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.squareup.otto.Subscribe;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.entities.ShoppingList;
import alanstudio.com.easyshop.services.ShoppingListServices;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddListDialogFragment extends BaseDialog implements View.OnClickListener {

    @BindView(R.id.dialog_list_add_editText)
    EditText newListName;

    public static AddListDialogFragment newInstance(){
        return new AddListDialogFragment();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View rootView = layoutInflater.inflate(R.layout.dialog_add_list,null);
        ButterKnife.bind(this,rootView);

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setView(rootView)
                .setPositiveButton("Create",null)
                .setNegativeButton("Cancel",null)
                .show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return alertDialog;
    }

    @Override
    public void onClick(View view) {

        bus.post(new ShoppingListServices.AddShoppingListRequest(newListName.getText().toString(), userName, userEmail));
    }

    @Subscribe
    public void AddShoppingList(ShoppingListServices.AddShoppingListResponse response){
        if (!response.didSucceed()){
            newListName.setError(response.getPropertyErrors("itemname"));
        } else {
            dismiss();
        }
    }
}
