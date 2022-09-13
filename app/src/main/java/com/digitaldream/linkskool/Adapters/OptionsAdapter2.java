package com.digitaldream.linkskool.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.digitaldream.linkskool.Activities.Login;
import com.digitaldream.linkskool.Activities.TestUpload;
import com.digitaldream.linkskool.Models.OptionsModel;
import com.digitaldream.linkskool.Models.QuestionsModel;
import com.digitaldream.linkskool.R;
import com.digitaldream.linkskool.Utils.QuestionDialog;
import com.digitaldream.linkskool.Utils.QuestionDialog2;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class OptionsAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<OptionsModel> list;
    OnClickListener1 onClickListener1;
    public static final int SELECT_OPTION_IMAGE=6;
    private static final int REQUEST_PERMISSIONS = 100;
    public static int optionPosition;


    public OptionsAdapter2(Context context, List<OptionsModel> list,OnClickListener1 onClickListener1) {
        this.context = context;
        this.list = list;
        this.onClickListener1 = onClickListener1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (viewType){
            case 0:
                view = inflater.inflate(R.layout.multichoice_option_layout,parent,false);
                return new MultipleChoiceVH(view,onClickListener1);
            case 1:
                view = inflater.inflate(R.layout.checkbox_options_layout,parent,false);
                return new CheckBoxVH(view);
            case 2:
                view = inflater.inflate(R.layout.dropdown_options_layout,parent,false);
                return new DropDownVH(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OptionsModel om = list.get(position);

        switch (holder.getItemViewType()){
            case 0:
                MultipleChoiceVH multipleChoiceVH = (MultipleChoiceVH) holder;
                multipleChoiceVH.optionText.setText(om.getOptionText());
                if(list.size()==position+1) {
                    multipleChoiceVH.optionText.requestFocus();
                }
                /*multipleChoiceVH.clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(position);
                        QuestionDialog2.adapter.notifyDataSetChanged();

                    }
                });*/
                if(om.getOptionsImage()!=null){
                    multipleChoiceVH.imageContainer.setVisibility(View.VISIBLE);
                    multipleChoiceVH.optionPic.setImageURI(om.getOptionsImage());
                    //multipleChoiceVH.addImgContainer.setVisibility(View.GONE);
                }
                else if(om.getOptionImageUrl()!=null&& !om.getOptionImageUrl().isEmpty()){
                    multipleChoiceVH.imageContainer.setVisibility(View.VISIBLE);
                    multipleChoiceVH.optionPic.setImageURI(om.getOptionsImage());
                    //multipleChoiceVH.addImgContainer.setVisibility(View.GONE);
                    String imgUrl = om.getOptionImageUrl();
                    Log.i("response","img "+imgUrl);
                    try {
                        //imgUrl = imgUrl.substring(2);
                        Picasso.with(context).load(Login.urlBase+"/"+imgUrl).into(multipleChoiceVH.optionPic);
                    }catch (StringIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                    multipleChoiceVH.clearContainer.setVisibility(View.VISIBLE);

                }
                else {
                    multipleChoiceVH.imageContainer.setVisibility(View.GONE);
                    multipleChoiceVH.clearContainer.setVisibility(View.GONE);

                }

                if(om.isChecked()) {
                    multipleChoiceVH.radioButton.setChecked(true);
                }else{
                    multipleChoiceVH.radioButton.setChecked(false);

                }
                multipleChoiceVH.radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for(int a=0;a<list.size();a++){
                            list.get(a).setChecked(false);
                        }
                        list.get(position).setChecked(true);

                        QuestionDialog2.adapter.notifyDataSetChanged();
                        TestUpload.questionAdapter2.notifyDataSetChanged();
                        om.setCorrectAnswer(list.get(position).getOptionText());
                    }
                });


                multipleChoiceVH.deleteImgContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //om.setOptionsImage(null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Delete ?");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.get(position).setOptionsImage(null);
                                list.get(position).setOptionImageUrl(null);
                                QuestionDialog2.adapter.notifyDataSetChanged();
                                TestUpload.questionAdapter2.notifyDataSetChanged();
                            }
                        });
                        builder.show();
                    }
                });
                multipleChoiceVH.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(context, multipleChoiceVH.more);
                        popup.inflate(R.menu.pop_menu);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.attach:
                                        optionPosition = position;
                                        Toast.makeText(context,"position "+optionPosition,Toast.LENGTH_SHORT).show();
                                        Activity activity = unwrap(v.getContext());
                                        selectOptionImage(activity);
                                        return true;
                                    case R.id.delete:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setTitle("Delete ?");
                                        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                list.remove(position);
                                                QuestionDialog2.adapter.notifyDataSetChanged();
                                            }
                                        });
                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        });
                                        builder.show();
                                        return true;
                                }
                                return false;
                            }
                        });
                        popup.show();

                    }
                });

                break;
            case 1:
                CheckBoxVH checkBoxVH = (CheckBoxVH) holder;
                checkBoxVH.optionText.setText(om.getOptionText());
                if(list.size()==position+1) {
                    checkBoxVH.optionText.requestFocus();
                }
                if(om.getOptionsImage()!=null){
                    checkBoxVH.imageContainer.setVisibility(View.VISIBLE);
                    checkBoxVH.optionPic.setImageURI(om.getOptionsImage());
                    //checkBoxVH.addImgContainer.setVisibility(View.GONE);
                }
                else if(om.getOptionImageUrl()!=null&& !om.getOptionImageUrl().isEmpty()){
                    checkBoxVH.imageContainer.setVisibility(View.VISIBLE);
                    checkBoxVH.optionPic.setImageURI(om.getOptionsImage());
                    //checkBoxVH.addImgContainer.setVisibility(View.GONE);
                    String imgUrl = om.getOptionImageUrl();
                    try {
                        //imgUrl = imgUrl.substring(2);
                        Picasso.with(context).load(Login.urlBase+"/"+imgUrl).into(checkBoxVH.optionPic);
                    }catch (StringIndexOutOfBoundsException e){
                        e.printStackTrace();
                    }
                    checkBoxVH.clearContainer.setVisibility(View.VISIBLE);

                }
                else {
                    checkBoxVH.imageContainer.setVisibility(View.GONE);
                    checkBoxVH.clearContainer.setVisibility(View.GONE);

                }
                checkBoxVH.clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(position);
                        QuestionDialog2.adapter.notifyDataSetChanged();
                    }
                });
                if(om.isChecked()){
                    checkBoxVH.checkBox.setChecked(true);
                }else{
                    checkBoxVH.checkBox.setChecked(false);
                }
                checkBoxVH.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            om.setChecked(true);
                        }else{
                            om.setChecked(false);
                        }
                    }
                });

                checkBoxVH.deleteImgContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //om.setOptionsImage(null);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Delete ?");
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.get(position).setOptionsImage(null);
                                list.get(position).setOptionImageUrl(null);
                                QuestionDialog2.adapter.notifyDataSetChanged();
                                TestUpload.questionAdapter2.notifyDataSetChanged();
                            }
                        });
                        builder.show();
                    }
                });

                checkBoxVH.more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, checkBoxVH.more);
                    popup.inflate(R.menu.pop_menu);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.attach:
                                    optionPosition = position;
                                    //Toast.makeText(context,"position "+optionPosition,Toast.LENGTH_SHORT).show();
                                    Activity activity = unwrap(v.getContext());
                                    selectOptionImage(activity);
                                    return true;
                                case R.id.delete:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("Delete ?");
                                    builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            list.remove(position);
                                            QuestionDialog2.adapter.notifyDataSetChanged();
                                        }
                                    });
                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    builder.show();
                                    return true;
                            }
                            return false;
                        }
                    });
                    popup.show();

                }
            });
                break;
            case 2:
                DropDownVH dropDownVH = (DropDownVH) holder;
                dropDownVH.optionText.setText(om.getOptionText());
                dropDownVH.clear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(position);
                        QuestionDialog2.adapter.notifyDataSetChanged();
                    }
                });
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        OptionsModel om = list.get(position);
        switch (om.getType()){
            case "multichoice":
                return 0;
            case "checkbox":
                return 1;
            case "dropdown":
                return 2;
        }
        return 0;
    }

    class MultipleChoiceVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        public EditText optionText;
        RadioButton radioButton;
        ImageView clear,addImage,optionPic,more;
        OnClickListener1 onClickListener1;
        RelativeLayout imageContainer;
        LinearLayout addImgContainer;
        LinearLayout deleteImgContainer,clearContainer,moreContainer;

        public MultipleChoiceVH(@NonNull View itemView,OnClickListener1 onClickListener1) {
            super(itemView);
            optionText = itemView.findViewById(R.id.option_field);
            clear = itemView.findViewById(R.id.clear);
            radioButton = itemView.findViewById(R.id.point);
            addImage = itemView.findViewById(R.id.add_image);
            optionPic = itemView.findViewById(R.id.option_pic);
            imageContainer = itemView.findViewById(R.id.image_container);
            deleteImgContainer = itemView.findViewById(R.id.delete_pic_container);
            clearContainer = itemView.findViewById(R.id.clear_container);
            //addImgContainer = itemView.findViewById(R.id.aM);
            addImage.setOnClickListener(this);
            optionPic.setOnClickListener(this);
            this.onClickListener1 = onClickListener1;
            optionText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAdapterPosition()).setOptionText(optionText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            more = itemView.findViewById(R.id.more);
            more.setOnClickListener(this);
            moreContainer = itemView.findViewById(R.id.more_container);
        }

        @Override
        public void onClick(View v) {
            onClickListener1.onAddImageClick(getAdapterPosition());
            onClickListener1.onImagePreviewClick(getAdapterPosition());
        }
    }

    class CheckBoxVH extends RecyclerView.ViewHolder{
        public EditText optionText;
        CheckBox checkBox;
        ImageView clear,addImage,optionPic,more;
        OnClickListener1 onClickListener1;
        RelativeLayout imageContainer;
        LinearLayout addImgContainer;
        LinearLayout deleteImgContainer,clearContainer,moreContainer;


        public CheckBoxVH(@NonNull View itemView) {
            super(itemView);
            optionText = itemView.findViewById(R.id.option_field);
            clear = itemView.findViewById(R.id.clear);
            checkBox = itemView.findViewById(R.id.point);
            optionPic = itemView.findViewById(R.id.option_pic);
            imageContainer = itemView.findViewById(R.id.image_container);
            deleteImgContainer = itemView.findViewById(R.id.delete_pic_container);
            clearContainer = itemView.findViewById(R.id.clear_container);
            //addImgContainer = itemView.findViewById(R.id.aM);
            optionText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAdapterPosition()).setOptionText(optionText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            more = itemView.findViewById(R.id.more);
            moreContainer = itemView.findViewById(R.id.more_container);
        }
    }

    class DropDownVH extends RecyclerView.ViewHolder{
        public EditText optionText;
        ImageView clear;
        public DropDownVH(@NonNull View itemView) {
            super(itemView);
            optionText = itemView.findViewById(R.id.option_field);
            clear = itemView.findViewById(R.id.clear);
            optionText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    list.get(getAdapterPosition()).setOptionText(optionText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public interface OnClickListener1{
        void onAddImageClick(int position);
        void onImagePreviewClick(int position);
    }

    private void selectOptionImage(Activity activity){
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            }
        }else{
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activity.startActivityForResult(galleryIntent, SELECT_OPTION_IMAGE);
            //activity.startActivityForResult(Intent.createChooser(intent,"Complete action using"), SELECT_IMAGE);
        }
    }

    private static Activity unwrap(Context context) {
        while (!(context instanceof Activity) && context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
        }

        return (Activity) context;
    }
}
