package com.example.petlocket2.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petlocket2.Model.PostPet;
import com.example.petlocket2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PostPetAdapter extends RecyclerView.Adapter<PostPetAdapter.ViewHolder> implements Filterable {

    public Context mcontext;
    public List<PostPet> mlist;
    public List<PostPet> mlistFull;
    public List<PostPet> mlistFull2;

    private FirebaseUser firebaseUser;
    private OnPostListener mOnpostlistener;
    public char w= 'c';
    // private OnPetListener mOnpetlistener;

    public PostPetAdapter(Context mcontext, List<PostPet> mlist, OnPostListener onPostListener) {
        this.mcontext = mcontext;
        this.mlist = mlist;
        this.mOnpostlistener=onPostListener;
        mlistFull=new ArrayList<>(mlist);
        mlistFull2=new ArrayList<>(mlistFull);

    }

    public PostPetAdapter(Context mcontext, List<PostPet> mlist, OnPostListener onPetListener, char w) {
        this.mcontext = mcontext;
        this.mlist = mlist;
        this.mOnpostlistener=onPetListener;
        this.w=w;
        mlistFull=new ArrayList<>(mlist);
        mlistFull2=new ArrayList<>(mlistFull);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(mcontext).inflate(R.layout.post_item, parent,false );
        mlistFull=new ArrayList<>(mlist);

        mlistFull2=new ArrayList<>(mlistFull);


        Log.d("Init", this.mlistFull.toString());

        return new PostPetAdapter.ViewHolder(view, mOnpostlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        final PostPet postt=mlist.get(position);

        Glide.with(mcontext).load(postt.getPostPetImage()).into(holder.post);
        holder.serviceText.setText(postt.getPostPetService());
        if(postt.getPostPetService().toLowerCase().equals("sell"))
            holder.service.setImageResource(R.drawable.sell);
        else if(postt.getPostPetService().toLowerCase().equals("adopt"))
            holder.service.setImageResource(R.drawable.adopt);
        else if(postt.getPostPetService().toLowerCase().equals("mate"))
            holder.service.setImageResource(R.drawable.mate);

        if(postt.getPostPetSex().toLowerCase().equals("male"))
            holder.gender.setImageResource(R.drawable.male1);
        else
            holder.gender.setImageResource(R.drawable.female);

        holder.name.setText(postt.getPostPetName());
        if(postt.getPostPetPrice().equals("")) {
            holder.price.setVisibility(View.GONE);
        }
        else{
            holder.price.setVisibility(View.VISIBLE);
            holder.price.setText(postt.getPostPetPrice() +" EGP");
        }

        if(postt.getPostPetTimestamp().equals(Long.valueOf(0)))
            holder.time.setVisibility(View.GONE);
        else{
            holder.time.setVisibility(View.VISIBLE);

            long temp=(-postt.getPostPetTimestamp()+System.currentTimeMillis());
            String tm=" days ago";
            if(temp/86400000==0) {

                if (temp/3600000 == 0) {
                    if (temp / 60000 == 0) {
                        temp = temp / 1000;
                        tm = " seconds ago";
                    } else {
                        temp = temp / 60000;
                        tm = " minutes ago";
                    }
                } else {
                    temp = temp / 3600000;
                    tm=" hours ago";
                }
            }else
                temp=temp/86400000;
            holder.time.setText((temp)+tm);
        }
        if(w=='a' || w=='i'){
            holder.save.setVisibility(View.GONE);
        }
        else {
            holder.save.setVisibility(View.VISIBLE);

            holder.save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.save.getTag().equals("save")) {
                        if (postt.getPostPetActive().equals("True"))
                            FirebaseDatabase.getInstance().getReference().child("SavedPets").child(firebaseUser.getUid()).child(postt.getPostPetId()).setValue(true);

                    } else {
                        if (postt.getPostPetActive().equals("True"))
                            FirebaseDatabase.getInstance().getReference().child("SavedPets").child(firebaseUser.getUid()).child(postt.getPostPetId()).removeValue();

                    }
                }
            });
            isSaved(postt.getPostPetId(), holder.save);
        }

    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView post, gender, service,save;
        public TextView price, name, serviceText, time;
        OnPostListener onPostListener;

        public ViewHolder(@NonNull View itemView, OnPostListener onPostListener) {
            super(itemView);
            post=itemView.findViewById(R.id.postImage);
            gender=itemView.findViewById(R.id.postGender);
            service=itemView.findViewById(R.id.postServiceImage);
            price= itemView.findViewById(R.id.postPrice);
            name= itemView.findViewById(R.id.postName);
            serviceText= itemView.findViewById(R.id.postService);
            time= itemView.findViewById(R.id.postTime);
            save = itemView.findViewById(R.id.favstar);
            this.onPostListener=onPostListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(w=='a' || w=='i')
                onPostListener.onPostClickSpecial(getAdapterPosition(),w);
            else
                onPostListener.OnPostClick(getAdapterPosition());
        }
    }

    public interface OnPostListener{
        void OnPostClick(int position);
        void onPostClickSpecial(int position, char type);
    }

