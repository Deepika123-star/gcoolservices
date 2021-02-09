package com.gcoolservices.acrepair.dashboard.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.models.CategoryModel;
import com.gcoolservices.acrepair.retrofit.UtilMethods;
import com.gcoolservices.acrepair.retrofit.mCallBackResponse;
import com.gcoolservices.acrepair.shopbycategory.ShopByCategoryActivity;
import com.gcoolservices.acrepair.utils.ApplicationConstants;
import com.gcoolservices.acrepair.utils.CustomSlider;

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener {

    private HomeViewModel homeViewModel;
    private SliderLayout home_list_banner;
    private RecyclerView recyclerView, recyclerViewBottom;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        init(view);

        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.slider1);
        list.add(R.drawable.slider2);
        list.add(R.drawable.slider3);
        setSlider(list);

        if (UtilMethods.INSTANCE.isNetworkAvialable(getActivity())) {
            UtilMethods.INSTANCE.categories(getActivity(), new mCallBackResponse() {
                @Override
                public void success(String from, String message) {
                    Type listType = new TypeToken<ArrayList<CategoryModel>>(){}.getType();
                    List<CategoryModel> list = new Gson().fromJson(message, listType);
                    SharedPreferences sharedpreferences = getActivity().getSharedPreferences(ApplicationConstants.INSTANCE.MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(ApplicationConstants.INSTANCE.PRODUCT_LIST, message);
                    editor.commit();
                    setTopRecycler(list);
                    setBottomRecycler(list);
                }

                @Override
                public void fail(String from) {
                }
            });

//            UtilMethods.INSTANCE.imageSlider(getActivity(), new mCallBackResponse() {
//                @Override
//                public void success(String from, String message) {
//                    Type listType = new TypeToken<ArrayList<SliderImageData>>(){}.getType();
//                    List<SliderImageData> list = new Gson().fromJson(message, listType);
//                    setSlider(list);
//                }
//
//                @Override
//                public void fail(String from) {
//                }
//            });

        } else {

            UtilMethods.INSTANCE.internetNotAvailableMessage(getActivity());
        }

        homeViewModel.getText().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        return view;
    }

    private void setBottomRecycler(List<CategoryModel> list) {
        BottomAdapter adapter = new BottomAdapter(requireActivity(), list);
        recyclerViewBottom.setAdapter(adapter);
    }


    private void init(View view){

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerViewBottom = (RecyclerView) view.findViewById(R.id.recyclerViewBottom);
        home_list_banner =  (SliderLayout) view.findViewById(R.id.home_img_slider);

    }

    private void setTopRecycler(List<CategoryModel> list) {
        TopAdapter adapter = new TopAdapter(requireActivity(), list);
        recyclerView.setLayoutManager(new GridLayoutManager(requireActivity(), 4));
        recyclerView.setAdapter(adapter);
    }

    public void shopbycategory(View view) {
        startActivity(new Intent(requireActivity(), ShopByCategoryActivity.class));
    }

    private void setSlider(List<Integer> list) {

        for (int data : list) {
            CustomSlider textSliderView = new CustomSlider(requireActivity());
            // initialize a SliderLayout
            textSliderView
//                    .description("")
                    .image(data)
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
//            textSliderView.bundle(new Bundle());
//            textSliderView.getBundle()
//                    .putString("extra","");

            home_list_banner.addSlider(textSliderView);
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}