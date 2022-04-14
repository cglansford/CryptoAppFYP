package com.example.cryptoapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PostRVAdapter extends RecyclerView.Adapter<PostRVAdapter.ViewHolder> {

//Creating the adapter for the recycler view to present the list data of the api data

    private ArrayList<PostRVModel> postRVModelArrayList;
    private Context context;
    private Activity activity;
    private OnEditListener mOnEditListener;




    public PostRVAdapter(ArrayList<PostRVModel> postRVModelArrayList, Activity activity, Context context, OnEditListener onEditListener) {
        this.postRVModelArrayList = postRVModelArrayList;
        this.activity = activity;
        this.context = context;
        this.mOnEditListener = onEditListener;
    }


    @NonNull
    @Override
    public PostRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.forum_post_rv_item, parent, false);
        return new PostRVAdapter.ViewHolder(view, mOnEditListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostRVAdapter.ViewHolder holder, int position) {
        PostRVModel postRVModel = postRVModelArrayList.get(position);
        holder.postTitle.setText(postRVModel.getPostTitle());
        holder.postDesc.setText(postRVModel.getPostDesc());
        holder.postCreator.setText(postRVModel.getUidOfCreator());
    }

    @Override
    public int getItemCount() {
        return postRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView postTitle, postDesc, postCreator;

        OnEditListener onEditListener;

        public ViewHolder(@NonNull View itemView, OnEditListener onEditListener) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.idPostTitle);
            postDesc = itemView.findViewById(R.id.idPostDesc);
            postCreator = itemView.findViewById(R.id.idPostUsername);
            this.onEditListener = onEditListener;
            postTitle.setOnClickListener(this);
            postDesc.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onEditListener.onEditClick(getAdapterPosition());
        }


    }

    //To send position of clicked item in activity
    public interface OnEditListener {
        void onEditClick(int position);
    }

    public List<PostRVModel> getList() {
        return postRVModelArrayList;
    }
}
