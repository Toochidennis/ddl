package com.digitaldream.ddl.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.digitaldream.ddl.Models.StudentTable;
import com.digitaldream.ddl.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ViewHolder> implements Filterable {

    private final Context mContext;
    private final List<StudentTable> mStudentTableList;
    private final List<StudentTable> mStudentTableListFull;
    private final OnDayClickListener mDayClickListener;

    public AttendanceAdapter(Context sContext, List<StudentTable> sStudentTableList,
                             OnDayClickListener sDayClickListener) {
        mContext = sContext;
        mStudentTableList = sStudentTableList;
        mDayClickListener = sDayClickListener;
        mStudentTableListFull = new ArrayList<>(sStudentTableList);
    }

    @NonNull
    @Override
    public AttendanceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =
                LayoutInflater.from(mContext).inflate(R.layout.attendance_list_item, parent, false);

        return new ViewHolder(view, mDayClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.ViewHolder holder, int position) {
        StudentTable model = mStudentTableList.get(position);

        holder.mAttendanceDate.setText(model.getDate());
        holder.mStudentCount.setText(model.getStudentCount());
        holder.mCourseCount.setText(model.getCourseCount());


        GradientDrawable mutate =
                (GradientDrawable) holder.mLinearLayout.getBackground().mutate();
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        mutate.setColor(currentColor);

        holder.mLinearLayout.setBackground(mutate);
        holder.countContainer.setBackground(mutate);

    }

    @Override
    public int getItemCount() {
        return mStudentTableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

       private final OnDayClickListener mDayClickListener;
       private final LinearLayout mLinearLayout;
       private final TextView mStudentCount;
       private final TextView mAttendanceDate;
       private final TextView mCourseCount;
       private final LinearLayout countContainer;


        public ViewHolder(@NonNull View itemView, OnDayClickListener sDayClickListener) {
            super(itemView);
            this.mDayClickListener = sDayClickListener;
            mLinearLayout = itemView.findViewById(R.id.student_count_container);
            mStudentCount = itemView.findViewById(R.id.student_count);
            mAttendanceDate = itemView.findViewById(R.id.attendance_date);
            mCourseCount = itemView.findViewById(R.id.course_count);
            countContainer = itemView.findViewById(R.id.count_container);

        }

        @Override
        public void onClick(View sView) {
            mDayClickListener.onDayClick(getAdapterPosition());

        }

        @Override
        public boolean onLongClick(View sView) {
            mDayClickListener.onDayLongClick(getAdapterPosition());
            return false;
        }
    }

    public interface OnDayClickListener{
        void onDayClick(int position);
        void onDayLongClick(int position);
    }

    @Override
    public Filter getFilter(){
        return attendanceFilter;
    }

    private final Filter attendanceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence sCharSequence) {
            List<StudentTable> filteredList = new ArrayList<>();

            if (sCharSequence == null || sCharSequence.length() == 0){
                filteredList.addAll(mStudentTableListFull);
            }{
                assert sCharSequence != null;
                String filteredItem =
                        sCharSequence.toString().toLowerCase().trim();

                for (StudentTable item: mStudentTableListFull) {

                    if (item.getDate().toLowerCase().contains(filteredItem)){
                        filteredList.add(item);
                    }

                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;

        }

        @Override
        protected void publishResults(CharSequence sCharSequence, FilterResults sFilterResults) {

            mStudentTableList.clear();
            mStudentTableList.addAll((List<StudentTable>) sFilterResults.values);
            notifyDataSetChanged();

        }
    };

}
