package com.smartwebarts.acrepair.RateCard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.category.CategoryListAdapter;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.RateCardModel;
import com.smartwebarts.acrepair.models.SubCategoryModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class RateCardActivity extends AppCompatActivity {
    private RecyclerView ratecardRecycler;
    CategoryModel category;
    ArrayList<RateCardModel> rm;
    RateCardHeaderAdapter adapter;

    public static final String CATEGORY = "category";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);
        ratecardRecycler=findViewById(R.id.rateCardRecyclerView);
        category = (CategoryModel) getIntent().getSerializableExtra(CATEGORY);
  rm=new ArrayList<>();
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        ratecardRecycler.setLayoutManager(manager);
        //set data in list from Rate card Model
//        List<RateCardModel>rateCardModel=new ArrayList<>();

        // check Internet connection
        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {

            UtilMethods.INSTANCE.ratecard(this, new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    //define Type Data Type if you don't know which type data come in list
//                    System.out.println("bnhhg"+message);

                    try {
                        JSONArray j = new JSONArray(message);
                       // setCategory(j);
                        for(int i=0;i<=j.length();i++)
                        {
                            JSONObject j1=j.getJSONObject(i);
                            String name=j1.getString("name");
                            String buingprice=j1.getString("buingprice");
                            RateCardModel rm1=new RateCardModel();
                            rm1.setName(name);
                            rm1.setBuingprice(buingprice);
                            System.out.println(name+"-"+buingprice);
                            rm.add(rm1);

                        }
                    }catch (Exception e)
                    {

                    }

//                    Type type = new TypeToken<List<RateCardModel>>(){}.getType();
//                    List<RateCardModel> list = new Gson().fromJson(message, type);
                    adapter=new RateCardHeaderAdapter(RateCardActivity.this,rm);
                    ratecardRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void fail(String from) {

                }
            });
        } else {
            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }




//        category = (CategoryModel) getIntent().getSerializableExtra("");
//        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
//            UtilMethods.INSTANCE.rateCard(this, category.getId(),new mCallBackResponse() {
//                @Override
//                public void success(String from, String message) {
//                    Type listType = new TypeToken<ArrayList<SubCategoryModel>>(){}.getType();
//                    List<SubCategoryModel> list = new Gson().fromJson(message, listType);
//                    setCategory(list);
//
//                    List<String> strings = new ArrayList<>();
//                    for (SubCategoryModel model: list) {
//                        strings.add(model.getImage());
//                    }
//
//                }
//
//                @Override
//                public void fail(String from) {
//                }
//            });
//
//        } else {
//
//            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
//        }
//        LinearLayoutManager manager=new LinearLayoutManager(this);
//        manager.setOrientation(LinearLayoutManager.VERTICAL);
//        ratecardRecycler.setLayoutManager(manager);
//        //set data in list from Rate card Model
//        List<RateCardModel>rateCardModel=new ArrayList<>();
//        RateCardAdapter adapter=new RateCardAdapter(rateCardModel);
//        //Set Data in adapter
//        ratecardRecycler.setAdapter(adapter);
////        // check Internet connection
//        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
//
//            UtilMethods.INSTANCE.setServices(this, new mCallBackResponse() {
//                @Override
//                public void success(String from, String message) {
//                    //define Type Data Type if you don't know which type data come in list
//                    Type type = new TypeToken<List<RateCardModel>>(){}.getType();
//                    List<RateCardModel> list = new Gson().fromJson(message, type);
//                    adapter.setRateCardModels(list);
//                }
//
//                @Override
//                public void fail(String from) {
//
//                }
//            });
//        } else {
//            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
//        }

    }

//    public void setCategory(List<SubCategoryModel> j){
//        RateCardAdapter adapter = new RateCardAdapter(rm);
//
//        ratecardRecycler.setAdapter(adapter);
//    }


}