package com.smartwebarts.acrepair.RateCard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.models.CategoryModel;
import com.smartwebarts.acrepair.models.RateCardModel;
import com.smartwebarts.acrepair.models.SubSubCategoryModel;

import java.util.List;

public class RateCardAdapter extends RecyclerView.Adapter<RateCardAdapter.ViewHolder> {
    private List<RateCardModel>rateCardModels;

    public RateCardAdapter(List<RateCardModel> rateCardModels) {
        this.rateCardModels = rateCardModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_card_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //get data from Model
        String rateCardservices=rateCardModels.get(position).getName();
        String rateCardPrice=rateCardModels.get(position).getBuingprice();
holder.setData(rateCardservices,rateCardPrice);
    }

    @Override
    public int getItemCount() {
        //get the size of List
        return rateCardModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView services;
        private TextView price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //initialization of textView which is created in layout file
            services=itemView.findViewById(R.id.rate_card_services);
            price=itemView.findViewById(R.id.rate_card_price);

        }

        public void setData(String rateCardservices, String rateCardPrice) {
            //set Data in layout file of rate card Layout
            String rs="Rs.";
            services.setText(rateCardservices);
            price.setText(rs+rateCardPrice);
        }
    }
//set data in rate Card Model List
    public void setRateCardModels(List<RateCardModel> rateCardModels) {
        this.rateCardModels = rateCardModels;
        notifyDataSetChanged();
    }
}
