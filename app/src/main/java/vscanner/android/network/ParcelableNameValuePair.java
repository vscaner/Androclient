package vscanner.android.network;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.http.message.BasicNameValuePair;

public final class ParcelableNameValuePair extends BasicNameValuePair implements Parcelable {
    public ParcelableNameValuePair(final String name, final String value) {
        super(name, value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel out, final int flags) {
        out.writeString(getName());
        out.writeString(getValue());
    }

    public static final Parcelable.Creator<ParcelableNameValuePair> CREATOR
            = new Parcelable.Creator<ParcelableNameValuePair>() {
        public ParcelableNameValuePair createFromParcel(final Parcel in) {
            return new ParcelableNameValuePair(in);
        }

        public ParcelableNameValuePair[] newArray(final int size) {
            return new ParcelableNameValuePair[size];
        }
    };

    private ParcelableNameValuePair(final Parcel in) {
        super(in.readString(), in.readString());
    }
}
