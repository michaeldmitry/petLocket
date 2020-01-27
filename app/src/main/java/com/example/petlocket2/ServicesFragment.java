package com.example.petlocket2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlocket2.Adapter.PostPetAdapter;
import com.example.petlocket2.Model.PostPet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesFragment extends Fragment implements PostPetAdapter.OnPostListener {

    private RecyclerView recyclerView;
    private PostPetAdapter postPetAdapter;
    private List<PostPet> posts;
    Button owner;
    EditText srch;
    Dialog mdialog;
    private Chip addchip;
    private ChipGroup chipGroup;
    public Spinner spinner;
    private HashMap<String,String> ft;
    public int pos2;
    Button more_info;
    androidx.appcompat.widget.SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_services,container,false);

        recyclerView= view.findViewById(R.id.recycler_view);
        srch= view.findViewById(R.id.search);
        recyclerView.setHasFixedSize(true);
        addchip= view.findViewById(R.id.chipAdd);
        chipGroup= view.findViewById(R.id.cgservicee);

        ft= new HashMap<String, String>(){
            {
                put("Service", ";");
                put("Sex", ";");
                put("minAge", ";");
                put("maxAge", ";");
                put("minPrice", ";");
                put("maxPrice", ";");
                put("Breed", ";");
            }
        };

        addchip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater= LayoutInflater.from(getActivity());
                View sub= layoutInflater.inflate(R.layout.dialog_template,null);
                final EditText subedit= sub.findViewById(R.id.write);

                AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
                builder.setTitle("Add a new Tag");
                spinner = sub.findViewById(R.id.spinner);
                builder.setIcon(R.drawable.ic_add_grey_24dp);
                builder.setView(sub);
                AlertDialog alertDialog = builder.create();

                builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(!subedit.getText().toString().equals("")){
//                        Log.d("nowww",spinner.getSelectedItem().toString());
                            final Chip chip= (Chip) inflater.inflate(R.layout.chip_item,null, false);
                            chip.setText(subedit.getText().toString());
                            chipGroup.addView(chip);
                            if(ft.get(spinner.getSelectedItem().toString()).equals(";")){
                                ft.put(spinner.getSelectedItem().toString(), chip.getText().toString());
                            }else
                                ft.put(spinner.getSelectedItem().toString(), ft.get(spinner.getSelectedItem().toString())+";"+chip.getText().toString());


                            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String k="";
                                    for (HashMap.Entry<String,String> entry : ft.entrySet()){

                                        if(entry.getValue().contains(chip.getText().toString()))
                                        {
                                            k=entry.getKey();
                                            break;
                                        }
                                    }

                                    if(ft.get(k).contains(";")) {
                                        Log.d("again",String.valueOf(5));
                                        if(ft.get(k).substring(0,chip.getText().toString().length()).equals(chip.getText().toString())){
                                            ft.put(k,ft.get(k).replace(chip.getText().toString(),""));
                                            ft.put(k, ft.get(k).replaceFirst(";",""));

                                        }else{
                                            ft.put(k,ft.get(k).replace(";"+chip.getText().toString(),""));
                                        }
                                        if(ft.get(k).equals(""))
                                            ft.put(k,";");
                                    }else
                                        ft.put(k,";");
                                    chipGroup.removeView(chip);
                                    postPetAdapter.getFilter().filter(ft.values().toString().substring(1,ft.values().toString().length()-1));
//                                Log.d("aaaaa",ft.values().toString().substring(1,ft.values().toString().length()-1));
                                }
                            });

                            postPetAdapter.getFilter().filter(ft.values().toString().substring(1,ft.values().toString().length()-1));
//                           Log.d("aaaaa",ft.values().toString().substring(1,ft.values().toString().length()-1));

                        }
                    }});
                builder.setNegativeButton("Cancel",null);
                builder.show();
            }
        });


        GridLayoutManager gridLayoutManagerr= new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManagerr);
        posts= new ArrayList<>();
        postPetAdapter= new PostPetAdapter(getContext(),posts, this);
        recyclerView.setAdapter(postPetAdapter);
        mdialog=new Dialog(getContext());
        srch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                postPetAdapter.getFiltersearch().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        readPosts();
        return view;
    }

    private void readPosts(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("pets");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    PostPet  post= snapshot.getValue(PostPet.class);
                    if(post.getPostPetActive().equals("True"))
                        posts.add(post);
                }
                Collections.reverse(posts);
                postPetAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
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




        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(posts.get(pos).getPostPetPublisher()))
            owner.setVisibility(View.GONE);
        else
            owner.setVisibility(View.VISIBLE);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mdialog.dismiss();
            }
        });
        Glide.with(this).load(posts.get(pos).getPostPetImage()).into(detailsimg);
//        detailsimg.setImageResource(posts.get(pos).getPostImage());
        namedetails.setText(posts.get(pos).getPostPetName());
        sex.setText(posts.get(pos).getPostPetSex());
        if(posts.get(pos).getPostPetAgeY().equals("")){
            age.setText(posts.get(pos).getPostPetAgeM() + " M");
        }
        else if(posts.get(pos).getPostPetAgeM().equals("")){
            age.setText(posts.get(pos).getPostPetAgeY() + " Y");
        }
        else {
            age.setText(posts.get(pos).getPostPetAgeY() + " Y " + posts.get(pos).getPostPetAgeM() + " M");
        }
        breed.setText(posts.get(pos).getPostPetBreed());
        location.setText(posts.get(pos).getPostPetArea()+ ", " + posts.get(pos).getPostPetCity()+ ", " +posts.get(pos).getPostPetCountry());
        if(!posts.get(pos).getPostPetPrice().equals("")) {
            price.setText(posts.get(pos).getPostPetPrice() + " " + "L.E");
        }
        else{
            price.setText("");
        }
        service.setText(posts.get(pos).getPostPetService());
        if(posts.get(pos).getPostPetService().toLowerCase().equals("sell"))
            serviceimage.setImageResource(R.drawable.sell);
        else if(posts.get(pos).getPostPetService().toLowerCase().equals("adopt"))
            serviceimage.setImageResource(R.drawable.adopt);
        else if(posts.get(pos).getPostPetService().toLowerCase().equals("mate"))
            serviceimage.setImageResource(R.drawable.mate);

        mdialog.show();
        pos2=pos;
        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getActivity(), MessageActivity.class);
                i.putExtra("userid",posts.get(pos2).getPostPetPublisher());
                startActivity(i);
            }
        });

    }



}
