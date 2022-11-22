package com.digitaldream.ddl.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.ddl.Adapters.CourseAttendanceAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class CourseAttendance extends AppCompatActivity implements CourseAttendanceAdapter.OnCourseClickListener{

    private RecyclerView mRecyclerView;
    private CourseAttendanceAdapter mCourseAttendanceAdapter;
    private  Dao<GeneralSettingModel, Long> mSettingModelDao;
    private Dao<CourseTable, Long> mCourseDao;
    private List<CourseTable> mCourseList;
    private  List<GeneralSettingModel> mSettingModelList;
    DatabaseHelper mDatabaseHelper;

    private  String mStudentLevelId;
    private  String mStudentClassId;
    private  String name;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_course);

        Toolbar toolbar = findViewById(R.id.bar_tool);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Course Attendance");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);


        Intent intent = getIntent();
        mStudentLevelId = intent.getStringExtra("levelId");
        mStudentClassId = intent.getStringExtra("classId");
         name = intent.getStringExtra("className");

        TextView className = findViewById(R.id.class_name);

        String title = name + " " + "Attendance";

        className.setText(title);


        mDatabaseHelper = new DatabaseHelper(this);
        try {
            mCourseDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(),CourseTable.class);
            mCourseList = mCourseDao.queryForAll();
            Random rnd = new Random();
            for (CourseTable ct: mCourseList){
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                ct.setColor(currentColor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        mRecyclerView = findViewById(R.id.courses_recycler);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);

        LinearLayout linearLayout  = findViewById(R.id.empty_state);

        if (mCourseList.isEmpty()){
            linearLayout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

        }else {
            linearLayout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mCourseAttendanceAdapter = new CourseAttendanceAdapter(this,
                    mCourseList, this);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setAdapter(mCourseAttendanceAdapter);
        }



    }

    @Override
    public void onCourseClick(int position) {
        Intent newIntent = new Intent(CourseAttendance.this,
                StudentCourseAttendance.class);
        newIntent.putExtra("levelId",mStudentLevelId);
        newIntent.putExtra("classId",mStudentClassId);
        newIntent.putExtra("className", name);
        startActivity(newIntent);

    }

    @Override
    public void onCourseLongClick(int position) {

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return false;
    }
}
