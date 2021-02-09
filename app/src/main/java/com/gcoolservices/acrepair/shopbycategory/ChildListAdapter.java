package com.gcoolservices.acrepair.shopbycategory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.models.CategoryModel;
import com.gcoolservices.acrepair.models.SubCategoryModel;
import com.gcoolservices.acrepair.productlist.ProductListActivity2;
import com.gcoolservices.acrepair.utils.ApplicationConstants;
import com.gcoolservices.acrepair.utils.MyGlide;

import java.util.List;

public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.MyViewHolder> {

    private Context mContext;
    private List<SubCategoryModel> list;
    private CategoryModel categoryModel;
    public ChildListAdapter(Context mContext, List<SubCategoryModel> list, CategoryModel categoryModel) {
        this.mContext = mContext;
        this.list = list;
        this.categoryModel = categoryModel;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.row_child, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        SubCategoryModel model = list.get(position);
        holder.groupHeader.setText(model.getName());
        MyGlide.with(mContext, ApplicationConstants.INSTANCE.CATEGORY_IMAGES + model.getImage(), holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductListActivity2.class);
                intent.putExtra(ProductListActivity2.CID, categoryModel.getId());
                intent.putExtra(ProductListActivity2.SID, model.getId());
                intent.putExtra(ProductListActivity2.SNAME, model.getName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView groupHeader;
        ImageView plus, imageView;
        RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            groupHeader = itemView.findViewById(R.id.groupHeader);
            plus = itemView.findViewById(R.id.plus);
            imageView = itemView.findViewById(R.id.imageView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
