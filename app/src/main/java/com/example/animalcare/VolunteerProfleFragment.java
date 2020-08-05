package com.example.animalcare;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VolunteerProfleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VolunteerProfleFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public VolunteerProfleFragment() {
        // Required empty public constructor
    }

    public static VolunteerProfleFragment newInstance(String param1, String param2) {
        VolunteerProfleFragment fragment = new VolunteerProfleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private Button btnVolunteerLogout;

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
        return inflater.inflate(R.layout.fragment_volunteer_profle, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnVolunteerLogout = view.findViewById(R.id.btnVolunteerLogout);
        btnVolunteerLogout.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.btnVolunteerLogout){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "LogOut Successful", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getContext(), MainActivity.class);
            Objects.requireNonNull(getActivity()).finish();
            startActivity(i);
        }

    }
}