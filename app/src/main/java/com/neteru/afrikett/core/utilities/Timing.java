package com.neteru.afrikett.core.utilities;

import android.content.Context;

import com.neteru.afrikett.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.neteru.afrikett.core.utilities.Constants.DATE_PATTERN;

public class Timing {
    private Context context;
    private String time, pattern, timePeriodVerdict;

    private Timing(Context context, String time, String pattern){
        this.context = context;
        this.time = time;
        this.pattern = pattern != null ? pattern : DATE_PATTERN;
        
    }

    public static Timing getInstance(Context ctx, @NonNull String time){
        return new Timing(ctx, time, null);
    }

    public static Timing getInstance(Context ctx, @NonNull String time, @Nullable String pattern){
        return new Timing(ctx, time, pattern);
    }

    /**
     * Recupération de la date courante
     * @return Date courante
     */
    public static String getCurrentDate(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.US);

        return dateFormat.format(date);
    }

    /**
     * Recupération de la date courante
     * @param pattern / Format de date
     * @return Date courante
     */
    public static String getCurrentDate(String pattern){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.US);

        return dateFormat.format(date);
    }

    /**
     * Recupération de la date courante
     * @param pattern / Format de date
     * @param locale / Constante de localité
     * @return Date courante
     */
    public static String getCurrentDate(String pattern, Locale locale){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);

        return dateFormat.format(date);
    }

    /**
     * Recupération de la période entre la date actuelle et la date donnée
     * @return Période de temps
     */
    public String getTimePeriod(){

        double minute = 60, hour = 3600, day = 86400, week = 604800, month = 2628002.88, year = 31536000;

        SimpleDateFormat formatted = new SimpleDateFormat(this.pattern, Locale.US);

        Calendar calendar = Calendar.getInstance();
        Date dater = null;

        try {
            dater = formatted.parse(this.time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.setTime(dater);
        
        double diff = ( System.currentTimeMillis() / 1000.0 ) - ( calendar.getTimeInMillis() / 1000.0 );

        if(diff <= minute){

            timePeriodVerdict = context.getString(R.string.just_now_str);

        }else if (diff > minute && diff <= hour){

            timePeriodVerdict = context.getString(R.string.ago_min_str, Math.round(diff / minute));

        }else if(diff > hour && diff <= day){

            timePeriodVerdict = context.getString(R.string.ago_hour_str, Math.round(diff / hour));

        }else if(diff > day && diff <= week){

            timePeriodVerdict = context.getString(R.string.ago_day_str, Math.round(diff / day));

        }else if(diff > week && diff <= month){

            timePeriodVerdict = context.getString(R.string.ago_week_str, Math.round(diff / week));

        }else if(diff > month && diff <= year){

            timePeriodVerdict = context.getString(R.string.ago_month_str, Math.round(diff / month));

        }else if(diff > year){

            timePeriodVerdict = context.getString(R.string.ago_year_str, Math.round(diff / year));

        }

        return timePeriodVerdict;
    }

    /**
     * Récupération de la date par rapport à la date actuelle
     * @return date relative
     */
    public String getTimeRelative(){

        String date = this.time.split("-")[0].split(" ")[1],
                hour = this.time.split("-")[1];
        String currentDate = getCurrentDate().split("-")[0].split(" ")[1],
                result;

        if (date.equals(currentDate)){

            result = hour;

        }else {

            if (date.split("\\.")[1].equals(currentDate.split("\\.")[1])
                    && date.split("\\.")[2].equals(currentDate.split("\\.")[2])) {

                Integer day = Integer.valueOf(date.split("\\.")[0]),
                        currentDay = Integer.valueOf(currentDate.split("\\.")[0]);

                if (currentDay - day == 1) {
                    result = context.getString(R.string.yesterday);
                } else if (currentDay - day == 2) {
                    result = context.getString(R.string.day_before_yesterday);
                } else {
                    result = date.split("\\.")[1] + "/" + date.split("\\.")[2];
                }

            }else {
                result = date.split("\\.")[1] + "/" + date.split("\\.")[2];
            }
        }

        return result;
    }
}
