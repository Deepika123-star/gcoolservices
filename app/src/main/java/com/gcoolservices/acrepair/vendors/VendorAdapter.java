package com.gcoolservices.acrepair.vendors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.models.VendorModel;
import com.gcoolservices.acrepair.productlist.ProductListActivity3;

import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.MyViewHolder>{

    private Context context;
    private List<VendorModel> list;

    public VendorAdapter(Context context, List<VendorModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VendorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VendorAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.vendor_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VendorAdapter.MyViewHolder holder, final int position) {

        String name = list.get(position).getShopName().trim();
        holder.categoryName.setText(name);
        holder.distance.setText(String.format("%s km", list.get(position).getLocation()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductListActivity3.class);
            intent.putExtra(ProductListActivity3.VID, list.get(position));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<VendorModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName, distance;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            distance = itemView.findViewById(R.id.distance);
        }
    }
}
