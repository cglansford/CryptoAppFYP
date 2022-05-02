package com.example.cryptoapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapp.models.FavRVModel;
import com.example.cryptoapp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FavRVAdapter extends RecyclerView.Adapter<FavRVAdapter.ViewHolder> {

    //Creating the adapter for the recycler view to present the list data of the api data

    private ArrayList<FavRVModel> favRVModelArrayList;
    private Context context;
    private Activity activity;
    //To convert prices into 6 decimals as some coins are small price, large mcap
    private static DecimalFormat df6 = new DecimalFormat("#.######");
    private OnEditListener mOnEditListener;

    public FavRVAdapter(ArrayList<FavRVModel> favRVModelArrayList, Activity activity, Context context, OnEditListener onEditListener) {
        this.favRVModelArrayList = favRVModelArrayList;
        this.activity = activity;
        this.context = context;
        this.mOnEditListener = onEditListener;
    }


    @NonNull
    @Override
    public FavRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.currency_rv_item,parent,false);
        return new FavRVAdapter.ViewHolder(view,mOnEditListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FavRVAdapter.ViewHolder holder, int position) {
        FavRVModel favRVModel = favRVModelArrayList.get(position);
        holder.currencyNameTV.setText(favRVModel.getName());
        holder.tickerTV.setText(favRVModel.getTicker());
        //Making sure price is in correct format
        holder.rateTV.setText("$ " + df6.format(favRVModel.getPrice()));
    }

    @Override
    public int getItemCount() {
        return favRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView currencyNameTV, tickerTV, rateTV;
        private ImageButton star;
        OnEditListener onEditListener;
        public ViewHolder(@NonNull View itemView, OnEditListener onEditListener) {
            super(itemView);
            star = itemView.findViewById(R.id.star);
            star.setImageResource(R.drawable.ic_full_star);
            currencyNameTV = itemView.findViewById(R.id.idTVCurrencyName);
            tickerTV = itemView.findViewById(R.id.idTVTicker);
            rateTV = itemView.findViewById(R.id.idTVCurrencyRate);
            this.onEditListener = onEditListener;
            star.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onEditListener.onEditClick(getAdapterPosition());
            star.setImageResource(R.drawable.ic_star_outline);
        }
    }



    //To send position of clicked item in activity
    public interface OnEditListener{
        void onEditClick(int position);
    }
    public List<FavRVModel> getList(){return favRVModelArrayList;}
}
