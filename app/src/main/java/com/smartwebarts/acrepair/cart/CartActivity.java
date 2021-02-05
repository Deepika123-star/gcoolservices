package com.smartwebarts.acrepair.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.SignInActivity;
import com.smartwebarts.acrepair.address.AddressActivity;
import com.smartwebarts.acrepair.dashboard.DashboardActivity;
import com.smartwebarts.acrepair.database.DatabaseClient;
import com.smartwebarts.acrepair.database.Task;
import com.smartwebarts.acrepair.models.CouponModels;
import com.smartwebarts.acrepair.models.VendorDeliveryChargesModel;
import com.smartwebarts.acrepair.models.VendorModel;
import com.smartwebarts.acrepair.retrofit.DeliveryChargesModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;
import com.smartwebarts.acrepair.utils.GPSTracker;
import com.smartwebarts.acrepair.utils.LocationAddress;
import com.smartwebarts.acrepair.utils.Toolbar_Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CartActivity extends AppCompatActivity implements ItemAdapter.ItemListener {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView tvFinalPrice;
    private ArrayList<Task> list = new ArrayList<>();
    private LinearLayout llNormal, llNoCart;
    HashMap<String, String> hashMap = new HashMap<>();
    private List<DeliveryChargesModel> chargesModelList;
    private List<VendorModel> vendorModelList;
    GPSTracker gpsTracker;
    BottomSheetBehavior behavior;
    RecyclerView vendorRecyclerView;
    private ItemAdapter mAdapter;
    CoordinatorLayout coordinatorLayout;
    private static String vendorid;

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        CartActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        gpsTracker = new GPSTracker(this);
        gpsTracker.getLocation();

        recyclerView = findViewById(R.id.recyclerView);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);
        llNormal = findViewById(R.id.ll_normal);
        llNoCart = findViewById(R.id.ll_nocart);
        Toolbar_Set.INSTANCE.setToolbar(this, "My Basket");
        adapter = new CartAdapter(this, list);
        recyclerView.setAdapter(adapter);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        View bottomSheet = findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        vendorRecyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        vendorRecyclerView.setHasFixedSize(true);
        vendorRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        getCartList(this, null);
    }

    public void getCartList(final Activity activity, HashMap<String, CouponModels> map) {

        class GetTasks extends AsyncTask<Void, Void, ArrayList<Task>> {

            @Override
            protected ArrayList<Task> doInBackground(Void... voids) {
                List<Task> tasks= DatabaseClient.getmInstance(activity.getApplicationContext())
                        .getAppDatabase()
                        .taskDao()
                        .getAll();
                return new ArrayList<>(tasks);
            }

            @Override
            protected void onPostExecute(ArrayList<Task> tasks) {
                Comparator<Task> comparator = new Comparator<Task>() {
                    @Override
                    public int compare(Task left, Task right) {
                        int intLeft = Integer.parseInt("0"+left.getId());
                        int intRight = Integer.parseInt("0"+right.getId());
                        return intLeft - intRight; // use your logic
                    }
                };
                Collections.sort(tasks, comparator);

                list = tasks;
                adapter.updateList(list);
                upDateUI(list.size());

                int size = tasks!=null?tasks.size():0;
                TextView cartItemsCount = activity.findViewById(R.id.cartItemsCount);
                cartItemsCount.setText(""+size);

                double sum = 0.00;

                for (Task task: tasks) {
                    double price = Double.parseDouble("0"+(task.getCurrentprice()==null?"":task.getCurrentprice()));
                    double qty = Double.parseDouble("0"+task.getQuantity());
                    double total = price*qty;

                    if (map != null && map.get(task.getId())!=null) {
                        CouponModels model = map.get(task.getId());

                        int discount = 0;
                        try {
                            discount = Integer.parseInt("0"+model.getCouponValue().trim().replaceAll("[^0-9]", ""));
                            double cal = total * discount / 100;

                            if (cal > 50) {
                                total = (int) (total - 50);
                            } else {
                                total = (int) (total - cal);
                            }

                            hashMap.put(task.getId(), ""+total);
                        } catch (Exception ignored){}
                    }
                    sum+=total;
                }

                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                tvFinalPrice.setText(""+nf.format(sum));

                if (sum == 0) {
                    findViewById(R.id.button).setVisibility(View.GONE);
                }
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    public void proceedToCheckout(View view) {

        double latitude, longitude;
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
        } else {
            showSettingsAlert();
            return;
        }

        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {

            UtilMethods.INSTANCE.getDeliveryChargesAndVendorList(this, latitude, longitude, list.get(0).getId(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    VendorDeliveryChargesModel vendorDeliveryChargesModel = new Gson().fromJson(message, VendorDeliveryChargesModel.class);
                    chargesModelList = vendorDeliveryChargesModel.getDelivery_charge();
                    vendorModelList = vendorDeliveryChargesModel.getVendors();

                    Comparator<VendorModel> comparator = (left, right) -> {
                        int intLeft = (int) Double.parseDouble("0"+left.getLocation());
                        int intRight = (int) Double.parseDouble("0"+right.getLocation());
                        return intLeft - intRight; // use your logic
                    };
                    Collections.sort(vendorModelList, comparator);

                    mAdapter = new ItemAdapter(CartActivity.this,vendorModelList);
                    vendorRecyclerView.setAdapter(mAdapter);

                    addDeliveryCharges();
                }

                @Override
                public void fail(String from) {

                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }

    }

    private void addDeliveryCharges() {
        int total = Integer.parseInt(tvFinalPrice.getText().toString().trim().replaceAll("[^0-9]", ""));

        if (total<=0) {
            Toast.makeText(this, "Add Items to Cart", Toast.LENGTH_SHORT).show();
            return;
        }

        if (chargesModelList !=null && chargesModelList.size()>0) {

            for (DeliveryChargesModel model : chargesModelList) {
                int min = Integer.parseInt("0"+model.getMinAmt());
                int max = Integer.parseInt("0"+model.getMaxAmt());
                int deliveryCharges = Integer.parseInt("0"+model.getAmt());

                if (total>min && total <=max) {

                    if (deliveryCharges!=0) {
                        AlertDialog dialog = new AlertDialog.Builder(this).create();
                        dialog.setTitle("Service Charges");
                        dialog.setMessage("Your Basket has less than "+getString(R.string.currency)+ max+" So "+getString(R.string.currency)+deliveryCharges+" service charges will be applied. Click OK to proceed");
                        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (dialog1, which) -> dialog1.dismiss());
                        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialog12, which) -> selectVendor());
                        dialog.show();
                    } else {
                        selectVendor();
                    }

                    break;
                }
            }
        } else {
            selectVendor();
        }

    }

    private void selectVendor() {
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void proceed() {
        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        String user = preferences.getLoginUserName();

        if (user!=null && !user.trim().isEmpty())
        {
            checkPermission();
        } else {
            SweetAlertDialog sad = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            sad.setTitleText("You are not logged in");
            sad.setContentText("Login to Continue");
            sad.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    startActivity(new Intent(CartActivity.this, SignInActivity.class));
                    sad.dismiss();
                }
            });
            sad.show();
        }
    }

    private void checkPermission() {
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // You can use the API that requires the permission.
            openNextActivity();
        }  else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }

    private void openNextActivity() {
        System.out.println(hashMap);
        Intent intent = new Intent(this, AddressActivity.class);
        intent.putExtra(AddressActivity.PRODUCT_LIST, list);
        intent.putExtra(AddressActivity.HASHMAP, hashMap);
        intent.putExtra(AddressActivity.VENDOR_ID, vendorid);
        int total = Integer.parseInt(tvFinalPrice.getText().toString().trim().replaceAll("[^0-9]", ""));

        for (DeliveryChargesModel model : chargesModelList) {
            int min = Integer.parseInt("0"+model.getMinAmt());
            int max = Integer.parseInt("0"+model.getMaxAmt());
            int deliveryCharges = Integer.parseInt("0"+model.getAmt());

            if (total>min && total <max) {
                total = total + deliveryCharges;
                break;
            }
        }
        intent.putExtra(AddressActivity.AMOUNT, ""+total);
        startActivity(intent);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Toolbar_Set.INSTANCE.getCartList(this);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 101:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openNextActivity();
                }  else {
                    openNextActivity();
                }
                return;
        }
    }

    public void upDateUI(int size) {
        if (size > 0) {
            llNormal.setVisibility(View.VISIBLE);
            llNoCart.setVisibility(View.GONE);
        } else {
            llNormal.setVisibility(View.GONE);
            llNoCart.setVisibility(View.VISIBLE);
        }
    }

    public void shopNow(View view) {

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void onItemClick(VendorModel item) {
        vendorid = item.getId();
        proceed();
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
