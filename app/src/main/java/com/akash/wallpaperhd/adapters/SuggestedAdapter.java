package com.akash.wallpaperhd.adapters;

import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akash.wallpaperhd.R;
import com.akash.wallpaperhd.interfaces.RecyclerViewClickListener;
import com.akash.wallpaperhd.models.SuggestedModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SuggestedAdapter extends RecyclerView.Adapter<SuggestedAdapter.SuggestedViewHolder> {
    ArrayList<SuggestedModel> suggestedModels;
    final private RecyclerViewClickListener clickListener;

    public SuggestedAdapter(ArrayList<SuggestedModel> suggestedModels, RecyclerViewClickListener clickListener) {
        this.suggestedModels = suggestedModels;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public SuggestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_item, parent, false);
        final SuggestedViewHolder suggestedViewHolder = new SuggestedViewHolder(view);
        return suggestedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedViewHolder holder, int position) {
        SuggestedModel suggestedModel = suggestedModels.get(position);
        holder.image.setImageResource(suggestedModel.getImage());
        holder.title.setText(suggestedModel.getTitle());

    }

    @Override
    public int getItemCount() {
        return suggestedModels.size();
    }
    public class SuggestedViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        TextView title;

        public SuggestedViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.suggestedImage);
            title = itemView.findViewById(R.id.suggestedTitle);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(getAdapterPosition());
                }
            });
        }
    }
}
