package com.example.cryptoapp;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PortfolioRVAdapter extends RecyclerView.Adapter<PortfolioRVAdapter.ViewHolder> {

    private ArrayList<PortfolioRVModel> portfolioRVModelArrayList;
    private Context context;
    private Activity activity;
    private OnEditListener mOnEditListener;

    public PortfolioRVAdapter(ArrayList<PortfolioRVModel> portfolioRVModelArrayList, Activity activity, Context context, OnEditListener onEditListener){
        this.portfolioRVModelArrayList = portfolioRVModelArrayList;
        this.activity = activity;
        this.context = context;
        this.mOnEditListener = onEditListener;
    }

    @NonNull
    @Override
    public PortfolioRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.portfolio_rv_item, parent,false);
        return new PortfolioRVAdapter.ViewHolder(view, mOnEditListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioRVAdapter.ViewHolder holder, int position){

        PortfolioRVModel portfolioRVModel = portfolioRVModelArrayList.get(position);
        holder.name.setText(portfolioRVModel.getName());
        holder.amount.setText(String.valueOf(portfolioRVModel.getHoldingAmount()));

    }

    @Override
    public int getItemCount(){
        return portfolioRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView amount, name, total;
        private ImageButton editButton;
        OnEditListener onEditListener;

        public ViewHolder(@NonNull View itemView, OnEditListener onEditListener){
            super(itemView);
            name = itemView.findViewById(R.id.idTVCurrencyName);
            amount = itemView.findViewById(R.id.idHolding);
            total = itemView.findViewById(R.id.idTotal);
            editButton = itemView.findViewById(R.id.imageButton);
            this.onEditListener = onEditListener;

            editButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEditListener.onEditClick(getAdapterPosition());
        }
    }

    //To send position of clicked item in activity
    public interface OnEditListener{
        void onEditClick(int position);
    }

    public List<PortfolioRVModel> getList(){return portfolioRVModelArrayList;}
}
