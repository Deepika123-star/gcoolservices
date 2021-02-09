package com.gcoolservices.acrepair.dashboard.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.models.CategoryModel;
import com.gcoolservices.acrepair.models.SubCategoryModel;
import com.gcoolservices.acrepair.productlist.ProductListActivity2;
import com.gcoolservices.acrepair.utils.ApplicationConstants;
import com.gcoolservices.acrepair.utils.MyGlide;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    private Context context;
//    private List<HomeProductsModel> list;
    private List<SubCategoryModel> list;
    private CategoryModel categoryModel;

//    public GridAdapter(Context context, List<HomeProductsModel> list) {
//        this.context = context;
//        this.list = list;
//    }

    public GridAdapter(Context context, List<SubCategoryModel> list, CategoryModel categoryModel) {
        this.context = context;
        this.list = list;
        this.categoryModel = categoryModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_image_view3, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        MyGlide.with(context, ApplicationConstants.INSTANCE.CATEGORY_IMAGES + list.get(position).getImage(), holder.imageView);

        holder.textView.setText(list.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductListActivity2.class);
                intent.putExtra(ProductListActivity2.CID, categoryModel.getId());
                intent.putExtra(ProductListActivity2.SID, list.get(position).getId());
                intent.putExtra(ProductListActivity2.SNAME, list.get(position).getName());
                context.startActivity(intent);
            }
        });

//        MyGlide.with(context, ApplicationConstants.INSTANCE.PRODUCT_IMAGES + list.get(position).getThumbnail(), holder.imageView);
//
//        holder.textView.setText(list.get(position).getName());
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, ProductDetailActivity.class);
//                intent.putExtra(ProductDetailActivity.ID, list.get(position).getId());
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.tvName);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}

