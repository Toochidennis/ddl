package com.digitaldream.ddl.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.digitaldream.ddl.Adapters.AdminElearningCourseAdapter;
import com.digitaldream.ddl.Adapters.StudentElearningCourseAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.R;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class AdminElearningCourses extends AppCompatActivity implements StudentElearningCourseAdapter.OnCourseClickListener {
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private DatabaseHelper databaseHelper;
    private Dao<CourseTable,Long> courseDao;
    private List<CourseTable> courseList;
    private String levelId;
    private String levelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_elearning_courses);
        recyclerView = findViewById(R.id.e_learning_student_course_recycler);
        toolbar = findViewById(R.id.toolbar);
        levelName = getIntent().getStringExtra("levelName");
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(levelName.toUpperCase()+" Courses");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.arrow_left);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        levelId = getIntent().getStringExtra("levelId");
        databaseHelper = new DatabaseHelper(this);
        try {
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            //QueryBuilder<CourseTable,Long> queryBuilder = courseDao.queryBuilder();
            //queryBuilder.groupBy("courseId").where().eq("levelId", AdminElearning.levelsId);
            //courseList = queryBuilder.query();
            courseList = courseDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.sort(courseList, (sCourseTable, sT1) ->
                sCourseTable.getCourseName().compareToIgnoreCase(sT1.getCourseName()));

        AdminElearningCourseAdapter adapter = new AdminElearningCourseAdapter(this,courseList,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCourseClick(int position) {
        Intent intent = new Intent(this,CourseOutlines.class);
        intent.putExtra("levelId",levelId);
        intent.putExtra("courseId",courseList.get(position).getCourseId());
        intent.putExtra("courseName",courseList.get(position).getCourseName());
        intent.putExtra("levelName",levelName);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ((item.getItemId())){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
}
