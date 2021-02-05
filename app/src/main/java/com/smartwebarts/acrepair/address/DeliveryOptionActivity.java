package com.smartwebarts.acrepair.address;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.smartwebarts.acrepair.MyApplication;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.dashboard.DashboardActivity;
import com.smartwebarts.acrepair.database.DatabaseClient;
import com.smartwebarts.acrepair.database.Task;
import com.smartwebarts.acrepair.models.OrderIdModel;
import com.smartwebarts.acrepair.models.OrderedResponse;
import com.smartwebarts.acrepair.retrofit.DeliveryChargesModel;
import com.smartwebarts.acrepair.retrofit.TimeModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;
import com.smartwebarts.acrepair.utils.ApplicationConstants;
import com.smartwebarts.acrepair.utils.Toolbar_Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DeliveryOptionActivity extends AppCompatActivity  implements PaymentResultListener {

    private static final String TAG = DeliveryOptionActivity.class.getSimpleName();
    private DatePicker datePicker;
    private TextView dateValueTextView;
    private Button updateDateButton;
    private RadioGroup timeGroup;
//    private RadioButton radio1, radio2, radio3;

    public static final String PRODUCT_LIST  = "product";
    public static final String ADDRESS  = "address";
    public static final String LANDMARK  = "landmark";
    public static final String PINCODE  = "pincode";
    public static final String AMOUNT  = "amount";
    public static final String HASHMAP  = "hashmap";
    public static final String VENDOR_ID = "vendorid";


    private String address, landmark, pincode, date, time = "1", paymentmethod = "", amount, vendorid;
    private ArrayList<Task> list;
    private HashMap<String, String> hashMap;
    private List<TimeModel> timinglist;

    private List<RadioButton> radioButtons = new ArrayList<>();


    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdf2 = new SimpleDateFormat("HHmmss");
    SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_option);

        initialise();

        getTimeSlot();
    }

    private void getTimeSlot() {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.getTimeSlot(this, pincode, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {

                    Type type = new TypeToken<List<TimeModel>>(){}.getType();
                    timinglist = new Gson().fromJson(message, type);
                    if (timinglist!=null && timinglist.size()>0) {
                        for (TimeModel model : timinglist) {
                            RadioButton radioButton = new RadioButton(DeliveryOptionActivity.this);
                            radioButton.setText(model.toString());
                            radioButton.setId(Integer.parseInt("0"+model.getId()));
                            radioButton.setChecked(true);
                            radioButtons.add(radioButton);
                            time = radioButton.getText().toString();
                            timeGroup.addView(radioButton);
                        }
                        enableDisable();
                        timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                int checkedRadioButtonId = group.getCheckedRadioButtonId();
                                RadioButton radioBtn = (RadioButton) findViewById(checkedRadioButtonId);
                                time = radioBtn.getText().toString();
                            }
                        });

                    }
                }

                @Override
                public void fail(String from) {
                    SweetAlertDialog sad = new SweetAlertDialog(DeliveryOptionActivity.this, SweetAlertDialog.ERROR_TYPE);
                    sad.setCancelable(false);
                    sad.setCanceledOnTouchOutside(false);
                    sad.setTitleText("Error");
                    sad.setContentText(""+from);
                    sad.setConfirmText("OK");
                    sad.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            DeliveryOptionActivity.this.finish();
                        }
                    });
                    sad.show();
                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void addListeners() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
                @Override
                public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                    String day = datePicker.getDayOfMonth()<10?"0"+datePicker.getDayOfMonth():""+datePicker.getDayOfMonth();
                    String month = (datePicker.getMonth()+1)<10?"0"+(datePicker.getMonth()+1): ""+(datePicker.getMonth()+1);

                    date = day + "/"  + month + "/" + year;
                    dateValueTextView.setText("Selected date: " + date);

                    enableDisable();
                }
            });
        } else {
            updateDateButton.setVisibility(View.VISIBLE);
            updateDateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dateValueTextView.setText("Selected date: " + (datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth() + "/" + datePicker.getYear());
                }
            });
        }
    }

    private void enableDisable() {
        try{

            Date today = new Date();
            String strToday = sdf.format(today);
            Log.e("TIMINGS", strToday+ " , "+date);
            if (strToday.equalsIgnoreCase(date)) {
                if (timinglist!=null && timinglist.size()>0 && radioButtons.size()>0) {

                    for (RadioButton rb: radioButtons) {
                        rb.setEnabled(true);
                    }

                    for (int i=0; i<timinglist.size(); i++){
                        String startTime = timinglist.get(i).getStartTime().replaceAll(":","");
                        int start = Integer.parseInt(startTime);
                        String endTime = timinglist.get(i).getEndTime().replaceAll(":","");
                        int end = Integer.parseInt(endTime);
                        String currentTime = sdf2.format(today);
                        int current = Integer.parseInt(currentTime);
                        Log.e("TIMINGS", "start " +start + " end "+end+" current "+current);
                        if (start<current && end<current) {
                            radioButtons.get(i).setEnabled(false);
                            radioButtons.get(i).setChecked(false);
                        } else {
                            radioButtons.get(i).setChecked(true);
                        }
                    }
                }
            }
            else {
                for (RadioButton rb: radioButtons) {
                    rb.setEnabled(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initialise() {

        Checkout.preload(getApplicationContext());

        Toolbar_Set.INSTANCE.setToolbar(this, "Delivery Options");

        address = getIntent().getExtras().getString(ADDRESS, "");
        landmark = getIntent().getExtras().getString(LANDMARK, "");
        pincode = getIntent().getExtras().getString(PINCODE, "");
        amount = getIntent().getExtras().getString(AMOUNT, "");
        vendorid = getIntent().getExtras().getString(VENDOR_ID, "");
        hashMap = (HashMap<String, String>) getIntent().getExtras().get(HASHMAP);
        System.out.println(hashMap);

        list = (ArrayList<Task>) getIntent().getExtras().getSerializable(PRODUCT_LIST);

        datePicker = (DatePicker) findViewById(R.id.date_picker);
        dateValueTextView = (TextView) findViewById(R.id.date_selected_text_view);
        updateDateButton = (Button) findViewById(R.id.update_date_button);
        timeGroup = (RadioGroup) findViewById(R.id.groupTiming);


        updateDateButton.setVisibility(View.GONE);

        // disable dates before two days and after two days after today
        Calendar today = Calendar.getInstance();
        Calendar sixDaysAgo = (Calendar) today.clone();
        sixDaysAgo.add(Calendar.DATE, 0);
        Calendar twoDaysLater = (Calendar) today.clone();
        twoDaysLater.add(Calendar.DATE, 6);
        datePicker.setMinDate(sixDaysAgo.getTimeInMillis());
        datePicker.setMaxDate(twoDaysLater.getTimeInMillis());

        String day = datePicker.getDayOfMonth()<10?"0"+datePicker.getDayOfMonth():""+datePicker.getDayOfMonth();
        String month = (datePicker.getMonth()+1)<10?"0"+(datePicker.getMonth()+1): ""+(datePicker.getMonth()+1);
        String year = ""+datePicker.getYear();

        date = day + "/"  + month + "/" + year;
        dateValueTextView.setText("Selected date: " +date);
        enableDisable();
        addListeners();
    }

    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID(""+getString(R.string.razor_api_key));


        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.logon);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "HappiIndia Food Technologies Private Limited");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. #123456");
            options.put("image", getDrawable(R.drawable.logon));
//            options.put("order_id", ""+model.getOrderid());
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */

            int dAmount = Integer.parseInt("0"+amount);
            dAmount*=100;

            options.put("amount", ""+dAmount);

            checkout.open(this, options);
        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
//        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
        buildJson(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("RazorCrash", s);
        Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();
    }

    public void payment(View view) {
        paymentmethod = "ONLINE";
        startPayment();
    }

    public void cod(View view) {
        paymentmethod = "COD";
        getOrderId(view);
    }

    public void getOrderId(View view) {

        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.orderid(this, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    OrderIdModel model = new Gson().fromJson(message, OrderIdModel.class);
                    buildJson(model.getOrderid());
                }

                @Override
                public void fail(String from) {

                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void buildJson( String orderid) {
        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        List<DeliveryProductDetails> products = new ArrayList<>();
        for (Task t: list) {
            DeliveryProductDetails d = new DeliveryProductDetails(preferences.getLoginUserLoginId(),
                    t.getQuantity(),
                    t.getId(),
                    t.getCurrentprice(),
                    t.getName(),
                    t.getUnit(),
                    t.getUnitIn(),
                    t.getThumbnail(),
                    preferences.getLoginMobile(),
                    orderid,
                    "1",
                    paymentmethod,
                    address,
                    landmark,
                    pincode,
                    date,
                    time);
            products.add(d);

            if (hashMap.get(d.getProId())!=null && !hashMap.get(d.getProId()).isEmpty()) {
                d.setAmount(hashMap.get(d.getProId()));
            }else {
                try {
                    int a = Integer.parseInt("0"+d.getAmount().trim());
                    int b = Integer.parseInt("0"+d.getQty().trim());
                    int f = a*b;
                    d.setAmount(f+"");
                } catch (Exception ignore){}
            }
        }

        Log.e("DeliveryDetails", new Gson().toJson(products));

        hitServiceOrder(products);
    }

    private void hitServiceOrder(List<DeliveryProductDetails> products) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {

            MyApplication application = (MyApplication) getApplication();
            AppSharedPreferences preferences1 = new AppSharedPreferences(application);
            application.logLeonEvent("Purchase", "Purchased"+ " by "+ preferences1.getLoginMobile(), 0);

            int tempTotal = 0;
            int discount = 0;
            try {

                for (DeliveryProductDetails product : products){
                    tempTotal = tempTotal + Integer.parseInt("0"+product.getAmount());
                }

                SharedPreferences preferences = getSharedPreferences(ApplicationConstants.INSTANCE.DELIVERY_PREFS, MODE_PRIVATE);
                String strResponse = preferences.getString(ApplicationConstants.INSTANCE.DELIVERY_CHARGES, "");
                Type type = new TypeToken<List<DeliveryChargesModel>>(){}.getType();
                List<DeliveryChargesModel> chargesModelList = new Gson().fromJson(strResponse, type);

                for (DeliveryChargesModel model : chargesModelList) {
                    int min = Integer.parseInt("0"+model.getMinAmt());
                    int max = Integer.parseInt("0"+model.getMaxAmt());
                    int deliveryCharges = Integer.parseInt("0"+model.getAmt());

                    if (tempTotal>min && tempTotal <max) {
                        tempTotal = tempTotal + deliveryCharges;
                        break;
                    }
                }
                discount = Integer.parseInt(amount) - tempTotal;
            }
            catch (Exception e) {
                tempTotal = 0;
                discount = 0;
            }
//            final Dialog dialog = new Dialog(this);
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.default_progress_dialog);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//            ProgressBar progressBar = (ProgressBar)dialog.findViewById(R.id.progress);
//            DoubleBounce doubleBounce = new DoubleBounce();
//            progressBar.setIndeterminateDrawable(doubleBounce);
//            dialog.show();
            for (DeliveryProductDetails product : products){
                UtilMethods.INSTANCE.order(this, product, amount, ""+discount, vendorid, new mCallBackResponse() {
                    @Override
                    public void success(String from, String message) {
//                        Toast.makeText(DeliveryOptionActivity.this, "Ordered", Toast.LENGTH_SHORT).show();

                        OrderedResponse response = new Gson().fromJson(message, OrderedResponse.class);
                        if (response != null && response.getMessage() !=null && response.getMessage().equalsIgnoreCase("success")) {
                            showSuccessMessage(response);
                        } else {
                            showErrorMessage(response);
                        }
                    }

                    @Override
                    public void fail(String from) {
                        showFailMessage(null);
                    }
                });

            }
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    private void showSuccessMessage(OrderedResponse response) {
        SweetAlertDialog sad = new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sad.setTitleText("Order Completed");
        sad.setCustomImage(R.drawable.successimage);
        sad.setCancelable(false);
        sad.setCanceledOnTouchOutside(false);
        sad.setContentText(""+response.getMessage());
        sad.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                     deleteAll();
                     sad.dismissWithAnimation();
            }
        });
        sad.show();
    }

    private void deleteAll() {
        class DeleteAll extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                DatabaseClient.
                        getmInstance(DeliveryOptionActivity.this)
                        .getAppDatabase()
                        .taskDao()
                        .deleteAll();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startActivity( new Intent(DeliveryOptionActivity.this, DashboardActivity.class));
                finishAffinity();
            }
        }

        new DeleteAll().execute();
    }

    private void showErrorMessage(OrderedResponse response) {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Retry")
                .setContentText(""+response.getMessage())
                .show();
    }
    private void showFailMessage(OrderedResponse response) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Failed")
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }
}