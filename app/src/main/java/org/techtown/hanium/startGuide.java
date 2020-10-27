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
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
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

public class startGuide extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {
    private static final int SMS_RECEIVE_PERMISSON = 1;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    ArrayList<String> pathData = new ArrayList<String>();
    Double myLongitude, myLatitude;
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
    public String login_id;
    private GpsTracker gpsTracker;
    boolean dest_arrive = false;
    boolean flag = false;
    //경도 : longitude 범위 : 127
    //위도 : latitude 범위 : 37
    String[] people_array;
    public people_list task;
    TMapPolyLine tpolyline2 = new TMapPolyLine();
    ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
    Double desDist;
    PowerManager.WakeLock wakeLock;
    JSONObject result;

    public void arrive_destination() {
        AlertDialog.Builder builder_arrive = new AlertDialog.Builder(startGuide.this);
        builder_arrive.setMessage("목적지에 도착하였습니다.");
        builder_arrive.setCancelable(false);
        builder_arrive.setPositiveButton("안내종료", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //버튼클릭시 동작
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder_arrive.create();
        alertDialog.show();
        dest_arrive = true;
        Log.d("목적지 도착", desDist.toString());
    }

    @Override
    public void onLocationChange(Location location) {
//        if (m_bTrackingMode) {
//            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
//            tmapview.setCenterPoint(location.getLongitude(), location.getLatitude());
//            Log.d("105라인 ", location.getLongitude() + "," + location.getLatitude());
//            alTMapPoint.add(new TMapPoint(location.getLatitude(), location.getLongitude()));
//            tmapview.removeTMapPolyLine("path2");
//            for (int i = 0; i < alTMapPoint.size(); i++) {
//                //Log.d("tetetest", Integer.toString(alTMapPoint.size()));
//                tpolyline2.addLinePoint(alTMapPoint.get(i));
//            }
//            tmapview.addTMapPolyLine("path2", tpolyline2);
//        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.
//            tpolyline2.setLineColor(Color.RED);
//            tpolyline2.setLineWidth(2);
//            tmapview.setLocationPoint(location.getLongitude(), location.getLatitude());
//            tmapview.setCenterPoint(location.getLongitude(), location.getLatitude());
//            Log.d("105라인 ", location.getLongitude() + "," + location.getLatitude());
//            alTMapPoint.add(new TMapPoint(location.getLatitude(), location.getLongitude()));
//            tmapview.removeTMapPolyLine("path2");
//            for (int i = 0; i < alTMapPoint.size(); i++) {
//                //Log.d("tetetest", Integer.toString(alTMapPoint.size()));
//                tpolyline2.addLinePoint(alTMapPoint.get(i));
//            }
//            tmapview.addTMapPolyLine("path2", tpolyline2);
            //135~144 라인 위에있던거임
            realtimeLongitude = location.getLongitude(); //현재 경도
            realtimeLatitude = location.getLatitude();   //현재 위도
            Log.d("현재 내 위치 ", realtimeLongitude + ", " + realtimeLatitude);
            Toast.makeText(getApplicationContext(), "현재 내 위치 : " + realtimeLongitude + ", " + realtimeLatitude, Toast.LENGTH_SHORT).show();
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            TMapPoint tp = new TMapPoint(latitude, longitude);
            Log.d("테스트", tp.toString());
            Location locationA = new Location("point a");
            locationA.setLatitude(latitude);
            locationA.setLongitude(longitude);
            Location locationB = new Location("point b");
            locationB.setLatitude(destLatitude);
            locationB.setLongitude(destLongitude);
            desDist = Double.valueOf(locationA.distanceTo(locationB));
            if (desDist <= 100) {
                if (dest_arrive == false) {
                    arrive_destination();
                    //Place this where you no longer need to have the processor running
                    wakeLock.release();
                }
            } else {
                Log.d("목적지 미도착", desDist.toString());
            }
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
        setContentView(R.layout.activity_startguide);
        Context mContext = getApplicationContext();
        PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PARTIAL_WAKE_LOCK, "motionDetection:keepAwake");
        wakeLock.acquire();
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.guide_map);
        LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        tmapview = new TMapView(this);
        Intent intent = getIntent();
        try {
            result = new JSONObject(getIntent().getStringExtra("pathInfo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        gpsTracker = new GpsTracker(startGuide.this);
        realtimeLatitude = gpsTracker.getLatitude();
        realtimeLongitude = gpsTracker.getLongitude();
        latitude = intent.getExtras().getDouble("Latitude");
        longitude = intent.getExtras().getDouble("Longitude");
        tmapview.setLocationPoint(longitude, latitude);
        Log.d("189라인 ", longitude + "," + latitude);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");
        tmapview.setZoomLevel(10);
        //tmapview.setMapPosition(TMapView.POSITION_DEFAULT);
        Log.d("193라인 ", String.valueOf(TMapView.POSITION_DEFAULT));
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
        TextView totalDistance = (TextView) findViewById(R.id.totalDistance);
        TextView destAltitude = (TextView) findViewById(R.id.myAltitude);
        final int destT = intent.getExtras().getInt("destT");
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH시 mm분");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, destT);
        String now = dateFormat.format(cal.getTime());
        destTime.setText("도착예정시간 : " + now);
        Double totalDist_km = intent.getExtras().getDouble("totalDist_km");
        Double moved_dist = intent.getExtras().getDouble("moved_dist");
        Double remain = totalDist_km - moved_dist;
        //totalDistance.setText("남은 거리 : " + remain / 1000.0 + "km");
        altitude = intent.getExtras().getDouble("altitude");
        destAltitude.setText("현재 고도 : " + altitude);
        login_id = intent.getExtras().getString("login_id");
        TMapMarkerItem myMarker = new TMapMarkerItem();
        TMapMarkerItem destMarker = new TMapMarkerItem();
        myLongitude = intent.getExtras().getDouble("myLongitude"); //출발지 위도
        myLatitude = intent.getExtras().getDouble("myLatitude");   //출발지 경도
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

        double centerLong, centerLat;
        centerLong = (myLongitude + destLongitude) / 2;
        centerLat = (myLatitude + destLatitude) / 2;
        tmapview.setCenterPoint(centerLong, centerLat); //지도의 중심지점 좌표 (경도, 위도 순서)
        Log.d("261라인 ", centerLong + "," + centerLat);
        Log.d("내 위도  ", String.valueOf(realtimeLatitude));
        Log.d("내 경도  ", String.valueOf(realtimeLongitude));
        Log.d("내 고도  ", String.valueOf(altitude));
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
            dist = new String[pathData.size()];
            dist[i] = String.valueOf(distance);
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
            Log.d("경로이탈탐지: ", min + "m");
            getpeople_list();
            Vibrator vi = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            //진동객체
            vi.vibrate(1000);
            //1초간 진동
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setCancelable(false);
            builder.setTitle("경로 이탈 탐지").setMessage("경로 이탈이 탐지되었습니다. 5초 후에 자동으로 문자가 전송됩니다.");
            builder.setNegativeButton("전송 취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "문자 전송을 취소하였습니다.", Toast.LENGTH_SHORT).show();
                    flag = true;
                }
            });
            builder.setPositiveButton("메세지 전송", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendSms();
                    SmsManager smsManager = SmsManager.getDefault();
                    ArrayList<String> partMessage = smsManager.divideMessage("[보디가드]\n 경로 이탈이 탐지되었습니다.\n 사용자의 현재 위치는 " + "https://www.google.com/maps/search/+" + realtimeLatitude + ",+" + realtimeLongitude + "/ 입니다.");
                    smsManager.sendMultipartTextMessage(people_array[0], null, partMessage, null, null);
                    smsManager.sendMultipartTextMessage(people_array[1], null, partMessage, null, null);
                    Toast.makeText(getApplicationContext(), "문자가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
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

    private class people_list extends AsyncTask<String, Void, String> {
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

        task = new people_list();
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

    public String server_network_check(String host) {
        return "1";
    }
}