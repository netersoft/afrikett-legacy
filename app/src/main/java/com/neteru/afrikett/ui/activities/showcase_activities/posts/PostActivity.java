package com.neteru.afrikett.ui.activities.showcase_activities.posts;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.adapters.PostImgAdapter;
import com.neteru.afrikett.core.models.RemoteDB.Post;
import com.neteru.afrikett.core.models.RemoteDB.PostImgPreview;
import com.neteru.afrikett.core.models.RemoteDB.PostModels.EventPost;
import com.neteru.afrikett.core.models.RemoteDB.PostModels.NewsPost;
import com.neteru.afrikett.core.models.RemoteDB.PostModels.ProductAndServicePost;
import com.neteru.afrikett.core.utilities.AfrikettBaseActivity;
import com.neteru.afrikett.core.utilities.Connectivity;
import com.neteru.afrikett.core.utilities.LoadingDialog;
import com.neteru.afrikett.ui.activities.others_activities.ui_utilities.ImageViewActivity;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import static com.neteru.afrikett.core.utilities.AppUtilities.copyFileOrDirectory;
import static com.neteru.afrikett.core.utilities.AppUtilities.createNomediaFile;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeInAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getFadeOutAnimation;
import static com.neteru.afrikett.core.utilities.AppUtilities.getLocalUserData;
import static com.neteru.afrikett.core.utilities.Constants.DATABASE_ROOT;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.EVENT;
import static com.neteru.afrikett.core.utilities.Constants.NEWS;
import static com.neteru.afrikett.core.utilities.Constants.POSTS_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.PRODUCT_AND_SERVICE;
import static com.neteru.afrikett.core.utilities.Constants.RANDOM_VALUE;
import static com.neteru.afrikett.ui.activities.others_activities.settings.ProfileSettingsActivity.formatDayAndMonth;

public class PostActivity extends AfrikettBaseActivity {

    private String showcaseId;
    private String showcaseName;
    private String showcaseLogo;
    private String showcasePrimaryColor;
    private String showcaseSecondaryColor;
    private TextView selector;
    private int selectorCursor;
    private CharSequence[] selectorModalities;
    private LinearLayout productAndServiceBox, eventBox, newsBox;
    private RecyclerView postImgRecycler;
    private PostImgAdapter postImgAdapter;
    private List<PostImgPreview> postImgList = new ArrayList<>();
    private ArrayList<String> postImgDownloadUrlList = new ArrayList<>();
    private TextInputEditText productOrServiceName;
    private TextInputEditText productOrServiceDescription;
    private TextInputEditText productOrServicePrice;
    private SwitchCompat productOrServiceAvailability;
    private TextInputEditText eventName;
    private TextInputEditText eventLocation;
    private TextInputEditText eventAbout;
    private TextView eventDate;
    private TextView eventHour;
    private TextInputEditText newsTitle;
    private TextInputEditText newsContent;
    private Button post;
    private Button addImages;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private LoadingDialog loadingDialog;
    private boolean publishButtonState;
    private String authorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        authorId = getLocalUserData(this).getId();

        showcaseId = getIntent().getStringExtra("showcaseId");
        showcaseName = getIntent().getStringExtra("showcaseName");
        showcaseLogo = getIntent().getStringExtra("showcaseLogo");
        showcasePrimaryColor = getIntent().getStringExtra("showcasePrimaryColor");
        showcaseSecondaryColor = getIntent().getStringExtra("showcaseSecondaryColor");

        postImgRecycler = findViewById(R.id.postImgRecycler);
        postImgRecycler.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        postImgRecycler.setHasFixedSize(true);

        productAndServiceBox = findViewById(R.id.product_and_service_box);
        eventBox = findViewById(R.id.event_box);
        newsBox = findViewById(R.id.news_box);

        productOrServiceName = findViewById(R.id.product_or_service_name);
        productOrServicePrice = findViewById(R.id.product_or_service_price);
        productOrServiceDescription = findViewById(R.id.product_or_service_description);
        productOrServiceAvailability = findViewById(R.id.product_or_service_availability);

