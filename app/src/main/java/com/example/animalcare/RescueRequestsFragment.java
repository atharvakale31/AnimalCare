package com.example.animalcare;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RescueRequestsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private RescueRequestRecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<AnimalHelpCase> rescueCardArrayList;
    private FirebaseFirestore firebaseFirestore;

    public RescueRequestsFragment() {
        // Required empty public constructor
    }


    public static RescueRequestsFragment newInstance(String param1, String param2) {
        RescueRequestsFragment fragment = new RescueRequestsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rescue_requests, container, false);
    }

    public void updateHelpRequests(final View view){


        firebaseFirestore.collection("Cases")
                .document("Topic")
                .collection("Pune")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Snackbar.make(view,"Some error occurred :(",Snackbar.LENGTH_LONG)
                            .show();
                }else {
                    if(value==null || value.isEmpty()){
                        Snackbar snackbar = Snackbar.make(view,"No recent requests !",Snackbar.LENGTH_LONG);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                        snackbar.setActionTextColor(Color.GREEN);
                        snackbar.show();
                    }else{
                        rescueCardArrayList.clear();
                        for(DocumentSnapshot documentSnapshot : value){
                            if(documentSnapshot!=null) {
                                rescueCardArrayList.add(documentSnapshot.toObject(AnimalHelpCase.class));
                            }
                        }
                        setUpRecyclerview();
                    }
                }
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_rescue_requests);
        rescueCardArrayList = new ArrayList<>();
        updateHelpRequests(view);

    }

    public void setUpRecyclerview(){

        recyclerViewAdapter = new RescueRequestRecyclerViewAdapter(rescueCardArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(new RescueRequestRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view, final View itemView) {

                switch (view.getId()){

                    case R.id.btnAcceptRescue:
                        getRescueData(position);
                        Toast.makeText(getContext(), "ACCEPT rescue " +
                                rescueCardArrayList.get(position).getAnimalType(), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.btnDeclineRescue:
                        createAlertDialog(position, itemView);
                        Toast.makeText(getContext(), "Decline rescue "+
                                rescueCardArrayList.get(position).getAnimalType(), Toast.LENGTH_SHORT).show();
                        break;

                }

            }
        });

    }

    public void getRescueData(int position){
        if(position>=0) {
            AnimalHelpCase rescueCase = rescueCardArrayList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("USERNAME", rescueCase.getUserName());
            String userNo = "user no";
            if(rescueCase.getUserNo()!=null)
                userNo = rescueCase.getUserNo();
            bundle.putString("PHONENO", ""+userNo);
            bundle.putString("ANIMALTYPE", rescueCase.getAnimalType());
            bundle.putString("LAT", String.valueOf(rescueCase.getLatitude()));
            bundle.putString("LNG", String.valueOf(rescueCase.getLongitude()));
            bundle.putString("URL", rescueCase.getPhotourl());
            bundle.putString("Location", rescueCase.getUserLocation());
            bundle.putString("description",rescueCase.getDesc());
            bundle.putString("time",rescueCase.getTimestamp().toDate().toString());
            bundle.putString("FromWhere", "NOTIF");
            Intent intent = new Intent(getContext(),RescueActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    public void createAlertDialog(final int position, final View itemView){
        new AlertDialog.Builder(Objects.requireNonNull(getContext()))
                .setTitle("Decline help request")
                .setMessage("Do you want to decline help request ?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideRescueButtons(position, itemView);
                        Toast.makeText(getContext(), "help request declined", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("NO",null)
                .show();
    }

    public void hideRescueButtons(int position, View itemView){
        itemView.findViewById(R.id.linearLayoutRescueBtn).setVisibility(View.GONE);
    }

//    public void temp(){
//        rescueCardArrayList.add(new RescueCard("dog","Near Kondhwa Budruk",
//                "Waiting for rescue"));
//        rescueCardArrayList.add(new RescueCard("cat","Near Vishwakarma College",
//                "Waiting for rescue"));
//        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Kondhwa Budruk",
//                "Waiting for rescue"));
//        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Vishwakarma College",
//                "Waiting for rescue"));
//        rescueCardArrayList.add(new RescueCard("cat","A cat needs your help near Kondhwa Budruk",
//                "Waiting for rescue"));
//        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Vishwakarma College",
//                "Waiting for rescue"));
//        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Kondhwa Budruk",
//                "Waiting for rescue"));
//    }

}