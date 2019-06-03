package com.example.songs.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Tracks implements Parcelable {

    private Long mTrackAlbumId;
    private Long mTrackId;
    private String mTrackData;
    private String mTrackName;
    private String mTrackArtistName;

    public Tracks() {
    }

    public Long getTrackId() {
        return mTrackId;
    }

    public void setTrackId(Long trackId) {
        mTrackId = trackId;
    }

    public Long getTrackAlbumId() {
        return mTrackAlbumId;
    }

    public void setTrackAlbumId(Long trackAlbumId) {
        mTrackAlbumId = trackAlbumId;
    }

    public String getTrackData() {
        return mTrackData;
    }

    public void setTrackData(String trackData) {
        mTrackData = trackData;
    }

    public String getTrackName() {
        return mTrackName;
    }

    public String getTrackArtistName() {
        return mTrackArtistName;
    }

    public void setTrackName(String trackName) {
        mTrackName = trackName;
    }

    public void setTrackArtistName(String trackArtistName) {
        mTrackArtistName = trackArtistName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mTrackAlbumId);
        dest.writeValue(this.mTrackId);
        dest.writeString(this.mTrackData);
        dest.writeString(this.mTrackName);
        dest.writeString(this.mTrackArtistName);
    }

    protected Tracks(Parcel in) {
        this.mTrackAlbumId = (Long) in.readValue(Long.class.getClassLoader());
        this.mTrackId = (Long) in.readValue(Long.class.getClassLoader());
        this.mTrackData = in.readString();
        this.mTrackName = in.readString();
        this.mTrackArtistName = in.readString();
    }

    public static final Creator<Tracks> CREATOR = new Creator<Tracks>() {
        @Override
        public Tracks createFromParcel(Parcel source) {
            return new Tracks(source);
        }

        @Override
        public Tracks[] newArray(int size) {
            return new Tracks[size];
        }
    };


    //    @NonNull
//    @Override
//    public TrackViewHolder getViewHolder(View v) {
//        return new TrackViewHolder(v);
//    }
//
//    @Override
//    public int getType() {
//        return 0;
//    }
//
//    @Override
//    public int getLayoutRes() {
//        return R.layout.row_track_item;
//    }
//
//
//    public class TrackViewHolder extends FastAdapter.ViewHolder<Tracks> {
//        private TextView mTrackTitle, mTrackSubtitle;
//        public ImageView mTrackArtwork;
//
//        public TrackViewHolder(@NonNull View itemView) {
//            super(itemView);
//            mTrackTitle = itemView.findViewById(R.id.track_row_title);
//            mTrackSubtitle = itemView.findViewById(R.id.track_row_artist);
//            mTrackArtwork = itemView.findViewById(R.id.track_row_image);
//        }
//
//        @RequiresApi(api = VERSION_CODES.LOLLIPOP)
//        @Override
//        public void bindView(Tracks item, List<Object> payloads) {
//
//            Uri uri = ContentUris.withAppendedId(mArtWorkUri, item.getTrackAlbumId());
//            Glide.with(itemView).load(uri)
//                    .apply(new RequestOptions().circleCropTransform())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.placeholder_gradient_track_artwork)
//                    .into(mTrackArtwork);
//
//            mTrackTitle.setText(item.getTrackName());
//            mTrackSubtitle.setText(item.getTrackArtistName());
//        }
//
//        @Override
//        public void unbindView(Tracks item) {
//
//        }
//
//
//    }


}









