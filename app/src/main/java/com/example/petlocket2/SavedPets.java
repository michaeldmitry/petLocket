package com.example.petlocket2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlocket2.Adapter.PostAdapter;
import com.example.petlocket2.Adapter.PostPetAdapter;
import com.example.petlocket2.Model.Post;
import com.example.petlocket2.Model.PostPet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SavedPets extends Fragment implements PostPetAdapter.OnPostListener {
    FirebaseUser firebaseUser;
    String userid;
    List<String> mysavespets;
    private RecyclerView recyclerViewSaves;
    private List<PostPet> petsListSaved;
    private PostPetAdapter postPetAdapterSaved;
    Dialog mdialog;
    Button owner;
    public int pos2;

    private ImageView star;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_saved_pets,container,false);
        recyclerViewSaves = view.findViewById(R.id.recycler_saved2);
//        recyclerViewSaves.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
        recyclerViewSaves.setLayoutManager(new GridLayoutManager(getContext(),2));
        petsListSaved= new ArrayList<>();
        mdialog=new Dialog(getContext());

        postPetAdapterSaved = new PostPetAdapter(getContext(), petsListSaved, this);
        recyclerViewSaves.setAdapter(postPetAdapterSaved);
        getSavedPosts();
        return view;
    }

    private void getSavedPosts() {

        mysavespets = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SavedPets")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    mysavespets.add(snapshot.getKey());
                }

                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("pets");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                petsListSaved.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PostPet pet = snapshot.getValue(PostPet.class);
                    for(String id:mysavespets){
                        if(pet.getPostPetId().equals(id) &&pet.getPostPetActive().equals("True")){
                            petsListSaved.add(pet);
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
        showPopUp(position);
    }

    @Override
    public void onPostClickSpecial(int position, char type) {

    }

    public void showPopUp(int pos){
        TextView txtclose, namedetails, sex, age, breed, location, price, service;
        ImageView detailsimg,serviceimage;
        mdialog.setContentView(R.layout.activity_details);
        txtclose = (TextView) mdialog.findViewById(R.id.closeDetails);
        detailsimg= (ImageView) mdialog.findViewById(R.id.imageDetails);
        namedetails = (TextView) mdialog.findViewById(R.id.nameDetails);
        sex = (TextView) mdialog.findViewById(R.id.genderTextDetails);
        age = (TextView) mdialog.findViewById(R.id.ageDetails);
        breed = (TextView) mdialog.findViewById(R.id.breedDetails);
        owner= mdialog.findViewById(R.id.ownerDetails);
        location = mdialog.findViewById(R.id.locationDetails);
        price = mdialog.findViewById(R.id.petprice);
        service = mdialog.findViewById(R.id.service);
        serviceimage = mdialog.findViewById(R.id.servimage);



        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(petsListSaved.get(pos).getPostPetPublisher()))
            owner.setVisibility(View.GONE);
        else
            owner.setVisibility(View.VISIBLE);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialog.dismiss();
            }
        });
        Glide.with(this).load(petsListSaved.get(pos).getPostPetImage()).into(detailsimg);
//        detailsimg.setImageResource(posts.get(pos).getPostImage());
        namedetails.setText(petsListSaved.get(pos).getPostPetName());
        sex.setText(petsListSaved.get(pos).getPostPetSex());
        if(petsListSaved.get(pos).getPostPetAgeY().equals("")){
            age.setText(petsListSaved.get(pos).getPostPetAgeM() + " M");
        }
        else if(petsListSaved.get(pos).getPostPetAgeM().equals("")){
            age.setText(petsListSaved.get(pos).getPostPetAgeY() + " Y");
        }
        else {
            age.setText(petsListSaved.get(pos).getPostPetAgeY() + " Y " + petsListSaved.get(pos).getPostPetAgeM() + " M");
        }
        breed.setText(petsListSaved.get(pos).getPostPetBreed());
        location.setText(petsListSaved.get(pos).getPostPetArea()+ ", " + petsListSaved.get(pos).getPostPetCity()+ ", " +petsListSaved.get(pos).getPostPetCountry());
        if(!petsListSaved.get(pos).getPostPetPrice().equals("")) {
            price.setText(petsListSaved.get(pos).getPostPetPrice() + " " + "L.E");
        }
        else{
            price.setText("");
        }
        service.setText(petsListSaved.get(pos).getPostPetService());
        if(petsListSaved.get(pos).getPostPetService().toLowerCase().equals("sell"))
            serviceimage.setImageResource(R.drawable.sell);
        else if(petsListSaved.get(pos).getPostPetService().toLowerCase().equals("adopt"))
            serviceimage.setImageResource(R.drawable.adopt);
        else if(petsListSaved.get(pos).getPostPetService().toLowerCase().equals("mate"))
            serviceimage.setImageResource(R.drawable.mate);

        mdialog.show();
        pos2=pos;
        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getActivity(), MessageActivity.class);
                i.putExtra("userid",petsListSaved.get(pos2).getPostPetPublisher());
                startActivity(i);
            }
        });

    }
}
