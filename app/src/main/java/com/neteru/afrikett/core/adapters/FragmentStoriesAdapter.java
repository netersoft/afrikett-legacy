package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Story;

import java.util.List;

import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;

public class FragmentStoriesAdapter extends RecyclerView.Adapter<FragmentStoriesAdapter.MyViewHolder> {
    private StoriesAdapterListener listener;
    private List<Story> stories;
    private Context context;
    private DatabaseReference databaseReference;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView preview;
        private TextView source;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            preview = itemView.findViewById(R.id.story_preview);
            source = itemView.findViewById(R.id.story_source);
        }

    }

    public FragmentStoriesAdapter(Context context, List<Story> list, StoriesAdapterListener listener){
        this.listener = listener;
        this.context = context;
        this.stories = list;

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template_fragment_stories, parent,false));

        myViewHolder.preview.setVisibility(View.VISIBLE);
        myViewHolder.preview.setOnClickListener(v -> listener.toStoryViewer(position, stories));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (stories.get(position).getId() == null){
            return;
        }

        Glide
                .with(context)
                .load(stories.get(position).getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.preview);

        databaseReference
                .child("showcases")
                .child(stories.get(position).getSource())
                .child("name")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) return;

                        String name = dataSnapshot.getValue(String.class);
                        holder.source.setText(name);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public interface StoriesAdapterListener{
        void toStoryViewer(int position, List<Story> stories);
    }
}