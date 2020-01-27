package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.petlocket2.Adapter.UserAdapter;
import com.example.petlocket2.Model.Chat;
import com.example.petlocket2.Model.ChatList;
import com.example.petlocket2.Model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter useradapter;
    private List<Users> muser;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    private List<ChatList> userslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Chat List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {/////////////////////////////////////////////////////////////////////////////////
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView= findViewById(R.id.recycler_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userslist= new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userslist.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ChatList chatList= snapshot.getValue(ChatList.class);
                    userslist.add(chatList);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

private void chatList(){
        muser=new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                muser.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Users user= snapshot.getValue(Users.class);
                    for(ChatList chatList: userslist){
                        if(user.getId().equals(chatList.getId())){
                            muser.add(user);
                        }
                    }
                }
                useradapter= new UserAdapter(ChatsActivity.this, muser);
                recyclerView.setAdapter(useradapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
}
}
