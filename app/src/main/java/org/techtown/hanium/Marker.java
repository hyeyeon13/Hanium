package org.techtown.hanium;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;

public class Marker extends AppCompatActivity {
    ArrayList<String> pathData = new ArrayList<String>();
    Double myLongitude, myLatitude;
    Double destLongitude, destLatitude;
    String min;
    Double num_min;
    Double distance;
    String[] dist;
    String stationName;
    //경도 : longitude 범위 : 127
    //위도 : latitude 범위 : 37

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);

        RelativeLayout relativeLayout = new RelativeLayout(this);
        TMapView tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa9511b15f91f4c3e97455a7a1ac155d2");
        tmapview.setZoomLevel(10);
        tmapview.setMapPosition(TMapView.POSITION_DEFAULT);
        TMapTapi tMapTapi = new TMapTapi(this);

        Intent intent=getIntent();

        TMapMarkerItem myMarker = new TMapMarkerItem();
        TMapMarkerItem destMarker = new TMapMarkerItem();
        myLongitude=intent.getExtras().getDouble("curLongitude");
        myLatitude=intent.getExtras().getDouble("curLatitude");
        destLongitude = intent.getExtras().getDouble("destLongitude");
        destLatitude = intent.getExtras().getDouble("destLatitude");
        //stationName=intent.getExtras().getString("stationName");
        TMapPoint mytMapPoint = new TMapPoint(myLatitude,myLongitude);// 마커 놓을 좌표 (위도, 경도 순서)
        TMapPoint desttMappoint = new TMapPoint(destLatitude,destLongitude); // 마커 놓을 좌표 (위도, 경도 순서)

// 마커 아이콘
        Bitmap Start = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.point);
        Bitmap dest = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.bluepoint);
        tmapview.setTMapPathIcon(Start, dest);

        myMarker.setIcon(Start); // 마커 아이콘 지정
        myMarker.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        myMarker.setTMapPoint( mytMapPoint ); // 마커의 좌표 지정
        myMarker.setName("start"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("startPoint", myMarker); // 지도에 마커 추가

        destMarker.setIcon(dest);
        destMarker.setPosition(0.5f, 1.0f);;
        destMarker.setTMapPoint(desttMappoint);;
        destMarker.setName("dest"); // 마커의 타이틀 지정
        tmapview.addMarkerItem("destPoint", destMarker);

        double centerLong, centerLat;
        centerLong = (myLongitude+destLongitude)/2;
        centerLat = (myLatitude+destLatitude)/2;
        tmapview.setCenterPoint( centerLong, centerLat ); //지도의 중심지점 좌표 (경도, 위도 순서)
        Log.d("내 위도  ", String.valueOf(myLatitude));
        Log.d("내 경도  ", String.valueOf(myLongitude));
        Log.d("목적지 위도  ", String.valueOf(destLatitude));
        Log.d("목적지 경도  ", String.valueOf(destLongitude));

        TMapPoint leftTop = new TMapPoint(myLatitude, myLongitude);
        TMapPoint rightBottom = new TMapPoint(destLatitude, destLongitude);
        tmapview.zoomToTMapPoint(leftTop,rightBottom);

        TMapPolyLine tpolyline = new TMapPolyLine();
        tpolyline.setLineColor(Color.BLUE);
        tpolyline.setLineWidth(2);

        pathData = intent.getExtras().getStringArrayList("pathData");
        for(int i=0;i<pathData.size();i++){
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
            if(i==0){
                min=dist[0];
            }
            if(Double.valueOf(min)>Double.valueOf(dist[i])) {
                min=dist[i];
            }
        }
        Log.d("내위치 사이 거리: ",min+"m");
        tmapview.addTMapPolyLine("path", tpolyline);

        num_min= Double.valueOf(min);

        if(num_min>50)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            Log.d("경로이탈감지: ",min+"m");
            builder.setTitle("경로이탈감지!").setMessage("경로를 벗어났습니다");

            AlertDialog alertDialog = builder.create();

            alertDialog.show();
        }
        relativeLayout.addView(tmapview);

        setContentView(relativeLayout);
    }
}