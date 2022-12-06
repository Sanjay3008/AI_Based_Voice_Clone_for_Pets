package com.example.xifinitypetinteractivesystem.Fragments;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xifinitypetinteractivesystem.MainActivity;
import com.example.xifinitypetinteractivesystem.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;


public class text_to_Speech extends Fragment {


    OkHttpClient httpClient;

    String url = "Voice Clone Server URL";                                              // Needs to be changed once hosted.

    Button start_convert;
    TextInputEditText command_send, command_title;
    ProgressBar loading;
    String title_saved = "";
    LinearLayout finished_saved;
    Button back_to_commands;

    TextView command_title_bottom;
    ImageButton play_btn;
    Button Send;

    SeekBar seekBar;
    CardView linearLayout;
    Boolean command_received = false;
    MediaPlayer mediaPlayer;
    Boolean media_paused = false;
    Handler handler;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        start_convert = view.findViewById(R.id.start_converion);
        command_send = view.findViewById(R.id.command_to_be_sent);
        command_title = view.findViewById(R.id.command_title);
        loading = view.findViewById(R.id.progressbar);


        command_title_bottom = view.findViewById(R.id.comand_title_bottom_converted);
        play_btn = view.findViewById(R.id.play_pause_btn_converted);
        Send = view.findViewById(R.id.send_convert);
        seekBar = view.findViewById(R.id.seekbar_audio_converted);
        linearLayout = view.findViewById(R.id.layout_bottom_converted);

        linearLayout.setVisibility(View.GONE);


        start_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start_convesion();
            }
        });


    }

    private void Start_convesion() {

        httpClient = new OkHttpClient.Builder().readTimeout(300000, TimeUnit.SECONDS).build();

        String com_send = command_send.getText().toString();
        String com_title = command_title.getText().toString();
        File file = new File(getActivity().getExternalFilesDir("/").getAbsolutePath(), "YourVoice/YourVoice.mp3");

        loading.setVisibility(View.VISIBLE);
        if (com_send.isEmpty() || com_title.isEmpty()) {
            Toast.makeText(getContext(), "Some Fields are missing!!", Toast.LENGTH_SHORT).show();
        } else if (!file.exists()) {
            Toast.makeText(getContext(), "Pre Recorded Voice not found. Please record your voice for training & try again.", Toast.LENGTH_SHORT).show();
        } else {
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("audio", file.getName(),
                    RequestBody.create(MediaType.parse("audio/mp3"), file)).addFormDataPart("command", com_send)
                    .addFormDataPart("title", com_title).build();

            title_saved = com_title;
            Request req = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Log.d("Files_er", "req_sent");
            httpClient.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("Files_1st", response.body().toString());
                    try {
                        ResponseBody body1 = response.body();
                        Log.d("Files_string", response.body().source().toString());

                        File file = new File(getActivity().getExternalFilesDir("/").getAbsolutePath(), "PetCommands/" + title_saved + ".mp3");

                        if (!response.isSuccessful()) {
                            Log.d("Files_un", response.body().string());
                        }

                        try {


                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            BufferedSource source = response.body().source();
                            BufferedSink sink = Okio.buffer(Okio.sink(file));
                            sink.writeAll(source);
                            sink.close();

                            Log.d("Files_received", "received");
                            command_received = true;
                            command_title_bottom.setText(title_saved+".mp3");

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                   
                                    linearLayout.setVisibility(View.VISIBLE);
                                    play_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            PlayAudio();
                                        }
                                    });

                                    Send.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(getContext(), "Command send", Toast.LENGTH_SHORT).show();
                                            PlayAudio();
                                        }
                                    });
                                    loading.setVisibility(View.GONE);

                                }
                            });


                        } catch (Exception e) {
                            Log.d("Files_er_c", e.getLocalizedMessage());
                            loading.setVisibility(View.GONE);
                        }

                    } catch (Exception e) {
                        Log.d("Files_er_oc", e.getLocalizedMessage());
                        loading.setVisibility(View.GONE);
                    }


                }
            });
        }

    }

    private void PlayAudio() {
        File file = new File(getActivity().getExternalFilesDir("/").getAbsolutePath(), "PetCommands/" + title_saved + ".mp3");
        if (mediaPlayer != null && mediaPlayer.isPlaying() && !media_paused) {
            mediaPlayer.pause();
            play_btn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            media_paused = true;
        } else if (media_paused) {
            mediaPlayer.start();
            play_btn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            updateSeekbar();
            media_paused = false;
        }else {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                seekBar.setMax(mediaPlayer.getDuration());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        updateSeekbar();
                        play_btn.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    }
                });

                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    seekBar.removeCallbacks(updater);
                    seekBar.setProgress(0);
                    mediaPlayer.stop();
                    play_btn.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);


                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
        }
    };

    private void updateSeekbar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            handler = new Handler();
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(updater, 100);

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_to__speech, container, false);
    }
}
