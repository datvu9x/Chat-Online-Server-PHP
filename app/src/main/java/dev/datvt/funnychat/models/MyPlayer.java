package dev.datvt.funnychat.models;

import android.content.Context;
import android.media.MediaPlayer;

import dev.datvt.funnychat.R;

/**
 * Created by datvt on 4/24/2016.
 */
public class MyPlayer {
    private MediaPlayer mediaPlayer;

    public MyPlayer(Context context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.see_you_again);
        mediaPlayer.setLooping(true);
    }

    public void fastForward(int pos) {
        mediaPlayer.seekTo(pos);
    }

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
