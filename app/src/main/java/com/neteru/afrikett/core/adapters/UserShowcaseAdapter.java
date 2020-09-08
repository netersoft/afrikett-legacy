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
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import static com.neteru.afrikett.core.utilities.AppUtilities.cutLongText;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFirstLetters;
import static com.neteru.afrikett.core.utilities.Constants.DEFAULT;

public class UserShowcaseAdapter extends RecyclerView.Adapter<UserShowcaseAdapter.MyViewHolder> {

    private ManageAdapterListener listener;
    private List<Showcase> showcaseList;
    private Context context;
    private int rowLayout;

    class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView banner, logo;
        private TextView name, description, subscribers, date, managers;
        private View bottomView;
        private CardView card;

        MyViewHolder(View view){
            super(view);

            card = view.findViewById(R.id.showcase_card);
            logo = view.findViewById(R.id.showcase_logo);
            banner = view.findViewById(R.id.showcase_banner);
            name = view.findViewById(R.id.showcase_name);
            description = view.findViewById(R.id.showcase_description);
            subscribers = view.findViewById(R.id.showcase_subscribers);
            date = view.findViewById(R.id.showcase_registration_date);
            managers = view.findViewById(R.id.showcase_managers);
            bottomView = view.findViewById(R.id.showcase_bottom);
        }

    }

    public UserShowcaseAdapter(Context ctx, int row, List<Showcase> showcases, ManageAdapterListener listener){

        this.context = ctx;
        this.rowLayout = row;
        this.showcaseList = showcases;
        this.listener = listener;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, final int position) {
        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent,false));

        myViewHolder.card.setOnClickListener(v -> listener.onCardClick(showcaseList.get(position)));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Showcase showcase = showcaseList.get(position);

        String dateText = context.getString(R.string.created_the)+ showcase.getRegistrationDate(),
                managersText = showcase.getOwners().size()+" "+context.getString(R.string.administrators),
                    subscribersText = showcase.getNb_subscribers()+" "+context.getString(R.string.subscribers);

        holder.name.setText(cutLongText(showcase.getName(), 21));
        holder.description.setText(cutLongText(showcase.getDescription(), 140));
        holder.date.setText(dateText);
        holder.managers.setText(managersText);
        holder.subscribers.setText(subscribersText);
        holder.bottomView.setBackgroundColor(Color.parseColor(showcase.getSecondaryColor()));

        if (showcase.getLogo().equals(DEFAULT)){

            holder.logo.setImageDrawable(TextDrawable.builder().buildRound(getFirstLetters(showcase.getName()), Color.parseColor(showcase.getSecondaryColor())));

        }else{

            Glide
                    .with(context)
                    .load(showcase.getLogo())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.logo);
        }

        if (showcase.getBanner().equals(DEFAULT)){

            holder.banner.setBackgroundColor(Color.parseColor(showcase.getPrimaryColor()));

        }else{

            Glide
                    .with(context)
                    .load(showcase.getBanner())
                    .into(holder.banner);
        }

    }

    @Override
    public int getItemCount() {
        return showcaseList.size();
    }

    public interface ManageAdapterListener{
        void onCardClick(Showcase showcase);
    }
}