//    public interface OnPetListener {
//        void OnPetClick(int position);
//    }

    @Override
    public Filter getFilter() {
        return mlistFilter;
    }

    public Filter getFiltersearch() {
        return mlistFilterSearch;
    }

    private Filter mlistFilterSearch= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<PostPet> filteredList= new ArrayList<>();
            if(charSequence==null ||charSequence.length()==0){
                filteredList.addAll(mlistFull2);
            }
            else {
                for (PostPet item : mlistFull2) {
                    if (item.getPostPetName().toLowerCase().trim().contains(charSequence.toString().toLowerCase().trim())) {
                        filteredList.add(item);
                        Log.d("ohh",item.getPostPetName());
                    }
                }
            }
            FilterResults res= new FilterResults();
            res.values=filteredList;
            return res;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mlist.clear();
            mlist.addAll((List)filterResults.values);

            notifyDataSetChanged();
        }
    };

    private Filter mlistFilter= new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {



            String[] splits= charSequence.toString().split(",");


            Log.d("sizeee", String.valueOf(splits.length));

            List<PostPet> filteredList= new ArrayList<>();
            //            if(charSequence==null ||charSequence.length()==0){
//                filteredList.addAll(mlistFull);
//
//            }
            if((int) splits.length==0){
                filteredList.addAll(mlistFull);
            }


            else{
                String [] breed = splits[0].split(";",-1);
                String [] maxage= splits[1].split(";",-1);
                String [] sex= splits[2].split(";",-1);
                String [] minage =splits[3].split(";",-1);
                String [] minprice=splits[4].split(";",-1);
                String [] service=splits[5].split(";",-1);
                String [] maxprice=splits[6].split(";",-1);
//                String filterPattern=charSequence.toString().toLowerCase().trim();
                Log.d("mann",sex[0]);
//                 if(service.length>1)
//                     Log.d("mann",service[1]);

                String regex="^\\s+";
                for(PostPet item :mlistFull){

                    boolean breedflag=false,sexflag=false,serviceflag=false, minpriceflag=false;

//                    if(item.getPostPetService().toLowerCase().contains(splits[0].toString().toLowerCase()) ){
//                        filteredList.add(item);
//                    }
                    for(int i=0;i<breed.length;i++){
                        breedflag=breedflag ||(item.getPostPetBreed().toLowerCase().replaceAll(regex,"").contains(breed[i].toLowerCase().replaceAll(regex,"")));
                    }
                    for(int i=0;i<sex.length;i++){
                        sexflag= sexflag || (item.getPostPetSex().toLowerCase().replaceAll(regex,"").equals(sex[i].toLowerCase().replaceAll(regex,""))|| sex[i].equals(""));
                    }
                    for(int i=0;i<service.length;i++){
                        serviceflag=serviceflag || (item.getPostPetService().toLowerCase().replaceAll(regex,"").contains(service[i].toLowerCase().replaceAll(regex,"")));
                    }
//                     if(item.getPostPetAge().equals("") || minprice[0].equals("")) {
//                         Log.d("mannn", "here");
//                         minpriceflag = true;
//                     }
//                     else{
//                         if(Integer.parseInt(item.getPostPetPrice())>=Integer.parseInt(minprice[0])){
//                             minpriceflag=true;
//                         }
//
//                     }

                    if(serviceflag &&breedflag && sexflag)
                        filteredList.add(item);
                }
            }
            FilterResults res= new FilterResults();
            res.values=filteredList;
            mlistFull2= new ArrayList<>(filteredList);
            return res;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mlist.clear();
            mlist.addAll((List)filterResults.values);
            notifyDataSetChanged();
        }
    };

    private void isSaved(final String postid, final ImageView img) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("SavedPets").child(firebaseUser.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postid).exists()) {
                    img.setImageResource(R.drawable.ic_star_yellow_24dp);
                    img.setTag("saved");
                } else {
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
