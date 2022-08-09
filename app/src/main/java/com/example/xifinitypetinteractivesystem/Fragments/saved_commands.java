package com.example.xifinitypetinteractivesystem.Fragments;

import static com.example.xifinitypetinteractivesystem.R.id.play_pause_btn;
import static com.example.xifinitypetinteractivesystem.R.id.recyclerview;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xifinitypetinteractivesystem.Adapters.AudioAdapter;
import com.example.xifinitypetinteractivesystem.Adapters.DataModel;
import com.example.xifinitypetinteractivesystem.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class saved_commands extends Fragment implements AudioAdapter.OnItemClickListener{

    RecyclerView recyclerView;
    private AudioAdapter audioAdapter;
    ArrayList<DataModel> audiofiles;
    ArrayList<DataModel> FILTERED;
    private Handler handler;

    private MediaPlayer mediaPlayer;

    SearchView searchbar;
    ImageButton play_pause;
    SeekBar seekBar;
    TextView title_bottom;
    LinearLayout finished_saved;

    boolean flag = true;
    private Boolean media_paused = false;
    private DataModel selectedCommand = null;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerview);
        searchbar = view.findViewById(R.id.searchView);
        seekBar = view.findViewById(R.id.seekbar_audio);
        play_pause = view.findViewById(play_pause_btn);
        title_bottom = view.findViewById(R.id.comand_title_bottom);



        File folder = new File(getActivity().getExternalFilesDir("/").getAbsolutePath(), "PetCommands");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filePath = getActivity().getExternalFilesDir("/PetCommands").getAbsolutePath();

        File directory = new File(filePath);
        audiofiles = new ArrayList<>();


        for(File i:directory.listFiles())
        {
            audiofiles.add(new DataModel(i.getName(),i));
        }


        audioAdapter = new AudioAdapter(audiofiles, this);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(audioAdapter);



        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                audioAdapter.getFilter().filter(query);
                return false;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                audioAdapter.getFilter().filter(newText);
                return false;
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedCommand==null)
                {
                    Toast.makeText(getContext(), "Commands are not selected", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if(flag)
                    {
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(selectedCommand.getFile().getAbsolutePath());
                            mediaPlayer.prepare();
                            seekBar.setMax(mediaPlayer.getDuration());
                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                    updateSeekbar();
                                    play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                                }
                            });

                                mediaPlayer.setOnCompletionListener(mediaPlayer -> {
                                seekBar.removeCallbacks(updater);
                                seekBar.setProgress(0);
                                mediaPlayer.stop();
                                flag=true;
                                play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                                selectedCommand = null;
                                title_bottom.setVisibility(View.GONE);


                            });

                            flag=false;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else if(mediaPlayer.isPlaying() && !media_paused)
                    {
                        mediaPlayer.pause();
                        play_pause.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                        media_paused = true;
                    }
                    else if(media_paused)
                    {
                        mediaPlayer.start();
                        play_pause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                        updateSeekbar();
                        media_paused = false;
                    }

                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                {
                    mediaPlayer.seekTo(i);
                    seekBar.setProgress(i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
        return inflater.inflate(R.layout.fragment_saved_commands, container, false);
    }

    @Override
    public void OnItemSelected(DataModel dataModel)
    {

        title_bottom.setVisibility(View.INVISIBLE);
        title_bottom.setText(dataModel.getTitle());
        title_bottom.setVisibility(View.VISIBLE);
        selectedCommand = dataModel;


        if(mediaPlayer!=null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            seekBar.removeCallbacks(updater);
            seekBar.setProgress(0);
        }




    }

    @Override
    public void OnItemDeleted(View view, int pos, DataModel dataModel) {

        title_bottom.setText("");
        Toast.makeText(getContext(), "Command Deleted Successfully", Toast.LENGTH_SHORT).show();
    }


}