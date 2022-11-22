package com.digitaldream.ddl.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Adapters.AttendanceAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Fragments.DateDialogFragment;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.Models.TeachersTable;
import com.digitaldream.ddl.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class AttendanceActivity extends AppCompatActivity implements AttendanceAdapter.OnDayClickListener, DatePickerDialog.OnDateSetListener {

    FloatingActionButton mTakeAttendance, mClassAttendance, mCourseAttendance,
            mTakeAttendanceEmpty, mClassAttendanceEmpty, mCourseAttendanceEmpty;
    Animation mFabOpen, mFabClose, mRotateForward, mRotateBackward;
    private boolean isOpen = false;

    private TextView mName;
    private  String mStudentLevelId, mStudentClassId, mClassName;
    private List<StudentTable> mStudentTableList;
    private AttendanceAdapter mAttendanceAdapter;
    private Dao<GeneralSettingModel, Long> mSettingModelDao;
    private  List<GeneralSettingModel> mSettingModelList;
    private Menu mMenu;

    private RecyclerView mRecyclerView;
    private RelativeLayout mEmptyLayout, mUnEmptyLayout;

    private String year, term, db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);


        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Attendance");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);


        Intent intent = getIntent();
        mStudentLevelId = intent.getStringExtra("levelId");
        mStudentClassId = intent.getStringExtra("classId");
        mClassName = intent.getStringExtra("className");

        mName = findViewById(R.id.class_name);
        mName.setText(mClassName);

        DatabaseHelper mDatabaseHelper = new DatabaseHelper(this);

        try {

            mSettingModelDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(),GeneralSettingModel.class);
            mSettingModelList = mSettingModelDao.queryForAll();

        }catch (SQLiteException | SQLException e){
            e.printStackTrace();
        }



        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        term = sharedPreferences.getString("term","");
        year = mSettingModelList.get(0).getSchoolYear();
        Log.i("term", year);




        mStudentTableList = new ArrayList<>();

        mRecyclerView = findViewById(R.id.attendance_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mAttendanceAdapter = new AttendanceAdapter(this, mStudentTableList,
                this);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);


        mEmptyLayout = findViewById(R.id.empty_state);
        mUnEmptyLayout = findViewById(R.id.un_empty_state);

        if (mStudentTableList.isEmpty()){
            mEmptyLayout.setVisibility(View.VISIBLE);
            mUnEmptyLayout.setVisibility(View.GONE);
        }else{
            mEmptyLayout.setVisibility(View.GONE);
            mUnEmptyLayout.setVisibility(View.VISIBLE);

            mRecyclerView.setAdapter(mAttendanceAdapter);
            mAttendanceAdapter.notifyDataSetChanged();

        }

        getAttendance();

        fabButtonAction();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        this.mMenu = menu;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.search:
                FragmentActivity activity = this;
                FragmentManager manager = activity.getSupportFragmentManager();
                DateDialogFragment dialogFragment = new DateDialogFragment();
                dialogFragment.show(manager, "date picker");
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;

    }

    @Override
    public void onDayClick(int position) {

    }

    @Override
    public void onDayLongClick(int position) {

    }

    @Override
    public void onDateSet(DatePicker sDatePicker, int year, int month,
                          int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate =
                DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        mAttendanceAdapter.getFilter().filter(currentDate);


    }

    public void fabButtonAction(){

        //Un empty state
        mTakeAttendance = findViewById(R.id.take_attendance);
        mClassAttendance =  findViewById(R.id.class_attendance);
        mCourseAttendance = findViewById(R.id.course_attendance);




        mFabOpen = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(this, R.anim.fab_close);

        mRotateForward = AnimationUtils.loadAnimation(this,
                R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(this,
                R.anim.rotate_backward);



        mTakeAttendance.setOnClickListener(v->{
            onFabAnimation(mTakeAttendance, mClassAttendance, mCourseAttendance);

        });

        mClassAttendance.setOnClickListener(v->{
            Intent newIntent = new Intent(AttendanceActivity.this,
                    ClassAttendance.class);
            newIntent.putExtra("levelId",mStudentLevelId);
            newIntent.putExtra("classId",mStudentClassId);
            newIntent.putExtra("className", mClassName);
            startActivity(newIntent);
        });

        mCourseAttendance.setOnClickListener(v ->{
            Intent newIntent = new Intent(AttendanceActivity.this,
                    CourseAttendance.class);
            newIntent.putExtra("levelId",mStudentLevelId);
            newIntent.putExtra("classId",mStudentClassId);
            newIntent.putExtra("className", mClassName);
            startActivity(newIntent);
        });



        //empty state
        mTakeAttendanceEmpty = findViewById(R.id.take_attendance_empty);
        mClassAttendanceEmpty =  findViewById(R.id.class_attendance_empty);
        mCourseAttendanceEmpty = findViewById(R.id.course_attendance_empty);


        mTakeAttendanceEmpty.setOnClickListener(v->{
            onFabAnimation(mTakeAttendanceEmpty, mClassAttendanceEmpty, mCourseAttendanceEmpty);

        });

        mClassAttendanceEmpty.setOnClickListener(v->{
            Intent newIntent = new Intent(AttendanceActivity.this,
                    ClassAttendance.class);
            newIntent.putExtra("levelId",mStudentLevelId);
            newIntent.putExtra("classId",mStudentClassId);
            newIntent.putExtra("className", mClassName);
            startActivity(newIntent);
        });

        mCourseAttendanceEmpty.setOnClickListener(v ->{
            Intent newIntent = new Intent(AttendanceActivity.this,
                    CourseAttendance.class);
            newIntent.putExtra("levelId",mStudentLevelId);
            newIntent.putExtra("classId",mStudentClassId);
            newIntent.putExtra("className", mClassName);
            startActivity(newIntent);
        });


    }

    public void onFabAnimation(FloatingActionButton sTake,
                               FloatingActionButton sClass,
                               FloatingActionButton sCourse){
        if (isOpen){
             sTake.startAnimation(mRotateForward);
             sClass.startAnimation(mFabClose);
             sCourse.startAnimation(mFabClose);
             sClass.setClickable(false);
             sClass.setClickable(false);
             isOpen = false;
        }else {
            sTake.startAnimation(mRotateBackward);
            sClass.startAnimation(mFabOpen);
            sCourse.startAnimation(mFabOpen);
            sCourse.setClickable(true);
            sClass.setClickable(true);

            sClass.setVisibility(View.VISIBLE);
            sCourse.setVisibility(View.VISIBLE);

            isOpen = true;
        }
    }




    public void getAttendance(){
        String url = Login.urlBase+"/getAttendance.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                url, response -> {
            Log.i("response", response);

        }, Throwable::printStackTrace){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("year", year);
                stringMap.put("class", mStudentClassId);
                stringMap.put("term", term);
                stringMap.put("_db", db);
                return stringMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public String dateConverter(String date){

       date =  DateFormat.getDateInstance(DateFormat.FULL).format(date);

        return date;

    }

}