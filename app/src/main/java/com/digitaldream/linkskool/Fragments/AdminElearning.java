package com.digitaldream.linkskool.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.digitaldream.linkskool.Activities.AdminElearningCourses;
import com.digitaldream.linkskool.Activities.ElearningCourses;
import com.digitaldream.linkskool.Adapters.ElearningLevelAdapter;
import com.digitaldream.linkskool.Adapters.ElearningLevelAdapter1;
import com.digitaldream.linkskool.DatabaseHelper;
import com.digitaldream.linkskool.Models.CourseTable;
import com.digitaldream.linkskool.Models.LevelTable;
import com.digitaldream.linkskool.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminElearning extends Fragment implements ElearningLevelAdapter.OnLevelClickListener {
    RecyclerView recyclerView;
    private List<CourseTable> coursesList;
    DatabaseHelper databaseHelper;
    Dao<CourseTable,Long> courseTableDao;
    Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private Toolbar toolbar;
    public static String levelsId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_elearning, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("E-learning");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.e_learning_levels);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        databaseHelper = new DatabaseHelper(getContext());
        try {
            courseTableDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            levelDao = DaoManager.createDao(databaseHelper.getConnectionSource(), LevelTable.class);
            //coursesList = courseTableDao.queryBuilder().groupBy("levelId").query();
            //coursesList = courseTableDao.queryForAll();
            levelList = levelDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ElearningLevelAdapter1 adapter = new ElearningLevelAdapter1(getContext(),levelList,this);
        recyclerView.setAdapter(adapter);
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    @Override
    public void onLevelClick(int position) {
        Intent intent = new Intent(getContext(), AdminElearningCourses.class);
        intent.putExtra("levelId",levelList.get(position).getLevelId());
        levelsId = levelList.get(position).getLevelId();
        intent.putExtra("levelName",levelList.get(position).getLevelName());
        startActivity(intent);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
