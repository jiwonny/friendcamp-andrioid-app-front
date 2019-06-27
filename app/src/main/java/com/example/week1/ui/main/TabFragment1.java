package com.example.week1.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.week1.R;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class TabFragment1 extends Fragment {


    private PageViewModel pageViewModel;

    public TabFragment1 () { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 리사이클러뷰에 표시할 데이터 리스트 생성.
        View view =  inflater.inflate(R.layout.contacts, container, false);
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<100; i++) {
            list.add(String.format("TEXT %d", i)) ;
        }
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = view.findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())) ;

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        SimpleTextAdapter adapter = new SimpleTextAdapter(list) ;
        recyclerView.setAdapter(adapter) ;
        return view;
    }
}