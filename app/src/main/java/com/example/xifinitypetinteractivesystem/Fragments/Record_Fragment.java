package com.example.xifinitypetinteractivesystem.Fragments;

import static com.example.xifinitypetinteractivesystem.R.id.saved_Commands;
import static com.example.xifinitypetinteractivesystem.R.id.start_record;
import static com.example.xifinitypetinteractivesystem.R.id.stop_record;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.example.xifinitypetinteractivesystem.MainActivity;
import com.example.xifinitypetinteractivesystem.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;

public class Record_Fragment extends Fragment implements View.OnClickListener {

    private static final int CHECK_PERMISION_REQUEST_CODE = 101;

    private boolean isrecording = false;

    private MediaRecorder mediaRecorder;
    private TextInputEditText record_title;

    private Chronometer timer;
    private Button start_record_btn, stop_record_btn, saved_commands_btn;


    // Actions performed after the view is created.-- similar to the methods in activity
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);


        start_record_btn = view.findViewById(R.id.start_record);
        stop_record_btn = view.findViewById(R.id.stop_record);
        timer = view.findViewById(R.id.chronometer);

        record_title = view.findViewById(R.id.commandtitle);
        saved_commands_btn = view.findViewById(R.id.saved_Commands);

        start_record_btn.setOnClickListener(this);
        stop_record_btn.setOnClickListener(this);


        stop_record_btn.setEnabled(false);
        saved_commands_btn.setOnClickListener(this);

        if(!CheckPermission())
        {
            RequestPermission();
        }



    }

    // creating a view for the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case start_record:

                if(CheckPermission())
                {
                    startRecording();
                }
                else
                {
                    Toast.makeText(getContext(), "Needed Permission to record voices", Toast.LENGTH_SHORT).show();
                    RequestPermission();
                }


                break;
            case stop_record:

                stopRecording();
                Toast.makeText(getContext(), "Recording Saved Successfully!!", Toast.LENGTH_SHORT).show();
                break;
            case saved_Commands:
                Fragment fragment = new saved_commands();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.main, fragment);
                ft.commit();


                for (int i = 0; i < 4; i++) {
                    ((MainActivity) getActivity()).setChecked(i, false);
                }
                ((MainActivity) getActivity()).setChecked(1, true);

                break;


        }

    }

    // Checking Permissions for Audio and External Storage Access
    private void RequestPermission() {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHECK_PERMISION_REQUEST_CODE);
    }

    private boolean CheckPermission() {
        int per1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int per2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int per3 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO);

        return (per1 == PackageManager.PERMISSION_GRANTED && per2 == PackageManager.PERMISSION_GRANTED && per3 == PackageManager.PERMISSION_GRANTED);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CHECK_PERMISION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Permission granted Successfully", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Permission Denied. Permissions are needed to perform this feature", Toast.LENGTH_SHORT).show();
                    if(getActivity() ==null)
                    {
                        return;
                    }
                    if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.RECORD_AUDIO))
                    {
                        Toast.makeText(getContext(), "This app uses certain features like Voice Recording, Read and Write External Storage. Permissions are needed for this app to function properly", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    private void startRecording() {

        if (!isrecording) {

            if (record_title.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Provide command Title before Recording", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Started Recording!!", Toast.LENGTH_SHORT).show();
                stop_record_btn.setEnabled(true);
                isrecording = true;
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();

                mediaRecorder = new MediaRecorder();

                String filePath = getActivity().getExternalFilesDir("/PetCommands").getAbsolutePath();

                String fileName = record_title.getText().toString() + ".mp3";


                File folder = new File(getActivity().getExternalFilesDir("/").getAbsolutePath(), "PetCommands");

                if (!folder.exists()) {
                    folder.mkdirs();
                }


                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                mediaRecorder.setOutputFile(filePath + "/" + fileName);


                try {
                    mediaRecorder.prepare();
                    mediaRecorder.start();

                } catch (IOException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        } else {
            Toast.makeText(getActivity(), "Recording already Started", Toast.LENGTH_SHORT).show();
        }


    }

    private void stopRecording() {
        if (isrecording) {

            isrecording = false;
            stop_record_btn.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            timer.stop();
            timer.setBase(SystemClock.elapsedRealtime());
            record_title.setText("");


            // <--------- ********* recording stop logic needs to be added *********** ---------->
        } else {
            Toast.makeText(getActivity(), "recording not started yet", Toast.LENGTH_SHORT).show();
        }
    }

}