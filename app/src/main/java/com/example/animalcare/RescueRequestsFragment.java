package com.example.animalcare;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

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
    private ArrayList<RescueCard> rescueCardArrayList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rescue_requests, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView_rescue_requests);
        rescueCardArrayList = new ArrayList<>();
        temp();
        recyclerViewAdapter = new RescueRequestRecyclerViewAdapter(rescueCardArrayList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void temp(){
        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Kondhwa Budruk",
                "Waiting for rescue"));
        rescueCardArrayList.add(new RescueCard("cat","A cat needs your help near Vishwakarma College",
                "Waiting for rescue"));
        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Kondhwa Budruk",
                "Waiting for rescue"));
        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Vishwakarma College",
                "Waiting for rescue"));
        rescueCardArrayList.add(new RescueCard("cat","A cat needs your help near Kondhwa Budruk",
                "Waiting for rescue"));
        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Vishwakarma College",
                "Waiting for rescue"));
        rescueCardArrayList.add(new RescueCard("dog","A dog needs your help near Kondhwa Budruk",
                "Waiting for rescue"));
    }

}