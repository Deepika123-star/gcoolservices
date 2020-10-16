package com.smartwebarts.acrepair.RateCard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.category.CategoryListAdapter;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.RateCardHeadingModel;
import com.smartwebarts.acrepair.models.RateCardModel;
import com.smartwebarts.acrepair.models.SubCategoryModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shopbycategory.ExpandableListAdapter;
import com.smartwebarts.acrepair.utils.ApplicationConstants;
import com.smartwebarts.acrepair.utils.Toolbar_Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
