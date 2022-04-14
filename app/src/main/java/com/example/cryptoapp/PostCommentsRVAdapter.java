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

public class PostCommentsRVAdapter extends RecyclerView.Adapter<PostCommentsRVAdapter.ViewHolder> {

    private ArrayList<String> commentArrayList;
    private Context context;
    private Activity activity;

    public PostCommentsRVAdapter(ArrayList<String> commentArrayList, Activity activity, Context context) {
        this.commentArrayList = commentArrayList;
        this.activity = activity;
        this.context = context;
    }


    @NonNull
    @Override
    public PostCommentsRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_rv_item, parent, false);
        return new PostCommentsRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostCommentsRVAdapter.ViewHolder holder, int position) {
        String commentModel = commentArrayList.get(position);
        holder.comment.setText(commentModel);
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView comment;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.idComment);
        }

    }
    public List<String> getList() {
        return commentArrayList;
    }
}
