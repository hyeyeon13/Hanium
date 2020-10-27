package org.techtown.hanium;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Map extends AppCompatActivity {
    Double Lon, Lat;
    private GpsTracker gpsTracker;
    private TMapGpsManager tmapgps = null;
    TMapView tmapview;

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
            tmapview.setCenterPoint(location.getLongitude(), location.getLatitude());
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        gpsTracker = new GpsTracker(Map.this);
        Double curLatitude = gpsTracker.getLatitude();
        Double curLongitude = gpsTracker.getLongitude();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
        tmapview = new TMapView(this);
        tmapview.setIconVisibility(true);
        tmapgps = new TMapGpsManager(this);
        tmapgps.setMinTime(1000);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");
        tmapview.setZoomLevel(10);
        tmapview.setLocationPoint(curLongitude, curLatitude);
        tmapview.setCenterPoint(curLongitude, curLatitude);
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);
        TMapTapi tMapTapi = new TMapTapi(this);
        Button setLocation = (Button) findViewById(R.id.buttonSetLocation);
        final TextView longitude = (TextView) findViewById(R.id.longitude);
        final TextView latitude = (TextView) findViewById(R.id.latitude);
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        tmapview.setOnLongClickListenerCallback(new TMapView.OnLongClickListenerCallback() {
            @Override
            public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, final TMapPoint tMapPoint) {
                TMapPoint point = tmapview.getLocationPoint();
                TMapData tmapdata = new TMapData();
                TMapMarkerItem destMarker = new TMapMarkerItem();
                TMapPoint desttMappoint = new TMapPoint(tMapPoint.getLatitude(), tMapPoint.getLongitude()); // 마커 놓을 좌표 (위도, 경도 순서)
                Bitmap dest = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bluepoint);
                destMarker.setIcon(dest);
                destMarker.setPosition(0.5f, 1.0f);
                destMarker.setTMapPoint(desttMappoint);
                destMarker.setName("dest"); // 마커의 타이틀 지정
                tmapview.addMarkerItem("destPoint", destMarker);
                Log.d("현재위치", desttMappoint.toString());
                latitude.setText("위도 : " + tMapPoint.getLatitude());
                longitude.setText("경도 : " + tMapPoint.getLongitude());
                Lat = tMapPoint.getLatitude();
                Lon = tMapPoint.getLongitude();
            }

        });
        setLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), select_path.class);
                intent.putExtra("latitude", Lat);
                intent.putExtra("longitude", Lon);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        relativeLayout.addView(tmapview);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mLocationListener);
    }
}



