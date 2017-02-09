package alanstudio.com.easyshop.live;


import com.google.firebase.auth.FirebaseAuth;
import com.squareup.otto.Bus;

import alanstudio.com.easyshop.infastructure.EasyShoppingApplication;

public class BaseLiveServices {

    protected Bus bus;
    protected EasyShoppingApplication application;
    protected FirebaseAuth auth;

    public BaseLiveServices(EasyShoppingApplication application) {
        this.application = application;
        bus = application.getBus();
        bus.register(this);
        auth = FirebaseAuth.getInstance();
    }
}
