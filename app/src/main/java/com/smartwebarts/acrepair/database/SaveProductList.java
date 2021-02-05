package com.smartwebarts.acrepair.database;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.google.gson.Gson;
import com.smartwebarts.acrepair.MyApplication;
import com.smartwebarts.acrepair.productlist.ProductDetailActivity;
import com.smartwebarts.acrepair.shared_preference.AppSharedPreferences;

public class SaveProductList extends AsyncTask<String, String, String> {

    private final List<Task> Tasks;
    private final Context context;

    public SaveProductList(Context context, List<Task> Tasks) {
        this.Tasks = Tasks;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {

        DatabaseClient.getmInstance(context)
                .getAppDatabase()
                .taskDao()
                .deleteAll();


        DatabaseClient.getmInstance(context)
                .getAppDatabase()
                .taskDao()
                .insert(Tasks);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
//        Toast.makeText(context, "Added to bag", Toast.LENGTH_SHORT).show();
        if (context instanceof ProductDetailActivity) {
            final SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog .setContentText("Added to Basket");
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    dialog.dismissWithAnimation();
                }
            });
            dialog.show();
        }

        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            MyApplication application = (MyApplication) activity.getApplication();
            AppSharedPreferences preferences = new AppSharedPreferences(application);
            application.logLeonEvent("Add to cart", "Add to Cart " + new Gson().toJson(this.Tasks) + " by "+ preferences.getLoginMobile(), 0);
        }
    }
}
