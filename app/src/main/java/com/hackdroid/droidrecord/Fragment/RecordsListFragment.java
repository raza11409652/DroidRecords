package com.hackdroid.droidrecord.Fragment;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.hackdroid.droidrecord.Model.AudioModel;
import com.hackdroid.droidrecord.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordsListFragment extends Fragment {
    List<AudioModel> list = new ArrayList<>();
    String TAG = RecordsListFragment.class.getSimpleName();
    static final int PERMISSION_READ = 120;

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

        if (permissionAvail()) {
            list = getAllAudioFromDevice(getContext());
            Log.d(TAG, "onViewCreated: " + list.toString());
        } else {
            Log.d(TAG, "onViewCreated: No permission for read external");
            askPermission();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ:
                if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Permission given");
                    list = getAllAudioFromDevice(getContext());
                    Log.d(TAG, "onViewCreated: " + list.toString());
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Not given");
                }
                break;
        }
    }

    private void askPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            }
        }
    }

    private boolean permissionAvail() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Permission available");
            return true;
        }
        Log.d(TAG, "No permission available");

        return false;
    }

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
}
