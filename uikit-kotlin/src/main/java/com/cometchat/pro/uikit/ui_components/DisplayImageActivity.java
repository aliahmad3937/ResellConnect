package com.cometchat.pro.uikit.ui_components;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.cometchat.pro.models.MediaMessage;
import com.cometchat.pro.uikit.R;


public class DisplayImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);


//        Glide.with(this).asBitmap().load( ((MediaMessage) CommonData.INSTANCE.getBaseMessage()).getAttachment().getFileUrl()).into( new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                findViewById<ImageView>(R.id.display_img).setImageBitmap(resource);
//            }
//
//
//        });

    }
    //2055
}