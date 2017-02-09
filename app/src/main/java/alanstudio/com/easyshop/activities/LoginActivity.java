package alanstudio.com.easyshop.activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.R2;
import alanstudio.com.easyshop.infastructure.Utils;
import alanstudio.com.easyshop.services.AccountServices;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class LoginActivity extends BaseActivity {

    @BindView(R2.id.activity_login_linear_layout)
    LinearLayout linearLayout;

    @BindView(R2.id.activity_login_registerButton)
    Button registerButton;

    @BindView(R2.id.activity_login_loginButton)
    Button loginButton;

    @BindView(R2.id.activity_login_userEmail)
    EditText userEmail;

    @BindView(R2.id.activity_login_userPassword)
    EditText userPassword;

    @BindView(R.id.activity_login_fbLogin)
    LoginButton FacebookButton;

    private ProgressDialog mProgressDialog;

    private CallbackManager mCallbackManager;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        linearLayout.setBackgroundResource(R.drawable.background_screen);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading......");
        mProgressDialog.setMessage("Attempting to Log In");
        mProgressDialog.setCancelable(false);

        sharedPreferences = getSharedPreferences(Utils.MY_PREFERENCE, Context.MODE_PRIVATE);
    }

    @OnClick(R2.id.activity_login_registerButton)
    public void setRegisterButton(){
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    @OnClick(R2.id.activity_login_loginButton)
    public void setLoginButton(){
        bus.post(new AccountServices.LogUserInRequest(userEmail.getText().toString(),
                userPassword.getText().toString(), mProgressDialog, sharedPreferences));
    }

    @OnClick(R.id.activity_login_fbLogin)
    public void setFacebookButton(){
        mCallbackManager = CallbackManager.Factory.create();
        FacebookButton.setReadPermissions("email","public_profile");

        FacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    bus.post(new AccountServices.LoginUserInFacebookRequest(loginResult.getAccessToken(),
                                            mProgressDialog, name, email, sharedPreferences));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "An unknown error occurred", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void LogUserIn(AccountServices.LoginUserResponse response){

        if (!response.didSucceed()){
            userEmail.setError(response.getPropertyErrors("email"));
            userPassword.setError(response.getPropertyErrors("password"));

        }
    }
}
