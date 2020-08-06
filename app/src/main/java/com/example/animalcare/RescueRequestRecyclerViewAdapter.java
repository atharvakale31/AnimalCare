package com.example.animalcare;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RescueRequestRecyclerViewAdapter extends RecyclerView.Adapter<RescueRequestRecyclerViewAdapter.RescueViewHolder> {

    private ArrayList<RescueCard> rescueArrayList;

    public RescueRequestRecyclerViewAdapter(ArrayList<RescueCard> rescueArrayList){
        this.rescueArrayList = rescueArrayList;

    }

    @NonNull
    @Override
    public RescueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rescue_requests,parent,false);
        return new RescueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RescueViewHolder holder, int position) {

        RescueCard currentRescueCard = rescueArrayList.get(position);

        if(currentRescueCard.getAnimalType().toLowerCase().equals("dog"))
            holder.imageViewAnimalType.setImageResource(R.drawable.doggylogo);
        else if(currentRescueCard.getAnimalType().toLowerCase().equals("cat"))
            holder.imageViewAnimalType.setImageResource(R.drawable.cat);

        holder.textViewRescueStatus.setText(currentRescueCard.getRescueStatus());
        holder.textViewAnimalLocationLandmark.setText(currentRescueCard.getAnimalLocationLandmark());

    }


    public static class RescueViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewAnimalLocationLandmark, textViewRescueStatus;
        private Button btnAcceptRescue, btnDeclineRescue;
        private ImageView imageViewAnimalType;

        public RescueViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAnimalLocationLandmark = itemView.findViewById(R.id.textViewAnimalLocationLandmark);
            textViewRescueStatus = itemView.findViewById(R.id.textViewRescueStatus);
            btnAcceptRescue = itemView.findViewById(R.id.btnAcceptRescue);
            btnDeclineRescue = itemView.findViewById(R.id.btnDeclineRescue);
            imageViewAnimalType = itemView.findViewById(R.id.imageViewAnimalType);

        }
    }

    @Override
    public int getItemCount() {
        return rescueArrayList.size();
    }
}
