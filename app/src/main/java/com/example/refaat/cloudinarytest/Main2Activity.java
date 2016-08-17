package com.example.refaat.cloudinarytest;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main2Activity extends AppCompatActivity {

    EditText before,after;
    TextView percnt;
    Button convert;
    ImageView img;

    Cloudinary cloudinary;

    RelativeLayout theView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Map config = new HashMap();
        config.put("cloud_name", "app3roodk");
        config.put("api_key", "936844595166333");
        config.put("api_secret", "vnIDUi3QVRk-a_wnJjFkGDslxvM");
        cloudinary = new Cloudinary(config);

        before = (EditText) findViewById(R.id.et_before);
        after = (EditText) findViewById(R.id.et_after);
        percnt = (TextView)findViewById(R.id.t_percnt);
        convert = (Button) findViewById(R.id.convert);
        img = (ImageView) findViewById(R.id.img);
        theView = (RelativeLayout) findViewById(R.id.the_view);

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ConvertToBitmap(theView);

                img.setImageBitmap(bitmap);

                upload(bitmap);


            }
        });


    }

    private void upload(Bitmap bitmap) {
        final Uri uri = getImageUri(this, bitmap);
new Thread(new Runnable() {
    @Override
    public void run() {

        try {
            cloudinary.uploader().upload(
                    getApplicationContext().getContentResolver().openInputStream(uri),
                    ObjectUtils.emptyMap());
            getContentResolver().delete(uri,null,null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}).start();


    }

    private Bitmap ConvertToBitmap(RelativeLayout theView) {
        theView.setDrawingCacheEnabled(true);
        theView.buildDrawingCache();

        Bitmap map = theView.getDrawingCache();

        return map ;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
