package com.gcoolservices.acrepair.productlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.gcoolservices.acrepair.MyApplication;
import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.database.SaveProductList;
import com.gcoolservices.acrepair.database.Task;
import com.gcoolservices.acrepair.models.ProductDetailImagesModel;
import com.gcoolservices.acrepair.models.ProductDetailModel;
import com.gcoolservices.acrepair.models.ProductModel;
import com.gcoolservices.acrepair.models.UnitModel;
import com.gcoolservices.acrepair.retrofit.UtilMethods;
import com.gcoolservices.acrepair.retrofit.mCallBackResponse;
import com.gcoolservices.acrepair.shared_preference.AppSharedPreferences;
import com.gcoolservices.acrepair.utils.ApplicationConstants;
import com.gcoolservices.acrepair.utils.CustomSlider;
import com.gcoolservices.acrepair.utils.GPSTracker;
import com.gcoolservices.acrepair.utils.Toolbar_Set;

public class ProductDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {

    private com.daimajia.slider.library.SliderLayout viewPager;
    private int currentPage = 0;
    private ArrayList<String> sliderImage= new ArrayList<String>();
    private ProductModel addToCartProductItem;
    private TextView tvName, txt_vName,  tvDescription2, tvPrice,tvPricen, tvCurrentPrice, tvDiscount, tvOffer;
    private CardView cvoffer;
    private ImageView ivVeg;
    public static final String ID = "id";
    private String pid;
    private RecyclerView recyclerView;
    private String unit="", unitIn="", currentPrice="0", buingPrice = "0", discount = "0";

    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar_Set.INSTANCE.setToolbar(this);
        viewPager = findViewById(R.id.viewPager);
        tvName = findViewById(R.id.txt_pName);
        txt_vName = findViewById(R.id.txt_vName);
//        tvDescription = findViewById(R.id.txt_pInfo);
        tvDescription2 = findViewById(R.id.tvDescription);
        tvDiscount = findViewById(R.id.txt_discount);
        ivVeg = findViewById(R.id.iv_veg);
        tvPrice = findViewById(R.id.txt_price);
        tvPricen = findViewById(R.id.txt_pricen);
        tvCurrentPrice = findViewById(R.id.txt_current_price);
        tvOffer = (TextView) findViewById(R.id.tvoffer);
        cvoffer = (CardView) findViewById(R.id.cvoffer);
        recyclerView = findViewById(R.id.recyclerView);
        pid = getIntent().getExtras().getString(ID);

        gpsTracker = new GPSTracker(this);
        gpsTracker.getLocation();

