package org.techtown.hanium;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;


import java.util.ArrayList;

public class Marker extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private static final int SMS_RECEIVE_PERMISSON = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    SmsManager mSMSManager;
    ArrayList<String> pathData = new ArrayList<String>();
    Double myLongitude, myLatitude, myAltitude;
    Double destLongitude, destLatitude;
    Double realtimeLongitude, realtimeLatitude;
    String min;
    Double num_min;
    Double distance;
    String[] dist;
    String stationName;
    double latitude = 0;
    double longitude = 0;
    double altitude = 0;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager tmapgps = null;
    TMapView tmapview;

    //경도 : longitude 범위 : 127
    //위도 : latitude 범위 : 37
    @Override
    public void onLocationChange(Location location) {
        if (m_bTrackingMode) {
            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
        }
    }


    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            realtimeLongitude = location.getLongitude(); //경도
            realtimeLatitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
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
        setContentView(R.layout.activity_marker);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");
        tmapview.setZoomLevel(10);
        tmapview.setMapPosition(TMapView.POSITION_DEFAULT);
        TMapTapi tMapTapi = new TMapTapi(this);

        tmapview.setCompassMode(false);
        //이거 true로 해놓으면 ㅈㄴ 지도가 ㅂㄷㅂㄷ거림
        tmapview.setIconVisibility(true);
        tmapgps = new TMapGpsManager(this);
        tmapgps.setMinTime(1000);
        tmapgps.setProvider(tmapgps.NETWORK_PROVIDER);
        tmapgps.OpenGps();
        tmapview.setTrackingMode(true);
        tmapview.setSightVisible(true);


        Intent intent = getIntent();
        realtimeLatitude = intent.getExtras().getDouble("curLatitude");
        realtimeLongitude = intent.getExtras().getDouble("curLongitude");
        TMapMarkerItem myMarker = new TMapMarkerItem();
        TMapMarkerItem destMarker = new TMapMarkerItem();
        myLongitude = intent.getExtras().getDouble("curLongitude");
        myLatitude = intent.getExtras().getDouble("curLatitude");
        destLongitude = intent.getExtras().getDouble("destLongitude");
        destLatitude = intent.getExtras().getDouble("destLatitude");
        //stationName=intent.getExtras().getString("stationName");
        TMapPoint mytMapPoint = new TMapPoint(myLatitude, myLongitude);// 마커 놓을 좌표 (위도, 경도 순서)
        TMapPoint desttMappoint = new TMapPoint(destLatitude, destLongitude); // 마커 놓을 좌표 (위도, 경도 순서)

// 마커 아이콘
        Bitmap Start = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.point);
        Bitmap dest = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bluepoint);
        tmapview.setTMapPathIcon(Start, dest);

        myMarker.setIcon(Start); // 마커 아이콘 지정
        myMarker.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        myMarker.setTMapPoint(mytMapPoint); // 마커의 좌표 지정
        myMarker.setName("start"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("startPoint", myMarker); // 지도에 마커 추가

        destMarker.setIcon(dest);
        destMarker.setPosition(0.5f, 1.0f);
        destMarker.setTMapPoint(desttMappoint);
        destMarker.setName("dest"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("destPoint", destMarker);

        double centerLong, centerLat;
        centerLong = (myLongitude + destLongitude) / 2;
        centerLat = (myLatitude + destLatitude) / 2;
        tmapview.setCenterPoint(centerLong, centerLat); //지도의 중심지점 좌표 (경도, 위도 순서)
        Log.d("내 위도  ", String.valueOf(realtimeLatitude));
        Log.d("내 경도  ", String.valueOf(realtimeLongitude));
        Log.d("내 고도  ", String.valueOf(myAltitude));
        Log.d("목적지 위도  ", String.valueOf(destLatitude));
        Log.d("목적지 경도  ", String.valueOf(destLongitude));

        TMapPoint leftTop = new TMapPoint(myLatitude, myLongitude);
        TMapPoint rightBottom = new TMapPoint(destLatitude, destLongitude);
        tmapview.zoomToTMapPoint(leftTop, rightBottom);

        TMapPolyLine tpolyline = new TMapPolyLine();
        tpolyline.setLineColor(Color.BLUE);
        tpolyline.setLineWidth(2);

        pathData = intent.getExtras().getStringArrayList("pathData");
        for (int i = 0; i < pathData.size(); i++) {
            double tempLat, tempLong;
            String tempS;
            tempS = pathData.get(i);
            String[] tempA = tempS.split(",");
            tempLat = Double.valueOf(tempA[1]);
            tempLong = Double.valueOf(tempA[0]);
            Log.e("test", tempA[0]);
            TMapPoint temp = new TMapPoint(tempLat, tempLong);
            tpolyline.addLinePoint(temp);

            Location locationA = new Location("point A");
            locationA.setLatitude(myLatitude);
            locationA.setLongitude(myLongitude);
            Location locationB = new Location("point B");
            locationB.setLatitude(tempLat);
            locationB.setLongitude(tempLong);

            distance = Double.valueOf(locationA.distanceTo(locationB));
//            Log.d(i+"번째 distance 거리",String.valueOf(distance)+"m");
            dist = new String[pathData.size()];
            dist[i] = String.valueOf(distance);
//            Log.d(i+"번째 dist 거리",String.valueOf(dist[i])+"m");
            if (i == 0) {
                min = dist[0];
            }
            if (Double.valueOf(min) > Double.valueOf(dist[i])) {
                min = dist[i];
            }
        }
        Log.d("내위치 사이 거리: ", min + "m");
        tmapview.addTMapPolyLine("path", tpolyline);

        num_min = Double.valueOf(min);

        if (num_min > 50) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("경로이탈감지").setMessage("경로를 이탈하였습니다. 문자를 전송하시겠습니까?");
            builder.setNegativeButton("전송 취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();

                }
            });
            builder.setPositiveButton("문자 전송", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try{
                        SmsManager sms = SmsManager.getDefault();
                        sendSms();
                    }catch (Exception e){
                        Log.d("에러",e.toString());
                    }

                }
            });
            Log.d("경로이탈감지: ", min + "m");
            builder.setTitle("경로이탈감지!").setMessage("경로를 벗어났습니다");

            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        }
        relativeLayout.addView(tmapview);
        //relativeLayout.addView(infoview);

        setContentView(relativeLayout);

        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                TMapPoint tp = new TMapPoint(latitude, longitude);
                Log.d("테스트", tp.toString());
                double altitude = location.getAltitude();


                Double desDist;
                Location locationA = new Location("point a");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point b");
                locationB.setLatitude(destLatitude);
                locationB.setLongitude(destLongitude);

                desDist = Double.valueOf(locationA.distanceTo(locationB));
                if (desDist <= 15) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Marker.this);
                    builder.setTitle("길찾기 종료").setMessage("목적지에 도착하였습니다.");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    Log.d("목적지 도착", desDist.toString());
                } else {
                    Log.d("목적지 미도착", desDist.toString());
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);

        int permissonCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissonCheck == PackageManager.PERMISSION_GRANTED){

        }else {
            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                Toast.makeText(getApplicationContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }
        }

    }

    public void sendSms(){
        //메시지
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("01050484236", null, "경로를 이탈하였습니다.", null, null);
                    Toast.makeText(getApplicationContext(), "문자 전송 완료.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "문자전송 실패", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }


}