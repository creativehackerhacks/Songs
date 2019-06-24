package com.example.songs.innerFragments;


import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.songs.R;
import com.example.songs.activity.MainActivity;
import com.example.songs.adapters.TrackRecyclerViewAdapter;
import com.example.songs.archComp.TrackModel;
import com.example.songs.data.model.Tracks;
import com.example.songs.interfaces.RecyclerViewSimpleClickListener;
import com.example.songs.util.UtilConstants;
import com.example.songs.util.dialogs.LongBottomSheetFragment;
import com.example.songs.util.dialogs.LongBottomSheetFragment.LongBottomSheetListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class TracksFragment extends Fragment {

    public static final String TRACK_FRAGMENT = TracksFragment.class.getSimpleName();

    // Firebase
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference, mSongDatabaseReference, mSongUriDatabaseReference;
    private StorageReference mStorageReference, mSongStorageReference, mSongUriStorageReference;

    private RecyclerView mRecyclerView;
    private TrackRecyclerViewAdapter mTrackRecyclerViewAdapter;
    private RecyclerViewSimpleClickListener mRecyclerViewSimpleClickListener;
    private List<Tracks> mTracks;
    private int mCurrentTrackPos;

    private int trackLoader = -1;

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private Button mShufflePlayButton;
    private TextView mNumOfSongs;
    private EditText mSearchEditTextView;
    private TrackModel mTrackModel;

    private Context mContext;

    public TracksFragment() {
        // Required empty public constructor
    }

    public static TracksFragment newInstance() {
        TracksFragment tracksFragment = new TracksFragment();
        return tracksFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTrackModel = ViewModelProviders.of(getActivity()).get(TrackModel.class);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        mStorageReference = FirebaseStorage.getInstance().getReference().child("users").child(mFirebaseUser.getUid()).child("userTrackSendData");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(mFirebaseUser.getUid()).child("userSendSongs");

        mSongDatabaseReference  = FirebaseDatabase.getInstance().getReference("userSongs").child(mFirebaseUser.getUid());
        mSongStorageReference = FirebaseStorage.getInstance().getReference("userSongData").child(mFirebaseUser.getUid());

        mSongUriDatabaseReference = FirebaseDatabase.getInstance().getReference("userSongUri");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = container.getContext();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracks, container, false);

        mCollapsingToolbarLayout = view.findViewById(R.id.f_tracks_collapsing_toolbar);
        mCollapsingToolbarLayout.setTitle("Songs");

        mNumOfSongs = view.findViewById(R.id.f_tracks_num_of_songs);
        mShufflePlayButton = view.findViewById(R.id.f_tracks_shuffle_play);
        mToolbar = view.findViewById(R.id.f_tracks_toolbar);
        mRecyclerView = view.findViewById(R.id.f_tracks_recyclerView);
        mSearchEditTextView = view.findViewById(R.id.f_tracks_search_ETV);

        AppCompatActivity appCompatActivity = ((AppCompatActivity) getActivity());
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        setUpRecyclerView(container.getContext());

        // Setting Adapter
        mTrackRecyclerViewAdapter.loadItems(mTrackModel.getTracks());
        Log.e(TRACK_FRAGMENT, "onCreateView: " +mTrackModel.getTracks().size());

        mRecyclerView.setAdapter(mTrackRecyclerViewAdapter);
        String numOfSongs = String.valueOf(mTrackRecyclerViewAdapter.getAllTrackList().size());
            mNumOfSongs.setText(numOfSongs + " songs");
//        Log.e(TRACK_FRAGMENT, "onClick: Num of songs : " + numOfSongs);


        mSearchEditTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTrackRecyclerViewAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
//                filter(s.toString());
            }
        });

        return view;
    }

    private void filter(String text) {
        List<Tracks> temp = new ArrayList();
        for(Tracks d: mTrackRecyclerViewAdapter.getAllTrackList()){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getTrackName().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        mTrackRecyclerViewAdapter.filterList(temp);
    }


    private void setUpRecyclerView(Context context) {
        mTrackRecyclerViewAdapter = new TrackRecyclerViewAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        mTrackRecyclerViewAdapter.setOnItemClickListener(mClickListener);
        mTrackRecyclerViewAdapter.setOnItemLongClickListener(mLongClickListener);
//        mRecyclerView.setHasFixedSize(true);
    }

    // More Option Click Listener
    private View.OnClickListener mMoreOptionClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), "I AM CLICKING", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            //TODO: Step 4 of 4: Finally call getTag() on the view.
            // This viewHolder will have all required values.
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            Tracks recentTrack = mTrackRecyclerViewAdapter.getAllTrackList().get(position);
//            addTracksToRecentlyPlayedList(recentTrack);
            ((MainActivity) getActivity()).playAudio(mTrackRecyclerViewAdapter.getAllTrackList(), position);
