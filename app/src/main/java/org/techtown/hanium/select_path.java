package org.techtown.hanium;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.odsay.odsayandroidsdk.API;
import com.odsay.odsayandroidsdk.ODsayData;
import com.odsay.odsayandroidsdk.ODsayService;
import com.odsay.odsayandroidsdk.OnResultCallbackListener;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.TMapData;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class select_path extends AppCompatActivity {
    String stationName;
    Double longitude, latitude;
    //경도 : longitude 범위 : 127 < 1st argument
    //위도 : latitude 범위 : 37 < 2nd argument (path arrayList에 들어가는 순서)
    Double destLongitude, destLatitude;
    double startLat = 0;
    double startLong = 0;
    double destLat = 0;
    double destLong = 0;
    private GpsTracker gpsTracker;
    Geocoder coder;
    JSONObject result;
    JSONObject result2;
    JSONArray subPath = null;
    int totalTime = 0;
    double totalDistance = 0;
    TMapData tmapdata = new TMapData();
    ArrayList<ArrayList<String>> array;
    Boolean[] flags;
    JSONArray trafficLaneData = null;
    Element root;
    NodeList nodeListPoint;
    NodeList nodeListPointItem;
    JSONObject intInfo;
    ODsayService odsayService = null;
    ODsayService oDsayServiceForSubTrans = null;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    final int DIALOG_TIME = 2;
    Intent MarkerIntent;

    public void startActivity(int length) {
        for (int i = 0; i < length; i++) {
            if (flags[i] == false) {
                return;
            }
        }
        MarkerIntent = new Intent(getApplicationContext(), Marker.class);
        String login_id;
        Intent intent = getIntent();
        login_id = intent.getExtras().getString("log_ok_id");
        MarkerIntent.putExtra("log_ok_id", login_id);
        MarkerIntent.putExtra("curLongitude", longitude);
        Log.d(" intent 출발지 경도", String.valueOf(MarkerIntent));
        MarkerIntent.putExtra("curLatitude", latitude);
        Log.d(" intent 출발지 위도", String.valueOf(MarkerIntent));
        MarkerIntent.putExtra("destLongitude", destLongitude);
        Log.d(" intent 도착지 경도", String.valueOf(MarkerIntent));
        MarkerIntent.putExtra("destLatitude", destLatitude);
        Log.d(" intent 도착지 위도", String.valueOf(MarkerIntent));
        MarkerIntent.putExtra("pathDataArray", array);
        Log.d(" intent pathData 삽입", String.valueOf(MarkerIntent));
        MarkerIntent.putExtra("totalTime", totalTime);
        Log.d(" intent totalTime 삽입", String.valueOf(MarkerIntent));
        MarkerIntent.putExtra("totalDistance", totalDistance);
        Log.d(" intent totalDistance", String.valueOf(MarkerIntent));
        startActivity(MarkerIntent);
        Log.d("Activity 시작", String.valueOf(MarkerIntent));
    }

    public OnResultCallbackListener OnResultCallbackListener = new OnResultCallbackListener() {
        //200924 ODSay API의 콜백리스너 함수
        //호출 성공시 데이터 들어옴
        @Override
        public void onSuccess(ODsayData oDsayData, API api) {
            //200924 API 호출 성공 시
            //200924 호출한 메서드가 requestPubTransPathSearch 일 때
            Log.d("API 호출 성공", String.valueOf(api));
            result = oDsayData.getJson();
            JSONArray key = result.names();
            //200924 출발지~목적지까지의 대중교통 정보가 json으로 반환되고 우리는 result라는 json에 해당 결과 저장
            try {
                if (key.getString(0).equals("error")) {
                    startLat = latitude;
                    startLong = longitude;
                    destLat = destLatitude;
                    destLong = destLongitude;
                    TMapPoint startPoint = new TMapPoint(startLat, startLong);// 마커 놓을 좌표 (위도, 경도 순서)
                    TMapPoint destPoint = new TMapPoint(destLat, destLong); // 마커 놓을 좌표 (위도, 경도 순서)
                    tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, destPoint, new TMapData.FindPathDataAllListenerCallback() {
                        @Override
                        public void onFindPathDataAll(Document document) {
                            ArrayList<String> pathData = new ArrayList<String>();
                            root = document.getDocumentElement();
                            nodeListPoint = root.getElementsByTagName("Point");
                            NodeList totaldist = root.getElementsByTagName("tmap:totalDistance");
                            totalDistance = Integer.parseInt(totaldist.item(0).getFirstChild().getNodeValue());
                            NodeList totaltime = root.getElementsByTagName("tmap:totalTime");
                            totalTime = Integer.parseInt(totaltime.item(0).getFirstChild().getNodeValue()) / 60;
                            for (int i = 0; i < nodeListPoint.getLength(); i++) {
                                nodeListPointItem = nodeListPoint.item(i).getChildNodes();
                                for (int j = 0; j < nodeListPointItem.getLength(); j++) {
                                    if (nodeListPointItem.item(j).getNodeName().equals("coordinates")) {
                                        pathData.add(nodeListPointItem.item(j).getTextContent().trim());
                                        Log.d("도보데이터 ", nodeListPointItem.item(j).getTextContent().trim());
                                    }
                                }
                            }
                            array = new ArrayList<ArrayList<String>>(1);
                            flags = new Boolean[1];
                            flags[0] = true;
                            Log.d("flag 변경", String.valueOf(0));
                            array.add(0, pathData);
                            startActivity(1);
                        }
                    });
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    String mapObj = oDsayData.getJson().getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getString("mapObj");
                    oDsayServiceForSubTrans.requestLoadLane("0:0@" + mapObj, new OnResultCallbackListener() {
                        @Override
                        public void onSuccess(ODsayData oDsayData, API api) {
                            Log.d("API 호출 성공", String.valueOf(api));
                            result2 = oDsayData.getJson();
                            //노선그래픽 리턴값에서 class는 버스/지하철을 나타낸다. 1-> 버스, 2-> 지하철
                            try {
                                trafficLaneData = result2.getJSONObject("result").getJSONArray("lane");
                                Log.d("`노선그래픽 호출 완료", "");
                                totalTime = result.getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getInt("totalTime");
                                totalDistance = result.getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONObject("info").getDouble("totalDistance");
                                subPath = result.getJSONObject("result").getJSONArray("path").getJSONObject(0).getJSONArray("subPath");
                                //200924 이 이후 데이터 추출은 result를 기반으로 이루어짐
                                //result에서 데이터 받아와 파싱 후 subPath에 저장
                                array = new ArrayList<ArrayList<String>>(subPath.length());
                                flags = new Boolean[subPath.length()];
                                for (int k = 0; k < subPath.length(); k++) {
                                    flags[k] = false;
                                }
                                int trafficidx = 0;
                                for (int k = 0; k < subPath.length(); k++) {
                                    //200924 이게아마 대중교통 경로에서 서로다른 대중교통 갯수만큼 나올거야
                                    //200924 예를들면 도보(1) - 버스(2) - 지하철(3) - 버스(4) - 도보(5) - 지하철(6) 이면
                                    //200924 subPath.length()의 값은 6이 된다
                                    JSONObject temp = subPath.getJSONObject(k);
                                    //200924 또 temp라는 JSONObject를 선언해서 subPath의 수 만큼 데이터를 받아오는듯?
                                    intInfo = null;
                                    intInfo = new JSONObject();
                                    int tempTrafficType = temp.getInt(("trafficType"));
                                    //trafficType 1:지하철 2:버스 3:도보
                                    if (tempTrafficType != 3) {
                                        final int finalK = k;
                                        JSONArray tempTrafficLaneData = trafficLaneData.getJSONObject(trafficidx).getJSONArray("section").getJSONObject(0).getJSONArray("graphPos");
                                        trafficidx++;
                                        ArrayList<String> pathData = new ArrayList<String>();
                                        int length = tempTrafficLaneData.length();
                                        for (int i = 0; i < length; i++) {
                                            double trafficLat = tempTrafficLaneData.getJSONObject(i).getDouble("x");
                                            double trafficLong = tempTrafficLaneData.getJSONObject(i).getDouble("y");
                                            String tempPath = String.valueOf(trafficLat) + "," + String.valueOf(trafficLong);
                                            pathData.add(tempPath);
                                            Log.d("tempPath ", String.valueOf(tempPath));
                                        }
                                        flags[finalK] = true;
                                        Log.d("flag 변경", String.valueOf(finalK));
                                        array.add(finalK, pathData);
                                        Log.d("array에 데이터 넣음", String.valueOf(finalK));
                                        startActivity(subPath.length());
                                    } else if (tempTrafficType == 3) {
                                        //출발은 왠만하면 도보다.
                                        //그러면 시작점은 내 위치가 되겠지.
                                        //첫번째 도보의 시작점은 내 위치고 목적지는 다음 교통수단의 첫 위치이다.
                                        //여기서 구해야 할 정보는 출발지 위/경도 , 도착지 위/경도이다.
                                        if (k == 0) {
                                            startLat = latitude;
                                            startLong = longitude;
                                            JSONObject temp2 = subPath.getJSONObject(k + 1);
                                            destLat = temp2.getDouble("startY");
                                            destLong = temp2.getDouble("startX");
                                        } else if (k == subPath.length() - 1) {
                                            destLat = destLatitude;
                                            destLong = destLongitude;
                                            JSONObject temp2 = subPath.getJSONObject(k - 1);
                                            startLat = temp2.getDouble("endY");
                                            startLong = temp2.getDouble("endX");
                                        } else {
                                            JSONObject temp2 = subPath.getJSONObject(k - 1);
                                            startLat = temp2.getDouble("endY");
                                            startLong = temp2.getDouble("endX");
                                            JSONObject temp3 = subPath.getJSONObject(k + 1);
                                            destLat = temp3.getDouble("startY");
                                            destLong = temp3.getDouble("startX");
                                        }
                                        TMapPoint startPoint = new TMapPoint(startLat, startLong);// 마커 놓을 좌표 (위도, 경도 순서)
                                        TMapPoint destPoint = new TMapPoint(destLat, destLong); // 마커 놓을 좌표 (위도, 경도 순서)
                                        final int finalK1 = k;
                                        tmapdata.findPathDataAllType(TMapData.TMapPathType.PEDESTRIAN_PATH, startPoint, destPoint, new TMapData.FindPathDataAllListenerCallback() {
                                            @Override
                                            public void onFindPathDataAll(Document document) {
                                                ArrayList<String> pathData = new ArrayList<String>();
                                                root = document.getDocumentElement();
                                                nodeListPoint = root.getElementsByTagName("Point");
                                                for (int i = 0; i < nodeListPoint.getLength(); i++) {
                                                    nodeListPointItem = nodeListPoint.item(i).getChildNodes();
                                                    for (int j = 0; j < nodeListPointItem.getLength(); j++) {
                                                        if (nodeListPointItem.item(j).getNodeName().equals("coordinates")) {
                                                            pathData.add(nodeListPointItem.item(j).getTextContent().trim());
                                                            Log.d("도보데이터 ", nodeListPointItem.item(j).getTextContent().trim());
                                                        }
                                                    }
                                                }
                                                flags[finalK1] = true;
                                                Log.d("flag 변경", String.valueOf(finalK1));
                                                array.add(finalK1, pathData);
                                                Log.d("array에 데이터 넣음", String.valueOf(finalK1));
                                                startActivity(subPath.length());
                                            }
                                        });
                                        try {
                                            sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Log.d("traffic type ", String.valueOf(tempTrafficType));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.d("교통수단 경로좌표 실행 완료", String.valueOf(trafficLaneData));
                        }

                        @Override
                        public void onError(int i, String s, API api) {
                        }
                    });
                    //원래 activity 스타트 하는 부분
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("SearchPubTransPath", String.valueOf(result));
        }

        //에러 표출시 데이터
        @Override
        public void onError(int i, String errorMessage, API api) {
            Log.i("경로검색 실패", errorMessage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_path);
        final TMapView tmapview = new TMapView(this);
        final EditText editStart = (EditText) findViewById(R.id.editstart);
        final EditText dest = (EditText) findViewById(R.id.editTextDest);
        Intent intent = getIntent();
        if (intent.getExtras().getString("px") != null) {
            destLatitude = Double.parseDouble(intent.getExtras().getString("px"));
            destLongitude = Double.parseDouble(intent.getExtras().getString("py"));
            dest.setText(Double.toString(destLatitude) + "," + Double.toString(destLongitude));
        }
        RelativeLayout relativeLayout = new RelativeLayout(this);
        odsayService = ODsayService.init(getApplicationContext(), "o35DS9VMHDOCosWoVhEYWv43HTeN5uX6ID/cO660rlI");
        // 싱글톤 생성, Key 값을 활용하여 객체 생성
        odsayService.setReadTimeout(1000);
        // 데이터 획득 제한 시간(단위(초), default : 5초)
        odsayService.setConnectionTimeout(1000);
        // 서버 연결 제한 시간(단위(초), default : 5초)
        oDsayServiceForSubTrans = ODsayService.init(getApplicationContext(), "o35DS9VMHDOCosWoVhEYWv43HTeN5uX6ID/cO660rlI");
        oDsayServiceForSubTrans.setReadTimeout(1000);
        oDsayServiceForSubTrans.setConnectionTimeout(1000);
        Button completeSetPath = (Button) findViewById(R.id.completeSetPath);
        completeSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("경로 설정 버튼 눌림", "");
                odsayService.requestSearchPubTransPath(longitude.toString(), latitude.toString(), destLongitude.toString(), destLatitude.toString(),
                        "0", "0", "0", OnResultCallbackListener);
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
                editStart.setText(latitude + ", " + longitude);
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
                        destLatitude = list.get(0).getLatitude();
                        destLongitude = list.get(0).getLongitude();
                    }
                }
            }
        });
        Button btnDestMap = (Button) findViewById(R.id.btndestmap);
        btnDestMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                startActivityForResult(intent, 1102);
            }
        });
        Button btnSearchMap = (Button) findViewById(R.id.btnstartmap);
        btnSearchMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Map.class);
                startActivityForResult(intent, 1101);
            }
        });
        //if (destaltitude != null) dest.setText(destLatitude + ", " + destLongitude);
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
        if (requestCode == 1101) {
            if (resultCode == RESULT_OK) {
                getIntent();
                latitude = data.getExtras().getDouble("latitude");
                longitude = data.getExtras().getDouble("longitude");
                EditText editStart = (EditText) findViewById(R.id.editstart);
                editStart.setText(latitude + ", " + longitude);
            } else {
            }
        } else if (requestCode == 1102) {
            if (resultCode == RESULT_OK) {
                getIntent();
                destLatitude = data.getExtras().getDouble("latitude");
                destLongitude = data.getExtras().getDouble("longitude");
                EditText editDest = (EditText) findViewById(R.id.editTextDest);
                editDest.setText(destLatitude + ", " + destLongitude);
            } else {
            }
        } else {
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
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}