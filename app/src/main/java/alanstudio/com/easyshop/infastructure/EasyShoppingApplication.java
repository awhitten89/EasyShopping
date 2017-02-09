package alanstudio.com.easyshop.infastructure;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseApp;
import com.squareup.otto.Bus;

import alanstudio.com.easyshop.live.Module;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by alanwhitten on 29/11/2016.
 *
 * Singleton base application allowing activities and dialogs access to this bus.
 * This will be used for all server calls
 */
public class EasyShoppingApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "7PCtI3OHFMnTkNOreTGypLU1p";
    private static final String TWITTER_SECRET = "9QxwKj8k6ZIQRrQikp4gx3tFMz2VRL2VE0ac3GOTSV49dslsy3";


    private Bus bus;

    public EasyShoppingApplication() {
        bus = new Bus();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        Module.Register(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

    public Bus getBus() {
        return bus;
    }
}
