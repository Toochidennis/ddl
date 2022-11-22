package com.digitaldream.ddl.Activities;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Adapters.ClassAttendanceAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.GeneralSettingModel;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.Models.TeachersTable;
import com.digitaldream.ddl.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClassAttendance extends AppCompatActivity implements ClassAttendanceAdapter.OnStudentClickListener {

    private FloatingActionButton mActionButton;
    private RecyclerView mRecyclerView;
    private ClassAttendanceAdapter mClassAttendanceAdapter;
    private List<StudentTable> mStudentTableList;
    private  Dao<StudentTable, Long> mStudentTableDao;
    private  Dao<GeneralSettingModel, Long> mSettingModelDao;
    private Dao<ClassNameTable, Long> mClassNameDao;
    private List<ClassNameTable> mClassNameList;
    private Dao<TeachersTable,Long> mTeacherDao;
    private List<TeachersTable> mTeachersTableList;
    private  List<GeneralSettingModel> mSettingModelList;
    DatabaseHelper mDatabaseHelper;

    private  String mStudentLevelId;
    private String staffId;
    private  String mStudentClassId;
    private boolean mSelectAll=false;
    private Menu mMenu;
    private static String db,year,term;

    TextView count, mStaffId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_class);

        Toolbar toolbar = findViewById(R.id.course_tool_bar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setTitle("Class Attendance");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.arrow_left);


        Intent intent = getIntent();
        mStudentLevelId = intent.getStringExtra("levelId");
        mStudentClassId = intent.getStringExtra("classId");
        String name = intent.getStringExtra("className");

        TextView className = findViewById(R.id.class_title);
        count = findViewById(R.id.attendance_count);
        mStaffId = findViewById(R.id.class_description);
        className.setText(name);

        mDatabaseHelper = new DatabaseHelper(this);


        try {
            mStudentTableDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(),StudentTable.class);
            mSettingModelDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(),GeneralSettingModel.class);
            mClassNameDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(), ClassNameTable.class);
            mTeacherDao = DaoManager.createDao(mDatabaseHelper.getConnectionSource(), TeachersTable.class);
            mSettingModelList = mSettingModelDao.queryForAll();
            QueryBuilder<StudentTable,Long> queryBuilder = mStudentTableDao.queryBuilder();

            queryBuilder.where().eq("studentLevel", mStudentLevelId).and().eq(
                    "studentClass", mStudentClassId);
            mStudentTableList = queryBuilder.query();
            Random rnd = new Random();
            for (StudentTable table: mStudentTableList){
                int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                table.setColor(currentColor);
            }

            QueryBuilder<ClassNameTable,Long> classNameQueryBuilder=mClassNameDao.queryBuilder();
            classNameQueryBuilder.where().eq("classId",mStudentClassId);
            mClassNameList = classNameQueryBuilder.query();

            QueryBuilder<TeachersTable,Long> teachersTableQueryBuilder= mTeacherDao.queryBuilder();
            teachersTableQueryBuilder.where().eq("staffId",
                    mClassNameList.get(0).getFormTeacher());
            mTeachersTableList = teachersTableQueryBuilder.query();
            staffId = mTeachersTableList.get(0).getStaffId();
            mStaffId.setText(staffId);
            Log.i("staffid", staffId);

        }catch (SQLiteException | SQLException e){
            e.printStackTrace();
        }



        SharedPreferences sharedPreferences = getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        db = sharedPreferences.getString("db","");
        term = sharedPreferences.getString("term","");
        year = mSettingModelList.get(0).getSchoolYear();
        Log.i("term", year);
        //int previousYear = Integer.parseInt(year)-1;
        String termText = "";
        switch (term) {
            case "1":
                termText = "First Term";
                break;
            case "2":
                termText = "Second Term";
                break;
            case "3":
                termText = "Third Term";
                break;
        }
        TextView classYear = findViewById(R.id.class_year);

      //  classYear.setText(String.format("%d/%s %s", previousYear, year,
             //   termText));

        mRecyclerView =  findViewById(R.id.students_recycler);
        mRecyclerView.setHasFixedSize(true);
        mClassAttendanceAdapter = new ClassAttendanceAdapter(this, mStudentTableList,
                this);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(manager);

        LinearLayout layout = findViewById(R.id.empty_state);

        if (mStudentTableList.isEmpty()){
            layout.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            layout.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            mRecyclerView.setAdapter(mClassAttendanceAdapter);
            mClassAttendanceAdapter.notifyDataSetChanged();
        }


        mActionButton = findViewById(R.id.submit_btn);
        mActionButton.setOnClickListener(v->{
            JSONArray jsonArray = new JSONArray();
            for (StudentTable studentTable : mStudentTableList){
                if(studentTable.isSelected()){
                    try {

                        JSONObject studentObject = new JSONObject();
                        studentObject.put("id",studentTable.getStudentId());
                        studentObject.put("name",getStudentName());

                        jsonArray.put(studentObject);

                        Log.i("value", jsonArray.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            takeClassAttendance(mStudentClassId,
                    mStaffId.getText().toString(),
                    jsonArray.toString(), year, term, db,
                    count.getText().toString());

        });


    }


    private int getSelectedCount(){
        int a =0;
        for(StudentTable table : mStudentTableList){
            if(table.isSelected()){
                a++;
            }
        }
        return a;
    }

    private void showHideFab(){
        boolean show = false;

        for(StudentTable table : mStudentTableList){
            if(table.isSelected()){
                show=true;
                break;
            }
        }

        if(show){

            mActionButton.setVisibility(View.VISIBLE);
        }else {
            mActionButton.setVisibility(View.GONE);
        }
    }


    private void selection(boolean value){
        boolean show = false;
        for ( StudentTable st : mStudentTableList){
            st.setSelected(value);
            if(st.isSelected()){
                show=true;
            }else {
                show=false;
            }
        }
        mClassAttendanceAdapter.notifyDataSetChanged();
        if(show){
            mActionButton.setVisibility(View.VISIBLE);
        }else {
            mActionButton.setVisibility(View.GONE);
        }
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
                if (mSelectAll){
                    mMenu.getItem(0).setVisible(false);
                    selection(false);
                    mSelectAll= false;
                }else {
                    mMenu.getItem(0).setIcon(ContextCompat.getDrawable(this,
                            R.drawable.ic_check));
                    selection(true);
                    mSelectAll= true;
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

        StudentTable studentTable = mStudentTableList.get(position);
        if(studentTable.isSelected()){
            studentTable.setSelected(false);
        }else {
            mMenu.getItem(0).setVisible(false);
            studentTable.setSelected(true);
        }

        count.setText(""+getSelectedCount());
        mClassAttendanceAdapter.notifyDataSetChanged();
        invalidateOptionsMenu();
        mSelectAll=false;
        showHideFab();

    }

    @Override
    public void onStudentLongClick(int position) {

    }


    private void takeClassAttendance(String sClassId, String sStaffId,
                                     String sStudents, String sYear,
                                     String sTerm, String sDb, String sCount){
        /*CustomDialog dialog = new CustomDialog(this);
        dialog.show();*/
        String url = Login.urlBase+"/setAttendance.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
            Log.i("Response", response);

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")){
                            Toast.makeText(ClassAttendance.this,"Operation was successful",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }else if(response.equals("0")) {
                        Toast.makeText(ClassAttendance.this,"Operation failed",
                                Toast.LENGTH_SHORT).show();
                    }
                    } catch (JSONException sE) {
                        sE.printStackTrace();
                    }

                }, Throwable::printStackTrace){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("class", sClassId);
                stringMap.put("staff", sStaffId);
                stringMap.put("year",sYear);
                stringMap.put("term",sTerm);
                stringMap.put("course", String.valueOf(0));
                stringMap.put("register",sStudents);
                stringMap.put("count", sCount);
                stringMap.put("date", getDate());
                stringMap.put("_db",sDb);
                return stringMap;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }


    public String getDate(){
        String date;
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String dayOfMonth = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

         date = year+"-"+month+"-"+dayOfMonth;

        return date;
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