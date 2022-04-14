package com.example.cryptoapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Post extends AppCompatActivity {

    private TextView postTitle, postContent;
    private RecyclerView commentsRV;
    private ArrayList<String> allComments;
    private PostCommentsRVAdapter commentsRVAdapter;
    private ImageButton addCommentBTN;
    private EditText commentContent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        allComments = new ArrayList<>();
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        commentsRV = findViewById(R.id.commentsList);
        addCommentBTN = findViewById(R.id.addComment);
        commentContent = findViewById(R.id.commentContent);

        loadComments();
        Intent incomingIntent = getIntent();
        PostRVModel intentPost = (PostRVModel) incomingIntent.getSerializableExtra("postData");

        postTitle.setText(intentPost.getPostTitle());
        postContent.setText(intentPost.getPostDesc());

        commentsRVAdapter = new PostCommentsRVAdapter(allComments, Post.this, this);
        commentsRV.setLayoutManager(new LinearLayoutManager(this));
        commentsRV.setAdapter(commentsRVAdapter);

        addCommentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

    }

    private void loadComments(){
        allComments.add("Testing");
        allComments.add("second comment test");
        allComments.add("third comment test");
        allComments.add("1");
        allComments.add(" comment test");
        allComments.add(" test");
    }

    private void addComment(){
        allComments.add(commentContent.getText().toString());
        commentsRVAdapter.notifyDataSetChanged();
        commentContent.setText("");
    }

    //Loads option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.portfolio_edit_menu, menu);

        return true;
    }

    //To change to new activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.backButton) {
            Intent intent = new Intent(this, Forum.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
