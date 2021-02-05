package com.smartwebarts.acrepair.RateCard;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.RateCardModel;
import com.smartwebarts.acrepair.models.SubCategoryModel;
import com.smartwebarts.acrepair.productlist.ProductListActivity2;
import com.smartwebarts.acrepair.shopbycategory.ChildListAdapter;
import com.smartwebarts.acrepair.utils.ApplicationConstants;
import com.smartwebarts.acrepair.utils.MyGlide;

import java.util.List;

public class RateCardChildListAdapter extends  RecyclerView.Adapter<RateCardChildListAdapter.MyViewHolder> {
    private Context mContext;
    private List<RateCardModel> list;
    private CategoryModel categoryModel;

    public RateCardChildListAdapter(Context mContext, List<RateCardModel> list, CategoryModel categoryModel) {
        this.mContext = mContext;
        this.list = list;
        this.categoryModel = categoryModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.ratecard_child_layout,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        RateCardModel model = list.get(position);
        holder.textView.setText(model.getName());
        holder.rate_card_price.setText(String.format("%s %s", mContext.getString(R.string.currency), model.getBuingprice()));
//        MyGlide.with(mContext, ApplicationConstants.INSTANCE.CATEGORY_IMAGES + model.getImage(), holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, ProductListActivity2.class);
//                intent.putExtra(ProductListActivity2.CID, categoryModel.getId());
//                intent.putExtra(ProductListActivity2.SID, model.getId());
//                intent.putExtra(ProductListActivity2.SNAME, model.getName());
//                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView , rate_card_price;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.groupHeaderratecard);
            rate_card_price=itemView.findViewById(R.id.rate_card_price);

        }
    }
}

