package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.User;

import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {
    private DatabaseReference databaseReference;
    private List<String> userIdList;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder{
        CardView userBox;
        TextView userName;
        ImageView userProfile;
        ImageView userContact;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            userBox = itemView.findViewById(R.id.userBox);
            userName = itemView.findViewById(R.id.userName);
            userProfile = itemView.findViewById(R.id.userProfile);
            userContact = itemView.findViewById(R.id.userContact);
        }

    }

    public UserListAdapter(Context context, List<String> list){
        this.context = context;
        this.userIdList = list;

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {

        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template_user_list, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String userId = userIdList.get(position);

        databaseReference
                .child("users")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) return;

                        User user = dataSnapshot.getValue(User.class);

                        if (user == null) return;

                        Glide
                                .with(context)
                                .load(user.getProfileUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(holder.userProfile);

                        holder.userName.setText(user.getName());

                        holder.userContact.setOnClickListener(v -> {

                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return userIdList.size();
    }
    
}
