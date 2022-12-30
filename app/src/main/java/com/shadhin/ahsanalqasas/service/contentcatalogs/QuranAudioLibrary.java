package com.shadhin.ahsanalqasas.service.contentcatalogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.util.Log;

import com.shadhin.ahsanalqasas.AyatModel;
import com.shadhin.ahsanalqasas.R;
import com.shadhin.ahsanalqasas.service.MusicService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class QuranAudioLibrary {
    private static QuranAudioLibrary instance;
    private Context mContext;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private final TreeMap<String, MediaMetadataCompat> chapter = new TreeMap<>();
    private final HashMap<String, Integer> albumRes = new HashMap<>();
    private final HashMap<String, String> chapterFileName = new HashMap<>();


    @SuppressLint("Range")
    private QuranAudioLibrary(Context context){
        this.mContext = context;
        //Prevent to make another Instance
        Log.d("LoadingJson", "QuranAudioLibrary: start singletone");
        try {
            InputStream jsonAsset = context.getAssets().open("allaudio.gz.json");
            GZIPInputStream decompressStream = new GZIPInputStream(jsonAsset);
            BufferedReader jsonBufferReader = new BufferedReader(new InputStreamReader(decompressStream, "UTF-8"));
            String str ;
            StringBuffer jsonTxt = new StringBuffer();
            while ((str = jsonBufferReader.readLine()) != null){
                jsonTxt.append(str);
            }
            jsonObject = new JSONObject(jsonTxt.toString());
            jsonArray = jsonObject.getJSONArray("audiofiles");

            File database = mContext.getDatabasePath("extractDb.db");
            SQLiteDatabase sqldb = SQLiteDatabase.openDatabase(database.getPath(), null, SQLiteDatabase.OPEN_READONLY);

            for(int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject tempJson = jsonArray.getJSONObject(i);
                Cursor cursor = sqldb.rawQuery("SELECT name, name_ar, meaning, ayat, place From sura WHERE id=\""+tempJson.getInt("chapter_id")+"\"", null);
                cursor.moveToFirst();
                createMediaMetadataCompat(
                        "surah_"+tempJson.getInt("chapter_id"),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("meaning")),
                        "Quran.com",
                        "gapless",
                        tempJson.getInt("duration"),
                        TimeUnit.MILLISECONDS,
                        tempJson.getInt("chapter_id")+".mp3",
                        R.drawable.album_jazz_blues,
                        "names"
                );
            }

            Log.d("LoadingJson", "QuranAudioLibrary: Json array Lenght: "+jsonArray.length());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public static QuranAudioLibrary getInstance() {
        if(instance == null)
        {
            throw new IllegalStateException("QuranAudioLibrary not Initialize");
        }
        return instance;
    }

    public static void init(Context context){
        if(instance == null)
            instance = new QuranAudioLibrary(context);
    }

    public static String getRoot() {
        return "root";
    }

    public String getMusicFilename(String mediaId) {
        return chapterFileName.containsKey(mediaId) ? chapterFileName.get(mediaId) : null;
    }


    public void values() {
        Log.d("LoadingJson", "QuranAudioLibrary: value"+jsonObject.toString());
    }


    public List<MediaBrowserCompat.MediaItem> getMediaItems() {
        List<MediaBrowserCompat.MediaItem> result = new ArrayList<>();

        for (MediaMetadataCompat metadata : chapter.values()) {
            result.add(
                    new MediaBrowserCompat.MediaItem(
                            metadata.getDescription(), MediaBrowserCompat.MediaItem.FLAG_PLAYABLE));
        }
        return result;
    }

    public MediaMetadataCompat getMetadata(Context context, String mediaId) {
        MediaMetadataCompat metadataWithoutBitmap = chapter.get(mediaId);
//        Bitmap albumArt = getAlbumBitmap(context, mediaId);

        // Since MediaMetadataCompat is immutable, we need to create a copy to set the album art.
        // We don't set it initially on all items so that they don't take unnecessary memory.
        MediaMetadataCompat.Builder builder = new MediaMetadataCompat.Builder();
        for (String key :
                new String[]{
                        MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                        MediaMetadataCompat.METADATA_KEY_ALBUM,
                        MediaMetadataCompat.METADATA_KEY_ARTIST,
                        MediaMetadataCompat.METADATA_KEY_GENRE,
                        MediaMetadataCompat.METADATA_KEY_TITLE
                }) {
            builder.putString(key, metadataWithoutBitmap.getString(key));
        }
        builder.putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                metadataWithoutBitmap.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
//        builder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, albumArt);
        return builder.build();
    }

    private void createMediaMetadataCompat(
            String mediaId,
            String title,
            String artist,
            String album,
            String genre,
            long duration,
            TimeUnit durationUnit,
            String musicFilename,
            int albumArtResId,
            String albumArtResName) {
        chapter.put(
                mediaId,
                new MediaMetadataCompat.Builder()
                        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaId)
                        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
                        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
                        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                                TimeUnit.MILLISECONDS.convert(duration, durationUnit))
                        .putString(MediaMetadataCompat.METADATA_KEY_GENRE, genre)
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(
                                MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                                getAlbumArtUri(albumArtResName))
                        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                        .build());
        albumRes.put(mediaId, albumArtResId);
        chapterFileName.put(mediaId, musicFilename);
    }

    private String getAlbumArtUri(String albumArtResName) {
        return "";
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    public Bitmap getAlbumBitmap(MusicService mService, String mediaId) {
        return textAsBitmap(mediaId, 20, Color.BLACK);
    }

    public long getSeekto(int chapter, int ayat) {
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject chap = jsonArray.getJSONObject(i);
                    if(chap.getInt("chapter_id") == chapter)
                    {
                        JSONArray timing = chap.getJSONArray("verse_timings");
                        for (int j = 0; j <timing.length(); j++) {
                            JSONObject verse = timing.getJSONObject(j);
                            if(verse.getString("verse_key").equals(""+chapter+":"+ayat))
                            {
                                return verse.getLong("timestamp_from");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return 0;
    }
}
