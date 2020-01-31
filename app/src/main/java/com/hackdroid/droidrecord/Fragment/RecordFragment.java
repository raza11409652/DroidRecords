package com.hackdroid.droidrecord.Fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.hackdroid.droidrecord.R;
import com.hackdroid.droidrecord.Util.ShowAlert;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends Fragment implements View.OnClickListener {
    private View view;
    ImageView listItem;
    ImageButton recordBtn;
    NavController navController;
    Boolean isRecording = false;
    String TAG = RecordFragment.class.getSimpleName();
    static final int PERMISSION_RECORD = 109;
    MediaRecorder mediaRecorder;
    String recordFile = null;
    private Chronometer recordFragmentTimer;
    ShowAlert showAlert;


    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);

//        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        listItem = view.findViewById(R.id.listRecords);
        recordBtn = view.findViewById(R.id.recordFragmentButton);
        recordFragmentTimer = view.findViewById(R.id.recordFragmentTimer);
        listItem.setOnClickListener(this);
        recordBtn.setOnClickListener(this);
        showAlert = new ShowAlert(getActivity(), getContext());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.listRecords:
                navController.navigate(R.id.action_recordFragment_to_recordsListFragment);
                break;
            case R.id.recordFragmentButton:
//                listItem.setImageDrawable(getResources().getDrawable(R.drawable.start_record));
                if (isRecording) {
                    //stop recording
                    isRecording = false;
                    stopRecording();
                } else {

                    //start recoding
                    /*
                     * @TODO: first check for permission if available
                     *   start recording
                     *   other wise ask for permission
                     *
                     * */
                    if (checkPermission()) {
                        startRecording();
                        isRecording = true;
                    } else {
                        Log.d(TAG, "Request permission");
                        //request permission
                        requestPermissionUser();
                    }
                }
                break;
        }
    }

    private void stopRecording() {
        recordFragmentTimer.stop();
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        /**
         * @alert the file output name that has been saved
         */
        showAlert.alert(recordFile +" has been saved to your mobile");

    }

    private void startRecording() {
        recordFragmentTimer.setBase(SystemClock.elapsedRealtime());
        recordFragmentTimer.start();

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss", Locale.US);
        Date now = new Date();
        recordFile = "droid_record_" + simpleDateFormat.format(now) + ".3gp";

        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(recordPath + "/" + recordFile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mediaRecorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaRecorder.start();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: " + permissions[0]);
        Log.d(TAG, "onRequestPermissionsResult: " + grantResults[0]);
        Log.d(TAG, "onRequestPermissionsResult: " + requestCode);
        /**
         * @if permission given by user start recording
         * other wise prompt alert for permission not given
         *
         * */
        switch (requestCode) {
            case PERMISSION_RECORD:
                if (permissions[0].equals(Manifest.permission.RECORD_AUDIO) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: " + "Permission granted");
                    isRecording = true;
                    startRecording();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: not");
                }
                break;
            default:
                Log.d(TAG, "onRequestPermissionsResult: no permission");
                break;
        }
    }

    private void requestPermissionUser() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD);
            }
        }
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission available");
            return true;
        }
        Log.d(TAG, "No permission available");

        return false;
    }
}
