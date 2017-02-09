package alanstudio.com.easyshop.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Bus;

import alanstudio.com.easyshop.infastructure.EasyShoppingApplication;
import alanstudio.com.easyshop.infastructure.Utils;

/**
 * Created by alanwhitten on 29/11/2016.
 *
 * All activites will extend this base activity
 */
public class BaseActivity extends AppCompatActivity {

    protected EasyShoppingApplication application;
    protected Bus bus;

    //normally we don't have server code inside our ui package but in the case of checking
    //if the user is logged in or not we have to. Due to auth having to be set up right away and that it takes to long in the bus.
    protected FirebaseAuth auth;
    protected FirebaseAuth.AuthStateListener authStateListener;

    protected String userEmail, userName;
    protected SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (EasyShoppingApplication) getApplication();
        bus = application.getBus();
        bus.register(this);

        sharedPreferences = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(Utils.USERNAME,"");
        userEmail = sharedPreferences.getString(Utils.EMAIL,"");

        auth = FirebaseAuth.getInstance();

        //we want the user to be authentiacted in our app but not in these activities
        if (!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))){
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = auth.getCurrentUser();
                    if (user == null) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Utils.EMAIL,null).apply();
                        editor.putString(Utils.USERNAME,null).apply();
                        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        finish();

                    }
                }
            };

            if (userEmail.equals("")) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Utils.EMAIL, null).apply();
                editor.putString(Utils.USERNAME, null).apply();
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))){
            //set up
            auth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bus.unregister(this);

        if (!((this instanceof LoginActivity) || (this instanceof RegisterActivity) || (this instanceof SplashScreenActivity))){
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
