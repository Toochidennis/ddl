package com.digitaldream.ddl.Activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.digitaldream.ddl.Adapters.AllStaffCourseAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;

public class StaffCourses extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Dao<CourseTable,Long> courseDao;
    private List<CourseTable> courseList;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private LinearLayout emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_courses);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Courses");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        databaseHelper = new DatabaseHelper(this);
        try {
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            courseList = courseDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        recyclerView = findViewById(R.id.all_staff_course_recycler);
        emptyState = findViewById(R.id.staff_course_empty_state);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if(!courseList.isEmpty()) {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            AllStaffCourseAdapter adapter = new AllStaffCourseAdapter(this, courseList);
            recyclerView.setAdapter(adapter);
        }else{
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return false;
    }
}
