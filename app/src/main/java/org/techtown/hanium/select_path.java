package org.techtown.hanium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;


public class select_path extends AppCompatActivity {
    String stationName;
    Double longitude, latitude, altitude;
    //경도 : longitude 범위 : 127
    //위도 : latitude 범위 : 37
    Double destLongitude, destLatitude, destaltitude;
    TextView tv;
    ToggleButton tb;
    boolean flag1 = false;
    private GpsTracker gpsTracker;
    Geocoder coder;
    JSONObject result;
    JSONArray subPath = null;
    TMapData tmapdata = new TMapData();
    ArrayList<String> pathData = new ArrayList<String>();
    Element root;
    NodeList nodeListPlacemark;
    NodeList nodeListCoordinates;
    NodeList nodeListPoint;
    NodeList nodeListPlacemarkItem;
    NodeList nodeListPointItem;
    JSONObject pathInfo = new JSONObject();
    JSONObject intInfo;
    JSONArray intervalPath = new JSONArray();
    ODsayService odsayService = null;
    int startStnID, endStnID;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final int DIALOG_TIME = 2;

    //Thread thread1, thread2, thread3, thread4, thread5;
    Button button;
    // 콜백 함수 구현
    public OnResultCallbackListener OnResultCallbackListener = new OnResultCallbackListener() {
        //200924 ODSay API의 콜백함수
        // 호출 성공시 데이터 들어옴
        //requestPubTransPath와 requestBusLaneDetail이 같은 리스너(콜백)사용
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            //200924 API 호출 성공 시
            //api 호출 성공 시 if문을 통해 api 확인하여 분기
            if(api == API.SEARCH_PUB_TRANS_PATH){
                //200924 호출한 메서드가 requestPubTransPathSearch 일 때
                Log.d("API 호출 성공", String.valueOf(api));
                result = oDsayData.getJson();
                //200924 출발지~목적지까지의 대중교통 정보가 json으로 반환되고 우리는 result라는 json에 해당 결과 저장
                try {
                    subPath = result.getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONArray("subPath");
                    flag1=true;
                    //200924 이 이후 데이터 추출은 result를 기반으로 이루어짐
                    //result에서 데이터 받아와 파싱 후 subPath에 저장
                    //Log.d("검사횟수", String.valueOf(subPath.length()));
                    for(int k=0;k<subPath.length();k++){
                        //200924 이게아마 대중교통 경로에서 서로다른 대중교통 갯수만큼 나올거야
                        //200924 예를들면 도보(1) - 버스(2) - 지하철(3) - 버스(4) - 도보(5) - 지하철(6) 이면
                        //200924 subPath.length()의 값은 6이 된다
                        JSONObject temp = subPath.getJSONObject(k);
                        //200924 또 temp라는 JSONObject를 선언해서 subPath의 수 만큼 데이터를 받아오는듯?
                        intInfo = null;
                        intInfo = new JSONObject();
                        int tempTrafficType = temp.getInt(("trafficType"));
                        //trafficType 1:지하철 2:버스 3:도보
                        if(tempTrafficType==1) {
                            //200924 subPath가 여러개인데 구분하는 기준은 위에 있어 1은 지하철 2는 버스 3은 도보
                            //200924 이경우는 type=1인 경우 (지하철)
                            //지하철
                            intInfo.put("trafficType", tempTrafficType);
                            intInfo.put("startX", temp.getDouble("startX"));//시작점 경도(출발역)
                            intInfo.put("startY", temp.getDouble("startY"));//시작점 위도(출발역)
                            intInfo.put("endX", temp.getDouble("endX"));//도착점 경도(도착역)
                            intInfo.put("endY", temp.getDouble("endY"));//도착점 위도(도착역)
                            intInfo.put("transID", temp.getJSONArray("lane").getJSONObject(0).getInt("subwayCode"));//노선번호
                            intInfo.put("startID", temp.getInt("startID"));//출발역 ID
                            intInfo.put("endID", temp.getInt("endID"));//도착역 ID
                            intervalPath.put(intInfo);
                            //200924 위에 전역변수에 보면 intervelPath라는 JSONArray를 선언함. 나중에 교통수단별로 다시 ODSay에 넣어서 상세정보 받아야 하니까.
                            intInfo = null;
                            Log.d("검사횟수", String.valueOf(subPath.length()));
                        } else if(tempTrafficType==2){
                            //버스
                            intInfo.put("trafficType", tempTrafficType);
                            intInfo.put("startX", temp.getDouble("startX"));//출발정류장 경도
                            intInfo.put("startY", temp.getDouble("startY"));//출발정류장 위도
                            intInfo.put("endX", temp.getDouble("endX"));//도착정류장 경도
                            intInfo.put("endY", temp.getDouble("endY"));//도착정류장 위도
                            intInfo.put("transID", temp.getJSONArray("lane").getJSONObject(0).getInt("busID"));//버스ID
                            startStnID = temp.getInt("startID");//출발정류장 ID 실제 공공정보시스템과 상이함
                            endStnID = temp.getInt("endID");//도착정류장 ID 실제 공공정보시스템과 상이함
                            requestBusLaneDetail(temp);
                            //startStnID, endStnID를 이용 requestBusLaneDetail(busID)를 통해 해당 버스의 경로 중 startStnID, endStnID와 일치하는 idx 리턴
                            intervalPath.put(intInfo);
                            intInfo = null;

                        }
                        Log.d("traffic type ", String.valueOf(tempTrafficType));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if(api==API.BUS_LANE_DETAIL){
                //200924 여기가 문제인 부분임. 어제인가 이혜연이 지적했던 것처럼 95 라인에 if만 있었어 근데 내가 그냥 else if 만든거. 왜냐면
                //200924 ODSay API를 여러번 호출해. 인스턴스는 78라인에 한번만 선언하는데 여러번 쓰는거지. 원래 이래도 되나싶다.
                //200924 하여간 이부분은 추후에 수정해야함
                //200924 이거는 결론적으로 141라인이 성공적으로 수행되면 콜백으로 들어가는 부분임.
               Log.d("API 호출 성공", String.valueOf(api));
                result = null;
                result = oDsayData.getJson();
                JSONArray station = null;
                int startID=0;
                int endID=0;
                try {
                    station = result.getJSONObject("result").getJSONArray("station");
                    //해당 버스의 전체 경로
                    Log.d("station 정보 받아옴", String.valueOf(station.length()));
                    for(int i=0;i<station.length();i++){
                        if(station.getJSONObject(i).getInt("stationID")==startStnID){
                            //stationID가 startStnID와 같으면 idx 받아옴
                            startID = station.getJSONObject(i).getInt("idx");
                        }else if(station.getJSONObject(i).getInt("stationID")==endStnID){
                            //stationID가 endStnID와 같으면 idx 받아옴
                            endID = station.getJSONObject(i).getInt("idx");
                        }
                    }
                    intInfo.put("startID", startID);
                    intInfo.put("endID", endID);
                    //trafficType==2일 때 와 연동하여 같은 JSON에 값 입력
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // 에러 표출시 데이터
        @Override
        public void onError(int i, String errorMessage, API api) {

            Log.i("경로검색 실패",errorMessage);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_path);

        EditText editText = (EditText) findViewById(R.id.editstart);
      //  final EditText editText1 = (EditText) findViewById(R.id.editText1);
        final EditText dest = (EditText)findViewById(R.id.editTextDest);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        odsayService = ODsayService.init(getApplicationContext(), "o35DS9VMHDOCosWoVhEYWv43HTeN5uX6ID/cO660rlI");
        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService.setReadTimeout(5000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(5000);
        // 서버 연결 제한 시간(단위(초), default : 5초)


        Button completeSetPath = (Button) findViewById(R.id.completeSetPath);
        completeSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pathData.clear(); //pathData 초기화
                odsayService.requestSearchPubTransPath(longitude.toString(), latitude.toString(), destLongitude.toString(), destLatitude.toString(),
                        "0", "0", "0", OnResultCallbackListener);
//               while(true){
//                    if(flag1==true) {
//                        break;
//                    } else Log.d("API 호출 중", String.valueOf(result));
//                }
                Log.d("callback 호출 끝", String.valueOf(result));
                //출발지부터 목적지까지의 대중교통 경로 요청
                //requestPubTransPath();
                TMapPoint startPoint = new TMapPoint(latitude,longitude);// 마커 놓을 좌표 (위도, 경도 순서)
                TMapPoint destPoint = new TMapPoint(destLatitude,destLongitude); // 마커 놓을 좌표 (위도, 경도 순서)
                //바로아래라인은 출발지부터 목적지까지의 도보경로를 리턴. trafficeType==3일 때 아래 api 사용하여 경로 좌표 쌍 받아낼 예정
                tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, destPoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        root = document.getDocumentElement();
                        nodeListPoint = root.getElementsByTagName("Point");
                        for( int i=0; i<nodeListPoint.getLength(); i++ ) {
                            nodeListPointItem = nodeListPoint.item(i).getChildNodes();
                            for( int j=0; j<nodeListPointItem.getLength(); j++ ) {
                                if( nodeListPointItem.item(j).getNodeName().equals("coordinates") ) {
                                    pathData.add(nodeListPointItem.item(j).getTextContent().trim());
                                    Log.d("debug", nodeListPointItem.item(j).getTextContent().trim() );
                                }
                            }
                        }
                        Intent intent = new Intent(getApplicationContext(), Marker.class);
                        intent.putExtra("curLongitude", longitude);
                        intent.putExtra("curLatitude", latitude);
                        intent.putExtra("destLongitude", destLongitude);
                        intent.putExtra("destLatitude", destLatitude);
                        intent.putExtra("pathData", pathData);
                        //pathData에 trafficeType별로 돌린 경도 위도 쌍을 넣어 intent에 넣어 Marker.java로 전달
                        startActivity(intent);
                    }
                });

            }

        });
        if (checkLocationServicesStatus()) {
            checkRunTimePermission();
        } else {
            showDialogForLocationServiceSetting();
        }

        Button ShowLocationButton = (Button) findViewById(R.id.currentLocation);
        ShowLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                gpsTracker = new GpsTracker(select_path.this);
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                Intent intent = new Intent(getApplicationContext(), Marker.class);
                intent.putExtra("stationName", stationName);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
               // startActivity(intent);
                EditText editText = (EditText) findViewById(R.id.editstart);
                editText.setText(latitude + ", " + longitude);
            }
        });
        coder = new Geocoder(this);

        Button button1 = (Button) findViewById(R.id.btnSearchDest);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Address> list = null;
                String strDest = dest.getText().toString();
                try {
                    list = coder.getFromLocationName(strDest, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test", "입출력 오류 - 서버에서 주소변환 중 에러 발생");
                }
                if (list != null) {
                    if (list.size() == 0) {
                        dest.setText("해당되는 주소 정보는 없습니다");
                    } else {
                        dest.setText(list.get(0).getLatitude() + ", " + list.get(0).getLongitude());
                        //          list.get(0).getCountryName();  // 국가명
                        //          list.get(0).getLatitude();        // 위도
                        //          list.get(0).getLongitude();    // 경도
                        destLatitude = list.get(0).getLatitude();
                        destLongitude = list.get(0).getLongitude();
                    }
                }
            }
        });
