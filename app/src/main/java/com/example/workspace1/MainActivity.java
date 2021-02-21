package com.example.workspace1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.workspace1.map.PhotoRegister;
import com.example.workspace1.sidetabs.Account;
import com.example.workspace1.sidetabs.Favorite;
import com.example.workspace1.sidetabs.Feed;
import com.example.workspace1.sidetabs.Popular;
import com.example.workspace1.sidetabs.Setting;
import com.example.workspace1.map.MapNaver;
import com.google.android.material.navigation.NavigationView;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.*;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import com.naver.maps.map.widget.CompassView;
import com.naver.maps.map.widget.LocationButtonView;
import com.naver.maps.map.widget.ScaleBarView;
import com.naver.maps.map.widget.ZoomControlView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private Context context = this;
    Fragment MapNaver, Account, Favorite, Feed, Popular, Setting;
    Toolbar toolbar;
    private FusedLocationSource mLocationSource;
    private NaverMap mNaverMap;
    private static ArrayList<Marker> markArray = new ArrayList<Marker>();
    private static ArrayList<InfoWindow> markInfoWindow = new ArrayList<InfoWindow>();
    private static ArrayList<LatLng> markLatLng = new ArrayList<LatLng>();
    OverlayOnClickListener onOverlayClickListener = new OverlayOnClickListener() ;

    HttpURLConnection con = null;
    String boundary = "******";
    String crlf = "\r\n";
    String twoHyphens = "--";
    OutputStream httpConnOutputStream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // inflater


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);

        toolbar = (Toolbar) findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//toolbar 보여주기


        toolbar.findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(!drawer.isDrawerOpen(Gravity.LEFT))
                     drawer.openDrawer(Gravity.LEFT);
             }
         });


        MapNaver = new MapNaver();
        Account = new Account();
        Favorite = new Favorite();
        Feed = new Feed();
        Popular = new Popular();
        Setting = new Setting();

        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.container, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);
        mLocationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawer.closeDrawers();

                int id = item.getItemId();

                if(id == R.id.map)
                {
                    onChangedFragemnt(1,null);
                    Toast.makeText(context, " 지도 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.account)
                {
                    onChangedFragemnt(2,null);
                    Toast.makeText(context, " 계정 정보를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.feed)
                {
                    onChangedFragemnt(3,null);
                    Toast.makeText(context, " 현재피드를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.popular)
                {
                    onChangedFragemnt(4,null);
                    Toast.makeText(context, " 인기글을 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.favorite)
                {
                    onChangedFragemnt(5,null);
                    Toast.makeText(context, "즐겨찾기를 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(id == R.id.setting)
                {
                    onChangedFragemnt(6,null);
                    Toast.makeText(context, " 설정을 확인합니다.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

    }


    public void onChangedFragemnt(int position,Bundle bundle)
    {
                Fragment fragment = null;

                switch (position){
                    case 1:
                        FragmentManager fm = getSupportFragmentManager();
                        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
                        if (mapFragment == null) {
                            mapFragment = MapFragment.newInstance();
                        }
                        mapFragment.getMapAsync(this);
                        fragment = mapFragment;
                        break;
                    case 2:
                        fragment = Account;
                        break;
                    case 3:
                        fragment = Favorite;
                        break;
                    case 4:
                        fragment = Feed;
                        break;
                    case 5:
                        fragment = Popular;
                        break;
                    case 6:
                        fragment = Setting;
                        break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {

        mNaverMap = naverMap;

        if(markArray.size()>0) {
            updateMarkers();
        }


        mNaverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
                if(markArray.size()>0) {
                    updateMarkers();
                }

            }
        });




        naverMap.setOnMapLongClickListener(new NaverMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull PointF pointF, @NonNull LatLng latLng) {


//                CameraPosition cameraPosition = mNaverMap.getCameraPosition();
//
//                System.out.println(latLng.distanceTo(cameraPosition.target.toLatLng()));
//                Log.d("Camera position", cameraPosition.target.latitude +" "+ cameraPosition.target.longitude);
//                Log.d("Click position",latLng.latitude + " " + latLng.longitude);


                Intent intent = new Intent(MainActivity.this, PhotoRegister.class);
                intent.putExtra("latitude",latLng.latitude);
                intent.putExtra("longtitude",latLng.longitude);
                startActivityForResult(intent,1234);
            }
        });

        mNaverMap.setLocationSource(mLocationSource);
        UiSettings uiSettings = mNaverMap.getUiSettings();
        uiSettings.setZoomControlEnabled(true); // 기본값 : true
        uiSettings.setLocationButtonEnabled(true); // 기본값 : false
        mNaverMap.setLocationTrackingMode(LocationTrackingMode.NoFollow);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (mLocationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!mLocationSource.isActivated()) { // 권한 거부됨
                mNaverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) { // PhotoRegister에서 저장한 사진의 마커가 제대로 저장된다면 marker 설정.
            Marker marker = new Marker();
            Log.d("상태코드", "여기");
            Double templatitude = data.getDoubleExtra("latitude", 0);
            Double templongtitude = data.getDoubleExtra("longtitude", 0);
            Toast.makeText(context, templatitude+ "\n" +templongtitude, Toast.LENGTH_SHORT).show();
            marker.setPosition(new LatLng(templatitude, templongtitude));
            marker.setMap(mNaverMap);
            marker.setWidth(100);
            marker.setHeight(100);

            Overlay o = (Overlay) marker;
//            o.setMinZoom(15);
//            o.setMinZoomInclusive(true);

            marker.setIcon(OverlayImage.fromResource(R.drawable.marker));
            markArray.add(marker);

            marker.setOnClickListener(onOverlayClickListener);

        } else if (resultCode == 2) {

        }
    }

    public void tryTo(View view) {

        CameraPosition cameraPosition = mNaverMap.getCameraPosition();
        System.out.println(cameraPosition.target.latitude +" "+ cameraPosition.target.longitude);



        LatLngBounds l = mNaverMap.getContentBounds();//지도 콘텐츠 영역에 대한 LatlngBounds를 반환.
        System.out.println(l.contains(new LatLng(37.5567, 126.9860)));

//        URL url = null;
//        try {
//            url = new URL("http://"+getString(R.string.ip)+":8000/photo/post/");
//            con = (HttpURLConnection) url.openConnection();
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setUseCaches(false);
//            con.setRequestMethod("GET");
//            con.setRequestProperty("Connection", "Keep-Alive");
//            con.setRequestProperty("Cache-Control", "no-cache");
////            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
//
////            int status = con.getResponseCode();
//
//            InputStream responseStream = new BufferedInputStream(con.getInputStream());
//            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
//            String line = "";
//            StringBuilder stringBuilder = new StringBuilder();
//            while((line = responseStreamReader.readLine()) !=null)
//            {
//                stringBuilder.append(line).append("\n");
//            }
//            responseStreamReader.close();
//            String s = stringBuilder.toString();
//
//            Toast.makeText(this, s , Toast.LENGTH_LONG).show();
//
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    public void  updateMarkers()
    {
        LatLngBounds mapBounds = mNaverMap.getContentBounds();//지도 콘텐츠 영역에 대한 LatlngBounds를 반환.
        for(int i = 0; i < markArray.size(); i++)
        {
            if(mapBounds.contains(markArray.get(i).getPosition()))
            {
                markArray.get(i).setMap(mNaverMap);
                markArray.get(i).setOnClickListener(onOverlayClickListener );
            }
            else
            {
                markArray.get(i).setMap(null);//마커 안보이도록.
            }
        }

    }

    class OverlayOnClickListener implements Overlay.OnClickListener {

        int index;// pirmary키를 서버에서 받아와서 삭제하면 될듯.
        InfoWindow infoWindow = new InfoWindow();


        @Override
        public boolean onClick(@NonNull Overlay overlay) {

            Marker marker = (Marker) overlay;
            mNaverMap.setOnMapClickListener((coord,point)->{//지도를 클릭하면 닫.
                infoWindow.close();
            });

            if (marker.getInfoWindow() == null)
            {
                infoWindow.open(marker);



            }
            else infoWindow.close();


//            overlay.setMaxZoom(14);
//            overlay.setMaxZoomInclusive(true);



            infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(getApplicationContext()) {
                @NonNull
                @Override
                protected View getContentView(@NonNull InfoWindow infoWindow) {

                    View view = View.inflate(MainActivity.this, R.layout.photo_point, null);

                    TextView txtTitle = (TextView) view.findViewById(R.id.txttitle);

                    ImageView imagePoint = (ImageView) view.findViewById(R.id.imagepoint);

                    txtTitle.setText("강릉");
                    imagePoint.setImageResource(R.drawable.example);

//                    infoWindow.setOnClickListener(new Overlay.OnClickListener() {
//                        @Override
//                        public boolean onClick(@NonNull Overlay overlay) {
//                            System.out.println("삭재.");
//
//                            return true;
//                        }
//                    });

                    System.out.println(txtTitle);
                    System.out.println(imagePoint);
//                    System.out.println(photo_delete);
                    return view;
                }
            });




            return true;
        }
    }

}