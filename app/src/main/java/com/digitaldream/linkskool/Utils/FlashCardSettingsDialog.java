package com.digitaldream.linkskool.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digitaldream.linkskool.Activities.CourseOutlines;
import com.digitaldream.linkskool.Activities.TestUpload;
import com.digitaldream.linkskool.DatabaseHelper;
import com.digitaldream.linkskool.Models.CourseOutlineTable;
import com.digitaldream.linkskool.Models.CourseTable;
import com.digitaldream.linkskool.Models.LevelTable;
import com.digitaldream.linkskool.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class FlashCardSettingsDialog extends BottomSheetDialogFragment {
    private DatabaseHelper databaseHelper;
    Dao<CourseTable,Long> courseTableDao;
    Dao<LevelTable,Long> levelDao;
    private List<LevelTable> levelList;
    private List<CourseTable> coursesList;
    private Dao<CourseOutlineTable,Long> courseOutlineDao;
    private List<CourseOutlineTable> list;
    List<CourseOutlineTable> courseList;
    private String accessLevel,sender,db;
    public static String title="";
    public static JSONObject levelObj ;
    public static JSONObject courseObj;

        public static FlashCardSettingsDialog newInstance(String from) {
        FlashCardSettingsDialog bottomSheetFragment = new FlashCardSettingsDialog();
        Bundle bundle = new Bundle();
        bundle.putString("from",from);
        bottomSheetFragment .setArguments(bundle);
        return bottomSheetFragment ;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.flash_card_setting,container,false);
        EditText titleEDT = view.findViewById(R.id.title);
        TextView submitBtn = view.findViewById(R.id.submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = titleEDT.getText().toString();
                if (title.isEmpty()){
                    Toast.makeText(getContext(),"Title can't be empty",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    FragmentTransaction transaction = ((FragmentActivity) getContext())
                            .getSupportFragmentManager()
                            .beginTransaction();
                    FlashCardTagsSettings dailog = new FlashCardTagsSettings();
                    dailog.show(transaction, "TagsBottomSheet");
                }
            }
        });

        return view;
    }
}