        getDetails();
    }

    private void setUpImageSlider(List<ProductDetailImagesModel> list) {

        for ( ProductDetailImagesModel data : list) {
            CustomSlider textSliderView = new CustomSlider(this);
            // initialize a SliderLayout
            textSliderView
                    .description("")
                    .image(ApplicationConstants.INSTANCE.PRODUCT_IMAGES + data.getThumbnail())
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra","");

            viewPager.addSlider(textSliderView);
        }

        viewPager.getPagerIndicator().setDefaultIndicatorColor (0xff68B936, 0xffD0D0D0);
        viewPager.startAutoCycle(10000, 10000, true);
        viewPager.setCurrentPosition(0);
    }

    public void addToBag(View view) {

        if (addToCartProductItem !=null){

            List<Task> items = new ArrayList<>();
            Task task = new Task(addToCartProductItem, "1", unit, unitIn, currentPrice, currentPrice);
            items.add(task);
            new SaveProductList(this,items).execute();
            Toolbar_Set.INSTANCE.getCartList(this);
        }

    }

     public  void getDetails() {

         if (UtilMethods.INSTANCE.isNetworkAvialable(this)) {

             UtilMethods.INSTANCE.getProductDetails(this, pid, new mCallBackResponse() {
                 @Override
                 public void success(String from, String message) {
                     if (!message.isEmpty()){
                         Type listType = new TypeToken<ArrayList<ProductDetailModel>>(){}.getType();
                         List<ProductDetailModel> list = new Gson().fromJson(message, listType);

                         addToCartProductItem = new ProductModel(list.get(0));

                         MyApplication application = (MyApplication) getApplication();
                         AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
                         application.logLeonEvent("View content","Product Viewed " + addToCartProductItem.getName().trim() + " by "+ preferences.getLoginMobile(), 0);


                         if (list.get(0)!=null && list.get(0).getVendorName()!=null) {
                             tvName.setText(list.get(0).getVendorName().trim());
                         }

                         if (list.get(0)!=null && list.get(0).getVendorId()!=null) {
                             getDistance(list.get(0).getVendorId());
                         }

//                         tvDescription.setText(addToCartProductItem.getDescription().trim());
                         tvDescription2.setText(addToCartProductItem.getDescription().trim());
                         if (addToCartProductItem.getUnits()!=null && addToCartProductItem.getUnits().size()>0) {
                             unit = addToCartProductItem.getUnits().get(0).getUnit().trim();
                             unitIn = addToCartProductItem.getUnits().get(0).getUnitIn().trim();
                             currentPrice = addToCartProductItem.getUnits().get(0).getBuingprice().trim();
                             buingPrice = addToCartProductItem.getUnits().get(0).getCurrentprice().trim();
                         }

                         try {
                             int a = (int) Double.parseDouble("0"+currentPrice);
                             int b = (int) Double.parseDouble("0"+buingPrice);
                             int c = b-a;
                             discount = ""+c;

                             try {
                                 int p = c*100/b;
                                 tvOffer.setText(p + "%");
                             } catch (Exception ignored){
                                 cvoffer.setVisibility(View.GONE);
                             }
                         } catch (Exception ignored) {}


                         tvCurrentPrice.setText(getString(R.string.currency) + " " + currentPrice);
                         tvPrice.setText(  unit + unitIn);

                         tvPricen.setText( getString(R.string.currency) + " " + buingPrice);
                         tvPricen.setPaintFlags(tvPricen.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                         tvDiscount.setText(getString(R.string.currency) + discount);

                         if (addToCartProductItem.getProductType()!=null) {
                             if (addToCartProductItem.getProductType().equalsIgnoreCase("Non-Vegetarian")) {
                                 ivVeg.setImageDrawable(getDrawable(R.drawable.nonveg));
                                 ivVeg.setVisibility(View.VISIBLE);
                             } else if (addToCartProductItem.getProductType().equalsIgnoreCase("Vegetarian")){
                                 ivVeg.setImageDrawable(getDrawable(R.drawable.veg));
                                 ivVeg.setVisibility(View.VISIBLE);
                             } else {
                                 ivVeg.setVisibility(View.GONE);
                             }
                         } else {
                             ivVeg.setVisibility(View.GONE);
                         }

                         setRecycler(addToCartProductItem.getUnits());
                     }
                 }

                 @Override
                 public void fail(String from) {

                 }
             });

             UtilMethods.INSTANCE.getProductImages(this, pid, new mCallBackResponse() {
                 @Override
                 public void success(String from, String message) {
                     if (!message.isEmpty()){
                         Type listType = new TypeToken<ArrayList<ProductDetailImagesModel>>(){}.getType();
                         List<ProductDetailImagesModel> list = new Gson().fromJson(message, listType);
                         setUpImageSlider(list);
                     }
                 }

                 @Override
                 public void fail(String from) {

                 }
             });
         } else {
             UtilMethods.INSTANCE.internetNotAvailableMessage(this);
         }
     }

    private void getDistance(String id) {
        AppSharedPreferences preferences = new AppSharedPreferences(getApplication());
        String distance = UtilMethods.getDistanceByVendorId(preferences, id);
        txt_vName.setText(String.format("%s km", distance));
    }

    private void setRecycler(List<UnitModel> list) {
        UnitListAdapter adapter = new UnitListAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    public void onClick(int position) {

        if (addToCartProductItem.getUnits().get(position) !=null) {
            UnitModel temp = addToCartProductItem.getUnits().get(position);
            unit = temp.getUnit();
            unitIn = temp.getUnitIn();
            currentPrice = temp.getBuingprice();
            buingPrice = temp.getCurrentprice();
            tvCurrentPrice.setText(getString(R.string.currency) + " " + currentPrice);
            tvPricen.setText(getString(R.string.currency) + " " + buingPrice);
            tvPricen.setPaintFlags(tvPricen.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvPrice.setText(  unit + unitIn);

            try {
                int a = (int) Double.parseDouble("0"+currentPrice);
                int b = (int) Double.parseDouble("0"+buingPrice);
                int c = b-a;
                discount = ""+c;
            } catch (Exception ignored) {}

            tvDiscount.setText(getString(R.string.currency) + discount);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar_Set.INSTANCE.getCartList(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}
