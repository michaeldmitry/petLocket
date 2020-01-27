package com.example.petlocket2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class NewPost extends AppCompatActivity {

    Uri imageuri=null;
    String myurl="";
    StorageTask uploadtask;
    StorageReference storageReference;

    ImageView close,imageadded;
    TextView add;
    EditText body;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        close= findViewById(R.id.NewPostclose);
        imageadded= findViewById(R.id.NewPostimageadded);
        add= findViewById(R.id.NewPostadd);
        body=findViewById(R.id.NewPostBody);

        storageReference= FirebaseStorage.getInstance().getReference("Posts");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewPost();
            }
        });

        imageadded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().setAspectRatio(1,1).start(NewPost.this);

            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver= getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void addNewPost(){
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Posting...");
        progressDialog.show();
        if(imageuri!=null){
            final StorageReference filreference= storageReference.child(System.currentTimeMillis() + "." +getFileExtension(imageuri));
            uploadtask=filreference.putFile(imageuri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filreference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloaduri= task.getResult();
                        myurl=downloaduri.toString();
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Posts");
                        String postId= databaseReference.push().getKey();
                        HashMap<String,Object> hashMap=new HashMap<>();
                        hashMap.put("postId", postId);
                        hashMap.put("postImage", myurl);
                        hashMap.put("postBody", body.getText().toString());
                        hashMap.put("postUser", FirebaseAuth.getInstance().getCurrentUser().getUid());


                        databaseReference.child(postId).setValue(hashMap);
                        progressDialog.dismiss();
//                        startActivity(new Intent(NewPet.this, HomeActivity.class));
                        finish();
                    }else{
                        Toast.makeText(NewPost.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NewPost.this, "Failure Listner", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
//            Toast.makeText(NewPost.this, "No Image Selected", Toast.LENGTH_SHORT).show();
            DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Posts");
            String postId= databaseReference.push().getKey();
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("postId", postId);
            hashMap.put("postImage", "");
            hashMap.put("postBody", body.getText().toString());
            hashMap.put("postUser", FirebaseAuth.getInstance().getCurrentUser().getUid());


            databaseReference.child(postId).setValue(hashMap);
            progressDialog.dismiss();
//                        startActivity(new Intent(NewPet.this, HomeActivity.class));
            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== RESULT_OK){
            CropImage.ActivityResult res= CropImage.getActivityResult(data);
            imageuri= res.getUri();

            if(imageuri.equals("") || imageuri.equals(null))
                imageadded.setVisibility(View.GONE);
            else {
                imageadded.setVisibility(View.VISIBLE);
                imageadded.setImageURI(imageuri);
            }
        }else{
            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(NewPet.this, HomeActivity.class));
//            finish();
        }
    }
}
