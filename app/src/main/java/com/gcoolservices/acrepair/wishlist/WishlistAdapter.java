package com.gcoolservices.acrepair.wishlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.gcoolservices.acrepair.R;
import com.gcoolservices.acrepair.database.DatabaseClient;
import com.gcoolservices.acrepair.database.SaveProductList;
import com.gcoolservices.acrepair.database.Task;
import com.gcoolservices.acrepair.database.WishItem;
import com.gcoolservices.acrepair.models.ProductModel;
import com.gcoolservices.acrepair.productlist.ProductDetailActivity;
import com.gcoolservices.acrepair.utils.ApplicationConstants;
import com.gcoolservices.acrepair.utils.MyGlide;
import com.gcoolservices.acrepair.utils.Toolbar_Set;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.MyViewHolder>{

    private Context context;
    private List<ProductModel> list;

    public WishlistAdapter(Context context, List<ProductModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WishlistAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishlistAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.grid_item_wishlist, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.MyViewHolder holder, final int position) {

        try {

            MyGlide.with(context,ApplicationConstants.INSTANCE.PRODUCT_IMAGES + list.get(position).getThumbnail(),holder.prodImage);

            holder.txt_pName.setText(list.get(position).getName().trim());
            holder.txt_pInfo.setText(list.get(position).getDescription().trim());
            holder.txt_unit.setText(list.get(position).getUnit().trim() + list.get(position).getUnitIn().trim());
            holder.currentprice.setText( list.get(position).getCurrentprice().trim());
//            holder.price.setText( list.get(position).getPrice().trim());
//            holder.price.setPaintFlags(holder.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.addToWishList.setImageDrawable(
                    context.getDrawable(list.get(position).isWishlist()?
                            R.drawable.ic_heart_red:
                            R.drawable.ic_heart_black));

//            double currentPrice = Double.parseDouble("0"+list.get(position).getCurrentprice().trim());
//            double price = Double.parseDouble("0"+list.get(position).getPrice().trim());
//            double discount = (price - currentPrice) *100/price;
//            NumberFormat nf = NumberFormat.getInstance();
//            nf.setMaximumFractionDigits(2);
//            holder.txt_discountOff.setText(""+nf.format(discount*-1)+"%");

        } catch ( Exception ignored){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra(ProductDetailActivity.ID, list.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView prodImage;
        ImageView addToWishList;
        TextView txt_pName, txt_pInfo, txt_unit, currentprice, price, txt_discountOff, txtQuan;
        RelativeLayout rlQuan;
        LinearLayout ll_addQuan, ll_Add;
        TextView plus, minus;
        int minteger = 0;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            prodImage = itemView.findViewById(R.id.prodImage);
            addToWishList = itemView.findViewById(R.id.addToWishList);

            txt_pName = itemView.findViewById(R.id.txt_pName);
            txt_pInfo = itemView.findViewById(R.id.txt_pInfo);
            txt_unit = itemView.findViewById(R.id.txt_current_price);
            currentprice = itemView.findViewById(R.id.currentprice);
            price = itemView.findViewById(R.id.txt_price);
            txt_discountOff = itemView.findViewById(R.id.txt_discountOff);

            rlQuan =  itemView.findViewById(R.id.rlQuan);
            ll_Add =  itemView.findViewById(R.id.ll_Add);
            ll_addQuan =  itemView.findViewById(R.id.ll_addQuan);
            txtQuan =  itemView.findViewById(R.id.txtQuan);
            minus =  itemView.findViewById(R.id.minus);
            plus =  itemView.findViewById(R.id.plus);

            ll_Add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_Add.setVisibility(View.GONE);
                    ll_addQuan.setVisibility(View.VISIBLE);
                    txtQuan.setText("1");
                    MyViewHolder.this.addToBag("1");
                }
            });

            plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyViewHolder.this.increaseInteger();

                    if (Float.parseFloat(txtQuan.getText().toString()) == 1) {

                    } else if (Float.parseFloat(txtQuan.getText().toString()) > 1) {
                        minus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v1) {
                                MyViewHolder.this.decreaseInteger();
                            }
                        });
                    }
                }
            });
        }


        public void increaseInteger() {
            minteger = minteger + 1;
            display(minteger);
        }

        public void decreaseInteger() {
            if (minteger == 1) {
                minteger = 1;
                display(minteger);
                ll_addQuan.setVisibility(View.GONE);
                ll_Add.setVisibility(View.VISIBLE);
            } else {
                minteger = minteger - 1;
                display(minteger);

            }
        }

        private void display(Integer number) {
            txtQuan.setText("" + number);
            addToBag(""+number);
        }

        public void addToBag(String quantity) {

            int qty = Integer.parseInt("0"+quantity.trim());
            int price = Integer.parseInt(list.get(getAdapterPosition()).getCurrentprice().trim());
            int finalPrice = qty*price;

            List<Task> items = new ArrayList<>();
            Task task = new Task(list.get(getAdapterPosition()), quantity, ""+finalPrice);
            items.add(task);
            new SaveProductList(context,items).execute();
            Toolbar_Set.INSTANCE.getCartList((Activity) context);
        }

    }

    public  void delete(final Activity activity, final WishItem task) {

        class GetTasks extends AsyncTask<Void, Void, ArrayList<Task>> {

            @Override
            protected ArrayList<Task> doInBackground(Void... voids) {
                DatabaseClient.getmInstance(activity.getApplicationContext())
                        .getAppDatabase()
                        .wishDao()
                        .delete(task);
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<Task> tasks) {

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

}
