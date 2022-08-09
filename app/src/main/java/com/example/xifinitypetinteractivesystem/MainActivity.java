package com.example.xifinitypetinteractivesystem;

import static com.example.xifinitypetinteractivesystem.R.id.dismiss_btn;
import static com.example.xifinitypetinteractivesystem.R.id.dismiss_your_voice;
import static com.example.xifinitypetinteractivesystem.R.id.image;
import static com.example.xifinitypetinteractivesystem.R.id.saved_voice;
import static com.example.xifinitypetinteractivesystem.R.id.text_to_speech;
import static com.example.xifinitypetinteractivesystem.R.id.to_record_voice;
import static com.example.xifinitypetinteractivesystem.R.id.voice_input;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;


import com.example.xifinitypetinteractivesystem.Fragments.Record_Fragment;
import com.example.xifinitypetinteractivesystem.Fragments.Voice_Input;
import com.example.xifinitypetinteractivesystem.Fragments.saved_commands;
import com.example.xifinitypetinteractivesystem.Fragments.text_to_Speech;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    FrameLayout frameLayout;
    Dialog dialog;
    MediaPlayer mediaPlayer;
    Boolean media_paused = false;
    SeekBar seekBar_play;
    ImageButton imageButton_play;
    Handler handler;



    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // defining the ids
        drawerLayout = findViewById(R.id.drawerlayout);
        navigationView = findViewById(R.id.navigationview);
        toolbar = findViewById(R.id.toolbar);

        // navigation view actions initialize
        navigationView.bringToFront();
        navigationView.setCheckedItem(to_record_voice);
        navigationView.setItemIconTintList(null);




        // Creating Default Fragment to be selected when the app launchees
        Fragment fragment;
        fragment=new Record_Fragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main,fragment);
        ft.commit();

        // Choosing different Fragment on selection
        navigationView.setNavigationItemSelectedListener(item -> {

            Fragment fragment1 = null;
            int index;
            switch(item.getItemId())
            {
                case to_record_voice:

                    index = 0;
                    for(int i=0;i<4;i++)
                    {
                        setChecked(i,false);
                    }

                    navigationView.getMenu().getItem(index).setChecked(true);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    fragment1 = new Record_Fragment();

                    break;
                case saved_voice:

                    index = 1;
                    for(int i=0;i<4;i++)
                    {
                        setChecked(i,false);
                    }

                    setChecked(index,true);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    fragment1 = new saved_commands();

                    break;
                case text_to_speech:

                    index = 2;
                    for(int i=0;i<4;i++)
                    {
                        setChecked(i,false);
                    }

                    setChecked(index,true);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    fragment1 = new text_to_Speech();

                    break;
                case voice_input:

                    index = 3;
                    for(int i=0;i<4;i++)
                    {
                        setChecked(i,false);
                    }

                    setChecked(index,true);
                    drawerLayout.closeDrawer(GravityCompat.START);
                    fragment1 = new Voice_Input();

                    break;
                case R.id.your_voice:

                    drawerLayout.closeDrawer(GravityCompat.START);

                    Show_Recorded_Voice();
                    break;


            }
            if(fragment1!=null){
                FragmentManager fragmentManager1 = getSupportFragmentManager();
                FragmentTransaction ft1 = fragmentManager1.beginTransaction();
                ft1.replace(R.id.main,fragment1);
                ft1.commit();
            }
            return false;
        });

        // Opening and Closing The Navigation bar using drawerToggle Icon.
        drawerToggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.tooglecolour));


    }

    private void Show_Recorded_Voice()
    {
        dialog = new Dialog(this);

        dialog.setContentView(R.layout.yout_recorded_voice);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button dismiss = dialog.findViewById(dismiss_your_voice);
        seekBar_play = dialog.findViewById(R.id.seekbar_audio_your_voice);
        imageButton_play = dialog.findViewById(R.id.play_pause_btn_your_voice);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        dialog.show();
        dialog.getWindow().setLayout((6 * width)/7, WindowManager.LayoutParams.WRAP_CONTENT);

        imageButton_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButton_play.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                Play_Your_Voice();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null)
                {
                    seekBar_play.removeCallbacks(updater);
                    seekBar_play.setProgress(0);
                    mediaPlayer.stop();
                    mediaPlayer = null;
                }

                imageButton_play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                dialog.dismiss();

            }
        });
    }

    private void Play_Your_Voice()
    {
        imageButton_play = dialog.findViewById(R.id.play_pause_btn_your_voice);
        File file = new File(this.getExternalFilesDir("/").getAbsolutePath(), "YourVoice/YourVoice.mp3");
        if (mediaPlayer != null && mediaPlayer.isPlaying() && !media_paused) {
            mediaPlayer.pause();
            imageButton_play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
            media_paused = true;
        } else if (mediaPlayer!=null && media_paused) {
            mediaPlayer.start();
            imageButton_play.setBackgroundResource(R.drawable.ic_baseline_pause_24);
            updateSeekbar();
            media_paused = false;
        }else {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                seekBar_play.setMax(mediaPlayer.getDuration());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        updateSeekbar();
                        seekBar_play.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    }
                });

                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    seekBar_play.removeCallbacks(updater);
                    seekBar_play.setProgress(0);
                    mediaPlayer.stop();
                    imageButton_play.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);


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
            seekBar_play.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(updater, 100);

        }
    }


    public void setChecked(int position, boolean selected)
    {
        navigationView = findViewById(R.id.navigationview);
        navigationView.getMenu().getItem(position).setChecked(selected);
    }




}