package com.neteru.afrikett.core.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Post;

import java.util.List;
import java.util.Random;

import static com.neteru.afrikett.core.utilities.AppUtilities.dpToPx;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.Constants.PRODUCT_AND_SERVICE;

public class RandomPostsAdapter extends RecyclerView.Adapter<RandomPostsAdapter.MyViewHolder> {

    private RandomPostsAdapterListener listener;
    private List<Post> postList;
    private Context context;
    private int rowLayout;
    private Random random;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView info;
        private ImageView preview;
        private LinearLayout infoBox;
        private RelativeLayout container;

        MyViewHolder(View view){
            super(view);

            container = view.findViewById(R.id.post_container);
            infoBox = view.findViewById(R.id.post_info_box);
            preview = view.findViewById(R.id.post_preview);
            info = view.findViewById(R.id.post_info);
        }

    }

    public RandomPostsAdapter(Context ctx, int row, List<Post> posts, RandomPostsAdapterListener listener){

        this.context = ctx;
        this.rowLayout = row;
        this.postList = posts;
        this.listener = listener;

        random = new Random();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent,false));

        myViewHolder.preview.setOnClickListener(v -> listener.onPreviewClick(postList.get(position), false));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Post post = postList.get(position);

        ViewGroup.LayoutParams params = holder.preview.getLayoutParams();
        params.height = dpToPx(random.nextInt(100) + 150);
        holder.preview.setLayoutParams(params);

        if (post.getPreviews() != null){

            Glide
                    .with(context)
                    .load(post.getPreviews().get(random.nextInt(post.getPreviews().size())))
                    .into(holder.preview);

        }else {

            holder.container.setVisibility(View.GONE);
            holder.container.startAnimation(getFadeOutAnimation(context));

        }

        if (post.getType() == PRODUCT_AND_SERVICE && post.getProductOrServicePrice() != null && !post.getProductOrServicePrice().isEmpty()){
            String price = post.getProductOrServicePrice() +"\t\t"+ context.getString(R.string.fcfa);
            holder.info.setText(price);
        }else {
            holder.infoBox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public interface RandomPostsAdapterListener{
        void onPreviewClick(Post post, boolean keyboard);
    }
}
