package com.hackdroid.droidrecord.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hackdroid.droidrecord.Adapter.RecordingAdapter;
import com.hackdroid.droidrecord.Model.AudioModel;
import com.hackdroid.droidrecord.R;
import com.hackdroid.droidrecord.Util.ShowAlert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordsListFragment extends Fragment implements RecordingAdapter.OnItemClick {

    String TAG = RecordsListFragment.class.getSimpleName();
    static final int PERMISSION_READ = 120;
    private File[] allFile;
    RecyclerView recyclerView;
    ShowAlert alert;
    RecordingAdapter adapter;
    MediaPlayer mediaPlayer = null;
    Boolean isplaying = false;
    File fileToPlay;

    //Media player UI Element
    TextView player_header_title, fileName;


    public RecordsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_records_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recordsListFragmentList);
        alert = new ShowAlert(getActivity(), getContext());
        recyclerView.setHasFixedSize(true);
        //TODO::View UI elements
        fileName = view.findViewById(R.id.fileName);
        player_header_title = view.findViewById(R.id.player_header_title);


        getRecordings();


    }

    private void getRecordings() {
        /**
         * Recoding save path
         * to get all data from absolute folder
         * we don't require permission
         */

        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        File directory = new File(recordPath);

        allFile = directory.listFiles();

        /**
         * if file length is less than 1
         * don't show recycler-view or show alert
         * */

        if (allFile.length < 1) {
            alert.alert("No Recoding found");
            return;
        }
        adapter = new RecordingAdapter(allFile, getActivity(), getContext(), this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
//        Log.d(TAG, "getRecordings: " + allFile);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ:
                if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission given");

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Not given");
                }
                break;
        }
    }

    /**
     * askpermission()=> asking permission above M
     */
    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            }
        }
    }

    /***
     *
     * @return
     * checking permission available
     */
    private boolean permissionAvail() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission available");
            return true;
        }
        Log.d(TAG, "No permission available");

        return false;
    }

    /**
     * this function will return all audio file
     */

    public List<AudioModel> getAllAudioFromDevice(final Context context) {
        final List<AudioModel> tempAudioList = new ArrayList<>();
        String recordPath = getActivity().getExternalFilesDir("/").getAbsolutePath();
        Uri uri = Uri.parse(recordPath);
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                AudioModel audioModel = new AudioModel();
                String path = c.getString(0);
                String name = c.getString(1);
                String album = c.getString(2);
                String artist = c.getString(3);

                audioModel.setaName(name);
                audioModel.setaAlbum(album);
                audioModel.setaArtist(artist);
                audioModel.setaPath(path);

                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);
            }
            c.close();
        }

        return tempAudioList;
    }

    @Override
    public void onAudioItemClick(File file, int position) {
        Log.d(TAG, "onAudioItemClick: " + position + " File Name : " + file);

        if (isplaying) {
            stopAudio();
            playAudio(fileToPlay);
        } else {
            fileToPlay = file;
            playAudio(fileToPlay);
        }
    }

    private void stopAudio() {
        //Stop the audio
        isplaying = false;

    }

    private void playAudio(File fileToPlay) {
        isplaying = true;
        //play the audio here
        mediaPlayer = new MediaPlayer();
        player_header_title.setText("Playing..");
        fileName.setText(fileToPlay.getName());
        try {
            mediaPlayer.setDataSource(fileToPlay.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                player_header_title.setText("finished");
                mediaPlayer.stop();
                isplaying = false;
            }
        });


    }
}
