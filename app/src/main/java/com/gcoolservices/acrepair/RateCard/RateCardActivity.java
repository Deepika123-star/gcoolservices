package com.gcoolservices.acrepair.RateCard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.models.CategoryModel;
import com.gcoolservices.acrepair.utils.ApplicationConstants;
import com.gcoolservices.acrepair.utils.Toolbar_Set;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RateCardActivity extends AppCompatActivity {

    //initialize
    private Activity activity;
    RecyclerView expandableListView;
   ExpandebleListViewAdapter expandableListAdapter;
    private List<CategoryModel> list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);

        Toolbar_Set.INSTANCE.setToolbar(this, "Rate Card");
//        ratecardRecycler = findViewById(R.id.rateCardRecyclerView);
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        ratecardRecycler.setLayoutManager(manager);



        SharedPreferences sharedpreferences = getSharedPreferences(ApplicationConstants.INSTANCE.MyPREFERENCES, Context.MODE_PRIVATE);
        String message = sharedpreferences.getString(ApplicationConstants.INSTANCE.PRODUCT_LIST, "");
        if (!message.isEmpty()) {
            Type listType = new TypeToken<ArrayList<CategoryModel>>() {}.getType();
            list = new Gson().fromJson(message, listType);
        }

        findViews();
        init();


    }

    private void findViews() {
        expandableListView = findViewById(R.id.rateCardRecyclerView);
    }


    private void init() {
        expandableListAdapter = new ExpandebleListViewAdapter(this, list);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setHasFixedSize(true);
    }
}
