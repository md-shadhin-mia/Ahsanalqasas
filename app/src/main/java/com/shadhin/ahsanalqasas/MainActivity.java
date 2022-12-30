package com.shadhin.ahsanalqasas;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shadhin.ahsanalqasas.client.MediaBrowserHelper;
import com.shadhin.ahsanalqasas.service.MusicService;
import com.shadhin.ahsanalqasas.service.contentcatalogs.QuranAudioLibrary;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class MainActivity extends AppCompatActivity {
    BDHelper bdHelper;
    private NotificationManager notificationManager;
    private MediaBrowserHelper mMediaBrowserHelper;
    private Boolean mIsPlaying;

    TextView seekview;
    SeekBar seekbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File database = getDatabasePath("extractDb.db");
        if(!database.exists()){
            try {
                exportDatabase("thequran.gz.db","extractDb.db");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        DbStore.init(this);
        ArrayList<String> storys= new ArrayList<>();
        storys.add("Surah Fatiha;1:1-1:7");
        storys.add("Story of Habil and Qabil;5:27-5:31");
        storys.add("Harut and Marut;2:101-2:103");
        storys.add("Dwellers of the Town;36:13-36:29");
        storys.add("Story of the Heifer;2:67-2:73");
        storys.add("Moses and AI-Khadir;18:60-18:82");
        storys.add("The Story of Qarun;28:76-8:83");
        storys.add("Bilqis(Queen of Sheba);27:20-27:44");
        storys.add("The Story of Saba'(Sheba);34:15-34:19");
        storys.add("Uzair(Ezra);2:259&9:30");
        storys.add("Dhul Qarnain;18:80-18:94");
        storys.add("Gog and Magog;18:94-18:98");
        storys.add("People of the Cave;18:9-18:26");
        storys.add("The Believer & The Disbeliever;18:32-18:44");
        storys.add("People of the Garden;68:17-68:33");
        storys.add("The Sabbath-Break;7:163-7:166,2:56&2:66");
        storys.add("Story of Luqman;31:12-31:19");
        storys.add("People of the Ditch;85:1-85:10");
        storys.add("Barsisa the Worshipper;59:16-59:17");
        storys.add("Owners of the Elephant;105:1-105:5");
        RecyclerView recView=(RecyclerView) findViewById(R.id.listOfQuranStory);
        recView.setLayoutManager(new LinearLayoutManager(this));
        ListOfStoryAdapter listOfStoryAdapter = new ListOfStoryAdapter(storys);
        recView.setAdapter(listOfStoryAdapter);
//        QuranAudioLibrary.init(this);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            QuranAudioLibrary.init(this);

        }else{
            requestStoragePermission();
        }




//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//            CreateNotification.createNotification(this,R.drawable.ic_baseline_play_arrow_24, 2, 3);
//        }
//        create
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

    }

    private void createChanel() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        MediaSessionCompat mediaSession = new MediaSessionCompat(this, "media");
        String NOTIFY_CH_ID = "shadhin_01";
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFY_CH_ID, "Play notification", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder;
        PendingIntent pendingIntentPlay = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class)
        , PendingIntent.FLAG_UPDATE_CURRENT);
        builder = new NotificationCompat.Builder(this, NOTIFY_CH_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Quran Story")
                .setContentText("New track")
                .setOnlyAlertOnce(true) //show notification for only first time
                .setShowWhen(false)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Previous", pendingIntentPlay)
                .addAction(R.drawable.ic_baseline_play_arrow_24, "Play", pendingIntentPlay)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", pendingIntentPlay)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(mediaSession.getSessionToken()))
                        .setPriority(NotificationCompat.PRIORITY_LOW);
        notificationManager.notify(1, builder.build());
    }

//    public void addUserToDb(View view) {
//        EditText namefild = findViewById(R.id.fildName);
//        EditText emailFild = findViewById(R.id.fildEmail);
//        EditText passwordFild = findViewById(R.id.fildpassword);
//
//        String name = namefild.getText().toString();
//        String email = emailFild.getText().toString();
//        String password = passwordFild.getText().toString();
//
//        bdHelper.insertUser(name, email, password);
//
//        Toast.makeText(this, "user added to database", Toast.LENGTH_SHORT).show();
//
//        namefild.setText("");
//        emailFild.setText("");
//        passwordFild.setText("");
//    }

    public void exportDatabase(String assetName, String datafilenmae) throws IOException {
            InputStream inputStream = getAssets().open(assetName);
            GZIPInputStream gis = new GZIPInputStream(inputStream);
            File file = getDatabasePath(datafilenmae);
            FileOutputStream fos = new FileOutputStream(file);
            final int Buffer_size = 1024*8;
            byte[] data = new byte[Buffer_size];
            int readed = -1;
            while ((readed = gis.read(data)) != -1){
                fos.write(data, 0, readed);
            }
            fos.close();
            gis.close();
            inputStream.close();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                QuranAudioLibrary.init(this);
            }
        }
    }

    //    public void viewStory(View view) {
//        Intent intent = new Intent(view.getContext(), QuranStory.class);
//        intent.putExtra("title","Story of Habil and Qabil" );
//        intent.putExtra("ayats", "5:27-5:31");
//        view.getContext().startActivity(intent);
//    }

    private class MediaBrowserConnection extends MediaBrowserHelper {
        private MediaBrowserConnection(Context context) {
            super(context, MusicService.class);
        }


        @Override
        protected void onChildrenLoaded(@NonNull String parentId,
                                        @NonNull List<MediaBrowserCompat.MediaItem> children) {
            super.onChildrenLoaded(parentId, children);

            final MediaControllerCompat mediaController = getMediaController();

            // Queue up all media items for this simple sample.
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mediaController.addQueueItem(mediaItem.getDescription());
            }

            // Call prepare now so pressing play just works.
            mediaController.getTransportControls().prepare();
        }
    }

    /**
     * Implementation of the {@link MediaControllerCompat.Callback} methods we're interested in.
     * <p>
     * Here would also be where one could override
     * {@code onQueueChanged(List<MediaSessionCompat.QueueItem> queue)} to get informed when items
     * are added or removed from the queue. We don't do this here in order to keep the UI
     * simple.
     */
}