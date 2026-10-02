package com.example.smartpantry.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartpantry.MainActivity;
import com.example.smartpantry.R;

// TEMPORARY so the Recipes tab opens
public class SuggestedRecipesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.layout_empty_recipes).setVisibility(View.VISIBLE);
        view.findViewById(R.id.btn_go_to_pantry).setOnClickListener(v ->
                ((MainActivity) requireActivity()).showPantryTab());
    }
}