//        Button btnDestMap = (Button) findViewById(R.id.btnSearchDest);
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TMapPoint destPoint = new TMapPoint(destLatitude,destLongitude); // 마커 놓을 좌표 (위도, 경도 순서)
//                Intent intent = new Intent(getApplicationContext(), Marker.class);
//                intent.putExtra("destLongitude", destLongitude);
//                intent.putExtra("destLatitude", destLatitude);
//                startActivity(intent);
//            }
//        });
        Button button=(Button)findViewById(R.id.timeselect);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME);
            }
        });
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;
            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(select_path.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(select_path.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
    synchronized void  requestPubTransPath(){
        odsayService.requestSearchPubTransPath(longitude.toString(), latitude.toString(), destLongitude.toString(), destLatitude.toString(), "0", "0", "0", OnResultCallbackListener);
    }

    synchronized void requestBusLaneDetail(JSONObject temp) throws JSONException {
        odsayService.requestBusLaneDetail(String.valueOf(temp.getJSONArray("lane").getJSONObject(0).getInt("busID")), OnResultCallbackListener);
    }
    void checkRunTimePermission() {
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(select_path.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(select_path.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(select_path.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(select_path.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(select_path.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(select_path.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(select_path.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        switch(id){
            case DIALOG_TIME :
                TimePickerDialog tpd =
                        new TimePickerDialog(select_path.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view,
                                                          int hourOfDay, int minute) {
                                        TextView textView=(TextView)findViewById(R.id.textView3);
                                        textView.setText(hourOfDay +"시 " + minute+"분");
                                    }
                                }, // 값설정시 호출될 리스너 등록
                                4,19, false); // 기본값 시분 등록
                // true : 24 시간(0~23) 표시
                // false : 오전/오후 항목이 생김
                return tpd;
        }
        return super.onCreateDialog(id);
    }

}


