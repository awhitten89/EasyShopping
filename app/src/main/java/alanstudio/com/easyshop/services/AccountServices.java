package alanstudio.com.easyshop.services;


import android.app.ProgressDialog;
import android.content.SharedPreferences;

import com.facebook.AccessToken;

import alanstudio.com.easyshop.infastructure.ServiceResponse;

public class AccountServices {

    private AccountServices() {
    }

    public static class RegisterUserRequest {

        public String userName;
        public String userEmail;
        public ProgressDialog progressDialog;

        public RegisterUserRequest(String userName, String userEmail, ProgressDialog progressDialog) {
            this.userName = userName;
            this.userEmail = userEmail;
            this.progressDialog = progressDialog;
        }
    }

    public static class RegisterUserResponse extends ServiceResponse {

    }

    public static class LogUserInRequest {
        public String userEmail;
        public String userPassword;
        public ProgressDialog progressDialog;
        public SharedPreferences sharedPreferences;

        public LogUserInRequest(String userEmail, String userPassword, ProgressDialog progressDialog, SharedPreferences sharedPreferences) {
            this.userEmail = userEmail;
            this.userPassword = userPassword;
            this.progressDialog = progressDialog;
            this.sharedPreferences = sharedPreferences;
        }
    }

    public static class LoginUserResponse extends ServiceResponse {

    }

    public static class LoginUserInFacebookRequest {
        public AccessToken accessToken;
        public ProgressDialog progressDialog;
        public String userName;
        public String userEmail;
        public SharedPreferences sharedPreferences;

        public LoginUserInFacebookRequest(AccessToken accessToken, ProgressDialog progressDialog, String userName, String userEmail, SharedPreferences sharedPreferences) {
            this.accessToken = accessToken;
            this.progressDialog = progressDialog;
            this.userName = userName;
            this.userEmail = userEmail;
            this.sharedPreferences = sharedPreferences;
        }
    }
}
