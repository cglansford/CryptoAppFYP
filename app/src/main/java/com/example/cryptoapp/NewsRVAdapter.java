package com.example.cryptoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NewsRVAdapter extends RecyclerView.Adapter<NewsRVAdapter.ViewHolder> {

    //Creating the adapter for the recycler view to present the list data of the api data

    private ArrayList<NewsRVModel> newsRVModelArrayList;
    private Context context;

    public NewsRVAdapter(ArrayList<NewsRVModel> newsRVModelArrayList, Context context) {
        this.newsRVModelArrayList = newsRVModelArrayList;
        this.context = context;
    }

    //Updates the list once filtered
    public void filterList(ArrayList<NewsRVModel> filteredList){
        newsRVModelArrayList = filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public NewsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_rv_item,parent,false);
        return new NewsRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsRVAdapter.ViewHolder holder, int position) {
        NewsRVModel newsRVModel = newsRVModelArrayList.get(position);
        holder.headlineTV.setText(newsRVModel.getHeadline());
        holder.urlSourceTV.setText(newsRVModel.getSourceURL());
        holder.sourceNameTV.setText(newsRVModel.getSourceName());
    }

    @Override
    public int getItemCount() {
        return newsRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView headlineTV, urlSourceTV, sourceNameTV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            headlineTV = itemView.findViewById(R.id.idTVHeadline);
            urlSourceTV = itemView.findViewById(R.id.idTVLink);
            sourceNameTV = itemView.findViewById(R.id.idTVSource);
        }
    }
}