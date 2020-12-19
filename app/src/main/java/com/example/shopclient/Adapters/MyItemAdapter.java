package com.example.shopclient.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopclient.Activities.MyOpenedActivity;
import com.example.shopclient.Models.Item;
import com.example.shopclient.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<Item> items;
    public String login_email;
    Activity mActivity;
    public MyItemAdapter(List<Item> items,String login_email,Activity mActivity) {
        this.items = items;
        this.login_email = login_email;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout2, parent, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, position);
            Item currentItem = items.get(position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    class ItemViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImage;
        public TextView mTitle;
        public TextView mPrice;
        public TextView mPlace;
        public TextView mDate;
        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.titleTextView);
            mPrice = itemView.findViewById(R.id.priceTextView);
            mPlace = itemView.findViewById(R.id.placeTextView);
            mDate = itemView.findViewById(R.id.dateTextView);
            mImage = itemView.findViewById(R.id.imageView1);
        }

    }


    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
    }

    private void populateItemRows(ItemViewHolder holder, int position) {
        Item currentItem = items.get(position);
        holder.mTitle.setText(currentItem.getTitle());
        holder.mPrice.setText(currentItem.getPrice());
        holder.mPlace.setText(currentItem.getAddress());
        String[] date = currentItem.getArrival().split("T");
        String[] date2 = date[0].split("-");
        String month = "";
        switch (date2[1]){
            case "1":month = "January";
            case "2":month = "February";
            case "3":month = "March";
            case "4":month = "April";
            case "5":month = "May";
            case "6":month = "June";
            case "7":month = "July";
            case "8":month = "August";
            case "9":month = "September";
            case "10":month = "October";
            case "11":month = "November";
            case "12":month = "December";
        }
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z");
        Date getdate = new Date(System.currentTimeMillis());
        String formatteddate = formatter.format(getdate);
        String[] getdatesp = formatteddate.split("T");
        if(getdatesp[0].equals(date[0])){
            holder.mDate.setText("Today at "+date[1].split(":")[0]+":"+date[1].split(":")[1]);
        }else{
            holder.mDate.setText(date2[2] + " "+month+ " "+date2[0]);
        }
        if(!currentItem.getImg().equals("empty")){
            byte[] decodeString = Base64.decode(currentItem.getImg(), Base64.DEFAULT);
            Bitmap decodebitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
            holder.mImage.setImageBitmap(decodebitmap);
        }
        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MyOpenedActivity.class);
                intent.putExtra("id", currentItem.getId());
                intent.putExtra("login_email", login_email);
                v.getContext().startActivity(intent);
                mActivity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            }
        });
    }
}
