package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.ChatImgVidPreview;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static com.neteru.afrikett.core.utilities.AppUtilities.getThumbnailFromVideoPath;
import static com.neteru.afrikett.core.utilities.Constants.IMAGE_MESSAGE;

public class ChatImgVidPreviewAdapter extends RecyclerView.Adapter<ChatImgVidPreviewAdapter.MyViewHolder> {

    private PreviewListener listener;
    private List<ChatImgVidPreview> previewList;
    private Context context;
    private int rowLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public RelativeLayout previewBox;
        private ImageView previewImg, playBut;

        MyViewHolder(View view){
            super(view);

            playBut = view.findViewById(R.id.playButton);
            previewImg = view.findViewById(R.id.previewImg);
            previewBox = view.findViewById(R.id.previewBox);
        }

    }

    public ChatImgVidPreviewAdapter(Context ctx, List<ChatImgVidPreview> list, int row, PreviewListener l){
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

        myViewHolder.playBut.setOnClickListener(view -> {
            listener.onPreviewClick(previewList.get(position));

            myViewHolder.previewBox.setBackground(context.getResources().getDrawable(R.drawable.chat_img_vid_item_checked_border));
        });

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final ChatImgVidPreview chatImgVidPreview = previewList.get(position);

        // Sélection par défaut du premier item
        if (position == 0){
            holder.previewBox.setBackground(context.getResources().getDrawable(R.drawable.chat_img_vid_item_checked_border));
        }

        // Chargement de la miniature
        if (chatImgVidPreview.getType() == IMAGE_MESSAGE) {

            Glide
                    .with(context)
                    .load(chatImgVidPreview.getUri())
                    .into(holder.previewImg);
            holder.playBut.setVisibility(View.GONE);

        }else {

            Glide
                    .with(context)
                    .load(getThumbnailFromVideoPath(chatImgVidPreview.getPath()))
                    .into(holder.previewImg);
            holder.playBut.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public int getItemCount() {
        return previewList.size();
    }

    public interface PreviewListener{
        void onPreviewClick(ChatImgVidPreview preview);
    }
}
