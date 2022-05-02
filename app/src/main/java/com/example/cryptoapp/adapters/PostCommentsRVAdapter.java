package com.example.cryptoapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapp.R;
import com.example.cryptoapp.models.CommentModel;

import java.util.ArrayList;
import java.util.List;

public class PostCommentsRVAdapter extends RecyclerView.Adapter<PostCommentsRVAdapter.ViewHolder> {

    private ArrayList<CommentModel> commentArrayList;
    private Context context;
    private Activity activity;

    public PostCommentsRVAdapter(ArrayList<CommentModel> commentArrayList, Activity activity, Context context) {
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
        CommentModel commentModel = commentArrayList.get(position);
        holder.comment.setText(commentModel.getComment());
        holder.userID.setText(commentModel.getUserID());
        holder.timeStamp.setText(commentModel.getTimeStamp());
    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView comment, userID, timeStamp;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.idComment);
            userID = itemView.findViewById(R.id.idUserID);
            timeStamp = itemView.findViewById(R.id.idTimeStamp);
        }

    }
    public List<CommentModel> getList() {
        return commentArrayList;
    }
}
