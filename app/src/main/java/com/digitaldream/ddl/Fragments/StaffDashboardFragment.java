package com.digitaldream.ddl.Fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.digitaldream.ddl.Activities.AnswerView;
import com.digitaldream.ddl.Activities.Login;
import com.digitaldream.ddl.Activities.NewsView;
import com.digitaldream.ddl.Activities.QuestionView;
import com.digitaldream.ddl.Activities.StaffCourses;
import com.digitaldream.ddl.Activities.StaffUtils;
import com.digitaldream.ddl.Adapters.QAAdapter;
import com.digitaldream.ddl.DatabaseHelper;
import com.digitaldream.ddl.Models.ClassNameTable;
import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.Models.NewsTable;
import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.NewsAdapter;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Utils.CustomDialog;
import com.digitaldream.ddl.Utils.QuestionBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class StaffDashboardFragment extends Fragment implements NewsAdapter.OnNewsClickListener, QAAdapter.OnQuestionClickListener {
    private Boolean exit = false;
    private DatabaseHelper databaseHelper;
    private LinearLayout studentContactsLink,resultLink,staffCoursesLink,eLearning;
    private List<CourseTable> courseList;
    private Dao<CourseTable,Long> courseDao;
    private Dao<StudentTable,Long> studentDao;
    private List<StudentTable> studentList;
    private Dao<NewsTable,Long> newsDao;
    private List<NewsTable> newsTitleList;
    private Dao<ClassNameTable,Long> classDao;
    private List<ClassNameTable> classList;
    private TextView studentCount,courseCount,user,userInitials,schoolSession,termTextview;
    private Toolbar toolbar;
    private LinearLayout newsContainer;
    private SwipeRefreshLayout newsRefresh;
    private RecyclerView newsRecylerView;
    private String db;
    private boolean fromLogin;
    private boolean isFirstTime =false;
    private NewsAdapter newsAdapter;
    RecyclerView qaRecycler;
    private JSONArray arr;
    private QAAdapter adapter;
    private LinearLayout emptyState;
    private QAAdapter.QAObject feed;
    List<QAAdapter.QAObject> list;
    public static QuestionBottomSheet questionBottomSheet=null;
    private static String json="";
    public static boolean refresh=false;





    public StaffDashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_staff_dashboard, container, false);
        emptyState = v.findViewById(R.id.qa_empty_state);

        studentContactsLink = v.findViewById(R.id.student_no_container_staff);
        staffCoursesLink = v.findViewById(R.id.teacher_courses);
        studentCount = v.findViewById(R.id.no_of_student_staff);
        courseCount = v.findViewById(R.id.no_of_teacher_staff);
        userInitials = v.findViewById(R.id.initials_staff);
        ;
        toolbar = v.findViewById(R.id.toolbar);
        newsRefresh = v.findViewById(R.id.swipeRefresh_news);
        newsRecylerView = v.findViewById(R.id.news_recycler);

        ((AppCompatActivity)(getActivity())).setSupportActionBar(toolbar);
        ActionBar actionBar =  ((AppCompatActivity)(getActivity())).getSupportActionBar();

        fromLogin = getActivity().getIntent().getBooleanExtra("isFromLogin",false);
        if(fromLogin==true){
            isFirstTime = true;
        }

        try {
            databaseHelper = new DatabaseHelper(getContext());
            studentDao = DaoManager.createDao(databaseHelper.getConnectionSource(), StudentTable.class);
            newsDao = DaoManager.createDao(databaseHelper.getConnectionSource(), NewsTable.class);
            courseDao = DaoManager.createDao(databaseHelper.getConnectionSource(), CourseTable.class);
            classDao = DaoManager.createDao(databaseHelper.getConnectionSource(), ClassNameTable.class);
            studentList = studentDao.queryForAll();
            newsTitleList = newsDao.queryForAll();
            courseList = courseDao.queryForAll();
            classList = classDao.queryForAll();
            studentCount.setText(String.valueOf(classList.size()));
            courseCount.setText(String.valueOf(courseList.size()));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        user = v.findViewById(R.id.userID_display);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("loginDetail", Context.MODE_PRIVATE);
        String user_name = sharedPreferences.getString("user","");
        String schoolYear = sharedPreferences.getString("school_year","");
        db = sharedPreferences.getString("db","");
        String[] strArray = user_name.toLowerCase().split(" ");
        String school_name = sharedPreferences.getString("school_name","");
        String term = sharedPreferences.getString("term","");
        String[] strArray1 = school_name.split(" ");
        StringBuilder builder = new StringBuilder();
        for(String s : strArray){
            try {
                //s.toLowerCase();
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder.append(cap + " ");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        StringBuilder builder1 = new StringBuilder();
        for(String s : strArray1){
            try {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                builder1.append(cap + " ");

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        user.setText( builder.toString());
        actionBar.setTitle(builder1.toString());
        String initials = user_name.substring(0,1).toUpperCase();
        userInitials.setText(initials);

        arr = new JSONArray();
        for(int a=0;a<courseList.size();a++){
            JSONObject staffObj = new JSONObject();
            try {
                staffObj.put("course",courseList.get(a).getCourseId());
                staffObj.put("course_name",courseList.get(a).getCourseName());
                staffObj.put("level",courseList.get(a).getLevelId());
                staffObj.put("level_name",courseList.get(a).getLevelName());
                arr.put(staffObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        staffCoursesLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), StaffCourses.class);
                startActivity(intent);
            }
        });

        studentContactsLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),StaffUtils.class);
                intent.putExtra("from","student");
                startActivity(intent);
            }
        });

        try {
            int previousYear = Integer.parseInt(schoolYear) - 1;
            if(term.equals("1")){
                term = term+"st term";
            }else if(term.equals("2")){
                term = term+"nd term";
            }else if(term.equals("3")){
                term = term+"rd term";

            }

        }catch (NumberFormatException e){
            e.printStackTrace();
        }

        qaRecycler = v.findViewById(R.id.qa_recycler);


        qaRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        qaRecycler.setLayoutManager(layoutManager);
        list=new ArrayList<>();




        adapter = new QAAdapter(getContext(),list,this);
        qaRecycler.setAdapter(adapter);
        LinearLayout emptyState = v.findViewById(R.id.qa_empty_state);
        if(list.isEmpty()){
            emptyState.setVisibility(View.VISIBLE);
        }else{
            emptyState.setVisibility(View.GONE);
        }

        FloatingActionButton addQuestionBtn = v.findViewById(R.id.add_question);
        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = ((FragmentActivity) getContext())
                        .getSupportFragmentManager()
                        .beginTransaction();
                questionBottomSheet = new QuestionBottomSheet();
                questionBottomSheet.show(transaction, "questionBottomSheet");
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(json.isEmpty()){
            getFeed();
        }else{
            if(refresh){
                getFeed();
            }else {
                parseJSON(json);
            }
        }
    }

    @Override
    public void onNewsClick(int position) {

    }

    @Override
    public void onQuestionClick(int position) {
        QAAdapter.QAObject object = list.get(position);
        if(object.getFeedType().equals("20")) {
            Intent intent = new Intent(getContext(), QuestionView.class);
            intent.putExtra("feed",object);
            startActivity(intent);
        }else if(object.getFeedType().equals("21")){
            Intent intent = new Intent(getContext(), AnswerView.class);
            intent.putExtra("feed", object);
            startActivity(intent);
        }else{
            Intent intent = new Intent(getContext(), NewsView.class);
            intent.putExtra("feed", object);
            startActivity(intent);
        }
    }



    private void getFeed(){
        CustomDialog dialog = new CustomDialog(getActivity());
        dialog.show();
        String url = Login.urlBase+"/getFeed.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                json =response;
                parseJSON(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",arr.toString());
                params.put("_db",db);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void parseJSON(String response){
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int a = 0; a < jsonArray.length(); a++) {
                JSONObject object = jsonArray.getJSONObject(a);
                String id = object.getString("id");
                String title = object.getString("title");
                String user = object.getString("author_name");
                String date = object.getString("upload_date");
                String commentsNo = object.getString("no_of_comment");
                String shareCount = object.getString("no_of_share");
                String upvotes = object.getString("no_of_like");
                String parent = object.getString("parent");
                String desc = object.getString("description");
                String type1 = object.getString("type");

                String body = "";
                body = object.optString("body");

                feed = new QAAdapter.QAObject();
                feed.setUser(user);
                    feed.setQuestionId(id);
                    feed.setId(id);
                if(title==null || title.isEmpty()){
                    feed.setQuestion(desc);

                }else {
                    feed.setQuestion(title);
                }
                feed.setAnswer("");
                    feed.setPicUrl("");
                    feed.setAnswerId(id);
                    if (!body.isEmpty()) {

                        Object json = new JSONTokener(body).nextValue();

                        if (json instanceof JSONArray) {
                            JSONArray answer = new JSONArray(body);
                            boolean checktext = true;
                            boolean checkImage = true;
                            for (int c = 0; c < answer.length(); c++) {
                                JSONObject object1 = answer.optJSONObject(c);
                                String type = object1.optString("type").trim();

                                if (type.equalsIgnoreCase("text") && checktext) {
                                    String content = object1.optString("content");

                                    feed.setPreText(content);
                                    checktext = false;
                                }
                                if (type.equalsIgnoreCase("image") && checkImage) {
                                    String content = object1.optString("src");
                                    feed.setPicUrl(content);
                                    checkImage = false;
                                }
                            }
                        }
                        feed.setAnswer(body);

                    //feed.setQuestion(desc);
                }

                feed.setDate(date);
                feed.setCommentNo(commentsNo);
                feed.setLikesNo(upvotes);
                feed.setShareNo(shareCount);
                feed.setFeedType(type1);
                list.add(feed);

            }

            Collections.reverse(list);
            if (list.isEmpty()) {
                emptyState.setVisibility(View.VISIBLE);

            } else {
                emptyState.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
