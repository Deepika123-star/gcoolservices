package com.smartwebarts.acrepair.RateCard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.IsVisible;
import com.smartwebarts.acrepair.models.RateCardModel;
import com.smartwebarts.acrepair.models.SubCategoryModel;

import java.util.ArrayList;
import java.util.List;

public class RateCardHeaderAdapter extends RecyclerView.Adapter<RateCardHeaderAdapter.MYViewHolder> {
    private Context context;
    private List<RateCardModel> list;
    private static int currentPosition = -1;
    private CategoryModel category;
    private List<IsVisible> isVisible;

    public RateCardHeaderAdapter(Context context, List<RateCardModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MYViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.ratecardheader_layout,parent,false);
        return new MYViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MYViewHolder holder, int position) {
//        System.out.println(list.get(position).getRateheading());
//        holder.tvCategoryName.setText(list.get(position).getRateheading());
//        holder.rvSubCategory.setVisibility(View.GONE);
String rate=list.get(position).getName();
//System.out.println(rate);
holder.setData(rate);

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MYViewHolder extends RecyclerView.ViewHolder {

       private TextView tvCategoryName;
        RecyclerView rvSubCategory;
        TextView services,price;
        ImageView shootup;
        public MYViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName=itemView.findViewById(R.id.tv_category_heading);
            rvSubCategory=itemView.findViewById(R.id.rvSubCategoryRate);
            shootup = itemView.findViewById(R.id.up);
        }

        public void setData(String rate) {
            tvCategoryName.setText(rate);
        }
    }
    //set data in rate Card Model List
    public void setRateCardModels(List<RateCardModel> rateCardModels) {
        this.list = rateCardModels;
        notifyDataSetChanged();
    }
}
