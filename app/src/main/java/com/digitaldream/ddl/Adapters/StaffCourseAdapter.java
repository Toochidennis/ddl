package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.digitaldream.ddl.Models.CourseTable;
import com.digitaldream.ddl.R;
import com.digitaldream.ddl.Activities.StaffEnterResult;

import java.util.List;

public class StaffCourseAdapter extends RecyclerView.Adapter<StaffCourseAdapter.StaffCourseViewHolder> {
    private Context context;
    private List<CourseTable> staffCourseList;

    public StaffCourseAdapter(Context context, List<CourseTable> staffCourseList) {
        this.context = context;
        this.staffCourseList = staffCourseList;
    }

    @NonNull
    @Override
    public StaffCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_staff_course_by_subject,viewGroup,false);
        return new StaffCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffCourseViewHolder staffCourseViewHolder, int i) {
        final CourseTable ct = staffCourseList.get(i);
        String courseName = ct.getCourseName();
        String[] strArray = courseName.split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        for(String s: strArray){
            try{
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
               stringBuilder.append(cap + " ");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        staffCourseViewHolder.courseName.setText(stringBuilder.toString());
        staffCourseViewHolder.courseClass.setText(ct.getClassName().toUpperCase() );
        staffCourseViewHolder.addResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StaffEnterResult.class);
                intent.putExtra("status","add_result");
                intent.putExtra("course_id",ct.getCourseId());
                intent.putExtra("class_id",ct.getClassId());
                intent.putExtra("level_id",ct.getLevelId());
                context.startActivity(intent);
            }
        });

        staffCourseViewHolder.viewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,StaffEnterResult.class);
                intent.putExtra("status","view_result");
                intent.putExtra("course_id",ct.getCourseId());
                intent.putExtra("class_id",ct.getClassId());
                intent.putExtra("level_id",ct.getLevelId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffCourseList.size();
    }

    class StaffCourseViewHolder extends RecyclerView.ViewHolder{
        private TextView courseName,courseClass;
        private LinearLayout viewResult,addResult;

        public StaffCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.staff_course_name);
            viewResult = itemView.findViewById(R.id.staff_view_student);
            addResult = itemView.findViewById(R.id.staff_add_student);
            courseClass = itemView.findViewById(R.id.staff_course_class);

        }
    }
}
