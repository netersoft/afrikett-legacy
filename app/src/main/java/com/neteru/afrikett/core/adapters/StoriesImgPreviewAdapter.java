package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.StoriesImgPreview;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StoriesImgPreviewAdapter extends RecyclerView.Adapter<StoriesImgPreviewAdapter.MyViewHolder> {
    private PreviewListener listener;
    private List<StoriesImgPreview> previewList;
    private Context context;
    private int rowLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout previewBox;
        private ImageView previewImg;

        MyViewHolder(View view){
            super(view);

            previewImg = view.findViewById(R.id.previewImg);
            previewBox = view.findViewById(R.id.previewBox);
        }

    }

    public StoriesImgPreviewAdapter(Context ctx, List<StoriesImgPreview> list, int row, PreviewListener l){
        this.context = ctx;
        this.previewList = list;
        this.rowLayout = row;
        this.listener = l;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {

        final MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false));

        /* Selection du box */
        myViewHolder.previewBox.setOnClickListener(view -> {
            listener.onPreviewClick(previewList.get(position));

            myViewHolder.previewBox.setBackground(context.getResources().getDrawable(R.drawable.chat_img_vid_item_checked_border));
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final StoriesImgPreview storiesImgPreview = previewList.get(position);

        // Sélection par défaut du premier item
        if (position == 0){
            holder.previewBox.setBackground(context.getResources().getDrawable(R.drawable.chat_img_vid_item_checked_border));
        }

        // Chargement de la miniature
        Glide
                .with(context)
                .load(storiesImgPreview.getUri())
                .into(holder.previewImg);

    }

    @Override
    public int getItemCount() {
        return previewList.size();
    }

    public interface PreviewListener{
        void onPreviewClick(StoriesImgPreview preview);
    }
}
