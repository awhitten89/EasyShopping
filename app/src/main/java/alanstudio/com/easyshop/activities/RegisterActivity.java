package alanstudio.com.easyshop.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;

import alanstudio.com.easyshop.R;
import alanstudio.com.easyshop.R2;
import alanstudio.com.easyshop.services.AccountServices;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @BindView(R2.id.activity_register_linear_layout)
    LinearLayout linearLayout;

    @BindView(R2.id.activity_register_registerButton)
    Button register;

    @BindView(R2.id.activity_register_backButton)
    Button backToLogin;

    @BindView(R2.id.activity_register_userEmail)
    EditText userEmail;

    @BindView(R2.id.activity_register_userName)
    EditText userName;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        linearLayout.setBackgroundResource(R.drawable.background_screen);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading......");
        mProgressDialog.setMessage("Attempting to Register Account");
        mProgressDialog.setCancelable(false);

    }

    @OnClick(R2.id.activity_register_backButton)
    public void setBackToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @OnClick(R2.id.activity_register_registerButton)
    public void setRegister(){
        bus.post(new AccountServices.RegisterUserRequest(userName.getText().toString(), userEmail.getText().toString(), mProgressDialog));
    }

    @Subscribe
    public void RegisterUser(AccountServices.RegisterUserResponse response) {

        if (!response.didSucceed()){
            userEmail.setError(response.getPropertyErrors("email"));
            userName.setError(response.getPropertyErrors("username"));
        }
    }

}
