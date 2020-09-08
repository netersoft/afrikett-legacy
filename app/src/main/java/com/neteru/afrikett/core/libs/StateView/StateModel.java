package com.neteru.afrikett.core.libs.StateView;

import android.graphics.drawable.Drawable;

@SuppressWarnings("unused, WeakerAccess")
public class StateModel {

    private String tag;
    private String title;
    private String description;
    private String buttonTitle;
    private Drawable imageDrawable;
    private Integer drawableColor;

    StateModel(){}

    public String getTag() {
        return tag;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getButtonTitle() {
        return buttonTitle;
    }

    public Drawable getImageDrawable() {
        return imageDrawable;
    }

    public Integer getDrawableColor() {
        return drawableColor;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setButtonTitle(String buttonTitle) {
        this.buttonTitle = buttonTitle;
    }

    public void setImageDrawable(Drawable imageDrawable) {
        this.imageDrawable = imageDrawable;
    }

    public void setDrawableColor(Integer drawableColor) {
        this.drawableColor = drawableColor;
    }
}
