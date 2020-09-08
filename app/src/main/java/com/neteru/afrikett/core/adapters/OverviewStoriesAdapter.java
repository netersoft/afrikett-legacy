package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Story;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OverviewStoriesAdapter extends RecyclerView.Adapter<OverviewStoriesAdapter.MyViewHolder> {
    private StoriesAdapterListener listener;
    private List<Story> stories;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView preview;
        private FloatingActionButton edit;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            preview = itemView.findViewById(R.id.story_preview);
            edit = itemView.findViewById(R.id.story_edit);
        }

    }

    public OverviewStoriesAdapter(Context context, List<Story> list, StoriesAdapterListener listener){
        this.listener = listener;
        this.context = context;
        this.stories = list;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template_overview_stories, parent,false));

        if (stories.get(position).getId() == null){

            myViewHolder.edit.setVisibility(View.VISIBLE);
            myViewHolder.edit.setOnClickListener(v -> listener.toStoriesEditor());

        }else {

            myViewHolder.preview.setVisibility(View.VISIBLE);
            myViewHolder.preview.setOnClickListener(v -> listener.toStoryViewer(position, stories.get(position)));

        }

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
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public interface StoriesAdapterListener{
        void toStoriesEditor();
        void toStoryViewer(int position, Story story);
    }
}
