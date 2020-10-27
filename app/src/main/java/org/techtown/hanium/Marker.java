package org.techtown.hanium;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.os.PowerManager.PARTIAL_WAKE_LOCK;

public class Marker extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private static final int SMS_RECEIVE_PERMISSON = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    ArrayList<String> pathData = new ArrayList<String>();
    Double myLongitude, myLatitude;
    Double destLongitude, destLatitude, altitude;
    Double realtimeLongitude, realtimeLatitude;
    String min;
    Double num_min;
    Double distance;
    String[] dist;
    Double latitude;
    Double longitude;
    private boolean m_bTrackingMode = true;
    private TMapGpsManager tmapgps = null;
    TMapView tmapview;
    public String login_id;
    private GpsTracker gpsTracker;
    Double moved_dist;
    boolean des_arrive = false;
    Double desDist;
    //경도 : longitude 범위 : 127
    //위도 : latitude 범위 : 37
    String[] people_array;
    public people_list task;
    int min_time = 0;
    int hour_time = 0;
    TMapPolyLine tpolyline2 = new TMapPolyLine();
    ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
    ArrayList<ArrayList<String>> array;
    JSONObject result;


    @Override
    public void onLocationChange(Location location) {
    }


//    private final LocationListener mLocationListener = new LocationListener() {
//        public void onLocationChanged(Location location) {
//            //여기서 위치값이 갱신되면 이벤트가 발생한다.
//            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
//            tpolyline2.setLineColor(Color.RED);
//            tpolyline2.setLineWidth(2);
//            realtimeLongitude = location.getLongitude(); //현재 경도
//            realtimeLatitude = location.getLatitude();   //현재 위도
////            altitude = location.getAltitude();   //고도
//            //float accuracy = location.getAccuracy();    //정확도
//            //String provider = location.getProvider();   //위치제공자
//            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
//            //Network 위치제공자에 의한 위치변화
//            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
//        }
//
//        public void onProviderDisabled(String provider) {
//            // Disabled시
//            Log.d("test", "onProviderDisabled, provider:" + provider);
//        }
//
//        public void onProviderEnabled(String provider) {
//            // Enabled시
//            Log.d("test", "onProviderEnabled, provider:" + provider);
//        }
//
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//            // 변경시
//            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        Context mContext = getApplicationContext();
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PARTIAL_WAKE_LOCK, "motionDetection:keepAwake");
        wakeLock.acquire();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.map_view);
        tmapview = new TMapView(this);
        Intent intent = getIntent();
        try {
            result = new JSONObject(getIntent().getStringExtra("pathInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gpsTracker = new GpsTracker(Marker.this);
        realtimeLatitude = gpsTracker.getLatitude();
        realtimeLongitude = gpsTracker.getLongitude();
        latitude = intent.getExtras().getDouble("curLatitude");
        longitude = intent.getExtras().getDouble("curLongitude");
        array = (ArrayList<ArrayList<String>>) intent.getExtras().get("pathDataArray");
        for (int i = 0; i < array.size(); i++) {
            ArrayList<String> tempData = new ArrayList<String>();
            tempData = array.get(i);
            for (int j = 0; j < tempData.size(); j++) {
                pathData.add(tempData.get(j));
            }
        }
        tmapview.setLocationPoint(longitude, latitude);
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

        TextView destTime = (TextView) findViewById(R.id.destTime);
        TextView totalTime = (TextView) findViewById(R.id.totalTime);
        TextView totalDistance = (TextView) findViewById(R.id.totalDistance);


        final int destT = intent.getExtras().getInt("totalTime");
//        String t = String.valueOf(destT);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH시 mm분");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, destT);
        String now = dateFormat.format(cal.getTime());
        destTime.setText("도착예정시간 : " + now);

        int totalT = intent.getExtras().getInt("totalTime");
        if (totalT >= 60) {
            min_time = totalT % 60;
            hour_time = totalT / 60;
            totalTime.setText("소요 시간 : " + String.valueOf(hour_time) + "시간" + String.valueOf(min_time) + "분");
        } else {
            totalTime.setText("소요 시간 : " + totalT + "분");
        }

        final Double totalDist = intent.getExtras().getDouble("totalDistance");
        final Double totalDist_km = totalDist / 1000.0;
        totalDistance.setText("총 거리 : " + totalDist_km + "km");


        login_id = intent.getExtras().getString("log_ok_id");
//        Log.d("Marker에서 login 아이디 ", login_id);
        TMapMarkerItem myMarker = new TMapMarkerItem();
        TMapMarkerItem destMarker = new TMapMarkerItem();
        myLongitude = intent.getExtras().getDouble("curLongitude"); //출발지 위도
        myLatitude = intent.getExtras().getDouble("curLatitude");
        destLongitude = intent.getExtras().getDouble("destLongitude");
        destLatitude = intent.getExtras().getDouble("destLatitude");

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

        double centerLong, centerLat, centerAlt;
        centerLong = (myLongitude + destLongitude) / 2;
        centerLat = (myLatitude + destLatitude) / 2;
        tmapview.setCenterPoint(centerLong, centerLat); //지도의 중심지점 좌표 (경도, 위도 순서)
        Log.d("내 위도  ", String.valueOf(realtimeLatitude));
        Log.d("내 경도  ", String.valueOf(realtimeLongitude));
        Log.d("목적지 위도  ", String.valueOf(destLatitude));
        Log.d("목적지 경도  ", String.valueOf(destLongitude));

        TMapPoint leftTop = new TMapPoint(myLatitude, myLongitude);
        TMapPoint rightBottom = new TMapPoint(destLatitude, destLongitude);
        tmapview.zoomToTMapPoint(leftTop, rightBottom);

        TMapPolyLine tpolyline = new TMapPolyLine();
        tpolyline.setLineColor(Color.BLUE);
        tpolyline.setLineWidth(2);

        for (int i = 0; i < pathData.size(); i++) {
            double tempLat, tempLong;
            String tempS;
            tempS = pathData.get(i);
            String[] tempA = tempS.split(",");
            tempLat = Double.valueOf(tempA[1]);
            tempLong = Double.valueOf(tempA[0]);
            //Log.e("test", tempA[0]);
            TMapPoint temp = new TMapPoint(tempLat, tempLong);
            tpolyline.addLinePoint(temp);


            Location locationA = new Location("point A");
            locationA.setLatitude(realtimeLatitude);
            locationA.setLongitude(realtimeLongitude);
            //locationA 는 내 위치
            Location locationB = new Location("point B");
            locationB.setLatitude(tempLat);
            locationB.setLongitude(tempLong);
            //locationB 는 pathData에서 가져옴

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
        relativeLayout.addView(tmapview);
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                TMapPoint tp = new TMapPoint(latitude, longitude);
                Log.d("테스트", tp.toString());
                TextView myAltitude = (TextView) findViewById(R.id.myAltitude);
                altitude = location.getAltitude();
                myAltitude.setText("현재 고도 : " + altitude);
                Log.d("현재 고도", String.valueOf(altitude));
                Location locationA = new Location("point a");
                locationA.setLatitude(latitude);
                locationA.setLongitude(longitude);
                Location locationB = new Location("point b");
                locationB.setLatitude(destLatitude);
                locationB.setLongitude(destLongitude);
                desDist = Double.valueOf(locationA.distanceTo(locationB));
                if (desDist <= 15) {
                    des_arrive = true;
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

        int permissonCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if (permissonCheck == PackageManager.PERMISSION_GRANTED) {

        } else {
            Toast.makeText(getApplicationContext(), "SMS 수신권한 없음", Toast.LENGTH_SHORT).show();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                Toast.makeText(getApplicationContext(), "SMS권한이 필요합니다", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, SMS_RECEIVE_PERMISSON);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setContentTitle("안내 중").setContentText("남은 시간:" + "남은 거리");

        final Button startGuide = (Button) findViewById(R.id.btn_startGuide);
        startGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), startGuide.class);
                intent1.putExtra("Latitude", latitude);
                intent1.putExtra("Longitude", longitude);
                intent1.putExtra("myLatitude", myLatitude);
                intent1.putExtra("myLongitude", myLongitude);
                intent1.putExtra("destLatitude", destLatitude);
                intent1.putExtra("destLongitude", destLongitude);
                intent1.putExtra("pathData", pathData);
                intent1.putExtra("login_id", login_id);
                intent1.putExtra("destT", destT);
                intent1.putExtra("totalDist_km", totalDist_km);
                //intent1.putExtra("totalDist",totalDist);
                intent1.putExtra("altitude", altitude);
                intent1.putExtra("pathInfo", result.toString());
                startActivity(intent1);
                //Place this where you no longer need to have the processor running
                wakeLock.release();
            }
        });
        if (des_arrive == true) {
            AlertDialog.Builder builder_arrive = new AlertDialog.Builder(Marker.this);
            builder_arrive.setTitle("길찾기 종료").setMessage("목적지에 도착하였습니다.");
            AlertDialog alertDialog = builder_arrive.create();
            alertDialog.show();
            Log.d("목적지 도착", desDist.toString());
        }
    }

    public void sendSms() {
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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

    static class people_list extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... arg0) {
            String id = arg0[0];
            try {
                String link = "http://211.221.215.166/androidwithdb/phoneNo.php?ID=" + id;

                String ret = server_network_check(link);
                if (ret.equals("1") != true) return ret;

                HttpURLConnection urlConn = null;
                URL url = new URL(link);
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setConnectTimeout(1000);
                urlConn.setRequestMethod("POST"); // URL 요청에 대한 메소드 설정 : POST.
                urlConn.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                urlConn.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");


                if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d("retret2", "" + ret);
                    return "-2";
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), "UTF-8"));

                StringBuffer sb = new StringBuffer("");
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                reader.close();
                if (urlConn != null) urlConn.disconnect();

                Log.d("라인값", sb.toString());

                return sb.toString();
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    private void getpeople_list() {
        String people_r = null;

        task = new Marker.people_list();
        try {
            people_r = task.execute(login_id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d("getpeople", "가져오기");

        if (people_r.equals("0") == true) Log.d("보호자없음", "등록된 보호자가 없습니다.");
        else if (people_r.equals("-2") == true) Log.d("네트워크", "네트워크에러임");
        else {
            people_array = people_r.split("@");
            Log.d("네트워크2", people_r);
            Log.d("보호자5", people_array[0]);
            Log.d("보호자6", people_array[1]);
        }


    }

    public static String server_network_check(String host) {
        return "1";
    }
}