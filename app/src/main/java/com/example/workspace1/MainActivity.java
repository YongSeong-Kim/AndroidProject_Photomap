package com.example.workspace1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById((R.id.toolbar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //왜 toolbar.에서 찾아줘야되나??
        toolbar.findViewById(R.id.menuButton).setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);

                 if(!drawer.isDrawerOpen(Gravity.LEFT))
                     drawer.openDrawer(Gravity.LEFT);
             }

         });



    }

}