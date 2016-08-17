package com.example.refaat.cloudinarytest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.github.ybq.android.spinkit.SpinKitView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static String postID;
    HashMap<String, Image_model> imagesMap;
    // String token = "EAACEdEose0cBAPDGZAFC14yNzU383gAl65o0wIgQysoZCLUSU3Jbad6Hp93KxXnTHJ3SsBWJ2ItZCPw6UFLFpxysIWqOis2nXvIJ2AVhlI1AeYfapp1baIH5AZCW3PoX0NMlJ5XdoXQny4D9BLD7Lgpezbc8KTnT4KUBoqG6IgZDZD";
    Button tags, prep, create, pub, show;

    Cloudinary cloudinary;

    Button addNewItem;
    LinearLayout itemsContainer;

    HashMap<Integer, UiItem> mapItems;

    Uri priceUri, ssssUri;

    Boolean initCode = true;
    int rc;

    private Uri mFileUri = null;
    private String m_Text = "";

//  private int generateUniqueInteger(){
//
//      ArrayList<Integer> list = new ArrayList<Integer>();
//      for (int i=1; i<100; i++) {
//          list.add(new Integer(i));
//      }
//      Collections.shuffle(list);
//         return list.get(15);
//    }


    static public String getCurrentDate(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(getApplicationContext(), "275139032860396");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //   AppEventsLogger.activateApp(getApplication());


        //Toast.makeText(this,getCurrentDate(new Date()).substring(9,16),Toast.LENGTH_LONG).show();

        // priceUri = Uri.parse("android.resource://com.example.refaat.cloudinarytest/drawable/price.png");;

        priceUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.price)
                + '/' + getResources().getResourceTypeName(R.drawable.price) + '/' + getResources().getResourceEntryName(R.drawable.price));

        ssssUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.ssssss)
                + '/' + getResources().getResourceTypeName(R.drawable.ssssss) + '/' + getResources().getResourceEntryName(R.drawable.ssssss));

        tags = (Button) findViewById(R.id.tags);
        prep = (Button) findViewById(R.id.preb);
        create = (Button) findViewById(R.id.create);
        pub = (Button) findViewById(R.id.pub);
        show = (Button) findViewById(R.id.show);


        Map config = new HashMap();
        config.put("cloud_name", "app3roodk");
        config.put("api_key", "936844595166333");
        config.put("api_secret", "vnIDUi3QVRk-a_wnJjFkGDslxvM");
        cloudinary = new Cloudinary(config);

        imagesMap = new HashMap<>();
        mapItems = new HashMap<>();
        itemsContainer = (LinearLayout) findViewById(R.id.items_container);

        addNewItem = (Button) findViewById(R.id.add_new_item);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inflateNewItem();
                if (itemsContainer.getChildCount() == 3) {
                    addNewItem.setVisibility(View.GONE);
                }
            }
        });

    }

    private void inflateNewItem() {
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.add_offer_item, null);
        itemsContainer.addView(rootView);

        final UiItem item = new UiItem();
        item.setUniqueNo(Integer.parseInt(getCurrentDate(new Date()).substring(11,16)));
        item.setRootV(rootView);
        mapItems.put(item.getUniqueNo(),item);

        rootView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (final Uploadclass uploadclass : mapItems.get(item.getUniqueNo()).imagesMap.values()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cloudinary.uploader().destroy(uploadclass.getPublic_id(), ObjectUtils.emptyMap());
                                mapItems.get(item.getUniqueNo()).imagesMap.remove(uploadclass.getUniqueKey());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                itemsContainer.removeView(rootView);
                addNewItem.setVisibility(View.VISIBLE);
            }
        });

        rootView.findViewById(R.id.add_new_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage(item.getUniqueNo());
            }
        });
    }

    private void selectImage(final int i) {

        final CharSequence[] items = {"إلتقط صوره!", "إختار صوره",
                "إلغاء"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ضيف صور العرض");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("إلتقط صوره!")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mFileUri);
                    startActivityForResult(intent, i);
                } else if (items[item].equals("إختار صوره")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "إختار صوره"),
                            i);
                } else if (items[item].equals("إلغاء")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (initCode) { // this boolean for passing the requestCode through cropActivity without change
            rc = requestCode;
            initCode = false;
        }
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("3roodk").setAutoZoomEnabled(true)
                        .setAspectRatio(3, 2)
                        .start(this);
            } else {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                inflateNewImage(resultUri, rc);


            }

        }
    }

    private void inflateNewImage(final Uri uri, int requestCode) {
        initCode = true;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootImage = inflater.inflate(R.layout.image_group, null);

        final String key = UUID.randomUUID().toString();
        final UiItem item = mapItems.get(requestCode);

        LinearLayout lytImagesGroupContainer = null;
        ImageButton item_delete_btn = null;
        if (itemsContainer != null) {
            lytImagesGroupContainer = (LinearLayout) mapItems.get(requestCode).getRootV().findViewById(R.id.imgscontainer);
            lytImagesGroupContainer.addView(rootImage);
            item_delete_btn = (ImageButton) mapItems.get(requestCode).getRootV().findViewById(R.id.delete_item);
        }

        if (lytImagesGroupContainer.getChildCount() ==4) {
            item.getRootV().findViewById(R.id.add_new_image).setVisibility(View.GONE);
        }

        final ImageView imgOffer = (ImageView) rootImage.findViewById(R.id.theimage);
        Glide.with(this).load(uri).into(imgOffer);
        imgOffer.setAlpha(0.5f);
        final ImageView imgDone = (ImageView) rootImage.findViewById(R.id.img_done);
        final ImageButton del_image = (ImageButton) rootImage.findViewById(R.id.delete_img);
        final ImageButton img_refresh = (ImageButton) rootImage.findViewById(R.id.img_refresh);
        final SpinKitView progress = (SpinKitView) rootImage.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);
        //showMessageToast("جاري رفع الصورة");


        final ImageButton finalItem_delete_btn = item_delete_btn;

        final Uploadclass uploadImage =
                new Uploadclass(uri, imgOffer, imgDone, del_image, img_refresh, progress, item_delete_btn);
        uploadImage.setUniqueKey(key);

        item.imagesMap.put(key,uploadImage);
        item.imagesMap.get(key).execute();
        if (item_delete_btn != null) {
            item_delete_btn.setVisibility(View.GONE);}

        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalItem_delete_btn != null) {
                    finalItem_delete_btn.setVisibility(View.GONE);
                }
                del_image.setVisibility(View.GONE);
                img_refresh.setVisibility(View.GONE);
                item.imagesMap.remove(key);
                item.imagesMap.put(key,uploadImage);
                item.imagesMap.get(key).execute();
                progress.setVisibility(View.VISIBLE);
            }
        });
        final LinearLayout fLytImagesGroupContainer = lytImagesGroupContainer;
        del_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!item.imagesMap.get(key).isDone())
                    if (item.imagesMap.size() != 0) {
                        item.imagesMap.get(key).cancel(true);
                    } else
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    cloudinary.uploader().destroy(
                                            item.imagesMap.get(key).getPublic_id(), ObjectUtils.emptyMap());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                item.imagesMap.remove(key);
                fLytImagesGroupContainer.findViewById(R.id.add_new_image).setVisibility(View.VISIBLE);
                fLytImagesGroupContainer.removeView(rootImage);
            }
        });
    }


    private void showMessageToast(String msg) {
        try {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
        }
    }

    class UiItem {
        private int uniqueNo;
        private View rootV;
        HashMap<String,Uploadclass> imagesMap = new HashMap<>();

        public int getUniqueNo() {
            return uniqueNo;
        }

        public void setUniqueNo(int uniqueNo) {
            this.uniqueNo = uniqueNo;
        }

        public View getRootV() {
            return rootV;
        }

        public void setRootV(View rootV) {
            this.rootV = rootV;
        }
    }

    class Uploadclass extends AsyncTask<Void, Void, HashMap> {
        private String public_id;
        private String Url;
        private String uniqueKey;

        boolean done, success;
        ImageView imageView, imageViewdone;
        Uri uri;
        SpinKitView progress;
        ImageButton delete_item;
        ImageButton imgdel, imgRefresh;

        public Uploadclass(Uri imageUri, ImageView imageView, ImageView imgdn, ImageButton del, ImageButton ref, SpinKitView progress, ImageButton deleteItem) {
            this.imageView = imageView;
            this.imageViewdone = imgdn;
            this.uri = imageUri;
            this.imgdel = del;
            this.imgRefresh = ref;
            this.progress = progress;
            this.delete_item = deleteItem;
        }

        public boolean isDone() {
            return done;
        }

        public String getUniqueKey() {
            return uniqueKey;
        }

        public void setUniqueKey(String uniqueKey) {
            this.uniqueKey = uniqueKey;
        }

        public String getUrl() {
            return Url;
        }

        public void setUrl(String url) {
            Url = url;
        }

        public String getPublic_id() {
            return public_id;
        }

        public void setPublic_id(String public_id) {
            this.public_id = public_id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Url = null;
            success = false;
            done = false;
        }

        @Override
        protected HashMap doInBackground(Void... voids) {
            HashMap uploadResult = null;
            try {
                uploadResult = (HashMap) cloudinary.uploader().upload(getApplicationContext().getContentResolver()
                        .openInputStream(uri), ObjectUtils.emptyMap());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return uploadResult;
        }

        @Override
        protected void onPostExecute(HashMap response) {
            super.onPostExecute(response);

            progress.setVisibility(View.GONE);
            imgdel.setVisibility(View.VISIBLE);
            delete_item.setVisibility(View.VISIBLE);

            if (response != null && response.get("url") != null) {
                Url = (String) response.get("url");
                public_id = (String) response.get("public_id");

                imageView.setAlpha(1.0f);
                imageViewdone.setVisibility(View.VISIBLE);
                imgdel.setVisibility(View.VISIBLE);
                showMessageToast("تم رفع الصورة");
                done = true;
                success = true;
            } else {
                Url = null;
                done = true;
                success = false;
                imgRefresh.setVisibility(View.VISIBLE);
                showMessageToast("حدث خطأ أثناء رفع الصورة");
            }
        }
    }
}
    /*

    public void addtag(View view) {

        final String[] ids_array = ids.toArray(new String[ids.size()]);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ادخل اسم التاج");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
             /*   new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cloudinary.uploader().addTag(m_Text, ids_array, ObjectUtils.emptyMap());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();xx
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
        prep.setVisibility(View.VISIBLE);

    }

    public void prepare(View view) {

        for (int i = 0; i < list_Uris.size(); i++) {

            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {

              /*  Transformation transformation = new Transformation();
                transformation.gravity("south_east");
                transformation.overlay("3roodk_Logo_etnsc5.png");
                try {
                    transformation.overlay(
                            (String) cloudinary.uploader().upload(
                                    getApplicationContext().getContentResolver().openInputStream(priceUri),
                                    ObjectUtils.emptyMap()).get("public_id"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                   String id = (String) cloudinary.uploader().upload(
                            getApplicationContext().getContentResolver().openInputStream(list_Uris.get(finalI)),
                            //ObjectUtils.emptyMap());
                            ObjectUtils.asMap("transformation", transformation, "tags","testo")).get("public_id");
                } catch (IOException e) {
                    e.printStackTrace();
                }xx


                    Transformation transformation = new Transformation()
                            .gravity("north_west").height(95).overlay("3roodk_Logo_etnsc5").crop("scale").chain()
                            .gravity("south_east").overlay("jffqr5ke6hzeadk2ryuf");

                    try {
                        cloudinary.uploader().upload(
                                getApplicationContext().getContentResolver().openInputStream(list_Uris.get(finalI)),
                                //ObjectUtils.emptyMap());
                                ObjectUtils.asMap("transformation", transformation, "tags", m_Text));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        showMessageToast("جارى رفع الصور استنى دقيقه كدا وبعدين اعمل create");


        create.setVisibility(View.VISIBLE);
    }

    public void createGif(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                // cloudinary.url().type("multi").imageTag("bank");


                try {

                    Transformation transformation = new Transformation();
                    transformation.delay(1600);


                    Map delay = new HashMap();
                    delay.put("transformation", transformation);
                    gifUrl = (String) cloudinary.uploader().multi(m_Text, delay).get("url");


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
        pub.setVisibility(View.VISIBLE);
    }

    public void postFacebok(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("اكتب كلام للبوست");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg = input.getText().toString();

                AccessToken accessToken = new AccessToken(token, "275139032860396", "830395877018340", null, null, null, null, null);

                String pgeId = "1591626347747816";

                Bundle params = new Bundle();
                params.putString("link", gifUrl);
                params.putString("message", msg);



        /* make the API call xx

                new GraphRequest(
                        accessToken,
                        "/" + pgeId + "/feed",
                        params,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
            /* handle the result xx
                                try {
                                    postID = (String) response.getJSONObject().get("id");
                                    showMsg(postID);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                ).executeAsync();


            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        show.setVisibility(View.VISIBLE);
    }

    private void showMsg(String id) {

        Toast.makeText(this, "res = " + id, Toast.LENGTH_LONG).show();
    }

    public void showPost(View view) {
        startActivity(getOpenFacebookIntent(this));
    }

    public Intent getOpenFacebookIntent(Context context) {

        showMessageToast("شوف اول بوست لما الصفحه تفتح");

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/830395877018340/"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/best.android.games4u/"));
        }
    }


}


*/

