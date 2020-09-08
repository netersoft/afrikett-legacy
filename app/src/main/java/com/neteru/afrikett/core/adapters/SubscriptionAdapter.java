package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;

import java.util.ArrayList;
import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder> {
    private SubscriptionAdapterListener listener;
    private List<Showcase> showcaseList;
    private Context context;
    private int rowLayout;
    private String userId;
    private DatabaseReference databaseReference;
    private DatabaseReference subscriptionDbReference;
    private DatabaseReference userSubscriptionDbReference;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private CardView showcaseBox;
        private ImageView showcaseLogo;
        private TextView showcaseName;
        private TextView showcaseDescription;
        private Button showcaseSubscribe;

        MyViewHolder(View view){
            super(view);

            showcaseBox = view.findViewById(R.id.showcaseBox);
            showcaseLogo = view.findViewById(R.id.showcaseLogo);
            showcaseName = view.findViewById(R.id.showcaseName);
            showcaseDescription = view.findViewById(R.id.showcaseDescription);
            showcaseSubscribe = view.findViewById(R.id.showcaseSubscribe);

        }
    }

    public SubscriptionAdapter(Context ctx, List<Showcase> list, int row, SubscriptionAdapterListener l){
        this.context = ctx;
        this.showcaseList = list;
        this.rowLayout = row;
        this.listener = l;

        userId = getLocalUserData(context).getId();
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
        
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {

        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent,false));

        Showcase showcase = showcaseList.get(position);
        
        myViewHolder.showcaseBox.setOnClickListener(v -> listener.toShowcaseOverview(showcase));

        myViewHolder.showcaseSubscribe.setOnClickListener(v -> {

            subscriptionDbReference = databaseReference.child("showcases").child(showcase.getId());
            userSubscriptionDbReference = databaseReference.child("users").child(userId).child("nbSubscriptions");

            subscriptionDbReference
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() == null) return;

                            Showcase s = dataSnapshot.getValue(Showcase.class);

                            if (s == null) return;

                            final List<String> subscribersList = s.getSubscribers();
                            final boolean increaseValue;

                            if (subscribersList != null) {

                                if (subscribersList.contains(userId)) {

                                    subscribersList.remove(userId);

                                    increaseValue = false;

                                } else {

                                    subscribersList.add(userId);

                                    increaseValue = true;

                                }

                            }else {

                                increaseValue = true;

                            }

                            if (increaseValue){

                                myViewHolder.showcaseSubscribe.setBackground(context.getResources().getDrawable(R.drawable.orange_button_bg_filled));
                                myViewHolder.showcaseSubscribe.setTextColor(context.getResources().getColor(R.color.white));
                                myViewHolder.showcaseSubscribe.setText(context.getString(R.string.subscribed));

                            }else {

                                myViewHolder.showcaseSubscribe.setText(context.getString(R.string.subscribe));
                                myViewHolder.showcaseSubscribe.setTextColor(context.getResources().getColor(R.color.dimgray));
                                myViewHolder.showcaseSubscribe.setBackground(context.getResources().getDrawable(R.drawable.gray_button_bg_stroke));

                            }

                            List<String> tempList = new ArrayList<>();
                            tempList.add(userId);

                            subscriptionDbReference.child("subscribers").setValue(subscribersList != null ? subscribersList : tempList);
                            subscriptionDbReference.child("nb_subscribers").setValue(subscribersList != null ? subscribersList.size() : tempList.size());

                            userSubscriptionDbReference
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            if (dataSnapshot.getValue() == null) return;

                                            Integer nb = dataSnapshot.getValue(Integer.class);

                                            if (nb != null){

                                                if (increaseValue){
                                                    userSubscriptionDbReference.setValue(nb + 1);
                                                }else if(nb > 0){
                                                    userSubscriptionDbReference.setValue(nb - 1);
                                                }

                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
            
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        final Showcase showcase = showcaseList.get(position);

        holder.showcaseName.setText(showcase.getName());
        holder.showcaseDescription.setText(showcase.getDescription());

        if (showcase.getLogo().equals(DEFAULT)){

            holder
                    .showcaseLogo
                    .setImageDrawable(TextDrawable.builder()
                            .buildRound(getFirstLetters(showcase.getName()), Color.parseColor(showcase.getSecondaryColor())));

        }else {

            // Chargement de la photo de profil
            Glide
                    .with(context)
                    .load(showcase.getLogo())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.showcaseLogo);

        }

    }

    @Override
    public int getItemCount() {
        return showcaseList.size();
    }

    public interface SubscriptionAdapterListener{
        void toShowcaseOverview(Showcase showcase);
    }
}

