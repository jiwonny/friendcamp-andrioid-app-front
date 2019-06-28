package com.example.week1.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;
import com.example.week1.ui.main.PageViewModel;


public class TabFragment2 extends Fragment {

    private PageViewModel pageViewModel;

    public TabFragment2 () { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tabfragment2, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.gallery_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),2));



        return root;
    }
}