        eventName = findViewById(R.id.event_name);
        eventLocation = findViewById(R.id.event_location);
        eventDate = findViewById(R.id.event_date);
        eventHour = findViewById(R.id.event_hour);
        eventAbout = findViewById(R.id.about_event);

        newsTitle = findViewById(R.id.news_title);
        newsContent = findViewById(R.id.news_content);

        post = findViewById(R.id.post);
        addImages = findViewById(R.id.add_images);

        storageReference = FirebaseStorage.getInstance().getReference("showcases").child("posts");
        databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_ROOT).child("posts");

        loadingDialog = new LoadingDialog(this);

        selectorCursor = 0;
        selectorModalities = new CharSequence[]{
                getString(R.string.a_product_or_service),
                getString(R.string.an_event),
                getString(R.string.a_news)
        };

        selector = findViewById(R.id.selectorText);

        setListeners();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(showcasePrimaryColor));
        }

        // Customisation des éléments la barre d'outils
        if (getSupportActionBar() != null) {

            // Customisation de la couleur de la fermeture
            final Drawable upArrow = getResources().getDrawable(R.mipmap.ic_clear_white_24dp);
            upArrow.setColorFilter(getResources().getColor(R.color.skyblue), PorterDuff.Mode.SRC_ATOP);

            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(EMPTY);

        }
    }

    private void setListeners(){

        switchBoxes(selectorCursor);
        selector.setText(selectorModalities[selectorCursor]);
        selector.setOnClickListener(v -> new AlertDialog.Builder(PostActivity.this)
                .setCancelable(true)
                .setSingleChoiceItems(selectorModalities, selectorCursor, (dialogInterface, i) -> {

                    selectorCursor = i;
                    switchBoxes(selectorCursor);
                    selector.setText(selectorModalities[selectorCursor]);

                    dialogInterface.dismiss();
                })
                .show());

        addImages.setOnClickListener(v -> ImagePicker.create(PostActivity.this)
                .returnMode(ReturnMode.CAMERA_ONLY)
                .folderMode(false)
                .toolbarFolderTitle(getString(R.string.gallery))
                .toolbarImageTitle(getString(R.string.select_an_image))
                .includeVideo(false)
                .showCamera(true)
                .theme(R.style.ImagePickerTheme)
                .multi()
                .start());

        View.OnClickListener onEventDateClick = v -> {

            Calendar mcurrentTime = Calendar.getInstance();

            new SpinnerDatePickerDialogBuilder()
                    .context(PostActivity.this)
                    .callback((view, year, monthOfYear, dayOfMonth) -> {

                        String newDate = formatDayAndMonth(dayOfMonth) + "/" + formatDayAndMonth(monthOfYear + 1) + "/" + year;

                        eventDate.setText(newDate);

                        if (eventName.getText() != null && eventName.getText().toString().length() > 0 &&
                                eventLocation.getText() != null && eventLocation.getText().toString().length() > 0 &&
                                    eventAbout.getText() != null && eventAbout.getText().toString().length() > 0 ) {
                                setButtonEnabled(true);
                        }

                    })
                    .spinnerTheme(R.style.DatePickerStyle)
                    .showTitle(false)
                    .showDaySpinner(true)
                    .defaultDate(mcurrentTime.get(Calendar.YEAR), mcurrentTime.get(Calendar.MONTH), mcurrentTime.get(Calendar.DAY_OF_MONTH))
                    .maxDate(2100, 0, 1)
                    .minDate(mcurrentTime.get(Calendar.YEAR), mcurrentTime.get(Calendar.MONTH), mcurrentTime.get(Calendar.DAY_OF_MONTH))
                    .build()
                    .show();

        },
        onEventHourClick = v -> {

            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(PostActivity.this, R.style.TimePickerStyle, (timePicker, selectedHour, selectedMinute) -> {
                String newHour =  (String.valueOf(selectedHour).length() == 1 ? "0" + selectedHour : selectedHour)
                                      + ":" +
                                  (String.valueOf(selectedMinute).length() == 1 ? "0" + selectedMinute : selectedMinute);

                eventHour.setText(newHour);
            }, hour, minute, true);
            mTimePicker.setTitle(getString(R.string.event_hour));
            mTimePicker.show();

        };

        eventDate.setOnClickListener(onEventDateClick);
        findViewById(R.id.event_date_layout).setOnClickListener(onEventDateClick);
        eventHour.setOnClickListener(onEventHourClick);
        findViewById(R.id.event_hour_layout).setOnClickListener(onEventHourClick);

        post.setOnClickListener(v -> {

            if (Connectivity.getInstance(PostActivity.this).isOnline()) {
                publish();
            }

        });

        setFieldListeners();
    }

    private void setFieldListeners(){

        productOrServiceName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (/* productOrServicePrice.getText() != null &&
                            productOrServicePrice.getText().toString().length() > 0 && */
                                productOrServiceDescription.getText() != null &&
                                    productOrServiceDescription.getText().toString().length() > 0 &&
                                        !postImgList.isEmpty()) {
                        setButtonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        productOrServiceDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (/* productOrServicePrice.getText() != null &&
                            productOrServicePrice.getText().toString().length() > 0 && */
                                productOrServiceName.getText() != null &&
                                    productOrServiceName.getText().toString().length() > 0 &&
                                        !postImgList.isEmpty()) {
                        setButtonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        productOrServicePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (productOrServiceName.getText() != null &&
                            productOrServiceName.getText().toString().length() > 0 &&
                               productOrServiceDescription.getText() != null &&
                                    productOrServiceDescription.getText().toString().length() > 0 &&
                                        !postImgList.isEmpty()) {
                        setButtonEnabled(true);
                    }
                }
                */
            }

            @Override
            public void afterTextChanged(Editable s) {
                String seq = s.toString().replace(".", EMPTY), firstPointResult, finalResult = seq;
                int seqLength = seq.length();

                if (seqLength > 3){

                    int limit = seqLength % 3 == 0 ? 3 : seqLength % 3;
                    firstPointResult = seq.substring(0, limit)+"."+seq.substring(limit);

                    String secondSection = firstPointResult.split("\\.")[1];
                    int nbSecondSectionPoints = (secondSection.length() / 3) - 1;

                    if (nbSecondSectionPoints > 0){
                        List<String> tempTab = new ArrayList<>();
                        int a = 0, b = 3;

                        for (int i = 0; i <= nbSecondSectionPoints; i++){

                            tempTab.add(secondSection.substring(a, b));
                            a += 3; b += 3;

                        }

                        StringBuilder builder = new StringBuilder();
                        for (int y = 0; y < tempTab.size(); y++){
                            builder.append(tempTab.get(y));

                            if (y + 1 != tempTab.size()){
                                builder.append(".");
                            }

                        }

                        finalResult = firstPointResult.split("\\.")[0] + "." + builder.toString();

                    }else {
                        finalResult = firstPointResult;
                    }

                }

                productOrServicePrice.removeTextChangedListener(this);
                productOrServicePrice.setText(finalResult);
                productOrServicePrice.setSelection(finalResult.length());
                productOrServicePrice.addTextChangedListener(this);
            }
        });

        eventName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (eventLocation.getText() != null &&
                            eventLocation.getText().toString().length() > 0 &&
                                eventAbout.getText() != null &&
                                    eventAbout.getText().toString().length() > 0 &&
                                        !eventDate.getText().toString().equals(getString(R.string.add))) {
                        setButtonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        eventLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (eventName.getText() != null &&
                            eventName.getText().toString().length() > 0 &&
                                eventAbout.getText() != null &&
                                    eventAbout.getText().toString().length() > 0 &&
                                        !eventDate.getText().toString().equals(getString(R.string.add))) {
                        setButtonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        eventAbout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (eventLocation.getText() != null &&
                            eventLocation.getText().toString().length() > 0 &&
                                eventName.getText() != null &&
                                    eventName.getText().toString().length() > 0 &&
                                        !eventDate.getText().toString().equals(getString(R.string.add))) {
                        setButtonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        newsTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (newsContent.getText() != null &&
                            newsContent.getText().toString().length() > 0 &&
                                !postImgList.isEmpty()) {
                        setButtonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        newsContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    setButtonEnabled(false);
                }else {
                    if (newsTitle.getText() != null &&
                            newsTitle.getText().toString().length() > 0 &&
                                !postImgList.isEmpty()) {
                        setButtonEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setButtonEnabled(boolean enabled){

        publishButtonState = enabled;

        if (enabled){
            post.setBackground(getResources().getDrawable(R.drawable.skyblue_button_bg_stroke));
            post.setTextColor(getResources().getColor(R.color.skyblue));
        }else {
            post.setBackground(getResources().getDrawable(R.drawable.gray_button_bg_stroke));
            post.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    private void switchBoxes(int cursor){

        setButtonEnabled(false);

        productOrServiceName.setText(EMPTY);
        productOrServiceDescription.setText(EMPTY);
        productOrServicePrice.setText(EMPTY);

        eventName.setText(EMPTY);
        eventLocation.setText(EMPTY);
        eventAbout.setText(EMPTY);
        eventHour.setText(getString(R.string.add));
        eventDate.setText(getString(R.string.add));

        newsTitle.setText(EMPTY);
        newsContent.setText(EMPTY);

        productAndServiceBox.setVisibility(View.GONE);
        eventBox.setVisibility(View.GONE);
        newsBox.setVisibility(View.GONE);

        switch (cursor){
            case PRODUCT_AND_SERVICE:
                productAndServiceBox.setVisibility(View.VISIBLE);
                break;

            case EVENT:
                eventBox.setVisibility(View.VISIBLE);
                break;

            case NEWS:
                newsBox.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void publish(){

        if (postImgList.isEmpty() &&
            (productOrServiceName.getText() != null &&
             productOrServiceName.getText().toString().length() > 0 &&
             /* productOrServicePrice.getText() != null &&
             productOrServicePrice.getText().toString().length() > 0 && */
             productOrServiceDescription.getText() != null &&
             productOrServiceDescription.getText().toString().length() > 0)
            ||
            (newsTitle.getText() != null &&
             newsTitle.getText().toString().length() > 0 &&
             newsTitle.getText() != null &&
             newsTitle.getText().toString().length() > 0)) {

            Toast.makeText(this, getString(R.string.add_at_least_one_preview), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!publishButtonState){
            return;
        }

        postImgDownloadUrlList.clear();
        loadingDialog.setMsg(getString(R.string.be_patient));
        loadingDialog.show();

        if (!postImgList.isEmpty()){

            uploadPreviews();

        }else {

            writePost();

        }
    }

    private void uploadPreviews() {

        for (PostImgPreview preview : postImgList){

            final String path = getLocalUserData(this).getId() + "/" + UUID.randomUUID().toString();

            storageReference
                    .child(path)
                    .putFile(preview.getUri())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){

                            storageReference
                                    .child(path)
                                    .getDownloadUrl()
                                    .addOnSuccessListener(uri -> {

                                        postImgDownloadUrlList.add(uri.toString());

                                        if (postImgDownloadUrlList.size() == postImgList.size()){
                                            writePost();
                                        }

                                    });
                        }else {
                            Toast.makeText(PostActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                            loadingDialog.dismiss();
                        }

                    });
        }
    }

    private void writePost() {

        String key = databaseReference.push().getKey();

        if (key == null){
            return;
        }

        Post newPost = null;

        switch (selectorCursor){
            case PRODUCT_AND_SERVICE:
                String name = productOrServiceName.getText() != null ? productOrServiceName.getText().toString() : null;
                String description = productOrServiceDescription.getText() != null ? productOrServiceDescription.getText().toString() : null;
                String price = productOrServicePrice.getText() != null ? productOrServicePrice.getText().toString() : null;
                Boolean availability = productOrServiceAvailability.isChecked();

                newPost = new ProductAndServicePost(key, showcaseId, authorId, PRODUCT_AND_SERVICE, name, description, price, availability, postImgDownloadUrlList);
                break;

            case EVENT:
                String nameEvent = eventName.getText() != null ? eventName.getText().toString() : null;
                String aboutEvent = eventAbout.getText() != null ? eventAbout.getText().toString() : null;
                String location = eventLocation.getText() != null ? eventLocation.getText().toString() : null;
                String date = eventDate.getText().toString();
                String hour = eventHour.getText().toString();

                newPost = new EventPost(key, showcaseId, authorId, EVENT, nameEvent, location, date, hour, aboutEvent, postImgDownloadUrlList);
                break;

            case NEWS:
                String title = newsTitle.getText() != null ? newsTitle.getText().toString() : null;
                String content = newsContent.getText() != null ? newsContent.getText().toString() : null;

                newPost = new NewsPost(key, showcaseId, authorId, NEWS, title, content, postImgDownloadUrlList);
                break;
        }

        databaseReference
                .child(key)
                .setValue(newPost)
                .addOnCompleteListener(task -> {

                    loadingDialog.dismiss();

                    if (task.isSuccessful()){

                        finish();
                        overridePendingTransition(R.anim.slide_in_left_activity, R.anim.slide_out_right_activity);

                    }else {
                        Toast.makeText(PostActivity.this, R.string.error_occurred, Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) { // Résultat initial image(s)/vidéo(s)

            List<Image> images = ImagePicker.getImages(data);

            if (images != null) {

                // Execution de la copie locale des fichiers et ouverture du visualisateur d'aperçu
                new ImgFileCopyTask(images).execute();
            }


        } else if (requestCode == RANDOM_VALUE) { // Résultat final image(s)/video(s)

            if (resultCode == RESULT_OK && data != null) {

                // Récupération des données sérializées
                postImgList = (List<PostImgPreview>) data.getSerializableExtra("previewData");

                if (postImgList != null){

                    addImages.setVisibility(View.GONE);
                    addImages.startAnimation(getFadeOutAnimation(this));
                    postImgRecycler.setVisibility(View.VISIBLE);
                    postImgRecycler.startAnimation(getFadeInAnimation(this));

                    postImgAdapter = new PostImgAdapter(PostActivity.this, postImgList, R.layout.template_post_img, new PostImgAdapter.PostImgAdapterListener() {

                        @Override
                        public void onDeleteItem(int position, PostImgPreview preview) {

                            postImgList.remove(position);
                            postImgAdapter.notifyItemRemoved(position);

                            if (postImgList.isEmpty()){
                                postImgRecycler.setVisibility(View.GONE);
                                postImgRecycler.startAnimation(getFadeOutAnimation(PostActivity.this));
                                addImages.setVisibility(View.VISIBLE);
                                addImages.startAnimation(getFadeInAnimation(PostActivity.this));
                            }
                        }

                        @Override
                        public void openImage(String uriStr) {

                            startActivity(new Intent(PostActivity.this, ImageViewActivity.class)
                                                    .putExtra("uri", uriStr));
                            overridePendingTransition(R.anim.slide_in_right_activity, R.anim.slide_out_left_activity);

                        }

                    });

                    postImgRecycler.setAdapter(postImgAdapter);
                    postImgAdapter.notifyDataSetChanged();

                }

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

            loadingDialog = new LoadingDialog(PostActivity.this);
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
                copyFileOrDirectory(image.getPath(), POSTS_DIRECTORY);

                // Création du fichier .nomedia
                createNomediaFile(POSTS_DIRECTORY);

                // Préparation des chaînes formatées
                pathBuilder.append(POSTS_DIRECTORY).append(File.separator).append(image.getName()).append("\n");
                nameBuilder.append(image.getName()).append("\n");

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            loadingDialog.dismiss();

            // Redirection vers le visualisateur d'aperçus
            startActivityForResult(new Intent(PostActivity.this, PostImgPreviewActivity.class)
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
