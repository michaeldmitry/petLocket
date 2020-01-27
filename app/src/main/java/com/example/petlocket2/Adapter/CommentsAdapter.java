package com.example.petlocket2.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlocket2.Model.Comment;
import com.example.petlocket2.Model.Users;
import com.example.petlocket2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder>{

    private Context mcontext;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;

    public CommentsAdapter(Context mcontext, List<Comment> mcomment) {
        this.mcontext = mcontext;
        this.mComment = mcomment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mcontext).inflate(R.layout.comment_item, parent, false);

        return new CommentsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            Comment commentf;
            commentf= mComment.get(position);
            holder.comment.setText(commentf.getComment());
            getUserInfo(holder.imgprofile, holder.username, commentf.getUser());


    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgprofile;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgprofile= itemView.findViewById(R.id.CommentItemImageProfile);
            username= itemView.findViewById(R.id.CommentsItemUsername);
            comment= itemView.findViewById(R.id.CommentsItemComment);

        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String user){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user1= dataSnapshot.getValue(Users.class);
                Glide.with(mcontext).load(user1.getImageUrl()).into(imageView);
                username.setText(user1.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
