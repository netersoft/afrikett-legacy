package com.neteru.afrikett.core.libs.BasicAudioPlayer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.neteru.afrikett.R;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class BasicAudioPlayer extends FrameLayout {
    private BasicAudioPlayerListener listener;
    private ImageView actionButton;
    private SeekBar progressBar;
    private MediaPlayer mPlayer;
    private TextView duration;
    private Uri currentUri;
    private int currentResource;
    private Handler mHandler;
    private Runnable mRunnable;
    private String totalDuration;
    private int length;

    private static String START_TIME = "00:00";

    public BasicAudioPlayer(@NonNull Context context) {
        super(context);
        init(null);
    }

    public BasicAudioPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BasicAudioPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasicAudioPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    /**
     * Initialisation...
     * @param attrs / Attributs
     */
    private void init(AttributeSet attrs){
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_basic_audio_player, this);

        this.actionButton = rootView.findViewById(R.id.actionBut);
        this.progressBar = rootView.findViewById(R.id.progressBar);
        this.duration = rootView.findViewById(R.id.duration);

        mHandler = new Handler();

        setListeners();

        if(attrs == null){
            return;
        }

        TypedArray styledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.BasicAudioPlayer);

        setActionButtonColor(styledAttributes.getColor(R.styleable.BasicAudioPlayer_buttonColor, Color.WHITE));
        setProgressBarColor(styledAttributes.getColor(R.styleable.BasicAudioPlayer_progressBarColor, Color.BLUE));
        setDurationTextColor(styledAttributes.getColor(R.styleable.BasicAudioPlayer_durationTextColor, Color.BLACK));

        styledAttributes.recycle();
    }

    /**
     * Initialisation des écouteurs
     */
    private void setListeners(){

        this.actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImageView button = (ImageView)view;

                if (mPlayer != null){

                    if (mPlayer.isPlaying()){

                        if (listener != null) {
                            listener.onPlayerPause();
                        }

                        button.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                        mPlayer.pause();

                        length = mPlayer.getCurrentPosition();

                    }else {

                        if (listener != null) {
                            listener.onPlayerStart();
                        }

                        button.setImageResource(R.drawable.ic_pause_white_48dp);

                        mPlayer.seekTo(length);
                        mPlayer.start();
                        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                button.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                                length = 0;
                            }
                        });

                        startSeekBarProgression();

                    }

                }
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer != null && fromUser){
                    mPlayer.seekTo(progress * 1000);

                    setDuration(getTimeFromMilliseconds(progress * 1000));
                }
            }
        });

    }

    private void startSeekBarProgression(){

        progressBar.setMax(mPlayer.getDuration() / 1000);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if(mPlayer!=null){
                    int mCurrentPosition = mPlayer.getCurrentPosition()/1000; // In milliseconds
                    progressBar.setProgress(mCurrentPosition);

                    setDuration(getTimeFromMilliseconds(mPlayer.getCurrentPosition()));
                }
                mHandler.postDelayed(mRunnable,1000);
            }
        };
        mHandler.postDelayed(mRunnable,1000);
    }

    /**
     * Chargement du player
     * @param uri / URI du fichier audio
     */
    public void loadAudioUri(Uri uri, String fileDuration){

        mPlayer = MediaPlayer.create(getContext(), uri);
        totalDuration = fileDuration;
        currentUri = uri;
        length = 0;

        setDuration(START_TIME);
    }

    /**
     * Chargement du player
     * @param resource / Identifiant de la ressource audio
     */
    public void loadAudioResource(int resource, String fileDuration){

        mPlayer = MediaPlayer.create(getContext(), resource);
        totalDuration = fileDuration;
        currentResource = resource;
        length = 0;

        setDuration(START_TIME);
    }

    /**
     * Instanciation de l'interface
     * @param l / Interface
     */
    public void setBasicAudioPlayerListener(BasicAudioPlayerListener l){
        this.listener = l;
    }

    /**
     * Change la couleur du bouton
     * @param color / Identifiant de la couleur
     */
    public void setActionButtonColor(int color){

        this.actionButton.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);

    }

    /**
     * Change la couleur de la barre de progression
     * @param color / Identifiant de la couleur
     */
    public void setProgressBarColor(int color){

        this.progressBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        this.progressBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);

    }

    /**
     * Change la couleur du label temps
     * @param color / Identifiant de la couleur
     */
    public void setDurationTextColor(int color){

        this.duration.setTextColor(color);

    }

    /**
     * Ecriture de la durée courante du fichier audio
     * @param durationText / durée courante
     */
    private void setDuration(String durationText){

        this.duration.setText(durationText+" / "+totalDuration);

    }

    public ImageView getActionButton(){
        return this.actionButton;
    }

    public SeekBar getProgressBar(){
        return this.progressBar;
    }

    public TextView getDurationView(){
        return this.duration;
    }

    public MediaPlayer getPlayer(){
        return this.mPlayer;
    }

    public Uri getCurrentUri() {
        return currentUri;
    }

    public int getCurrentResource() {
        return currentResource;
    }

    public boolean isPlaying(){
        return this.mPlayer.isPlaying();
    }

    public void stopPlaying(){

        if(mPlayer != null){
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;

            if(mHandler != null){
                mHandler.removeCallbacks(mRunnable);
            }

            loadAudioUri(currentUri, totalDuration);
            progressBar.setProgress(0);
            actionButton.setImageResource(R.drawable.ic_play_arrow_white_48dp);

        }
    }

    /**
     * Formatage de temps en millisecondes
     * @param time / Temps en millisecondes
     * @return / Format Min : Sec
     */
    private static String getTimeFromMilliseconds(long time){

        String result = String.format(Locale.US, "%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time)));

        String[] tempTab = result.split(":");

        return (tempTab[0].length() < 2 ? "0"+tempTab[0] : tempTab[0])
                + ":" +
                (tempTab[1].length() < 2 ? "0"+tempTab[1] : tempTab[1]);


    }

}
