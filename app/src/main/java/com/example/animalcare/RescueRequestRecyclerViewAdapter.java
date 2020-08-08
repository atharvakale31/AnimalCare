package com.example.animalcare;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RescueRequestRecyclerViewAdapter extends RecyclerView.Adapter<RescueRequestRecyclerViewAdapter.RescueViewHolder> {

    private ArrayList<AnimalHelpCase> rescueArrayList;

    public RescueRequestRecyclerViewAdapter(ArrayList<AnimalHelpCase> rescueArrayList){
        this.rescueArrayList = rescueArrayList;

    }

    public OnItemClickListener itemClickListener;

    public interface  OnItemClickListener{
        void onItemClick(int position, View view, View itemView);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }

    @NonNull
    @Override
    public RescueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_rescue_requests,parent,false);
        return new RescueViewHolder(view,itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RescueViewHolder holder, int position) {

        AnimalHelpCase currentRescueCard = rescueArrayList.get(position);

        if(currentRescueCard.getAnimalType().toLowerCase().equals("dog")) {
            holder.imageViewAnimalType.setImageResource(R.drawable.doggylogo);
            holder.textViewAnimalLocationLandmark.setText("A dog needs your help");
        }
        else if(currentRescueCard.getAnimalType().toLowerCase().equals("cat")) {
            holder.imageViewAnimalType.setImageResource(R.drawable.cat);
            holder.textViewAnimalLocationLandmark.setText("A cat needs your help");
        }

        holder.textViewRescueStatus.setText(currentRescueCard.getRescueStatus());
        if(currentRescueCard.getTimestamp()!=null) {
            Date date = currentRescueCard.getTimestamp().toDate();

            String time = new SimpleDateFormat("h:mm a").format(date);
            String rescueDate = new SimpleDateFormat("dd-MMM-yyyy").format(date);
            Log.d("DATETIME", time +" - - " + rescueDate);

            holder.textViewRescueTime.setText(time);
            holder.textViewRescueDate.setText(rescueDate);
        }

        //holder.textViewAnimalLocationLandmark.setText(currentRescueCard.getAnimalLocationLandmark());

    }


    public static class RescueViewHolder extends RecyclerView.ViewHolder{

        private TextView textViewAnimalLocationLandmark, textViewRescueStatus, textViewRescueTime, textViewRescueDate;
        private Button btnAcceptRescue, btnDeclineRescue;
        private ImageView imageViewAnimalType;

        public RescueViewHolder(@NonNull final View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);

            textViewAnimalLocationLandmark = itemView.findViewById(R.id.textViewAnimalLocationLandmark);
            textViewRescueStatus = itemView.findViewById(R.id.textViewRescueStatus);
            btnAcceptRescue = itemView.findViewById(R.id.btnAcceptRescue);
            btnDeclineRescue = itemView.findViewById(R.id.btnDeclineRescue);
            imageViewAnimalType = itemView.findViewById(R.id.imageViewAnimalType);
            textViewRescueTime = itemView.findViewById(R.id.textViewRescueTime);
            textViewRescueDate = itemView.findViewById(R.id.textViewRescueDate);

            btnAcceptRescue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(itemClickListener!=null && position!=RecyclerView.NO_POSITION){
                        itemClickListener.onItemClick(position, btnAcceptRescue, itemView);
                    }
                }
            });

            btnDeclineRescue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(itemClickListener!=null && position!=RecyclerView.NO_POSITION){
                        itemClickListener.onItemClick(position, btnDeclineRescue, itemView);
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return rescueArrayList.size();
    }
}
