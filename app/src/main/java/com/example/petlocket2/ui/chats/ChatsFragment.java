//package com.example.petlocket2.ui.chats;
//
//import androidx.lifecycle.ViewModelProviders;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.example.petlocket2.Adapter.UsersAdapter;
//import com.example.petlocket2.Model.Chatlist;
//import com.example.petlocket2.Model.Message;
//import com.example.petlocket2.Model.Users;
//import com.example.petlocket2.Notifications.Token;
//import com.example.petlocket2.R;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.iid.FirebaseInstanceId;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ChatsFragment extends Fragment {
//
////    private ChatsViewModel mViewModel;
//
//    private RecyclerView recyclerView;
//
//    private UsersAdapter usersAdapter;
//    private List<Users> users;
//
//    private List<Chatlist> usersList;
//
//    FirebaseUser firebaseUser;
//    DatabaseReference reference;
//
//    public static ChatsFragment newInstance() {
//        return new ChatsFragment();
//    }
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_chats, container, false);
//
//        recyclerView = view.findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        usersList = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
//                    usersList.add(chatlist);
//                }
//
//                chatList();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//        /*
//        reference = FirebaseDatabase.getInstance().getReference("Messages");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersList.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    Message chat = snapshot.getValue(Message.class);
//
//                    if (chat.getSender().equals(firebaseUser.getUid()))
//                    {
//                        usersList.add(chat.getReceiver());
//                    }
//                    if (chat.getReceiver().equals(firebaseUser.getUid()))
//                    {
//                        usersList.add(chat.getSender());
//                    }
//                }
//
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });*/
//
//        updateToken(FirebaseInstanceId.getInstance().getToken());
//
//        return view;
//    }
//
////    @Override
////    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
////        super.onActivityCreated(savedInstanceState);
////        mViewModel = ViewModelProviders.of(this).get(ChatsViewModel.class);
////        // TODO: Use the ViewModel
////    }
//
//    /*
//    private void readChats()
//    {
//        users = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                users.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    Users user = snapshot.getValue(Users.class);
//
//                    for (String id : usersList)
//                    {
//                        if (user.getId().equals(id))
//                        {
//                            if (users.size() != 0)
//                            {
//                                for (Users user_ : users)
//                                {
//                                    if (!user.getId().equals(user_.getId()))
//                                    {
//                                        users.add(user);
//                                    }
//                                }
//                            }
//                            else
//                            {
//                                users.add(user);
//                            }
//                        }
//                    }
//                }
//
//                usersAdapter = new UsersAdapter(getContext(), users);
//                recyclerView.setAdapter(usersAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }*/
//
//    private void updateToken(String token)
//    {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1 = new Token(token);
//        reference.child(firebaseUser.getUid()).setValue(token1);
//    }
//
//    private void chatList()
//    {
//        usersList = new ArrayList<>();
//
//        reference = FirebaseDatabase.getInstance().getReference("Users");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                users.clear();
//
//                for (DataSnapshot snapshot : dataSnapshot.getChildren())
//                {
//                    Users user = snapshot.getValue(Users.class);
//
//                    for (Chatlist chatlist : usersList)
//                    {
//                        if (user.getId().equals(chatlist.getId()))
//                        {
//                            users.add(user);
//                        }
//                    }
//                }
//
//                usersAdapter = new UsersAdapter(getContext(), users);
//                recyclerView.setAdapter(usersAdapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
//}
