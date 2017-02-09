package alanstudio.com.easyshop.dialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.squareup.otto.Bus;

import alanstudio.com.easyshop.infastructure.EasyShoppingApplication;
import alanstudio.com.easyshop.infastructure.Utils;

/**
 * Created by alanwhitten on 29/11/2016.
 *
 * All dialogs will extend this base dialog
 */
public class BaseDialog extends DialogFragment {

    protected EasyShoppingApplication application;
    protected Bus bus;
    protected String userEmail, userName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (EasyShoppingApplication) getActivity().getApplication();
        bus = application.getBus();
        bus.register(this);

        //make username and email available to all dialog fragments
        userEmail = getActivity().getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE).getString(Utils.EMAIL,"");
        userName = getActivity().getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE).getString(Utils.USERNAME,"");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }
}
