package com.smartwebarts.acrepair.productlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.smartwebarts.acrepair.MyApplication;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.models.ProductModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;
import com.smartwebarts.acrepair.utils.Toolbar_Set;

public class ProductListActivity2 extends AppCompatActivity {

    RecyclerView rvProductList, rvProductGrid;
    TextView tv_subsubCategory;

    public static final String CID = "cid";
    public static final String SID = "sid";
    public static final String SNAME = "sname";

    private String strCid, strSid, strName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        strCid = getIntent().getExtras().getString(CID, "");
        strSid = getIntent().getExtras().getString(SID, "");
        strName = getIntent().getExtras().getString(SNAME, "");

        rvProductList = findViewById(R.id.rvProductList);
        rvProductGrid = findViewById(R.id.rvProductGrid);
        tv_subsubCategory = findViewById(R.id.subsubCategory);
        tv_subsubCategory.setText(strName);

        MyApplication application = (MyApplication) getApplication();
        AppSharedPreferences preferences = new AppSharedPreferences(application);
        application.logLeonEvent("Category", "Category Viewed " + strName + " by "+ preferences.getLoginMobile(), 0);


        tv_subsubCategory.setVisibility(View.GONE);


        Toolbar_Set.INSTANCE.setToolbar(this, strName);
        Toolbar_Set.INSTANCE.setBottomNav(this);

        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.products(this, strCid,strSid,new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<ProductModel>>(){}.getType();
                    List<ProductModel> list = new Gson().fromJson(message, listType);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                            //sort list on the basis of distance
                            Collections.sort(list, (l1, l2) -> (int) (Double.parseDouble(l1.getDistance(preferences)) - Double.parseDouble(l2.getDistance(preferences))));
                            setProduct(list);
                        }
                    });
                }

                @Override
                public void fail(String from) {
                }
            });

        } else {

            UtilMethods.INSTANCE.internetNotAvailableMessage(this);
        }

    }

    private void setProduct(List<ProductModel> list) {
        ProductListGridAdapter adapter = new ProductListGridAdapter(this, list);
        rvProductGrid.setLayoutManager(new GridLayoutManager(this, 2));
        rvProductGrid.setAdapter(adapter);

        ProductListAdapter adapter2 = new ProductListAdapter(this, list);
        rvProductList.setLayoutManager(new GridLayoutManager(this, 1));
        rvProductList.setAdapter(adapter2);
    }

    public void changeLayout(View view) {
        if (rvProductList.getVisibility() == View.VISIBLE) {
            rvProductList.setVisibility(View.GONE);
            rvProductGrid.setVisibility(View.VISIBLE);
            ((ImageView) view).setImageDrawable(getDrawable(R.drawable.ic_grid));
        } else {
            rvProductList.setVisibility(View.VISIBLE);
            rvProductGrid.setVisibility(View.GONE);
            ((ImageView) view).setImageDrawable(getDrawable(R.drawable.ic_view_list));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }
}
