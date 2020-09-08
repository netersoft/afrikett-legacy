package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.PostImgPreview;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostImgAdapter extends RecyclerView.Adapter<PostImgAdapter.MyViewHolder> {
    private PostImgAdapterListener listener;
    private List<PostImgPreview> postImgList;
    private Context context;
    private int rowLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout imgBox;
        private ImageView img, deleteImg;

        MyViewHolder(View view) {
            super(view);

            img = view.findViewById(R.id.img);
            imgBox = view.findViewById(R.id.imgBox);
            deleteImg = view.findViewById(R.id.deleteImg);
        }

    }

    public PostImgAdapter(Context ctx, List<PostImgPreview> list, int row, PostImgAdapterListener l) {
        this.context = ctx;
        this.postImgList = list;
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

        myViewHolder.imgBox.setOnClickListener(v -> listener.openImage(postImgList.get(position).getUriStr()));

        myViewHolder.deleteImg.setOnClickListener(view -> listener.onDeleteItem(position, postImgList.get(position)));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final PostImgPreview postImg = postImgList.get(position);

        // Chargement de la miniature
        Glide
                .with(context)
                .load(postImg.getUri())
                .into(holder.img);

    }

    @Override
    public int getItemCount() {
        return postImgList.size();
    }

    public interface PostImgAdapterListener {
        void openImage(String uriStr);
        void onDeleteItem(int position, PostImgPreview preview);
    }
}
