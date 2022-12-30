package com.shadhin.ahsanalqasas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.Toast;

import com.shadhin.ahsanalqasas.client.MediaBrowserHelper;
import com.shadhin.ahsanalqasas.service.MusicService;
import com.shadhin.ahsanalqasas.service.contentcatalogs.QuranAudioLibrary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuranStory extends AppCompatActivity {

    private MediaBrowserHelper mMediaBrowserHelper;
    private boolean mIsPlaying;

    private ImageButton playBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quran_story);
        Intent intent = getIntent();
        setTitle(intent.getStringExtra("title"));
        String ayatsEn = intent.getStringExtra("ayats");
        Log.d("ayats string", "onCreate: "+ayatsEn);

        //play ayat initialize
        mMediaBrowserHelper = new MediaBrowserHelper(this, MusicService.class);
        mMediaBrowserHelper.registerCallback(new QuranStory.MediaBrowserListener());
        ArrayList<AyatModel> ayatModels = DbStore.getDbStoreInstant().getAyatModelFrom(ayatsEn);
        //recycle view
        playBtn = findViewById(R.id.playBtn);
        RecyclerView recView=(RecyclerView) findViewById(R.id.quran_story_rv);

        StoreysRecycle storeysRecycle = new StoreysRecycle(ayatModels);
        storeysRecycle.setPlayAyatClickListener(new PlayAyatClickListener() {
            @Override
            public void onClick(int chapter, int ayat) {
                Intent inten = new Intent(QuranStory.this, Tafsir.class);
                inten.putExtra("sura", chapter);
                inten.putExtra("ayat", ayat);
                startActivity(inten);
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaControllerCompat controller = mMediaBrowserHelper.getMediaController();

                int chapter = ayatModels.get(0).getSurah_no();
                int ayat = ayatModels.get(0).getAyat_no();

                mMediaBrowserHelper.getTransportControls().stop();
                List<MediaBrowserCompat.MediaItem> children = mMediaBrowserHelper.getLoadedChildren();
                for (MediaBrowserCompat.MediaItem item: children){
                    if (item.getMediaId().equals("surah_"+chapter))
                    {
                        controller.addQueueItem(item.getDescription());
                    }else{
                        controller.removeQueueItem(item.getDescription());
                    }
                }

                controller.getTransportControls().prepare();
                long seekto = QuranAudioLibrary.getInstance().getSeekto(chapter, ayat);

                mMediaBrowserHelper.getTransportControls().play();
                mMediaBrowserHelper.getTransportControls().seekTo(seekto);
                playBtn.setImageResource(R.drawable.ic_baseline_pause_24);
            }
        });

//        storeysRecycle.setPlayAyatClickListener(new PlayAyatClickListener() {
//            @Override
//            public void onClick(int chapter, int ayat) {
//                MediaControllerCompat controller = mMediaBrowserHelper.getMediaController();
//                mMediaBrowserHelper.getTransportControls().stop();
//                List<MediaBrowserCompat.MediaItem> children = mMediaBrowserHelper.getLoadedChildren();
//                for (MediaBrowserCompat.MediaItem item: children){
//                    if (item.getMediaId().equals("surah_"+chapter))
//                    {
//                        controller.addQueueItem(item.getDescription());
//                    }else{
//                        controller.removeQueueItem(item.getDescription());
//                    }
//                }
//
//                controller.getTransportControls().prepare();
//                long seekto = QuranAudioLibrary.getInstance().getSeekto(chapter, ayat);
//
//                mMediaBrowserHelper.getTransportControls().play();
//                mMediaBrowserHelper.getTransportControls().seekTo(seekto);
//                Toast.makeText(QuranStory.this, "play "+chapter+" : "+ayat + "seek: "+seekto, Toast.LENGTH_SHORT).show();
//            }
//        });
        recView.setLayoutManager(new LinearLayoutManager(this));
        recView.setAdapter(storeysRecycle);

    }

    @Override
    public void onStart() {
        super.onStart();
        mMediaBrowserHelper.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        mMediaBrowserHelper.onStop();
    }

    public class MediaBrowserListener extends MediaControllerCompat.Callback {
        private long duration;
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
            if (mediaMetadata == null) {
                return;
            }

            Log.d("LoadMedia", "onMetadataChanged: "+mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            duration = mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        }

        @Override
        public void onSessionDestroyed() {
            super.onSessionDestroyed();
        }

        @Override
        public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
            super.onQueueChanged(queue);
        }
    }
}