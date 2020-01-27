package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
//import com.example.petlocket2.Adapter.MessageAdapter;
//import com.example.petlocket2.Model.Message;
import com.example.petlocket2.Adapter.MessageAdapter;
import com.example.petlocket2.Model.Chat;
import com.example.petlocket2.Model.Post;
import com.example.petlocket2.Model.Users;
//import com.example.petlocket2.Notifications.Client;
//import com.example.petlocket2.Notifications.Data;
//import com.example.petlocket2.Notifications.MyResponse;
//import com.example.petlocket2.Notifications.Sender;
//import com.example.petlocket2.Notifications.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    ImageView profile_image;
    TextView username;

    ImageButton sendButton;
    EditText sendText;
//
    MessageAdapter messageAdapter;
    List<Chat> mchat;
//
    RecyclerView recyclerView;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Intent intent;

    String userid;

//    APIService apiService;

    boolean notify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.chatToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {/////////////////////////////////////////////////////////////////////////////////
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//
//        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
//
        recyclerView = findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.chatProfileImage);
        username = findViewById(R.id.chatUsername);
        sendButton = findViewById(R.id.chatSendButton);
        sendText = findViewById(R.id.chatSendText);

        intent = getIntent();
        userid = intent.getStringExtra("userid");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                notify = true;
                String message = sendText.getText().toString();

                if (message.equals(""))
                {
                    Toast.makeText(MessageActivity.this, "The message is empty!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    sendMessage(firebaseUser.getUid(), userid, message);
                }

                sendText.setText("");
            }
        });

       reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
//                Log.d("mann",user.toString());
                username.setText(user.getUsername());
//

//                Log.d("Hello",user.getImageUrl());

                if (user.getImageUrl().equals(""))
                {
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else
                {
                    Glide.with(MessageActivity.this).load(user.getImageUrl()).into(profile_image);
                }

                readMessages(firebaseUser.getUid(), userid, user.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,  Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

        final DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid()).child(userid);
//
        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                {
                    chatref.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//
//        final String msg = message;
//
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Users user = dataSnapshot.getValue(Users.class);
//
//                if (notify)
//                {
//                    sendNotification(receiver, user.getUsername(), msg);
//
//                }
//
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }
//
//    private void sendNotification(String receiver, final String username, final String message)
//    {
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//
//        Query query = tokens.orderByKey().equalTo(receiver);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    Token token = snapshot.getValue(Token.class);
//
//                    Data data = new Data(firebaseUser.getUid(), R.mipmap.ic_launcher, username + ": " + message, "New Message", userid);
//
//                    Sender sender = new Sender(data, token.getToken());
//
//                    apiService.sendNotification(sender).enqueue(new Callback<MyResponse>() {
//                        @Override
//                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//
//                            if (response.code() == 200)
//                            {
//                                if (response.body().success != 1)
//                                {
//                                    Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//
    private void readMessages(final String id, final String userid, final String imageurl)
    {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Chat message = snapshot.getValue(Chat.class);

                    if ((message.getReceiver().equals(id) && message.getSender().equals(userid)) || (message.getReceiver().equals(userid) && message.getSender().equals(id)))
                    {
                        mchat.add(message);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerView.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
