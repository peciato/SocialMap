package pecia.socialmap;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Francesco on 14/06/17.
 */

public class MyMarker  {

    private Marker marker;
    private String key;

    public MyMarker (Marker marker, String key) {

        this.marker = marker;
        this.key = key;

    }

    public MyMarker(String key) {
        this.marker = null;
        this.key = key;
    }


    public Marker getMarker() {
        return this.marker;
    }
    @Override
    public boolean equals(Object obj) {
        MyMarker myMarker= (MyMarker) obj;
        return (myMarker.key.equals(this.key));
    }

}
