package com.example.cryptoapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CurrencyRVAdapter extends RecyclerView.Adapter<CurrencyRVAdapter.ViewHolder> {

    //Creating the adapter for the recycler view to present the list data of the api data

    private ArrayList<CurrencyRVModel> currencyRVModelArrayList;
    private ArrayList<String> favListCurrencies;
     private Context context;
     private Activity activity;
     //To convert prices into 6 decimals as some coins are small price, large mcap
     private static DecimalFormat df6 = new DecimalFormat("#.######");
    private OnEditListener mOnEditListener;

    public CurrencyRVAdapter(ArrayList<CurrencyRVModel> currencyRVModelArrayList, ArrayList<String> favListCurrencies, Activity activity, Context context, OnEditListener onEditListener) {
        this.currencyRVModelArrayList = currencyRVModelArrayList;
        this.favListCurrencies = favListCurrencies;
        this.activity = activity;
        this.context = context;
        this.mOnEditListener = onEditListener;
    }

    //Updates the list once filtered
    public void filterList(ArrayList<CurrencyRVModel> filteredList){
        currencyRVModelArrayList = filteredList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CurrencyRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.currency_rv_item,parent,false);
        return new CurrencyRVAdapter.ViewHolder(view,mOnEditListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CurrencyRVAdapter.ViewHolder holder, int position) {
        CurrencyRVModel currencyRVModel = currencyRVModelArrayList.get(position);
        holder.currencyNameTV.setText(currencyRVModel.getName());
        holder.tickerTV.setText(currencyRVModel.getTicker());
        //Making sure price is in correct format
        holder.rateTV.setText("$ " + df6.format(currencyRVModel.getPrice()));

        //Sets star to be full if crypto is in users favlist
        for(int i = 0; i< favListCurrencies.size(); i++){
            if(currencyRVModel.getName().equalsIgnoreCase(favListCurrencies.get(i))){
                holder.star.setImageResource(R.drawable.ic_full_star);
            }
        }
    }

    @Override
    public int getItemCount() {
        return currencyRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView currencyNameTV, tickerTV, rateTV;
        private ImageButton star;
        OnEditListener onEditListener;
        public ViewHolder(@NonNull View itemView,OnEditListener onEditListener) {
            super(itemView);
            currencyNameTV = itemView.findViewById(R.id.idTVCurrencyName);
            tickerTV = itemView.findViewById(R.id.idTVTicker);
            rateTV = itemView.findViewById(R.id.idTVCurrencyRate);
            star = itemView.findViewById(R.id.star);
            this.onEditListener = onEditListener;
            star.setOnClickListener(this);
        }

        //When star is clicked change image
        @Override
        public void onClick(View v) {
            onEditListener.onEditClick(getAdapterPosition());
            if(String.valueOf(star.getTag()).equalsIgnoreCase("starOutline")){
                star.setImageResource(R.drawable.ic_full_star);
                star.setTag("starFull");
            }else{
                star.setImageResource(R.drawable.ic_star_outline);
                star.setTag("starOutline");
            }
        }
    }

    //To send position of clicked item in activity
    public interface OnEditListener{
        void onEditClick(int position);
    }

    public List<CurrencyRVModel> getList(){return currencyRVModelArrayList;}
}
