package com.digitaldream.ddl.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.ddl.Adapters.StudentCourseAttendanceAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

public class StudentCourseAttendance extends AppCompatActivity implements StudentCourseAttendanceAdapter.OnStudentClickListener {

    private RecyclerView mRecyclerView;
    private StudentCourseAttendanceAdapter mAttendanceAdapter;
    private List<StudentTable> mStudentTableList;
    private Dao<StudentTable, Long> mStudentTableDao;
    private TextView count;
    private Menu mMenu;
    private FloatingActionButton mActionButton;
    DatabaseHelper mDatabaseHelper;


    private  String mStudentLevelId;
    private  String mStudentClassId;
    private  String name;
    private boolean isSelectAll = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_course_attendance);

        Toolbar toolbar = findViewById(R.id.toolBar);
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

        TextView className = findViewById(R.id.class_title);
        count = findViewById(R.id.student_count);
        className.setText(name);

        mDatabaseHelper = new DatabaseHelper(this);
        try {

            mStudentTableDao =
                    DaoManager.createDao(mDatabaseHelper.getConnectionSource(), StudentTable.class);
            QueryBuilder<StudentTable, Long> queryBuilder =
                    mStudentTableDao.queryBuilder();
            queryBuilder.where().eq("studentLevel", mStudentLevelId).and().eq("studentClass", mStudentClassId);
            mStudentTableList = queryBuilder.query();

            for (StudentTable item: mStudentTableList) {
                Random random = new Random();
                int color = Color.argb(255, random.nextInt(256),
                        random.nextInt(256), random.nextInt(256));
                item.setColor(color);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


        mRecyclerView = findViewById(R.id.students_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mAttendanceAdapter = new StudentCourseAttendanceAdapter(this,
                mStudentTableList, this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(manager);


        LinearLayout layout = findViewById(R.id.empty_state);

        if (mStudentTableList.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            layout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mRecyclerView.setAdapter(mAttendanceAdapter);
            mAttendanceAdapter.notifyDataSetChanged();
        }


        mActionButton = findViewById(R.id.submit_btn);
        mActionButton.setOnClickListener(v ->{

            JSONArray jsonArray = new JSONArray();
            for (StudentTable item: mStudentTableList) {
                if (item.isSelected()){
                    try {
                        JSONObject object = new JSONObject();

                        object.put("id", item.getStudentId());
                        object.put("name", getStudentName());
                        jsonArray.put(object);

                        Log.i("Data", jsonArray.toString());
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }
            finish();
        });



    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.check_all,menu);
        this.mMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.check_all:
                if (isSelectAll){
                    mMenu.getItem(0).setVisible(false);
                    selection(false);
                    isSelectAll= false;
                }else {
                    mMenu.getItem(0).setIcon(ContextCompat.getDrawable(this,
                            R.drawable.ic_check));
                    selection(true);
                    isSelectAll= true;
                }
                count.setText(""+getSelectedCount());

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

        }


        return false;
    }

    @Override
    public void onStudentClick(int position) {

        StudentTable item = mStudentTableList.get(position);

        if (item.isSelected()){
            item.setSelected(false);
        }else {
            mMenu.getItem(0).setVisible(false);
            item.setSelected(true);
        }

        count.setText(""+getSelectedCount());
        mAttendanceAdapter.notifyDataSetChanged();
        invalidateOptionsMenu();
        isSelectAll = false;
        showHideButton();

    }

    @Override
    public void onLongClickStudent(int position) {

    }

    public int getSelectedCount(){
        int a = 0;
        for (StudentTable item: mStudentTableList) {

            if (item.isSelected()){
                a++;
            }
        }
        return a;
    }


    public void showHideButton(){

        for (StudentTable item: mStudentTableList) {

            if (item.isSelected()) {
                isSelectAll = true;
                break;
            }

        }
        if (isSelectAll){
            mActionButton.setVisibility(View.VISIBLE);
        }else{
            mActionButton.setVisibility(View.GONE);
        }
    }

    public void selection(boolean sB){
        boolean show = false;
        for (StudentTable item: mStudentTableList) {
            item.setSelected(sB);

            show = item.isSelected();
        }
        mAttendanceAdapter.notifyDataSetChanged();

        if (show){
            mActionButton.setVisibility(View.VISIBLE);
        }else{
            mActionButton.setVisibility(View.GONE);
        }
    }


    public String getStudentName(){
        String name = "";

        for (StudentTable studentTable: mStudentTableList) {

            String surName = studentTable.getStudentSurname();
            String middleName =  studentTable.getStudentMiddlename();
            String firstName = studentTable.getStudentFirstname();


            try{
                if(surName!=null) {
                    surName = surName.substring(0, 1).toUpperCase() + "" + surName.substring(1).toLowerCase();
                }else {
                    surName = "";
                }
                if(middleName!=null) {
                    middleName = middleName.substring(0, 1).toUpperCase() + "" + middleName.substring(1).toLowerCase();
                }else {
                    middleName="";
                }
                if(firstName!=null) {
                    firstName = firstName.substring(0, 1).toUpperCase() + "" + firstName.substring(1).toLowerCase();
                }else {
                    firstName = "";
                }

            }catch (StringIndexOutOfBoundsException | NullPointerException e){
                e.printStackTrace();
            }

            name = surName+ " " + middleName + " " + firstName;
        }

        return name;
    }

}