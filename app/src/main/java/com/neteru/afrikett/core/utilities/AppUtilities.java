package com.neteru.afrikett.core.utilities;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.neteru.afrikett.BuildConfig;
import com.neteru.afrikett.R;
import com.neteru.afrikett.core.models.RemoteDB.User;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.neteru.afrikett.core.utilities.Constants.CAMERA;
import static com.neteru.afrikett.core.utilities.Constants.DATE_PATTERN;
import static com.neteru.afrikett.core.utilities.Constants.EMPTY;
import static com.neteru.afrikett.core.utilities.Constants.GALLERY;
import static com.neteru.afrikett.core.utilities.Constants.IMAGE_DIRECTORY;
import static com.neteru.afrikett.core.utilities.Constants.INFO_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.LAUNCH_PREFS;
import static com.neteru.afrikett.core.utilities.Constants.LETTER_RES;
import static com.neteru.afrikett.core.utilities.Constants.NUMBER_RES;

@SuppressWarnings("unused, WeakerAccess")
public abstract class AppUtilities {

    /*---------------------- SharedPreferences ----------------------*/

    public static void setStringPreference(Context context, String file, String key, String value){
        context.getSharedPreferences(file, Context.MODE_PRIVATE).edit()
            .putString(key, value)
            .apply();
    }

    public static void setIntPreference(Context context, String file, String key, int value){
        context.getSharedPreferences(file, Context.MODE_PRIVATE).edit()
                .putInt(key, value)
                .apply();
    }

    public static void setBooleanPreference(Context context, String file, String key, boolean value){
        context.getSharedPreferences(file, Context.MODE_PRIVATE).edit()
                .putBoolean(key, value)
                .apply();
    }

    public static String getStringPreference(Context context, String file, String key, @Nullable String defaultValue){
        return context.getSharedPreferences(file, Context.MODE_PRIVATE).getString(key, defaultValue);
    }

    public static int getIntPreference(Context context, String file, String key, int defaultValue){
        return context.getSharedPreferences(file, Context.MODE_PRIVATE).getInt(key, defaultValue);
    }

    public static boolean getBooleanPreference(Context context, String file, String key, boolean defaultValue){
        return context.getSharedPreferences(file, Context.MODE_PRIVATE).getBoolean(key, defaultValue);
    }

    public static void removePreference(Context context, String file, String key){
        context.getSharedPreferences(file, Context.MODE_PRIVATE)
                .edit()
                .remove(key)
                .apply();
    }

    /*---------------------- Animations ----------------------*/

