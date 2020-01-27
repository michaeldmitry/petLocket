package com.example.petlocket2;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petlocket2.Adapter.PostAdapter;
import com.example.petlocket2.Adapter.PostPetAdapter;
import com.example.petlocket2.Model.Post;
import com.example.petlocket2.Model.PostPet;
import com.example.petlocket2.Model.Users;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class SavedPosts extends Fragment implements PostPetAdapter.OnPostListener{

    FirebaseUser firebaseUser;
    String userid;
    List<String> mysaves;
    private RecyclerView recyclerViewSaves;
    private List<Post> postsListSaved;
    private PostAdapter postPetAdapterSaved;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_saved_posts,container,false);
        recyclerViewSaves = view.findViewById(R.id.recycler_saved);
        recyclerViewSaves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewSaves.setLayoutManager(linearLayoutManager);
        postsListSaved= new ArrayList<>();

        postPetAdapterSaved = new PostAdapter(getContext(), postsListSaved);
        recyclerViewSaves.setAdapter(postPetAdapterSaved);
        getSavedPosts();
        return view;
    }

    private void getSavedPosts() {

        mysaves = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mysaves.add(snapshot.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsListSaved.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post = snapshot.getValue(Post.class);
                    for(String id:mysaves){
                        if(post.getPostId().equals(id)){
                            postsListSaved.add(post);
                        }
                    }
                }
                postPetAdapterSaved.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void OnPostClick(int position) {

    }

    @Override
    public void onPostClickSpecial(int position, char type) {

    }
}
