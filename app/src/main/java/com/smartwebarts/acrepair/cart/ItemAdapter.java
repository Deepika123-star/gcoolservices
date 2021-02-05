package com.smartwebarts.acrepair.cart;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartwebarts.acrepair.R;
import com.smartwebarts.acrepair.models.VendorModel;
import com.smartwebarts.acrepair.utils.ApplicationConstants;
import com.smartwebarts.acrepair.utils.MyGlide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    CartActivity context; List<VendorModel> vendorModelList;

    public ItemAdapter(CartActivity context, List<VendorModel> vendorModelList) {
        this.context = context;
        this.vendorModelList = vendorModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.bottom_sheet_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (vendorModelList != null && vendorModelList.get(position) != null) {
            holder.shaopname.setText(vendorModelList.get(position).getShopName());
            holder.location.setText(String.format("%s \u2022 %s km", vendorModelList.get(position).getAddress(), vendorModelList.get(position).getLocation()));
            holder.txtBook.setOnClickListener(v -> context.onItemClick(vendorModelList.get(position)));
            holder.txtCall.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+vendorModelList.get(position).getContact()));
                context.startActivity(intent);
            });
            MyGlide.with(context, ApplicationConstants.INSTANCE.SITE_URL +"assets/img/cat_img/" + vendorModelList.get(position).getThumbnail(), holder.imageView2);
        }
    }

    @Override
    public int getItemCount() {
        return vendorModelList!=null?vendorModelList.size():vendorModelList.size();
    }

    interface ItemListener {
        void onItemClick(VendorModel item);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView shaopname, location, txtCall, txtBook;
        ImageView imageView2;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            shaopname = itemView.findViewById(R.id.textView7);
            location = itemView.findViewById(R.id.textView8);
            imageView2 = itemView.findViewById(R.id.imageView3);
            txtCall = itemView.findViewById(R.id.txt_call);
            txtBook = itemView.findViewById(R.id.txt_book);
        }
    }
}
