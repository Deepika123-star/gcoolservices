package com.smartwebarts.acrepair.productlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.smartwebarts.acrepair.MyApplication;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.RateCard.RateCardActivity;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.ProductModel;
import com.smartwebarts.acrepair.models.SubCategoryModel;
import com.smartwebarts.acrepair.models.SubSubCategoryModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;
import com.smartwebarts.acrepair.utils.Toolbar_Set;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView rvProductList, rvProductGrid;
    SubSubCategoryModel subSubCategory;
    CategoryModel category;
    SubCategoryModel subCategory;
    TextView tv_subsubCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        rvProductList = findViewById(R.id.rvProductList);
        rvProductGrid = findViewById(R.id.rvProductGrid);

        tv_subsubCategory = findViewById(R.id.subsubCategory);

        subCategory = (SubCategoryModel) getIntent().getSerializableExtra("subCategory");
        category = (CategoryModel) getIntent().getSerializableExtra("category");
        subSubCategory = (SubSubCategoryModel) getIntent().getSerializableExtra("subsubcategory");
        tv_subsubCategory.setText(subSubCategory.getName());
        MyApplication application = (MyApplication) getApplication();
        AppSharedPreferences preferences = new AppSharedPreferences(application);
        application.logLeonEvent("Category", "Category Viewed " + subSubCategory.getName() + " by "+ preferences.getLoginMobile(), 0);


        Toolbar_Set.INSTANCE.setToolbar(this, subCategory.getName());
        Toolbar_Set.INSTANCE.setBottomNav(this);

        if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {
            UtilMethods.INSTANCE.products(this, category.getId(),subCategory.getId(), subSubCategory.getId(),new mCallBackResponse() {
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

    public void rateCard(View view) {
        Intent intent=new Intent(ProductListActivity.this, RateCardActivity.class);
        startActivity(intent);
    }
}
