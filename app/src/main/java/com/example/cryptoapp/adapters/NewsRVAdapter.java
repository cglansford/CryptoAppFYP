package com.example.cryptoapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapp.R;
import com.example.cryptoapp.RecyclerViewInterface;
import com.example.cryptoapp.models.NewsRVModel;

import java.util.ArrayList;
import java.util.List;

public class NewsRVAdapter extends RecyclerView.Adapter<NewsRVAdapter.ViewHolder> {

    //Creating the adapter for the recycler view to present the list data of the api data
    private final RecyclerViewInterface recyclerViewInterface;
    private ArrayList<NewsRVModel> newsRVModelArrayList;
    private Context context;

    public NewsRVAdapter(ArrayList<NewsRVModel> newsRVModelArrayList, Context context, RecyclerViewInterface recyclerViewInterface) {
        this.newsRVModelArrayList = newsRVModelArrayList;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface;
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
        return new NewsRVAdapter.ViewHolder(view, recyclerViewInterface);
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
        public ViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            headlineTV = itemView.findViewById(R.id.idTVHeadline);
            urlSourceTV = itemView.findViewById(R.id.idTVLink);
            sourceNameTV = itemView.findViewById(R.id.idTVSource);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION)
                            recyclerViewInterface.onItemLongClick(pos);
                    }
                    return true;
                }
            });


        }
    }

    public List<NewsRVModel> getList(){return newsRVModelArrayList;}
}