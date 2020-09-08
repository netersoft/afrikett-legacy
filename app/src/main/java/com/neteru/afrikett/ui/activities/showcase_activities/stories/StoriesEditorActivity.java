package com.neteru.afrikett.ui.activities.showcase_activities.stories;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.baoyz.widget.PullRefreshLayout;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.StoryListAdapter;
import com.neteru.afrikett.core.models.RemoteDB.Showcase;
import com.neteru.afrikett.core.models.RemoteDB.StoriesImgPreview;
import com.neteru.afrikett.core.models.RemoteDB.Story;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.core.libs.StateView.StateView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.neteru.afrikett.core.utilities.AppUtilities.copyFileOrDirectory;
import static com.neteru.afrikett.core.utilities.AppUtilities.createNomediaFile;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.RANDOM_VALUE;
import static com.neteru.afrikett.core.utilities.Constants.SINGLE;
import static com.neteru.afrikett.core.utilities.Constants.STORIES_DIRECTORY;

public class StoriesEditorActivity extends AfrikettBaseActivity {
    private int indicator;
    private String userId;
    private String showcaseId;
    private String showcaseName;
    private String showcaseLogo;
    private String showcasePrimaryColor;
    private String showcaseSecondaryColor;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private List<Story> storyList = new ArrayList<>();
    private StoryListAdapter adapter;
    private StateView stateView;
    private PullRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories_editor);

        userId = getLocalUserData(this).getId();

        showcaseId = getIntent().getStringExtra("showcaseId");
        showcaseName = getIntent().getStringExtra("showcaseName");
        showcaseLogo = getIntent().getStringExtra("showcaseLogo");
        showcasePrimaryColor = getIntent().getStringExtra("showcasePrimaryColor");
        showcaseSecondaryColor = getIntent().getStringExtra("showcaseSecondaryColor");

        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT);
        storageReference = FirebaseStorage.getInstance().getReference("showcases").child("stories");

        RecyclerView recyclerView = findViewById(R.id.recycler);
        refreshLayout = findViewById(R.id.refresh);
        stateView = findViewById(R.id.stateview);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshLayout.setOnRefreshListener(this::loadStories);

        adapter = new StoryListAdapter(StoriesEditorActivity.this, storyList, new StoryListAdapter.StoryListAdapterListener() {
            @Override
            public void toStoryViewer(int position, Story story) {

                startActivity(
                        new Intent(StoriesEditorActivity.this, StoriesViewerSingleSourceActivity.class)
                                .putExtra("mode", SINGLE)
                                .putExtra("position", position)
                                .putExtra("showViewCounter", true)
                                .putExtra("showcaseId", showcaseId)
                                .putExtra("showcaseName", showcaseName)
                                .putExtra("showcaseLogo", showcaseLogo)
                                .putExtra("showcasePrimaryColor", showcasePrimaryColor)
                                .putExtra("showcaseSecondaryColor", showcaseSecondaryColor));
                overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

            }

            @Override
            public void deleteStory(Story story) {

                databaseReference
                        .child("stories")
                        .child(story.getId())
                        .setValue(null);

            }
        });

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        FloatingActionButton addStory = findViewById(R.id.add_story);
        addStory.setOnClickListener(v -> ImagePicker.create(StoriesEditorActivity.this)
                .returnMode(ReturnMode.CAMERA_ONLY)
                .folderMode(false)
                .toolbarFolderTitle(getString(R.string.gallery))
                .toolbarImageTitle(getString(R.string.select_an_image))
                .includeVideo(false)
                .showCamera(true)
                .theme(R.style.ImagePickerTheme)
                .multi()
                .start());

        loadStories();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(showcasePrimaryColor));
        }

        if (getSupportActionBar() != null){
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));

            // Customisation de la couleur de la flèche "Retour"
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_arrow_back_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            // Customisation du titre de la barre d'outils
            Spannable title = new SpannableString(getString(R.string.stories));
            title.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(title);

        }
    }

    private void loadStories() {

        stateView.displayLoadingState();

        databaseReference
                .child("stories")
                .orderByChild("source")
                .equalTo(showcaseId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null){

                            stateView.displayState("no_story");
                            refreshLayout.setRefreshing(false);
                            adapter.notifyDataSetChanged();

                            return;
                        }

                        storyList.clear();
                        stateView.hideStates();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Story story = snapshot.getValue(Story.class);

                            storyList.add(story);
                        }

                        adapter.notifyDataSetChanged();

                        refreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        stateView.hideStates();
                        refreshLayout.setRefreshing(false);

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) { // Résultat initial image(s)/vidéo(s)

            List<Image> images = ImagePicker.getImages(data);

            if (images != null){

                // Execution de la copie locale des fichiers et ouverture du visualisateur d'aperçu
                new ImgFileCopyTask(images).execute();
            }


        }else if (requestCode == RANDOM_VALUE) { // Résultat final image(s)/video(s)

            if (resultCode == RESULT_OK && data != null) {

                // Récupération des données sérializées
                @SuppressWarnings("unchecked") final List<StoriesImgPreview> storiesImgPreviewList = (List<StoriesImgPreview>) data.getSerializableExtra("previewData");

                indicator = 0;

                final LoadingDialog loadingDialog = new LoadingDialog(this);
                loadingDialog.show();

                databaseReference
                        .child("showcases")
                        .child(showcaseId)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) return;

                                Showcase showcase = dataSnapshot.getValue(Showcase.class);

                                if (showcase == null) return;

                                for (StoriesImgPreview storiesImgPreview : storiesImgPreviewList){

                                    final String path = userId + "/" + UUID.randomUUID().toString();

                                    storageReference
                                            .child(path)
                                            .putFile(storiesImgPreview.getUri())
                                            .addOnSuccessListener(taskSnapshot -> storageReference
                                                    .child(path)
                                                    .getDownloadUrl()
                                                    .addOnSuccessListener(uri -> {

                                                        String key = databaseReference.child("stories").push().getKey();

                                                        if (key == null) return;

                                                        // Enregistrement définitif
                                                        databaseReference
                                                                .child("stories")
                                                                .child(key)
                                                                .setValue(new Story(key, showcaseId, uri.toString(), showcase.getOwners()))
                                                                .addOnCompleteListener(task -> {
                                                                    indicator += 1;

                                                                    if (indicator == storiesImgPreviewList.size()){
                                                                        loadingDialog.dismiss();
                                                                    }
                                                                });

                                                    }));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

        }
    }

    /**
     * Tâche de copie de fichiers image(s)/vidéo(s)
     */
    @SuppressLint("StaticFieldLeak")
    class ImgFileCopyTask extends AsyncTask<String, Void, Void> {

        private List<Image> imageList;
        private LoadingDialog loadingDialog;
        private StringBuilder pathBuilder = new StringBuilder(),
                nameBuilder = new StringBuilder();

        ImgFileCopyTask(List<Image> images){
            imageList = images;

            loadingDialog = new LoadingDialog(StoriesEditorActivity.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            for (Image image: imageList){

                // Copie du fichier
                copyFileOrDirectory(image.getPath(), STORIES_DIRECTORY);

                // Création du fichier .nomedia
                createNomediaFile(STORIES_DIRECTORY);

                // Préparation des chaînes formatées
                pathBuilder.append(STORIES_DIRECTORY).append(File.separator).append(image.getName()).append("\n");
                nameBuilder.append(image.getName()).append("\n");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            loadingDialog.dismiss();

            // Redirection vers le visualisateur d'aperçus
            startActivityForResult(new Intent(StoriesEditorActivity.this, StoriesImgPreviewActivity.class)
                    .putExtra("names", nameBuilder.toString())
                    .putExtra("paths", pathBuilder.toString())
                    .putExtra("showcaseId", showcaseId)
                    .putExtra("showcaseName", showcaseName)
                    .putExtra("showcaseLogo", showcaseLogo)
                    .putExtra("showcasePrimaryColor", showcasePrimaryColor)
                    .putExtra("showcaseSecondaryColor", showcaseSecondaryColor), RANDOM_VALUE);

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            super.onPostExecute(aVoid);
        }
    }
}
