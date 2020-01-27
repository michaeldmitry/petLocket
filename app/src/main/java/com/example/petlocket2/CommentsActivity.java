package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.petlocket2.Adapter.CommentsAdapter;
import com.example.petlocket2.Adapter.PostAdapter;
import com.example.petlocket2.Model.Comment;
import com.example.petlocket2.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    EditText addcomment;
    ImageView imageprof;
    TextView post;
    private RecyclerView recyclerView;
    private CommentsAdapter commentsAdapter;
    private List<Comment> commentslist;
    String postid;
    String userid;

    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar= findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        recyclerView =findViewById(R.id.recycler_view4);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        commentslist = new ArrayList<>();
        commentsAdapter = new CommentsAdapter(this, commentslist);
        recyclerView.setAdapter(commentsAdapter);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
        addcomment= (EditText) findViewById(R.id.CommentsAddComment);
        imageprof= (ImageView) findViewById(R.id.CommentsImageProfile);
        post= (TextView) findViewById(R.id.CommentsPost);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Intent intent=  getIntent();
        postid= intent.getStringExtra("postId");
        userid= intent.getStringExtra("userId");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addcomment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "Can't send an empty comment", Toast.LENGTH_SHORT).show();

                }else
                    addcomment();
            }
        });

        getImage();
        readComments();
    }

    private void addcomment() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", addcomment.getText().toString());
        hashMap.put("user", firebaseUser.getUid());
        databaseReference.push().setValue(hashMap);
        addcomment.setText("");
    }

   private void getImage(){
       DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

               Users user= dataSnapshot.getValue(Users.class);
               Glide.with(getApplicationContext()).load(user.getImageUrl()).into(imageprof);

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

   }

   private void readComments(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentslist.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Comment cm= snapshot.getValue(Comment.class);
                    commentslist.add(cm);
                }
                commentsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
   }
}
