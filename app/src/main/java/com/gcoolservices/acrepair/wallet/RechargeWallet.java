package com.gcoolservices.acrepair.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.models.MessageModel;
import com.gcoolservices.acrepair.retrofit.UtilMethods;
import com.gcoolservices.acrepair.retrofit.mCallBackResponse;
import com.gcoolservices.acrepair.shared_preference.AppSharedPreferences;
import com.gcoolservices.acrepair.utils.Toolbar_Set;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.android.volley.VolleyLog.TAG;

public class RechargeWallet extends AppCompatActivity implements PaymentResultListener {

    private RechargeWallet activity;
    private EditText Wallet_Ammount;
    private RelativeLayout Recharge_Button;
    private String ammount;
    private String userid, name, email, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_wallet);

        activity = RechargeWallet.this;
        Checkout.preload(getApplicationContext());

        Toolbar_Set.INSTANCE.setToolbar(this, "My Wallet");
        Toolbar_Set.INSTANCE.setBottomNav(this);

        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        name = preferences.getLoginUserName();
        email = preferences.getLoginEmail();
        mobile = preferences.getLoginMobile();
        userid = preferences.getLoginUserLoginId();

        Wallet_Ammount = (EditText) findViewById(R.id.et_wallet_ammount);
        Recharge_Button = (RelativeLayout) findViewById(R.id.recharge_button);

        Recharge_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ammount = Wallet_Ammount.getText().toString();
                startPayment(name,ammount,email,mobile);
            }
        });
    }

    private void startPayment(String name, String amount, String email, String mobile) {

        final Checkout co = new Checkout();
        co.setKeyID(""+getString(R.string.razor_api_key));

        try {

            JSONObject options = new JSONObject();

            options.put("name", name);
            options.put("description", "Demoing Charges");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", R.drawable.logon);
            options.put("currency", "INR");

            options.put("amount", Integer.parseInt(amount)*100);

            JSONObject preFill = new JSONObject();

            preFill.put("email", email);

            preFill.put("contact", mobile);

            options.put("prefill", preFill);

            co.open(activity, options);

        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        try {
            Recharge_wallet(s);
            Toast.makeText(this, "Wallet Recharge Successful", Toast.LENGTH_SHORT).show();
            overridePendingTransition(0, 0);

//            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {
        Recharge_wallet("failed");
    }

    private void Recharge_wallet(String msg) {

        if (!msg.equalsIgnoreCase("failed")) {

            if (UtilMethods.INSTANCE.isNetworkAvialable(activity)) {
                UtilMethods.INSTANCE.addWallet(RechargeWallet.this, msg, ammount, userid, mobile, new mCallBackResponse() {
                    @Override
                    public void success(String from, String message) {

                        MessageModel messageModel = new Gson().fromJson(message, MessageModel.class);
                        new SweetAlertDialog(RechargeWallet.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Request")
                                .setContentText(""+messageModel.getMsg())
                                .show();
                    }

                    @Override
                    public void fail(String from) {
                        new SweetAlertDialog(RechargeWallet.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Request")
                                .setContentText(""+from)
                                .show();
                    }
                });

            } else {
                UtilMethods.INSTANCE.internetNotAvailableMessage(activity);
            }
        }
    }

}