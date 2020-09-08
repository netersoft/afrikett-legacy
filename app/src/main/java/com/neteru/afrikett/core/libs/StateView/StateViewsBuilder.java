package com.neteru.afrikett.core.libs.StateView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused, WeakerAccess")
public class StateViewsBuilder {

    @SuppressLint("StaticFieldLeak")
    private static StateViewsBuilder instance;
    private Context context;
    private List<StateModel> stateViews;
    private Integer buttonBackgroundColor;
    private Integer buttonTextColor;
    private Integer titleColor;
    private Integer descriptionColor;
    private Typeface titleFont;
    private Typeface descriptionFont;
    private Typeface buttonTextFont;
    private Integer iconSize;

    private StateViewsBuilder(Context context){
        this.context = context;
        this.stateViews = new ArrayList<>();
    }

    public static StateViewsBuilder init(Context context){
        if (instance == null) {
            instance = new StateViewsBuilder(context);
        }

        return instance;
    }

    public static StateViewsBuilder getInstance(){
        return instance;
    }

    public StateViewsBuilder addState(String tag, String title, String description, Drawable drawable, String buttonTitle){
        StateModel stateModel = new StateModel();
        stateModel.setTag(tag);
        stateModel.setTitle(title);
        stateModel.setDescription(description);
        stateModel.setImageDrawable(drawable);
        stateModel.setButtonTitle(buttonTitle);
        stateModel.setDrawableColor(null);

        stateViews.add(stateModel);

        return this;
    }

    public StateViewsBuilder addState(String tag, String title, String description, Drawable drawable, String buttonTitle, Integer drawableColor){
        StateModel stateModel = new StateModel();
        stateModel.setTag(tag);
        stateModel.setTitle(title);
        stateModel.setDescription(description);
        stateModel.setImageDrawable(drawable);
        stateModel.setButtonTitle(buttonTitle);
        stateModel.setDrawableColor(drawableColor);

        stateViews.add(stateModel);

        return this;
    }

    public Context getContext() {
        return context;
    }

    public List<StateModel> getStateViews() {
        return stateViews;
    }

    public StateViewsBuilder setStateViews(List<StateModel> stateViews) {
        this.stateViews = stateViews;

        return this;
    }

    public Integer getButtonBackgroundColor() {
        return buttonBackgroundColor;
    }

    public StateViewsBuilder setButtonBackgroundColor(Integer buttonBackgroundColor) {
        this.buttonBackgroundColor = buttonBackgroundColor;

        return this;
    }

    public Integer getButtonTextColor() {
        return buttonTextColor;
    }

    public StateViewsBuilder setButtonTextColor(Integer buttonTextColor) {
        this.buttonTextColor = buttonTextColor;

        return this;
    }

    public Integer getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(Integer titleColor) {
        this.titleColor = titleColor;
    }

    public Integer getDescriptionColor() {
        return descriptionColor;
    }

    public void setDescriptionColor(Integer descriptionColor) {
        this.descriptionColor = descriptionColor;
    }

    public Typeface getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Typeface titleFont) {
        this.titleFont = titleFont;
    }

    public Typeface getDescriptionFont() {
        return descriptionFont;
    }

    public void setDescriptionFont(Typeface descriptionFont) {
        this.descriptionFont = descriptionFont;
    }

    public Typeface getButtonTextFont() {
        return buttonTextFont;
    }

    public void setButtonTextFont(Typeface buttonTextFont) {
        this.buttonTextFont = buttonTextFont;
    }

    public Integer getIconSize() {
        return iconSize;
    }

    public void setIconSize(Integer iconSize) {
        this.iconSize = iconSize;
    }
}
