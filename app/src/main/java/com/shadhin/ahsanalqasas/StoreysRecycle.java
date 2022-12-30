package com.shadhin.ahsanalqasas;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.MetricAffectingSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.shadhin.ahsanalqasas.client.MediaBrowserHelper;
import com.shadhin.ahsanalqasas.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class StoreysRecycle extends RecyclerView.Adapter<StoreysRecycle.ViewHolder> {
    private ArrayList<AyatModel> ayats;
    private Context context;
    private MediaPlayer mediaPlayer;
    private MediaBrowserCompat.ConnectionCallback connectionCallback;
    private boolean mIsPlaying;
    private MediaBrowserHelper mMediaBrowserHelper;
    private PlayAyatClickListener playAyatClickListener;
    private DbStore db;

    public StoreysRecycle(ArrayList<AyatModel> ayats){
        this.ayats = ayats;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context  = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View StoryView = inflater.inflate(R.layout.sigle_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(StoryView);
        mMediaBrowserHelper = new MediaBrowserHelper(context, MusicService.class);
        mMediaBrowserHelper.registerCallback(new MediaBrowserListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(StoreysRecycle.ViewHolder holder, int position) {
        AyatModel ayat = ayats.get(position);
        Spannable spannable = new SpannableString(ayat.getAyah_ar()+ayat.getArabicAyat_no()+'\n'
                +ayat.getAyah_trans()+"\n"
                +ayat.getAyah_bn()
        );
        spannable.setSpan(new BackgroundColorSpan(Color.BLUE),
                ayat.pay_s, ayat.pay_e,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );


        spannable.setSpan(new RelativeSizeSpan(2),
                0, ayat.getAyah_ar().length()+ayat.getArabicAyat_no().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannable.setSpan(new CustomTypeface(Typeface.create(ResourcesCompat.getFont(this.context,R.font.uthmatic_end),
                Typeface.NORMAL)), ayat.getAyah_ar().length(),
                ayat.getAyah_ar().length()+ayat.getArabicAyat_no().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(new ForegroundColorSpan(Color.GREEN),
                ayat.getAyah_ar().length()+ayat.getArabicAyat_no().length()+1,
                ayat.getAyah_ar().length()+ayat.getArabicAyat_no().length()+1+ayat.getAyah_trans().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        spannable.setSpan(new ForegroundColorSpan(Color.YELLOW),
                ayat.getAyah_ar().length()+ayat.getArabicAyat_no().length()+1+ayat.getAyah_trans().length()+1,
                ayat.getAyah_ar().length()+ayat.getArabicAyat_no().length()+1+ayat.getAyah_trans().length()+1+ayat.getAyah_bn().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        holder.storyText.setText(spannable);
        db = DbStore.getDbStoreInstant();
        holder.ayatInfo.setText(db.getSuraName(ayat.getSurah_no())+ " "+ayat.getSurah_no()+":"+ ayat.getAyat_no());
        holder.button.setOnClickListener(new playClickListener(position,ayat.getSurah_no(), ayat.getAyat_no()));
    }

    @Override
    public int getItemCount() {
        return ayats.size();
    }

    public void setPlayAyatClickListener(PlayAyatClickListener playAyatClickListener) {
        this.playAyatClickListener = playAyatClickListener;
    }

    public void setHilight(int index, int start, int end){
        AyatModel ayat = ayats.get(index);
        ayat.pay_s = start;
        ayat.pay_e = end;
        ayats.set(index, ayat);
        this.notifyItemChanged(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storyText;
        TextView ayatInfo;
        Button button;
        public ViewHolder(View itemView) {
            super(itemView);
            storyText = itemView.findViewById(R.id.ayatText);
            ayatInfo = itemView.findViewById(R.id.ayatinfo);
            button = itemView.findViewById(R.id.play);
        }
    }

    public class CustomTypeface extends MetricAffectingSpan{
        private Typeface typeface;

        public CustomTypeface(Typeface typeface){
            this.typeface = typeface;
        }

        @Override
        public void updateMeasureState(@NonNull TextPaint textPaint) {
            textPaint.setTypeface(typeface);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setTypeface(typeface);
        }
    }


    public class playClickListener implements View.OnClickListener {
        private int position;
        private int surah_no;
        private int ayat_no;
        MediaBrowserCompat mediaBrowserCompat;

        public playClickListener(int position, int surah_no, int ayat_no) {
            this.position = position;
            this.surah_no = surah_no;
            this.ayat_no = ayat_no;
        }

        @Override
        public void onClick(View view) {
            if(playAyatClickListener != null)
                playAyatClickListener.onClick(surah_no, ayat_no);

            int wordCount = ayats.get(this.position).getAyah_ar().split(" ").length;
            Toast.makeText(context, "Surah word Count: "+wordCount, Toast.LENGTH_SHORT).show();
        }
    }


    private class MediaBrowserListener extends MediaControllerCompat.Callback {
        private long duration;
        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat playbackState) {
            mIsPlaying = playbackState != null &&
                    playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
            if(mIsPlaying){
                long start = playbackState != null ? playbackState.getPosition() : 0;
//                long max = playbackState != null ? getMax: 0;

                ValueAnimator valueAlm = ValueAnimator.ofFloat(0, duration).setDuration(duration);
                valueAlm.setInterpolator(new LinearInterpolator());
                valueAlm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator) {

                    }
                });
                valueAlm.start();
                Log.d("LoadMedia", "onPlaybackStateChanged: "+start);
            }
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