//            Toast.makeText(getContext(), "You Clicked: " + mTracks.get(position).getTrackName(), Toast.LENGTH_SHORT).show();

        }
    };

    // Adding and checking the recently played list.
    private void addTracksToRecentlyPlayedList(Tracks tracks) {
        if(!UtilConstants.mRecentlyPlayedSongs.contains(tracks)) {
            if(UtilConstants.mRecentlyPlayedSongs.size() < 20) {
                UtilConstants.mRecentlyPlayedSongs.add(tracks);
            } else {
                int pos = UtilConstants.mRecentlyPlayedSongs.size()-1;
                UtilConstants.mRecentlyPlayedSongs.remove(pos);
                UtilConstants.mRecentlyPlayedSongs.add(tracks);
            }
        }
    }

    private View.OnLongClickListener mLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            RecyclerView.ViewHolder viewHolder = (ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();
            showDialog(position);
            return true;
        }
    };

    private void showDialog(int position) {
        LongBottomSheetFragment longBottomSheetFragment = LongBottomSheetFragment.getInstance();
        longBottomSheetFragment.show(getChildFragmentManager(), "long_bottomsheet_dialog");
        longBottomSheetFragment.setLongBottomListener(new LongBottomSheetListener() {
            @Override
            public void onButtonClicked(int id) {
                if(id == R.id.long_bottomsheet_delete) {
                    Toast.makeText(getContext(), "Deleting the selected song", Toast.LENGTH_SHORT).show();
//                    deleteSingleSong(position);
                }
                if(id == R.id.long_bottomsheet_send) {
                    Toast.makeText(getContext(), "Sending the selected song", Toast.LENGTH_SHORT).show();
//                    sendSong(position);
                    sendSongUriPath(position);
                }
            }
        });
    }

    private void sendSongUriPath(int position) {
        Uri uri = Uri.parse(mTrackRecyclerViewAdapter.getAllTrackList().get(position).getTrackData());
        Map<String, Object> addSongUriData = new HashMap<>();
        addSongUriData.put("songName", mTrackRecyclerViewAdapter.getAllTrackList().get(position).getTrackName());
        addSongUriData.put("songPath", uri.toString());
        addSongUriData.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("userSongUri");
        String postId = reference.push().getKey();
        reference.child(postId).setValue(addSongUriData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Uploaded the Song URI", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Failed to upload Song URI", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void sendSong(int position) {
        Uri uri = Uri.parse(mTrackRecyclerViewAdapter.getAllTrackList().get(position).getTrackData());
//        ContentResolver contentResolver = getContext().getContentResolver();
//        long currentTrackId = mTrackRecyclerViewAdapter.getAllTrackList().get(position).getTrackId();

        String songName = mTrackRecyclerViewAdapter.getAllTrackList().get(position).getTrackName();

        Log.e(TRACK_FRAGMENT, "sendSong: -- Get Uri: " + uri);
        Log.e(TRACK_FRAGMENT, "sendSong -- Get Path: " + uri.getPath());



//        uploadSong(uri, songName);

//        MediaPlayer mediaPlayer = new MediaPlayer();
//        try {
//            mediaPlayer.setDataSource(uri.getPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();


//        mDatabaseReference.setValue(uri.getPath());
    }

    private void deleteSingleSong(int position) {
        Log.e(TRACK_FRAGMENT, "deleteSingleSong: " + position);
        Uri uri = Uri.parse(mTrackRecyclerViewAdapter.getAllTrackList().get(position).getTrackData());
        Log.e(TRACK_FRAGMENT, "deleteSingleSong: " + uri.getPath());
        Tracks currentTrack = mTrackRecyclerViewAdapter.getAllTrackList().get(position);
//        deleteFile(uri.getPath(), currentTrack);
    }

    private void deleteFile(String path, Tracks track) {
        if(path.startsWith("content://")) {
            ContentResolver contentResolver = getContext().getContentResolver();
            contentResolver.delete(Uri.parse(path), null, null);
            mTrackRecyclerViewAdapter.deleteSingleRow(track);
        } else {
            File file = new File(path);
            if(file.exists()) {
                if(file.delete()) {
                    Log.e(TRACK_FRAGMENT, "deleteFile: " + "File Deleted.");
                    mTrackRecyclerViewAdapter.deleteSingleRow(track);
                } else {
                    Log.e(TRACK_FRAGMENT, "deleteFile: " + "Failed to Delete.");
                }
            } else {
                Log.e(TRACK_FRAGMENT, "deleteFile: " + " File not exist.");
            }
        }
    }



    private void uploadSong(Uri resultUri, String songName) {

        Uri newFileUri = Uri.fromFile(new File(resultUri.toString()));

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType(UtilConstants.UPLOAD_SONG_TYPE).build();

        StorageReference songUriStorageReference = mSongStorageReference.child(songName);

        UploadTask songUriUploadTask = songUriStorageReference.putFile(newFileUri, metadata);
        Task<Uri> songUriTask = songUriUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()) {
                    throw task.getException();
                }
                return songUriStorageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Toast.makeText(getContext(), "Song URI Uploaded", Toast.LENGTH_SHORT).show();
                Uri songUri = task.getResult();

                mSongDatabaseReference.child("sharedSongs").push().setValue(songUri.toString());

                Log.e(TRACK_FRAGMENT, "onComplete Song Uri Upload: " + "--- " + songUri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TRACK_FRAGMENT, "onFailure: " + "Failed to Upload Song URI.");
            }
        });
    }


}
