package com.example.localreminder;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class CompletedFragment extends Fragment {

    RecyclerView completedListRecyclerView;
    CompletedAdapter completedAdapter;
    MyDataBaseHandler myDataBaseHandler;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        completedListRecyclerView = view.findViewById(R.id.completedListRecyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipe);
        myDataBaseHandler = new MyDataBaseHandler(requireContext());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ArrayList<MyDbDataModel> completedDataList = myDataBaseHandler.getAllCompletedData();
                completedAdapter = new CompletedAdapter(requireContext(), completedDataList);
                completedListRecyclerView.setAdapter(completedAdapter);
                completedListRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }
}