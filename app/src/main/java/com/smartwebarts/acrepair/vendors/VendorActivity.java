package com.smartwebarts.acrepair.vendors;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.models.VendorModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;
import com.smartwebarts.acrepair.utils.GPSTracker;
import com.smartwebarts.acrepair.utils.Toolbar_Set;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VendorActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView recyclerView;
    VendorAdapter adapter;
    List<VendorModel> list = new ArrayList<>();
    GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        gpsTracker = new GPSTracker(this);
        gpsTracker.getLocation();

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        //sort by distance

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                //sort list on the basis of distance
                Collections.sort(list, (l1, l2) -> (int) (Double.parseDouble(l1.getDistance(preferences)) - Double.parseDouble(l2.getDistance(preferences))));

                adapter = new VendorAdapter(VendorActivity.this, list);
                recyclerView.setAdapter(adapter);
            }
        });

        Toolbar_Set.INSTANCE.setBottomNav(this);
        Toolbar_Set.INSTANCE.setToolbar(this, "Vendors");

        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        String s = preferences.getVendorDetails();

        if (s!=null && !s.isEmpty()) {
            Type listType = new TypeToken<ArrayList<VendorModel>>(){}.getType();
            VendorActivity.this.list = new Gson().fromJson(preferences.getVendorDetails(), listType);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                    //sort list on the basis of distance
                    Collections.sort(list, (l1, l2) -> (int) (Double.parseDouble(l1.getDistance(preferences)) - Double.parseDouble(l2.getDistance(preferences))));

                    adapter.updateList(VendorActivity.this.list);
                }
            });


        }else {
            search("");
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                search(newText);
                upDate(newText);
                return false;
            }
        });
    }

    private void upDate(String newText) {
        if (newText.isEmpty()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                    //sort list on the basis of distance
                    Collections.sort(list, (l1, l2) -> (int) (Double.parseDouble(l1.getDistance(preferences)) - Double.parseDouble(l2.getDistance(preferences))));
                    adapter.updateList(list);
                }
            });


        } else {
            List<VendorModel> models = new ArrayList<>();
            for (VendorModel model: list) {
                if (model.getShopName()!=null && model.getShopName().trim().toLowerCase().contains(newText.toLowerCase().trim())) {
                    models.add(model);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                    //sort list on the basis of distance
                    Collections.sort(models, (l1, l2) -> (int) (Double.parseDouble(l1.getDistance(preferences)) - Double.parseDouble(l2.getDistance(preferences))));
                    adapter.updateList(models);
                }
            });
        }
    }

    private void search(String newText) {
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            double mlat = gpsTracker.getLatitude();
            double mlng = gpsTracker.getLongitude();
            UtilMethods.INSTANCE.vendor(this, mlat+"", mlng+"",  new mCallBackResponse() {
                @Override
                public void success(String from, String message) {

                    AppSharedPreferences appSharedPreferences = new AppSharedPreferences(getApplication());
                    appSharedPreferences.setVendorDetails(message);
                    Type listType = new TypeToken<ArrayList<VendorModel>>(){}.getType();
                    List<VendorModel> list = new Gson().fromJson(message, listType);
                    VendorActivity.this.list = list;
                    adapter.updateList(VendorActivity.this.list);
                }

                @Override
                public void fail(String from) {

                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }

}