package com.example.petlocket2;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlocket2.Adapter.PostPetAdapter;
import com.example.petlocket2.Model.Post;
import com.example.petlocket2.Model.PostPet;
import com.example.petlocket2.Model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment implements PostPetAdapter.OnPostListener{

    FloatingActionButton fab;
    FirebaseUser firebaseUser;
    String userid;
    TextView username, activetext, petsNumber;
    ImageView imgprof;

    Uri imageuri=null;
    String myurl="";
    StorageTask uploadtask;
    StorageReference storageReference;

    private RecyclerView recyclerViewActive;
    private RecyclerView recyclerViewInActive;
    private List<PostPet> postsA;
    private List<PostPet> postsI;
    private PostPetAdapter postPetAdapterA;
    private PostPetAdapter postPetAdapterI;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile,container,false);
        recyclerViewActive= view.findViewById(R.id.recycler_viewActivePrfile);
        recyclerViewInActive= view.findViewById(R.id.recycler_viewInActivePrfile);
        fab= (FloatingActionButton) view.findViewById(R.id.fab);
        username= (TextView) view.findViewById(R.id.usernameProfile);
        activetext= (TextView) view.findViewById(R.id.petsActiveNumberProfile);
        petsNumber= (TextView) view.findViewById(R.id.petsNumberProfile);
        imgprof= (ImageView) view.findViewById(R.id.imageProfile);
        Log.d("mannnn", FirebaseAuth.getInstance().getCurrentUser().getUid());
        recyclerViewActive.setHasFixedSize(true);
        recyclerViewInActive.setHasFixedSize(true);
        storageReference= FirebaseStorage.getInstance().getReference("Users");

        final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals("imageUrl")){
                        if(!snapshot.getValue().equals(""))
                            Glide.with(getContext()).load(snapshot.getValue()).into(imgprof);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        LinearLayoutManager linearLayoutManager2= new LinearLayoutManager(getContext());
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);

        recyclerViewActive.setLayoutManager(linearLayoutManager);
        recyclerViewInActive.setLayoutManager(linearLayoutManager2);
        postsA=new ArrayList<>();
        postsI=new ArrayList<>();

        postPetAdapterA= new PostPetAdapter(getContext(), postsA, this,'a');
        recyclerViewActive.setAdapter(postPetAdapterA);
        postPetAdapterI= new PostPetAdapter(getContext(), postsI, this,'i');
        recyclerViewInActive.setAdapter(postPetAdapterI);

        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getActivity(),NewPet.class);
                startActivity(i);

            }
        });

        imgprof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= CropImage.activity().getIntent(getContext());
                startActivityForResult(i,CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);

            }
        });
        getUserInfo();
        getPets();

        return view;
    }

    public void getUserInfo(){
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userid=firebaseUser.getUid();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null)
                    return;

                Users user= dataSnapshot.getValue(Users.class);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getPets(){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("pets");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postsA.clear();
                postsI.clear();
                int i=0;
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PostPet pet= snapshot.getValue(PostPet.class);
                    if(pet.getPostPetPublisher().equals(userid)){
                        i++;
                        if(pet.getPostPetActive().equals("True")) {
                            pet.setPostPetPrice("");
                            pet.setPostPetTimestamp(Long.valueOf(0));
                            postsA.add(pet);
                        }
                        else {
                            pet.setPostPetPrice("");
                            pet.setPostPetTimestamp(Long.valueOf(0));
                            postsI.add(pet);
                        }
                    }
                }
                postPetAdapterA.notifyDataSetChanged();
                postPetAdapterI.notifyDataSetChanged();
                activetext.setText(String.valueOf(postsA.size()));
                petsNumber.setText(String.valueOf(i));
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
        Log.d("ddd", String.valueOf(position)+" "+type);
        if(type=='i')
            showActiveAlert(position);
        else if (type == 'a')
            openEditPetActivity(position);
    }

    public void openEditPetActivity(int position)
    {
        Intent intent = new Intent(getActivity(), EditPetActivity.class);
        intent.putExtra("id", postsA.get(position).getPostPetId());
        intent.putExtra("name", postsA.get(position).getPostPetName());
        intent.putExtra("age", postsA.get(position).getPostPetAgeY());
        intent.putExtra("price", postsA.get(position).getPostPetPrice());
        intent.putExtra("service", postsA.get(position).getPostPetService());
        startActivity(intent);

    }

    public void showActiveAlert(final int position)
    {

        AlertDialog alert= new AlertDialog.Builder(getActivity()).setTitle("Set Pet Active").setMessage("Are you sure you want to set your pet active?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                DatabaseReference reference= FirebaseDatabase.getInstance().getReference("pets").child(postsI.get(position).getPostPetId()).child("postPetActive");
                reference.setValue("True");



            }
        }).setNegativeButton("No",null).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver= getActivity().getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== getActivity().RESULT_OK){

            CropImage.ActivityResult res= CropImage.getActivityResult(data);
            imageuri= res.getUri();
//            imgprof.setImageURI(imageuri);

            final ProgressDialog progressDialog= new ProgressDialog(getActivity());
            progressDialog.setMessage("Changing Display Picture...");
            progressDialog.show();
            if(imageuri!=null) {
                final StorageReference filreference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageuri));
                uploadtask = filreference.putFile(imageuri);
                uploadtask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return filreference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloaduri = task.getResult();
                            myurl = downloaduri.toString();
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).child("imageUrl");
                            databaseReference.setValue(myurl);
                            progressDialog.dismiss();

                        } else {
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failure Listener", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }else{
            Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
    }


}
