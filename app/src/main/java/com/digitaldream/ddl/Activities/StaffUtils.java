package com.digitaldream.ddl.Activities;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.digitaldream.ddl.Fragments.ContactsStaff;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Fragments.ResultStaff;

public class StaffUtils extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_utils);
        Intent i = getIntent();
        String from = i.getStringExtra("from");
        if(from.equals("result")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ResultStaff()).commit();
        }else if(from.equals("student")){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ContactsStaff()).commit();
        }
    }
}
