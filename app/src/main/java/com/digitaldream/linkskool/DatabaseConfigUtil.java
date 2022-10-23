package com.digitaldream.linkskool;

import com.digitaldream.linkskool.Models.AssessmentModel;
import com.digitaldream.linkskool.Models.ClassNameTable;
import com.digitaldream.linkskool.Models.CommentTable;
import com.digitaldream.linkskool.Models.CourseOutlineTable;
import com.digitaldream.linkskool.Models.CourseTable;
import com.digitaldream.linkskool.Models.Exam;
import com.digitaldream.linkskool.Models.ExamQuestions;
import com.digitaldream.linkskool.Models.ExamType;
import com.digitaldream.linkskool.Models.FormClassModel;
import com.digitaldream.linkskool.Models.GeneralSettingModel;
import com.digitaldream.linkskool.Models.GradeModel;
import com.digitaldream.linkskool.Models.LevelTable;
import com.digitaldream.linkskool.Models.NewsTable;
import com.digitaldream.linkskool.Models.StaffTableUtil;
import com.digitaldream.linkskool.Models.StudentCourses;
import com.digitaldream.linkskool.Models.StudentResultDownloadTable;
import com.digitaldream.linkskool.Models.StudentTable;
import com.digitaldream.linkskool.Models.TeacherCourseModel;
import com.digitaldream.linkskool.Models.TeacherCourseModelCopy;
import com.digitaldream.linkskool.Models.TeachersTable;
import com.digitaldream.linkskool.Models.VideoTable;
import com.digitaldream.linkskool.Models.VideoUtilTable;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?> [] classes = new Class[]{StudentTable.class, TeachersTable.class, ClassNameTable.class, LevelTable.class, NewsTable.class, CourseTable.class, StudentResultDownloadTable.class, StudentCourses.class, VideoTable.class, GradeModel.class, GeneralSettingModel.class, VideoUtilTable.class
    , AssessmentModel.class, Exam.class, ExamQuestions.class, ExamType.class, StaffTableUtil.class, FormClassModel.class, TeacherCourseModel.class, TeacherCourseModelCopy.class, CourseOutlineTable.class, CommentTable.class};

    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile("ormlite_config",classes);
    }
}
