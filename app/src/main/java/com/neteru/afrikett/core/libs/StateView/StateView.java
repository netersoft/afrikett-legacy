package com.neteru.afrikett.core.libs.StateView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.neteru.afrikett.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.widget.ContentLoadingProgressBar;

public class StateView extends FrameLayout {

    private View view;
    private StateViewsBuilder stateViewsBuilder;
    private final String TAG = "StateView";
    private TextView title;
    private TextView description;
    private Button button;
    private AppCompatImageView icon;
    private ContentLoadingProgressBar progressBar;

    public StateView(@NonNull Context context) {
        super(context);
        setupStateView();
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupStateView();
    }

    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupStateView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupStateView();
    }

    @SuppressLint("InflateParams")
    private void setupStateView(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        stateViewsBuilder = StateViewsBuilder.getInstance();
        view = inflater.inflate(R.layout.template_stateview_model, this);

        title = view.findViewById(R.id.state_title);
        description = view.findViewById(R.id.state_description);
        button = view.findViewById(R.id.state_button);
        icon = view.findViewById(R.id.state_icon);
        progressBar = view.findViewById(R.id.state_progress_bar);
        
        hideStates();
    }

    public void hideStates(){
        view.setVisibility(GONE);

        Log.d(TAG, "Hide all states");
    }

    public void displayLoadingState(){

        view.setVisibility(VISIBLE);
        title.setVisibility(GONE);
        description.setVisibility(GONE);
        button.setVisibility(GONE);
        icon.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);

    }

    public void displayState(String tagName){

        boolean exist = false;
        List<StateModel> states = stateViewsBuilder.getStateViews();

        view.setVisibility(VISIBLE);
        progressBar.setVisibility(GONE);
        
        for (StateModel stateModel : states){
            if (tagName.equalsIgnoreCase(stateModel.getTag())){
                exist = true;

                setTitleValue(stateModel.getTitle(), stateViewsBuilder.getTitleColor(), stateViewsBuilder.getTitleFont());
                setDescriptionValue(stateModel.getDescription(), stateViewsBuilder.getDescriptionColor(), stateViewsBuilder.getDescriptionFont());
                setButtonStyle(stateModel.getButtonTitle(), stateViewsBuilder.getButtonTextFont(), stateViewsBuilder.getButtonBackgroundColor(), stateViewsBuilder.getButtonTextColor());
                setIcon(stateModel.getImageDrawable(), stateModel.getDrawableColor(), stateViewsBuilder.getIconSize());
            }
        }

        if (!exist){
            Log.e(TAG, "Tag name Incorrect or not found");
        }else {
            Log.d(TAG, "Show state with Tag Name : "+tagName);
        }
    }

    private void setTitleValue(String value, Integer color, Typeface typeface) {

        title.setGravity(Gravity.CENTER_HORIZONTAL);
        setTextColor(title, color);
        setTextFont(title, typeface);
        if (!TextUtils.isEmpty(value)) {
            title.setText(value);
            title.setVisibility(View.VISIBLE);
        } else {
            title.setVisibility(View.GONE);
        }

    }

    private void setTextFont(TextView mTextView, Typeface typeface) {
        if (typeface != null) {
            mTextView.setTypeface(typeface);
        }
    }

    private void setTextColor(TextView textView, Integer color) {

        if (color != null) {
            textView.setTextColor(color);
        }
    }

    private void setDescriptionValue(String value, Integer color, Typeface typeface) {

        description.setGravity(Gravity.CENTER_HORIZONTAL);
        setTextColor(description, color);
        setTextFont(description, typeface);
        if (!TextUtils.isEmpty(value)) {
            description.setText(value);
            description.setVisibility(View.VISIBLE);
        } else {
            description.setVisibility(View.GONE);
        }

    }

    public void setOnStateButtonClicked(OnClickListener clickListener) {

        button.setOnClickListener(clickListener);
        
    }

    private void setButtonStyle(String buttonTitle, Typeface typeface, Integer buttonBackgroundColor, Integer buttonTextColor) {

        setButtonFont(button, typeface);

        if (buttonBackgroundColor != null) {
            button.getBackground().setColorFilter(buttonBackgroundColor, PorterDuff.Mode.MULTIPLY);
        }

        if (buttonTextColor != null) {
            button.setTextColor(buttonTextColor);
        }

        if (!TextUtils.isEmpty(buttonTitle)) {
            button.setText(buttonTitle);
            button.setVisibility(VISIBLE);
        } else {
            button.setVisibility(GONE);
        }
    }

    private void setButtonFont(Button button, Typeface typeface) {
        if (typeface != null) {
            button.setTypeface(typeface);
        }
    }

    private void setIcon(Drawable drawable, Integer color, Integer iconSize) {

        if (color != null) {
            icon.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
        }

        if (iconSize != null && iconSize > 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSize, iconSize);
            icon.setLayoutParams(params);
        }

        if (drawable != null) {
            icon.setImageDrawable(drawable);
            icon.setVisibility(View.VISIBLE);
        } else {
            icon.setVisibility(View.GONE);
        }
    }
}