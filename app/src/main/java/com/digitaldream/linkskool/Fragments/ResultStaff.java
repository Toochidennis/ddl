package com.digitaldream.linkskool.Fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.digitaldream.linkskool.Adapters.StaffCourseAdapter;
import com.digitaldream.linkskool.DatabaseHelper;
import com.digitaldream.linkskool.Models.ClassNameTable;
import com.digitaldream.linkskool.Models.CourseTable;
import com.digitaldream.linkskool.Models.LevelTable;
import com.digitaldream.linkskool.Models.StudentTable;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.StudentResultAdapter;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResultStaff extends Fragment  {
    private RecyclerView recyclerView;
    private Spinner level,classes;
    private List<String> spinnerLevelList,spinnerClassList;
    private DatabaseHelper databaseHelper;
    private Dao<LevelTable,Long> levelDao;
    private Dao<ClassNameTable,Long> classDao;
    private List<LevelTable> levelList;
    private List<ClassNameTable> classList;
    private Dao<CourseTable,Long> coursetDao;
    private List<StudentTable> studentResultList;
    private String studentLevelId;
    private StudentResultAdapter studentResultAdapter;
    private LinearLayout emptyState;
    private List<CourseTable> courseList;
    private Toolbar toolbar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_staff, container, false);
        try {
            databaseHelper = new DatabaseHelper(getContext());
            coursetDao = DaoManager.createDao(databaseHelper.getConnectionSource(),CourseTable.class);
            courseList = coursetDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setHasOptionsMenu(true);

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Results");
        toolbar.setNavigationIcon(R.drawable.arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        emptyState = view.findViewById(R.id.staff_studentResult_empty_state);

        recyclerView = view.findViewById(R.id.staff_course_results);
        if(!courseList.isEmpty()) {

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            StaffCourseAdapter adapter = new StaffCourseAdapter(getContext(), courseList);
            recyclerView.setAdapter(adapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);

        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

    }
}