    public static Animation getFadeInAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.fade_in);
    }

    public static Animation getFadeOutAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.fade_out);
    }

    public static Animation getFadeInSlowAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.fade_in_slow);
    }

    public static Animation getFadeOutSlowAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.fade_out_slow);
    }

    public static Animation getSlideDownAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.slide_down);
    }

    public static Animation getSlideUpAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.slide_up);
    }

    public static Animation getShakeAnimation(Context context){
        return AnimationUtils.loadAnimation(context, R.anim.shake);
    }

    /*---------------------- Dates ----------------------*/

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

    /*---------------------- Mail ----------------------*/

    /**
     * Validation syntaxique des Adresses Email
     * @param mail / Adresse email
     * @return / etat de validation
     */
    public static boolean validateMail(String mail){

        Pattern pattern = Pattern.compile(Constants.MAIL_RGX);
        Matcher matcher = pattern.matcher(mail);

        return matcher.matches();

    }

    /*---------------------- UserData ----------------------*/

    /**
     * Inscription des données locales
     * @param context / Contexte
     * @param user / Instance regroupant les données primaire de chaque utilisateur
     */
    public static void setLocalUserData(Context context, User user){
        setStringPreference(context, Constants.USER_PREFS, "id", user.getId());
        setStringPreference(context, Constants.USER_PREFS, "name", user.getName());
        setStringPreference(context, Constants.USER_PREFS, "nationalNumber", user.getNationalNumber());
        setStringPreference(context, Constants.USER_PREFS, "countryCode", user.getCountryCode());
        setStringPreference(context, Constants.USER_PREFS, "number", user.getNumber());
        setStringPreference(context, Constants.USER_PREFS, "country", user.getCountry());
        setStringPreference(context, Constants.USER_PREFS, "email", user.getEmail());
        setStringPreference(context, Constants.USER_PREFS, "connectedWith", user.getConnectedWith());
    }

    /**
     * Récupération des données locales
     * @param context / Contexte
     * @return user / Instance regroupant les données primaire de chaque utilisateur
     */
    public static User getLocalUserData(Context context){
        return new User(
                getStringPreference(context, Constants.USER_PREFS, "id", FirebaseAuth.getInstance().getUid()),
                getStringPreference(context, Constants.USER_PREFS, "name", EMPTY),
                getStringPreference(context, Constants.USER_PREFS, "nationalNumber", EMPTY),
                getStringPreference(context, Constants.USER_PREFS, "countryCode", EMPTY),
                getStringPreference(context, Constants.USER_PREFS, "number", EMPTY),
                getStringPreference(context, Constants.USER_PREFS, "country", EMPTY),
                getStringPreference(context, Constants.USER_PREFS, "email", EMPTY),
                getStringPreference(context, Constants.USER_PREFS, "connectedWith", EMPTY)
        );
    }

    /*---------------------- First Launch ----------------------*/

    /**
     * Recuperation de la variable préférentielle booléenne de la première ouverture
     * @param context / Contexte
     * @return valeur de la valeur préférentielle booléenne
     */
    public static boolean isFirstLaunch(Context context){
        return getBooleanPreference(context, LAUNCH_PREFS, "firstLaunch", true);
    }

    /**
     * Edition de la valeur préférentielle booléenne
     * @param context / Contexte
     */
    public static void setFirstLaunchDone(Context context){
        setBooleanPreference(context, LAUNCH_PREFS, "firstLaunch", false);
    }

    /*---------------------- InfoActivity ----------------------*/

    public static void saveInfoData(Context context, String nationalNumber, String countryCode, String number, String country){
        setStringPreference(context, INFO_PREFS, "nationalNumber", nationalNumber);
        setStringPreference(context, INFO_PREFS, "countryCode", countryCode);
        setStringPreference(context, INFO_PREFS, "number", number);
        setStringPreference(context, INFO_PREFS, "country", country);
    }

    public static String[] getInfoData(Context context){
        return new String[]{
                getStringPreference(context, INFO_PREFS, "nationalNumber", null),
                getStringPreference(context, INFO_PREFS, "countryCode", null),
                getStringPreference(context, INFO_PREFS, "number", null),
                getStringPreference(context, INFO_PREFS, "country", null)
        };
    }

    /*---------------------- Image Processing ----------------------*/

    /**
     * Conversion Drawable en Bitmap
     * @param drawable / Drawable
     * @return Bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * Convertisseur d'URI en Bitmap
     * @param c / Context
     * @param uri / Uri
     * @return Bitmap
     */
    public static Bitmap Uri2Bitmap(Context c, Uri uri){
        try {

            return MediaStore.Images.Media.getBitmap(c.getContentResolver() , uri);
        }
        catch (Exception e) {
            //handle exception
            Toast.makeText(c, R.string.loading_failure, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return null;
        }
    }

    /*---------------------- Cryptographic Tools ----------------------*/

    /**
     * Mélangeur de tableau
     * @param array / Tableau d'entier à mélanger
     */
    private static void shuffleArray(int[] array)
    {
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    /**
     * Générateur de clé de cryptage
     * @return Clé de cryptage
     */
    public static String getCryptKey(){
        int[] tempKeys = new int[Constants.START.length()];
        StringBuilder result = new StringBuilder(EMPTY);

        for (int i = 0; i < Constants.START.length(); i++){ tempKeys[i] =  i; }

        shuffleArray(tempKeys);

        for (int i = 0; i < Constants.START.length(); i++){
            if (i == Constants.START.length() - 1){
                result.append(tempKeys[i]);
            }else {
                result.append(tempKeys[i]).append("-");
            }
        }

        return result.toString();
    }

    /**
     * Cryptage de chaîne de caractère
     * @param str / Séquence à crypter
     * @param key / Clé de cryptage
     * @return { Séquence cryptée, Clé de cryptage}
     */
    public static String[] encrypt(@NonNull String str, @Nullable String key){
        StringBuilder result = new StringBuilder(EMPTY);
        if (key == null){
            key = getCryptKey();
        }

        for (int y = 0; y < str.length(); y++){

            if (Constants.START.indexOf(str.charAt(y)) != -1){
                String[] strKeys = key.split("-");

                result.append(Constants.ARRIVAL.charAt(Integer.valueOf(strKeys[Constants.START.indexOf(str.charAt(y))])));

            }else {

                result.append(str.charAt(y));

            }

        }

        return new String[]{result.toString(), key};
    }

    /**
     * Décryptage de chaîne de caractère
     * @param str / Séquence à décrypter
     * @param key / Clé de cryptage
     * @return Séquence décryptée
     */
    public static String decrypt(@NonNull String str, @NonNull String key){
        StringBuilder result= new StringBuilder(EMPTY);
        String[] strKeys = key.split("-");

        for (int i = 0; i < str.length(); i++){

            if (Constants.ARRIVAL.indexOf(str.charAt(i)) != -1){

                for (int y = 0; y < strKeys.length; y++){
                    if (Integer.valueOf(strKeys[y]) == Constants.ARRIVAL.indexOf(str.charAt(i))){

                        result.append(Constants.START.charAt(y));

                    }
                }

            }else{

                result.append(str.charAt(i));

            }

        }
        return result.toString();
    }

    /**
     * Génère une clé alphanumérique aléatoire supportant la casse
     * @param length / longueur de la clé
     * @return Chaîne de sortie
     */
    public static String generateKey(int length){
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {

            int z = new Random().nextInt(3);
            switch (z) {
                case 0:
                    password.append(LETTER_RES[new Random().nextInt(LETTER_RES.length)].toUpperCase());
                    break;
                case 1:
                    password.append(LETTER_RES[new Random().nextInt(LETTER_RES.length)]);
                    break;
                case 2:
                    password.append(NUMBER_RES[new Random().nextInt(NUMBER_RES.length)]);
                    break;
            }

        }

        return password.toString();
    }

    /*---------------------- Permission ----------------------*/

    /**
     * Intent de galerie
     * @param activity / Activité de provenance
     */
    public static void choosePhotoFromGallery(Activity activity) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        activity.startActivityForResult(galleryIntent, GALLERY);
    }

    /**
     * Intent de caméra
     * @param activity / Activité de provenance
     */
    public static void takePhotoFromCamera(Activity activity) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivityForResult(intent, CAMERA);
    }

    /**
     * Ecriture de Bitmap en format JPEG
     * @param context / contexte
     * @param myBitmap / fichier bitmap
     * @return URI
     */
    public static Uri saveImage(Context context, Bitmap myBitmap, @Nullable String path) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File wallpaperDirectory;
        if (path != null && !path.isEmpty()){

            wallpaperDirectory = new File(path);

        }else {

            wallpaperDirectory = new File(IMAGE_DIRECTORY);

        }

        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            boolean mkdirsTask = wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            Uri uri = FileProvider.getUriForFile(context , BuildConfig.APPLICATION_ID + ".provider", f);
            boolean createNewFileTask = f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(context,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return uri;
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return Uri.EMPTY;
    }

    /*---------------------- Random Tools ----------------------*/

    /**
     * Recupération des premières lettres d'une chaîne de caractère
     * @param string / chaîne de départ
     * @param uppercase / détermine la casse de la chaîne de sortie
     * @return chaîne de sortie
     */
    public static String getFirstLetters(String string, Boolean uppercase){
        String[] sections = string.trim().split(" ");
        StringBuilder stringBuilder = new StringBuilder();
        String result;

        for (String s: sections){ stringBuilder.append(s.substring(0, 1)); }

        if (stringBuilder.length() > 2){
            result = String.valueOf(stringBuilder.charAt(0)) + stringBuilder.charAt(stringBuilder.length() - 1);
        }else {
            result = stringBuilder.toString();
        }

        if (!uppercase){ return result; }

        return result.toUpperCase();
    }

    /**
     * Recupération des premières lettres d'une chaîne de caractère
     * @param string / Chaîne de départ
     * @return chaîne de sortie
     */
    public static String getFirstLetters(String string){
        String[] sections = string.trim().split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s: sections){ stringBuilder.append(s.substring(0, 1)); }

        if (stringBuilder.length() > 2){
            return String.valueOf(stringBuilder.charAt(0)) + stringBuilder.charAt(stringBuilder.length() - 1);
        }

        return stringBuilder.toString().toUpperCase();
    }

    /**
     * Extraction du code hexadécimal d'une couleur
     * @param color / Nombre entier
     * @return chaîne héxadécimale
     */
    public static String getColorHex(int color){
        return String.format("#%06X", (0xFFFFFF & color));
    }

    /**
     * Raccourcissement des chaînes de caractère trop longue
     * @param text / Chaîne d'entrée
     * @param size / Taille maximale de la chaîne de sortie
     * @return Chaîne de sortie
     */
    public static String cutLongText(String text, int size){
        if (text.length() > size){
            return text.trim().substring(0, size - 3)+"...";
        }

        return text;
    }

    /**
     * Casse majuscule pour les premières lettres de chaque mot de la chaîne
     * @param str / Chaîne d'entrée
     * @return Chaîne de sortie
     */
    public static String capitalize(String str){
        final String SPACE = " ";
        String[] tab = str.trim().split(SPACE);
        StringBuilder result = new StringBuilder();

        int i = 0;
        for (String t: tab){

            if (i == 0){
                result.append(t.substring(0, 1).toUpperCase()).append(t.substring(1).toLowerCase());
            }else {
                result.append(SPACE).append(t.substring(0, 1).toUpperCase()).append(t.substring(1).toLowerCase());
            }
            i++;
        }

        return result.toString();
    }

    /**
     * Enlève les doublons
     * @param input / Liste d'entrée
     * @return Liste de sortie
     */
    static List<String> removeStringRedundancies(List<String> input){
        List<String> output = new ArrayList<>();

        if (input != null) {
            for (String s : input) {
                if (!output.contains(s)){
                    output.add(s);
                }
            }

            return output;
        }

        return null;
    }

    /**
     * Retire une séquence d'une chaîne de caractère
     * @param sequency / séquence à retirer
     * @param input / chaîne d'entrée
     * @return chaîne de sortie
     */
    static String removeCharSequencies(String sequency, String input){
        if (!input.isEmpty() && !sequency.isEmpty()) {

            String[] temp = input.split(sequency);
            StringBuilder output = new StringBuilder();

            for (String s : temp) {
                output.append(s);
            }

            return output.toString();

        }

        return null;
    }

    /**
     * Vérifie si une chaîne de caractère est susceptible d'être convertie en entier
     * @param str / Chaîne à vérifier
     * @return booleen
     */
    public static boolean isNumeric(String str)
    {
        try
        {
            int d = Integer.parseInt(str);
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    /**
     * Conversion dp en px
     * @param dp / Valeur en dp (int) à convertir
     * @return valeur en px (int)
     */
    public static int dpToPx(int dp) {

        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);

    }

    /**
     * Conversion px en dp
     * @param px / Valeur en px (int) à convertir
     * @return valeur en dp (int)
     */
    public static int pxToDp(int px) {

        return (int) (px / Resources.getSystem().getDisplayMetrics().density);

    }

    /**
     * Conversion dp en px
     * @param context / le context
     * @param valueInDp / valeur en dp (float) à convertir
     * @return valeur en px (float)
     */
    public static float dpToPx(Context context, float valueInDp) {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, context.getResources().getDisplayMetrics());

    }

    /**
     * Conversion px en dp
     * @param context / le context
     * @param valueInPx / valeur en dp (float) à convertir
     * @return valeur en dp (float)
     */
    public static float pxToDp(Context context, float valueInPx){

        return valueInPx / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);

    }

    /**
     * Verifie l'etat du clavier
     * @param contentView / La vue racine
     * @return un booleen
     */
    public static boolean isKeyboardShown(View contentView){
        Rect r = new Rect();
        contentView.getWindowVisibleDisplayFrame(r);
        int screenHeight = contentView.getRootView().getHeight();

        // r.bottom is the position above soft keypad or device button.
        // if keypad is shown, the r.bottom is smaller than that before.
        int keypadHeight = screenHeight - r.bottom;

        return keypadHeight > screenHeight * 0.15;
    }

    /**
     * Check si le fichier est une image
     * @param path / Chemin vers le fichier
     * @return booleen
     */
    public static boolean isImage(String path){
        String mimeType = URLConnection.guessContentTypeFromName(path);

        return mimeType != null && mimeType.startsWith("image");
    }

    /**
     * Check si le fichier est une video
     * @param path / Chemin vers le fichier
     * @return booleen
     */
    public static boolean isVideo(String path){
        String mimeType = URLConnection.guessContentTypeFromName(path);

        return mimeType != null && mimeType.startsWith("video");
    }

    /**
     * Extrait l'image d'aperçu d'une video locale
     * @param path / Chemin du fichier vidéo
     * @return image au format Bitmap
     */
    public static Bitmap getThumbnailFromVideoPath(String path){
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
    }

    /**
     * Extrait l'image d'aperçu d'une video sur un serveur distant
     * @param videoUrl / Lien du fichier vidéo
     * @return image au format Bitmap
     * @throws Throwable / ...
     */
    public static Bitmap getThumbnailFromVideoUrl(String videoUrl)
            throws Throwable {
        Bitmap bitmap;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();

            mediaMetadataRetriever.setDataSource(videoUrl, new HashMap<>());

            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in getThumbnailFromVideoUrl(String videoUrl)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    /**
     * Extrait le nom du fichier à partir de son URI
     * @param context / Le contexte
     * @param uri / URI du fichier
     * @return Nom du fichier
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public static String getNameFromUri(Context context, Uri uri){
        String name = null;
        if (uri != null) {
            File myFile = new File(uri.toString());

            if (uri.toString().startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = context.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            } else if (uri.toString().startsWith("file://")) {
                name = myFile.getName();
            }

            return name;
        }

        return null;
    }

    /**
     * Extraction du nom à partir du chemin vers le fichier
     * @param path / chemin vers le fichier
     * @return / nom du fichier
     */
    public static String getNameFromPath(String path){

        return path.substring(path.lastIndexOf("/") + 1);

    }

    /**
     * Copie de fichier ou de dossier
     * @param srcDir / lien du fichier source
     * @param dstDir / lien du répertoire cible
     */
    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String[] files = src.list();
                for (String file : files) {
                    String src1 = (new File(src, file).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFileMethodA(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Copie de fichier
     * @param sourceFile / Fichier source
     * @param destFile / Fichier de destination
     * @throws IOException / ...
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private static void copyFileMethodA(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            boolean mkdirsTask = destFile.getParentFile().mkdirs();
        }

        if (!destFile.exists()) {
            boolean createNewFileTask = destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    /**
     * Copie de fichier
     * @param sourceLocation / Fichier source
     * @param targetLocation / Fichier de destination
     * @throws IOException / ...
     */
    private static void copyFileMethodB(File sourceLocation, File targetLocation) throws IOException {
        if(sourceLocation.exists()){

            if (!targetLocation.getParentFile().exists()) {
                boolean mkdirsTask = targetLocation.getParentFile().mkdirs();
            }

            if (!targetLocation.exists()) {
                 boolean createNewFileTask = targetLocation.createNewFile();
            }

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();

        }
    }

    /**
     * Extracteur de Path à partir d'une Uri
     * @param context / Contexte
     * @param uri / Uri
     * @return Path
     */
    @SuppressWarnings("NewApi")
    public static String getPathFromUri(Context context, Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else
            if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Création d'un fichier .nomedia
     * @param path / Chemin vers le répertoire
     * @return resultat de l'opération
     */
    public static boolean createNomediaFile(String path){
        return createFile(path, ".nomedia");
    }

    /**
     * Création d'un fichier vide
     * @param path / Chemin vers le répertoire
     * @param fileName / Nom du fichier à créer
     * @return résultat de l'opération
     */
    public static boolean createFile(String path, String fileName){
        File filePath = new File(path);

        if (!filePath.exists()){ boolean mkdirsTask = filePath.mkdirs(); }

        File file = new File(filePath, fileName);

        try {

            return file.createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Crée un fichier temporaire
     * @param path / chemin vers le fichier
     * @param fileName / Nom du fichier
     * @return fichier créé
     */
    public static File createTempFile(String path, String fileName){
        File filePath = new File(path);

        if (!filePath.exists()){ boolean mkdirsTask = filePath.mkdirs(); }

        try {

            return File.createTempFile(fileName.split("\\.")[0], "."+fileName.split("\\.")[1], filePath);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Extrait la longueur d'un fichier audio ou video
     * @param context / Contexte
     * @param uri / URI du fichier
     * @return la longueur du média
     */
    public static String getMediaFileLength(Context context, Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        retriever.setDataSource(context, uri);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInMillisec = Long.parseLong(time );

        retriever.release();

        return getTimeFromMilliseconds(timeInMillisec);
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

    /**
     * Retourne la taille du fichier
     * @param path / Chemin vers le fichier
     * @return la taille du fichier
     */
    public static String getFileSizeFromPath(String path){
        File file = new File(path);

        return humanReadableByteCount(file.length(), true);
    }

    /**
     * Convertisseur de taille de fichier
     * @param bytes / taille en bytes
     * @param si / Kilo ou Kibi , Mega ou Mebi , Giga Gibi ?
     * @return la taille du fichier convertie
     */
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? EMPTY : "i");

        return String.format(Locale.US,"%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Génère un chiffre unique à partir d'une chaîne de caractères
     * @param in / Chaîne d'entrée
     * @return chiffre unique
     */
    public static int getDigitFromString(String in){

        String length = String.valueOf(in.length());
        int l = length.length();

        while (l > 1){

            int sum = 0;

            for (int i = 0; i < l; i++){
                sum += Character.getNumericValue(length.charAt(i));
            }

            length = String.valueOf(sum);

            l = length.length();
        }

        return Integer.valueOf(length);

    }

    /**
     * Convertisseur chemin en URI à partir du provider
     * @param context / Contexte
     * @param path / Chemin vers le fichier
     * @return Uri généré à partir du provider
     */
    public static Uri getUriFromPath(Context context, String path){
        return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", new File(path));
    }

    /**
     * Change les paramètres marges d'une View
     * @param v / Vue
     * @param l / Marge gauche
     * @param t / Marge haute
     * @param r / Marge droite
     * @param b / Marge basse
     */
    public static void setMargins(View v, int l, int t, int r, int b){
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    /**
     * Retourne la position absolue d'une chaîne dans une liste
     * @param str / chaîne
     * @param list / liste
     * @param b / ordre croissant ou decroissant
     * @return position absolue
     */
    public static int getStringAbsolutePosition(String str, List<String> list, boolean b){

        if (!list.contains(str)){ list.add(str); }

        if (b){
            Collections.sort(list);
        }else {
            Collections.reverse(list);
        }

        return list.indexOf(str);
    }

    /**
     * Donne le focus à un champ de texte
     * @param context / contexte
     * @param editText / Champ de texte
     */
    public static void requestEditTextFocus(Context context, EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Retire le focus à un champ de texte
     * @param context / contexte
     * @param editText / Champ de texte
     */
    public static void removeEditTextFocus(Context context, EditText editText){
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Fermeture du clavier
     * @param activity / Activité source
     */
    public static void hideKeyboard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = activity.getCurrentFocus();
        if (view == null){
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Active ou désactive un groupe de vues
     * @param view / Groupe de vue
     * @param enabled / Booleen
     */
    public static void enableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                enableView(child, enabled);
            }
        }
    }

    /**
     * Met toute la chaîne de caractère en gras
     * @param s / chaîne en entrée
     * @return chaîne en sortie
     */
    public static SpannableStringBuilder getBoldString(String s){
        SpannableStringBuilder str = new SpannableStringBuilder(s);
        str.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

    /**
     * Met une portion indiquée de la chaîne en gras
     * @param s / chaîne en entrée
     * @param start / position de départ
     * @param end / position d'arrivée
     * @return chaîne en sortie
     */
    public static SpannableStringBuilder getBoldString(String s, int start, int end){
        SpannableStringBuilder str = new SpannableStringBuilder(s);
        str.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return str;
    }

    /**
     * Change la couleur des drawables d'un textview
     * @param textView / TextView
     * @param color / Couleur
     */
    public static void setTextViewDrawableColor(TextView textView, int color){
        for (Drawable drawable : textView.getCompoundDrawables()){
            if (drawable != null){
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
            }
        }
    }

    /**
     * Manipule la clarté de la couleur issue de la palette
     * @param color coleur fournie
     * @param factor facteur d'assombrissement de la couleur
     * @return la couleur manipulée sous forme d'entier
     */
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    /**
     * Compactage de nombre
     * @param in / chaîne d'entrée
     * @return / chaîne de sortie compactée
     */
    public static String compactNumber(String in){
        int length = in.length();
        String out = in;
        String suffix;
        int limit;

        if (length > 3 && length <= 6){

            limit = length - 3; suffix = "K";

        }else if (length > 6 && length <= 9){

            limit = length - 6; suffix = "M";

        }else if (length > 9){

            limit = length - 9; suffix = "Md";

        }else {
            return out;
        }

        out = (in.substring(limit, limit + 1).equals("0")
                ? in.substring(0, limit)
                : in.substring(0, limit) + "." + in.substring(limit, limit + 1))
                + suffix;

        return out;
    }

    /**
     * Compactage de nombre
     * @param in / Nombre d'entrée
     * @return / chaîne de sortie compactée
     */
    public static String compactNumber(int in){

        return compactNumber(String.valueOf(in));

    }

}
