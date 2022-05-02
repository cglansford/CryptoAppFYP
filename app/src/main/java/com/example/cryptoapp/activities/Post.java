package com.example.cryptoapp.activities;

import static com.android.volley.VolleyLog.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cryptoapp.adapters.PostCommentsRVAdapter;
import com.example.cryptoapp.R;
import com.example.cryptoapp.models.CommentModel;
import com.example.cryptoapp.models.PostRVModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

//Displays post info with previous comments. User can add comments to post
public class Post extends AppCompatActivity {

    private TextView postTitle, postContent;
    private RecyclerView commentsRV;
    private ArrayList<CommentModel> allComments;
    private PostCommentsRVAdapter commentsRVAdapter;
    private ImageButton addCommentBTN;
    private EditText commentContent;


    FirebaseUser mUser;
    FirebaseFirestore db;
    DocumentReference docRef;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        allComments = new ArrayList<>();
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        commentsRV = findViewById(R.id.commentsList);
        addCommentBTN = findViewById(R.id.addComment);
        commentContent = findViewById(R.id.commentContent);

        commentsRVAdapter = new PostCommentsRVAdapter(allComments, Post.this, this);
        commentsRV.setLayoutManager(new LinearLayoutManager(this));
        commentsRV.setAdapter(commentsRVAdapter);

        db = FirebaseFirestore.getInstance();

        //Post data from forum list after clicked
        Intent incomingIntent = getIntent();
        PostRVModel intentPost = (PostRVModel) incomingIntent.getSerializableExtra("postData");
        loadComments(intentPost.getPostTitle());

        postTitle.setText(intentPost.getPostTitle());
        postContent.setText(intentPost.getPostDesc());

        addCommentBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
                //Delay to allow comment be written to DB and then loaded for updated comments list
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadComments(postTitle.getText().toString());
                    }
                }, 500);

            }
        });

    }

    private void loadComments(String postTitle){
        allComments.clear();
        CollectionReference collectionRef = db.collection("posts");
        collectionRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(postTitle.equals(document.getString("postTitle"))){
                                    //load comments first and gets the doc ref for when adding comments
                                    docRef = document.getReference();
                                    try{
                                        if(document.get("commentsList") != null) {
                                            JSONArray json = new JSONArray(((List<?>) Objects.requireNonNull(document.get("commentsList"))).toArray());

                                            //reverse through array to display newest to oldest
                                            for (int i = json.length()-1; i >= 0; i--) {
                                                String comment = json.getJSONObject(i).getString("comment");
                                                String userID = json.getJSONObject(i).getString("userID");
                                                String timeStamp = json.getJSONObject(i).getString("timeStamp");
                                                allComments.add(new CommentModel(userID, comment, timeStamp));
                                            }
                                        }
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        commentsRVAdapter.notifyDataSetChanged();
                    }
                });
        commentsRVAdapter.notifyDataSetChanged();
    }

    private void addComment(){
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy_HH:mm:ss").format(Calendar.getInstance().getTime());
        String comment = commentContent.getText().toString();

        if(TextUtils.isEmpty(comment)){
            commentContent.setError("Required");
        }

        CommentModel aComment = new CommentModel(mUser.getEmail(), comment, timeStamp);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                docRef.update("commentsList", FieldValue.arrayUnion(aComment))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                docRef.update("commentsList", FieldValue.arrayUnion(aComment))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                            }
                        });
            }
        });

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
