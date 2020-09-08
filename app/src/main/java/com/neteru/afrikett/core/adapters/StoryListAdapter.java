package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Story;
import com.neteru.afrikett.core.utilities.Timing;

import java.util.List;

public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.MyViewHolder> {
    private StoryListAdapterListener listener;
    private List<Story> stories;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder{
        CardView storyBox;
        TextView storyDate, storyViews;
        ImageView storyPreview, storyDelete;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            storyBox = itemView.findViewById(R.id.storyBox);
            storyDate = itemView.findViewById(R.id.storyDate);
            storyViews = itemView.findViewById(R.id.storyViews);
            storyDelete = itemView.findViewById(R.id.storyDelete);
            storyPreview = itemView.findViewById(R.id.storyPreview);
        }

    }

    public StoryListAdapter(Context context, List<Story> list, StoryListAdapterListener listener){
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
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.template_story_list, parent,false));

        myViewHolder.storyBox.setOnClickListener(v -> listener.toStoryViewer(position, stories.get(position)));

        myViewHolder.storyDelete.setOnClickListener(v -> new AlertDialog
                .Builder(context)
                .setMessage(context.getString(R.string.story_deletion_msg))
                .setPositiveButton(context.getString(R.string.yes), (dialog, which) -> listener.deleteStory(stories.get(position)))
                .setNegativeButton(context.getString(R.string.cancel), null)
                .show());

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Story story = stories.get(position);

        Glide
                .with(context)
                .load(story.getUrl())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.storyPreview);

        holder.storyViews.setText(String.valueOf(story.getViews()));
        holder.storyDate.setText(Timing.getInstance(context, story.getDate()).getTimePeriod());
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public interface StoryListAdapterListener{
        void toStoryViewer(int position, Story story);
        void deleteStory(Story story);
    }
}
