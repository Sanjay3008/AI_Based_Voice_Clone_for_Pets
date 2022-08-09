package com.example.xifinitypetinteractivesystem.Fragments;

import static com.example.xifinitypetinteractivesystem.R.id.dismiss_btn;
import static com.example.xifinitypetinteractivesystem.R.id.start_record_your_voice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.text.LineBreaker;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xifinitypetinteractivesystem.R;


import java.io.File;
import java.io.IOException;
import java.util.Objects;


public class Voice_Input extends Fragment implements View.OnClickListener {


    private Dialog dialog;
    private Button stop_record;
    private static final int CHECK_PERMISION_REQUEST_CODE = 102;


    private boolean isrecording = false;
    private MediaRecorder mediaRecorder;
    private Chronometer timer;
    // Actions need to be taken after a view is created

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case dismiss_btn:
                dialog.dismiss();
                break;
            case start_record_your_voice:
                if (CheckPermission()) {
                    Toast.makeText(getContext(), "Started Recording!!!", Toast.LENGTH_SHORT).show();
                    startRecording();
                } else {
                    RequestPermission();
                }
                break;
            case R.id.stop_record_your_voice:
                stopRecording();
                Toast.makeText(getContext(), "Recording Saved Successfully!!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView paragraph = view.findViewById(R.id.paragraph);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            paragraph.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
            paragraph.setText(paragraph.getText());
        }

        Button start_record = view.findViewById(start_record_your_voice);
        stop_record = view.findViewById(R.id.stop_record_your_voice);
        timer = view.findViewById(R.id.chronometer_your_voice);

        stop_record.setOnClickListener(this);
        start_record.setOnClickListener(this);


        dialog = new Dialog(getContext());

        dialog.setContentView(R.layout.info_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button dismiss = dialog.findViewById(dismiss_btn);
        dialog.show();
        dismiss.setOnClickListener(this);
    }

    // create a view for the Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voice__input, container, false);
    }

    private void RequestPermission() {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHECK_PERMISION_REQUEST_CODE);
    }

    private boolean CheckPermission() {
        int per1 = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.READ_EXTERNAL_STORAGE);
        int per2 = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int per3 = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.RECORD_AUDIO);

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
                    RequestPermission();
                }
            } else {
                Toast.makeText(getActivity(), "Permission Denied. Permissions are needed to perform this feature", Toast.LENGTH_SHORT).show();
                RequestPermission();
            }
        }
    }

    private void startRecording() {

        if (!isrecording) {

            stop_record.setEnabled(true);
            isrecording = true;
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();

            mediaRecorder = new MediaRecorder();

            String filePath = Objects.requireNonNull(getActivity()).getExternalFilesDir("/YourVoice").getAbsolutePath();

            String fileName = "YourVoice" + ".mp3";


            File folder = new File(getActivity().getExternalFilesDir("/").getAbsolutePath(), "YourVoice");

            if (!folder.exists()) {
                if(folder.mkdirs())
                {
                    return;
                }
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

        } else {
            Toast.makeText(getActivity(), "Recording already Started", Toast.LENGTH_SHORT).show();
        }


    }

    private void stopRecording() {
        if (isrecording) {

            isrecording = false;
            stop_record.setEnabled(false);
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            timer.stop();
            timer.setBase(SystemClock.elapsedRealtime());
                        // <--------- ********* recording stop logic needs to be added *********** ---------->
        } else {
            Toast.makeText(getActivity(), "recording not started yet", Toast.LENGTH_SHORT).show();
        }
    }
}