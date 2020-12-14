package com.smartwebarts.acrepair.RateCard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.IsVisible;
import com.smartwebarts.acrepair.models.RateCardModel;
import com.smartwebarts.acrepair.models.SubCategoryModel;
import com.smartwebarts.acrepair.retrofit.UtilMethods;
import com.smartwebarts.acrepair.retrofit.mCallBackResponse;
import com.smartwebarts.acrepair.shopbycategory.ChildListAdapter;
import com.smartwebarts.acrepair.shopbycategory.ExpandableListAdapter;
import com.smartwebarts.acrepair.utils.ApplicationConstants;
import com.smartwebarts.acrepair.utils.MyGlide;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ExpandebleListViewAdapter extends RecyclerView.Adapter<ExpandebleListViewAdapter.MyViewHolder> {

    private Context mContext;
    private List<CategoryModel> categoryModels;
    private List<IsVisible> isVisible;
    private static int currentPosition = -1;


    public ExpandebleListViewAdapter(Context mContext, List<CategoryModel> categoryModels) {
        this.mContext = mContext;
        this.categoryModels = categoryModels;
        isVisible = new ArrayList<>();
        for (int i = 0; i < categoryModels.size(); i++) {
            isVisible.add(new IsVisible());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rate_row_group, parent, false ));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CategoryModel category = categoryModels.get(position);

        holder.groupHeader.setText(category.getName());
        MyGlide.with(mContext, ApplicationConstants.INSTANCE.CATEGORY_IMAGES + category.getImage(), holder.imageView);

        if (isVisible.get(position).isVisible()) {
            holder.recyclerView.setVisibility(View.VISIBLE);
        } else {
            holder.recyclerView.setVisibility(View.GONE);
        }

        UtilMethods.INSTANCE.setServices(mContext, category.getId(),  new mCallBackResponse() {
            @Override
            public void success(String from, String message) {

                Type listType = new TypeToken<ArrayList<RateCardModel>>(){}.getType();
                List<RateCardModel> list = new Gson().fromJson(message, listType);
                setProductRecycler(holder.recyclerView, list,category);
            }

            @Override
            public void fail(String from) {
            }
        });

        holder.shootup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.shootup.setVisibility(View.GONE);
                holder.dropdown.setVisibility(View.VISIBLE);
                holder.recyclerView.setVisibility(View.GONE);
                isVisible.get(position).setVisible(false);
            }
        });

        if (isVisible.get(position).isVisible()) {
            holder.shootup.setVisibility(View.GONE);
            holder.dropdown.setVisibility(View.VISIBLE);
            Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
            holder.recyclerView.startAnimation(slideUp);
            holder.recyclerView.setVisibility(View.GONE);
        }

        holder.recyclerView.setVisibility(View.GONE);

        if (currentPosition == position) {
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.dropdown.setVisibility(View.GONE);
            holder.shootup.setVisibility(View.VISIBLE);
        }

        holder.dropdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getting the position of the item to expand it
                isVisible.get(position).setVisible(true);
                currentPosition = position;

                //reloding the list
                notifyDataSetChanged();
            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.shootup.getVisibility() == View.VISIBLE) {
                    holder.shootup.performClick();
                } else if (holder.dropdown.getVisibility() == View.VISIBLE) {
                    holder.dropdown.performClick();
                }
            }
        });
    }

    private void setProductRecycler(RecyclerView rvSubCategory, List<RateCardModel> list, CategoryModel categoryModel) {
        RateCardChildListAdapter adapter = new RateCardChildListAdapter(mContext, list,  categoryModel);
        rvSubCategory.setItemAnimator(new DefaultItemAnimator());
        rvSubCategory.setAdapter(adapter);

    }

    @Override
    public int getItemCount() {
        return categoryModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView groupHeader;
        ImageView dropdown, shootup, imageView;
        RecyclerView recyclerView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            groupHeader = itemView.findViewById(R.id.groupHeader);
            dropdown = itemView.findViewById(R.id.plus);
            shootup = itemView.findViewById(R.id.minus);
            imageView = itemView.findViewById(R.id.imageView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
