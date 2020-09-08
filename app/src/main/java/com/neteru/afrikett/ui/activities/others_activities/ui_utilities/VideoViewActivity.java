package com.neteru.afrikett.ui.activities.others_activities.ui_utilities;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;

import com.neteru.afrikett.R;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;

import static com.neteru.afrikett.core.utilities.Constants.EMPTY;

public class VideoViewActivity extends AfrikettBaseActivity {

    public final static String URL = "url";
    public final static String PATH = "path";
    public final static String URI_STR = "uri";
    public final static String LEGEND = "legend";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        // Barre de chargement
        progressBar = findViewById(R.id.progress);

        // Lecteur video
        VideoView videoView = findViewById(R.id.videoView);

        // Controleur media
        MediaController mediaController = new MediaController(this);

        if (getIntent() != null){

            if (getIntent().hasExtra(URL)){ // Si URL existe

                videoView.setVideoPath(getIntent().getStringExtra(URL));

            }else if(getIntent().hasExtra(PATH)){ // Si PATH existe

                videoView.setVideoPath(getIntent().getStringExtra(PATH));

            }else if(getIntent().hasExtra(URI_STR)){ // Si URI existe

                videoView.setVideoURI(Uri.parse(getIntent().getStringExtra(URI_STR)));

            }

            // Configuration du lecteur video
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
            videoView.seekTo(100);
            videoView.requestFocus();

            // Customisation de la barre de chargement
            progressBar
                    .getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

            // Apparition de la barre de chargement
            progressBar.setVisibility(View.VISIBLE);

            videoView.setOnPreparedListener(mediaPlayer -> {
                // Disparition de la barre de chargement
                progressBar.setVisibility(View.GONE);
            });

            // Démarrage de la lecture
            videoView.start();

        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);
        }

        // Adaptation de la barre de status aux versions supérieures à LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(VideoViewActivity.this,R.color.black));
        }
    }
}
