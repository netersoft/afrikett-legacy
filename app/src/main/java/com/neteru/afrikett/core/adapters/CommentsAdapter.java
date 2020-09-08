package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.libs.EmojiAndSocialTextView.EmojiAndSocialTextView;
import com.neteru.afrikett.core.models.RemoteDB.Comment;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.User;
import com.neteru.afrikett.core.utilities.Timing;

import java.util.List;

import static com.neteru.afrikett.core.utilities.AppUtilities.getDigitFromString;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.Constants.COLORS;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;
import static com.neteru.afrikett.core.utilities.Constants.LINK_EMAIL;
import static com.neteru.afrikett.core.utilities.Constants.LINK_HASHTAG;
import static com.neteru.afrikett.core.utilities.Constants.LINK_PHONE;
import static com.neteru.afrikett.core.utilities.Constants.LINK_URL;
import static com.neteru.afrikett.core.utilities.Constants.USER;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder>{
    private Context context;
    private List<Comment> comments;
    private DatabaseReference databaseReference;
    private CommentsAdapterListener listener;
    private String showcaseId;
    private String userId;

    class MyViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView img;
        TextView name;
        TextView date;
        EmojiAndSocialTextView content;

        MyViewHolder(View view){
            super(view);

            box = view.findViewById(R.id.commentBox);
            img = view.findViewById(R.id.authorImg);
            name = view.findViewById(R.id.authorName);
            date = view.findViewById(R.id.commentDate);
            content = view.findViewById(R.id.commentContent);
        }
    }

    public CommentsAdapter(Context context, List<Comment> comments, String userId, String showcaseId, DatabaseReference reference, CommentsAdapterListener listener){

        this.context = context;
        this.comments = comments;
        this.listener = listener;
        this.databaseReference = reference;
        this.showcaseId = showcaseId;
        this.userId = userId;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template_comment_list, parent,false));

        Comment comment = comments.get(position);

        if (comment.getAuthorId().equals(userId) || comment.getAuthorId().equals(showcaseId) || comment.getShowcaseId().equals(showcaseId)) {

            myViewHolder.box.setOnClickListener(v -> {

                AlertDialog.Builder optionsDialog = new AlertDialog.Builder(context);

                String[] optionsDialogItems = {context.getString(R.string.delete_opinion)};

                optionsDialog.setItems(optionsDialogItems, (dialog, which) -> {

                    if (which == 0) {
                        listener.deleteComment(comments.get(position));
                    }
                });

                optionsDialog.show();

            });

        }

        myViewHolder.content.setOnLinkClickListener((linkType, matchedText) -> {

            switch (linkType){

                case LINK_EMAIL:
                    break;

                case LINK_HASHTAG:
                    break;

                case LINK_PHONE:
                    break;

                case LINK_URL:
                    break;

            }

        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Comment comment = comments.get(position);

        holder.content.setLinkText(comment.getContent());
        holder.date.setText(Timing.getInstance(context, comment.getDate()).getTimePeriod());

        if (comment.getAuthorStatus() == USER) {

            databaseReference
                    .child("users")
                    .child(comment.getAuthorId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                return;
                            }

                            User author = dataSnapshot.getValue(User.class);

                            if (author == null) {
                                return;
                            }

                            holder.name.setText(author.getName());

                            String profile = author.getProfileUrl();
                            if (profile != null) {
                                if (profile.equals(DEFAULT)) {

                                    holder.img.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(author.getName()), COLORS[getDigitFromString(author.getName())]));

                                } else {

                                    Glide
                                            .with(context)
                                            .load(profile)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(holder.img);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }else {

            databaseReference
                    .child("showcases")
                    .child(comment.getAuthorId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.getValue() == null) { return; }

                            Showcase showcase = dataSnapshot.getValue(Showcase.class);

                            if (showcase == null) { return; }

                            holder.name.setText(showcase.getName());

                            String profile = showcase.getLogo();
                            if (profile != null) {
                                if (profile.equals(DEFAULT)) {

                                    holder.img.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcase.getName()), Color.parseColor(showcase.getSecondaryColor())));

                                } else {

                                    Glide
                                            .with(context)
                                            .load(profile)
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(holder.img);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public interface CommentsAdapterListener{
        void deleteComment(Comment comment);
    }
}
