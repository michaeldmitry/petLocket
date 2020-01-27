package com.example.petlocket2.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlocket2.CommentsActivity;
import com.example.petlocket2.Model.Post;
import com.example.petlocket2.Model.Users;
import com.example.petlocket2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    public Context context;
    public List<Post> postList;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> post) {
        this.context = context;
        this.postList = post;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.post_newsfeed_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = postList.get(position);

        Glide.with(context).load(post.getPostImage()).into(holder.imagePost);     //////////////////////////////////////////////////// /SAME IF AS BELOW
        holder.body.setText(post.getPostBody());
        publisherInfo(holder.imageProfile, holder.username, post.getPostUser());
        getComments(post.getPostId(), holder.viewcomment);
        isSaved(post.getPostId(),holder.save);

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).setValue(true);

                }else{
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid()).child(post.getPostId()).removeValue();

                }
            }
        });
        if(post.getPostUser().equals(firebaseUser.getUid())){

            holder.delete.setVisibility(View.VISIBLE);
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                AlertDialog alert= new AlertDialog.Builder(context).setTitle("Delete Post").setMessage("Are you sure you want to delete this post?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseDatabase.getInstance().getReference("Comments").child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference("Posts").child(post.getPostId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Post Removed!", Toast.LENGTH_SHORT);
                                    }
                                });
                            }
                        });
                    }
                }).setNegativeButton("No",null).setIcon(android.R.drawable.ic_dialog_alert).show();

                }


            });
        }else{
            holder.delete.setVisibility(View.GONE);

        }
    holder.comment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent i =new Intent(context, CommentsActivity.class);
            i.putExtra("postId", post.getPostId());
            i.putExtra("userId", post.getPostUser());
            context.startActivity(i);
        }
    });

        holder.viewcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(context, CommentsActivity.class);
                i.putExtra("postId", post.getPostId());
                i.putExtra("userId", post.getPostUser());
                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile, imagePost, comment, delete,save;
        public TextView username, body, viewcomment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.NewsfeedImageProfile);
            imagePost = itemView.findViewById(R.id.Newsfeedpost_image);
            comment= itemView.findViewById(R.id.Newsfeedcomment);
            save= itemView.findViewById(R.id.NewsfeedStar);

            username = itemView.findViewById(R.id.Newsfeedusername);
            body = itemView.findViewById(R.id.Newsfeedpost_body);
            viewcomment= itemView.findViewById(R.id.NewsfeedcommentsNo);
            delete= itemView.findViewById(R.id.NewsfeedDelete);

        }
    }

    private void getComments(String postid, final TextView comments){
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.setText("View all "+dataSnapshot.getChildrenCount()+" Comments");


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void publisherInfo(final ImageView imageProfile, final TextView username, String userId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                Glide.with(context).load(user.getImageUrl()).into(imageProfile);  //change later
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved (final String postid,final ImageView img){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists()){
                    img.setImageResource(R.drawable.ic_star_yellow_24dp);
                    img.setTag("saved");
                }else{
                    img.setImageResource(R.drawable.ic_star_border_grey_24dp);
                    img.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
