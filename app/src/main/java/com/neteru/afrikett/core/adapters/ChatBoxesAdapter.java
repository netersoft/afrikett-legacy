package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.MessengerNode;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.utilities.Timing;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;

public class ChatBoxesAdapter extends RecyclerView.Adapter<ChatBoxesAdapter.MyViewHolder> {
    private ChatBoxesAdapterListener listener;
    private List<MessengerNode> messengerNodes;
    private DatabaseReference databaseReference;
    private Context context;
    private int rowLayout;
    private String userId;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView chatBoxLastMsg, chatBoxLastTime, chatBoxName;
        private CardView chatBox;
        private ImageView profile;

        MyViewHolder(View view){
            super(view);

            chatBox = view.findViewById(R.id.chatBox);
            profile = view.findViewById(R.id.chatBoxImg);
            chatBoxName = view.findViewById(R.id.chatBoxName);
            chatBoxLastMsg = view.findViewById(R.id.chatBoxLastMsg);
            chatBoxLastTime = view.findViewById(R.id.chatBoxLastTime);
        }
    }

    public ChatBoxesAdapter(Context ctx, List<MessengerNode> list, int row, ChatBoxesAdapterListener l){
        this.context = ctx;
        this.messengerNodes = list;
        this.rowLayout = row;
        this.listener = l;

        userId = getLocalUserData(context).getId();
        // Référence à la base de données
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

        myViewHolder.chatBox.setOnClickListener(v -> listener.toChatBox(getTargetId(position), messengerNodes.get(position).getFormalBlock()));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {

        // Noeud de discussion
        final MessengerNode messengerNode = messengerNodes.get(position);

        // Identifiant de la cible
        String targetId = getTargetId(position);

        // Nom de la cible
        String targetName = messengerNodes.get(position).getFirstId().equals(userId)
                            ? messengerNodes.get(position).getSecondName()
                            : messengerNodes.get(position).getFirstName();

        // Chargement des informations de la cible
        holder.chatBoxName.setText(targetName);
        holder.chatBoxLastMsg.setText(messengerNode.getLastMsg());
        holder.chatBoxLastTime.setText(Timing.getInstance(context, messengerNode.getLastTime()).getTimeRelative());

        if (messengerNode.getFormalBlock()){

            databaseReference
                    .child("showcases")
                    .child(targetId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){
                                Showcase showcase = dataSnapshot.getValue(Showcase.class);
                                if (showcase == null) return;

                                if (showcase.getLogo().equals(DEFAULT)){

                                    holder
                                            .profile
                                            .setImageDrawable(TextDrawable.builder()
                                                    .buildRound(getFirstLetters(targetName), Color.parseColor(showcase.getSecondaryColor())));

                                }else {

                                    // Chargement de la photo de profil
                                    Glide
                                            .with(context)
                                            .load(showcase.getLogo())
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(holder.profile);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }else {

            databaseReference
                    .child("users")
                    .child(targetId)
                    .child("profileUrl")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null){
                                String url = dataSnapshot.getValue(String.class);
                                if (url == null){
                                    return;
                                }

                                if (url.equals(DEFAULT)){

                                    holder
                                            .profile
                                            .setImageDrawable(TextDrawable.builder()
                                                    .buildRound(getFirstLetters(targetName), COLORS[getDigitFromString(targetName)]));

                                }else {

                                    // Chargement de la photo de profil
                                    Glide
                                            .with(context)
                                            .load(url)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(holder.profile);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

        }

        if (messengerNode.getPendingMsg() && messengerNode.getPendingMsgTarget().equals(getLocalUserData(context).getId())){
            holder.chatBox.setCardBackgroundColor(context.getResources().getColor(R.color.silver));
        }else {
            holder.chatBox.setCardBackgroundColor(context.getResources().getColor(R.color.white));
        }
    }

    private String getTargetId(int position){

        return messengerNodes.get(position).getFirstId().equals(userId)
                             ? messengerNodes.get(position).getSecondId()
                             : messengerNodes.get(position).getFirstId();
    }

    @Override
    public int getItemCount() {
        return messengerNodes.size();
    }

    public interface ChatBoxesAdapterListener{
        void toChatBox(String targetId, boolean isShowcase);
    }
}
