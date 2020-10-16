package com.smartwebarts.acrepair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.navigation.Navigation;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.goodiebag.pinview.Pinview;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import com.smartwebarts.acrepair.dashboard.DashboardActivity;
import com.smartwebarts.acrepair.models.OTPModel;
import com.smartwebarts.acrepair.models.SocialDataCheckModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;
import com.smartwebarts.acrepair.shared_preference.LoginData;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignInActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN=1;
//    List<String> permissionNeeds = Arrays.asList("user_photos", "email", "user_birthday", "public_profile", "AccessToken");
    List<String> permissionNeeds = Arrays.asList("email", "public_profile", "user_friends");
    GoogleSignInOptions gso;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private TextView tvMobile, tvPassword;
    private Dialog dialog;

    private String accessToken;
    private String Uid;
    private String Uname;
    private String Uemail;
    private String UimageUrl;
    private String Uphone;

    private String SocialName;
    private String SocialEmail;
    private String SocialImage;
    private Pinview verifyPin;

    private OTPModel data;
    private  String mobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        tvMobile = findViewById(R.id.mobile);
        tvPassword = findViewById(R.id.password);

//        if (getArguments() != null) {
//            data = (OTPModel) getArguments().getSerializable("data");
//            mobileNumber = getArguments().getString("mobile", "");
//        }

        loginButton.setReadPermissions(permissionNeeds);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                accessToken = loginResult.getAccessToken().getToken();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
//                                Uid = object.getString("id");

                                    SocialImage = "http://graph.facebook.com/" + Uid + "/picture?type=large";
                                    SocialName = object.getString("name");
                                    SocialEmail = object.getString("email");
//                                saveUser(Uid, Uname, Uemail, UimageUrl, "", accessToken);
                                    checksocialuser(object.getString("email"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(SignInActivity.this, "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SignInActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LoginActivity", ""+error.getMessage());
            }
        });
    }

    private void checksocialuser(String email) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.socialcheck(this, email, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    SocialDataCheckModel data = new Gson().fromJson(message, SocialDataCheckModel.class);
                    if (data.getMessage().equalsIgnoreCase("success")){
                        Uphone = data.getContact();
                        Uid = data.getId();
                        Uemail = data.getEmail();
                        UimageUrl = SocialImage;
                        Uname = data.getUsername();
                        saveUser(Uid, Uname, Uemail, UimageUrl, Uphone, "");
                    } else {
                        register();
                    }
                }

                @Override
                public void fail(String from) {
                    register();
                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void register() {
        Intent intent = new Intent(SignInActivity.this, RegisterSocial.class);
        intent.putExtra(RegisterSocial.NAME, SocialName);
        intent.putExtra(RegisterSocial.EMAIL, SocialEmail);
        intent.putExtra(RegisterSocial.IMAGE, SocialImage);
        startActivity(intent);
    }

    public void loginWithMobile(View view) {

        if (UtilMethods.INSTANCE.isNetworkAvialable(getApplicationContext())) {

            if (tvMobile.getText().toString().isEmpty() || tvMobile.getText().toString().length()<10){
            tvMobile.setError("10 digit Number Required");
            return;
        }
            final String mobileNumber = tvMobile.getText().toString();
            UtilMethods.INSTANCE.otp(SignInActivity.this, mobileNumber, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    OTPModel otpModel = new Gson().fromJson(message, OTPModel.class);
                    if (otpModel.getMessage().equalsIgnoreCase("success")){
                       dialog = new Dialog(SignInActivity.this);
                       dialog.setContentView(R.layout.verifyotp);
                       dialog.setCancelable(false);
                       dialog.setTitle("Enter Otp");

                       AppCompatButton verify = dialog.findViewById(R.id.verify);
                       AppCompatButton cancel = dialog.findViewById(R.id.cancelbutton);
                       Pinview pinview = dialog.findViewById(R.id.verifyotp);
cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        dialog.dismiss();
    }
});
                       verify.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               if (pinview.getValue().isEmpty() || !pinview.getValue().equalsIgnoreCase(otpModel.getOtp())) {
                                   Toast.makeText(SignInActivity.this, "Inavlid OTP", Toast.LENGTH_SHORT).show();
                                   return;
                               } else {
                                   requestlogin(mobileNumber, "123456");
                               }
                           }
                       });
                       dialog.show();


                    } else {
                        Toast.makeText(getApplicationContext(), ""+otpModel.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void fail(String from) {

                }
            });

        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(getApplicationContext());
        }

//
//        if (tvPassword.getText().toString().isEmpty()){
//            tvMobile.setError("Password Requied");
//            return;
//        }
//
//        requestlogin(tvMobile.getText().toString().trim(), tvPassword.getText().toString().trim());
        //Toast.makeText(this, "welcome", Toast.LENGTH_SHORT).show();
    }

    private void matchOtp(View view) {

        if (data.getOtp().equals(verifyPin.getValue().toString())){
            Bundle bundle = new Bundle();
            bundle.putString("mobile", mobileNumber);
            Navigation.findNavController(view).navigate(R.id.action_OTPFragment_to_signupFragment, bundle);
        } else {
           verifyPin.setValue("");
            Toast.makeText(SignInActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
        }

    }



    private void requestlogin(String mobile, String password) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.Login(this, mobile, password, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                    preferences.setLoginDetails(message);
                    startActivity(new Intent(SignInActivity.this, DashboardActivity.class));
                }

                @Override
                public void fail(String from) {
                    Toast.makeText(SignInActivity.this, "Invalid User Id or Password", Toast.LENGTH_SHORT).show();
                }
            });

        } else {

            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    public void loginWithGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void loginWithFacebook(View view) {
        loginButton.performClick();
    }

    public void signUp(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == RC_SIGN_IN && data !=null) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("LoginActivity", "Google sign in failed"+e.getMessage());
                // ...
            }
            return;
        }
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.e("LoginActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("LoginActivity", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("LoginActivity", "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.container), "Authentication Failed."+task.getException(), Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user!=null){
//            Uid=user.getUid();
            SocialName=user.getDisplayName();
            SocialEmail=user.getEmail();
            SocialImage=user.getPhotoUrl().toString();
            checksocialuser(user.getEmail());
        }
    }

    private void saveUser(String id, String name, String email, String photourl, String phone, String accessToken) {


        LoginData data = new LoginData(id, "", "", "", name, "", email, "",
                phone, "User", "", "", "", "", "", "", "",
                "", "", photourl,photourl,photourl, "", "", "");
        String strdata = new Gson().toJson(data);
        AppSharedPreferences preferences = new AppSharedPreferences(this.getApplication());
        preferences.setLoginDetails(strdata);
        startActivity(new Intent(SignInActivity.this,DashboardActivity.class));
        finishAffinity();
    }
}