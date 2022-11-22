package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.R;

import java.util.List;

public class StudentCourseAttendanceAdapter extends RecyclerView.Adapter<StudentCourseAttendanceAdapter.ViewHolder> {

    private final Context mContext;
    private final List<StudentTable> mStudentList;
    private final OnStudentClickListener mStudentClickListener;

    public StudentCourseAttendanceAdapter(Context sContext,
                                          List<StudentTable> sStudentTableList,
                                          OnStudentClickListener sStudentClickListener){
        mContext = sContext;
        mStudentList = sStudentTableList;
        mStudentClickListener = sStudentClickListener;
    }


    @NonNull
    @Override
    public StudentCourseAttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(mContext).inflate(R.layout.student_course_attendance_item, parent, false);
        return new ViewHolder(view, mStudentClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentCourseAttendanceAdapter.ViewHolder holder, int position) {
        StudentTable studentTable = mStudentList.get(position);

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

        String name = surName+ " " + middleName + " " + firstName;

        holder.mName.setText(name);

        if (studentTable.isSelected()){
            holder.mImageView.setVisibility(View.VISIBLE);
        }else{
            holder.mImageView.setVisibility(View.GONE);
        }



        try {
            holder.mInitial.setText(name.substring(0, 1).toUpperCase());
        }catch (StringIndexOutOfBoundsException e){
            e.printStackTrace();
        }

        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        mutate.setColor(studentTable.getColor());

        holder.mLinearLayout.setBackground(mutate);


    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private final OnStudentClickListener mStudentClickListener;
        private final LinearLayout mLinearLayout;
        private final ImageView mImageView;
        private final TextView mInitial, mName;

        public ViewHolder(@NonNull View itemView, OnStudentClickListener sStudentClickListener) {
            super(itemView);
            mStudentClickListener = sStudentClickListener;
            mLinearLayout = itemView.findViewById(R.id.checkBtn_layout);
            mImageView = itemView.findViewById(R.id.checkBtn);
            mInitial = itemView.findViewById(R.id.name_initial);
            mName = itemView.findViewById(R.id.student_name);
            itemView.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View sView) {
            mStudentClickListener.onLongClickStudent(getAdapterPosition());
            return false;
        }

        @Override
        public void onClick(View sView) {
            mStudentClickListener.onStudentClick(getAdapterPosition());

        }
    }

    public interface OnStudentClickListener{
        void onStudentClick(int position);
        void onLongClickStudent(int position);
    }
}
