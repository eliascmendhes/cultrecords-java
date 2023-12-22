package com.example.myapplication;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    private static final String STREAM_URL = "https://streaming.radio.co/sefac315e7/listen";
    private static final String STATUS_URL = "https://public.radio.co/stations/sefac315e7/status?v=1703130051008";

    private MediaPlayer mediaPlayer;
    private TextView tvNowPlaying;
    private Button btnPlayPause;
    private ImageView ivArtwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());

        tvNowPlaying = findViewById(R.id.tvNowPlaying);
        btnPlayPause = findViewById(R.id.btnPlayPause);
        ivArtwork = findViewById(R.id.ivArtwork);

        btnPlayPause.setOnClickListener(view -> {
            if (mediaPlayer.isPlaying()) {
                stopPlayback();
            } else {
                startPlayback();
            }
        });

        // Fetch and update current track information including artwork
        updateCurrentTrackInfo();
    }

    private void startPlayback() {
        try {
            mediaPlayer.setDataSource(STREAM_URL);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                mediaPlayer.start();
                btnPlayPause.setText("Pause");
                // Fetch and update current track information
                updateCurrentTrackInfo();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopPlayback() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        btnPlayPause.setText("Play");
        tvNowPlaying.setText("Now Playing: ");
        ivArtwork.setImageResource(android.R.color.transparent); // Clear the image
    }

    private void updateCurrentTrackInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://public.radio.co/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RadioService radioService = retrofit.create(RadioService.class);

        Call<RadioStatus> call = radioService.getRadioStatus();
        call.enqueue(new Callback<RadioStatus>() {
            @Override
            public void onResponse(@NonNull Call<RadioStatus> call, @NonNull Response<RadioStatus> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RadioStatus radioStatus = response.body();
                    RadioStatus.CurrentTrack currentTrack = radioStatus.getCurrentTrack();

                    if (currentTrack != null) {
                        String title = currentTrack.getTitle();
                        String artworkUrl = currentTrack.getArtworkUrlLarge();

                        // Update UI with the current track title and artwork
                        tvNowPlaying.setText(getString(R.string.now_playing, title));
                        Picasso.get().load(artworkUrl).into(ivArtwork);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RadioStatus> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    // Retrofit service interface
    public interface RadioService {
        @GET("stations/sefac315e7/status?v=1703130051008")
        Call<RadioStatus> getRadioStatus();
    }
}
