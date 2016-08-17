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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    static String postID;
    HashMap<String, Image_model> imagesMap;
    int decreasedValueFormIndex1 = 0;
    int decreasedValueFormIndex2 = 0;
    // String token = "EAACEdEose0cBAPDGZAFC14yNzU383gAl65o0wIgQysoZCLUSU3Jbad6Hp93KxXnTHJ3SsBWJ2ItZCPw6UFLFpxysIWqOis2nXvIJ2AVhlI1AeYfapp1baIH5AZCW3PoX0NMlJ5XdoXQny4D9BLD7Lgpezbc8KTnT4KUBoqG6IgZDZD";
    Button tags, prep, create, pub, show;

    Cloudinary cloudinary;

    Button addNewItem;
    LinearLayout itemsContainer;
    ArrayList<Image_model>[] images_Array = new ArrayList[3];
    ArrayList<String>[] images_publicIDs_Array = new ArrayList[3];

    ArrayList<Integer> REQUEST_CAMERA = new ArrayList<>();
    ArrayList<Integer> SELECT_FILE = new ArrayList<>();

    ArrayList<Uri> list_Uris = new ArrayList<>();
    HashMap<String, Uploadclass> mapImageUploading;
    List<String> ids = new ArrayList<>();
    Uri priceUri, ssssUri;

    Boolean initCode = true;
    int rc;
    String gifUrl;
    private Uri mFileUri = null;
    private String m_Text = "";

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

        images_Array[0] = new ArrayList<>();
        images_Array[1] = new ArrayList<>();
        images_Array[2] = new ArrayList<>();

        images_publicIDs_Array[0] = new ArrayList<>();
        images_publicIDs_Array[1] = new ArrayList<>();
        images_publicIDs_Array[2] = new ArrayList<>();

        REQUEST_CAMERA.add(55);
        REQUEST_CAMERA.add(66);
        REQUEST_CAMERA.add(77);

        SELECT_FILE.add(21);
        SELECT_FILE.add(28);
        SELECT_FILE.add(35);

        mapImageUploading = new HashMap<>();

        imagesMap = new HashMap<>();


        itemsContainer = (LinearLayout) findViewById(R.id.items_container);


        addNewItem = (Button) findViewById(R.id.add_new_item);
        addNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                inflateNewItem();
                if (itemsContainer.getChildCount() == 3) {
                    addNewItem.setVisibility(View.GONE);
                }

                tags.setVisibility(View.VISIBLE);
            }
        });

    }

    private void inflateNewItem() {
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.add_offer_item, null);
        itemsContainer.addView(rootView);


        rootView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int position = itemsContainer.indexOfChild(rootView);
                final List<Image_model> finalDeletedImages = images_Array[position];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = finalDeletedImages.size(); i > 0; i--) {
                            try {
                                cloudinary.uploader().destroy(finalDeletedImages.get(i - 1).getPublic_id(), ObjectUtils.emptyMap());
                                if (finalDeletedImages.size() == 0)
                                    return;
                                finalDeletedImages.remove(i - 1);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
                if (images_Array[position].size() != 0) {
                    if (position == 0) {
                        images_Array[0] = images_Array[1];
                        //images_Array[1].clear();
                        images_Array[1] = images_Array[2];
                        //images_Array[2].clear();

                        decreasedValueFormIndex1 = 1;
                        decreasedValueFormIndex2 = 1;
                    } else if (position == 1) {
                        images_Array[1] = images_Array[2];
                        //images_Array[2].clear();
                        if (decreasedValueFormIndex2 == 0) {
                            decreasedValueFormIndex2 = 1;
                        } else {
                            decreasedValueFormIndex2 = 2;
                        }
                    }
                }
                itemsContainer.removeView(rootView);
                addNewItem.setVisibility(View.VISIBLE);
            }
        });

        rootView.findViewById(R.id.add_new_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout imgsContainer = (LinearLayout) rootView.findViewById(R.id.imgscontainer);
                selectImage(itemsContainer.indexOfChild(rootView));
                if (imgsContainer.getChildCount() == 3) {
                    rootView.findViewById(R.id.add_new_image).setVisibility(View.GONE);
                }
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
                    startActivityForResult(intent, REQUEST_CAMERA.get(i));
                } else if (items[item].equals("إختار صوره")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "إختار صوره"),
                            SELECT_FILE.get(i));
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
        if (initCode) {
            rc = requestCode;
            initCode = false;
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode != CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                // inflateNewImage(data.getData(), requestCode);
                //beginCrop(data.getData());
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setActivityTitle("3roodk").setAutoZoomEnabled(true)
                        .setAspectRatio(3, 2)
                        .start(this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                // handleCrop(resultCode, data, rc);
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri resultUri = result.getUri();
                inflateNewImage(resultUri, rc);


            }

        }
    }

    private void inflateNewImage(final Uri uri, int requestCode) {
        final Image_model imageModel = new Image_model();
        initCode = true;
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootImage = inflater.inflate(R.layout.image_group, null);
        final String key = UUID.randomUUID().toString();
        imageModel.setUniqueId(key);

        final int indexOfItem;
        if (requestCode < 50) {
            indexOfItem = (requestCode / 7) - 3;
        } else {
            indexOfItem = (requestCode / 11) - 5;
        }
        LinearLayout lytImagesGroupContainer = null;
        ImageButton item_delete_btn = null;
        if (itemsContainer != null) {
            lytImagesGroupContainer = (LinearLayout) itemsContainer.getChildAt(indexOfItem).findViewById(R.id.imgscontainer);
            lytImagesGroupContainer.addView(rootImage);
            item_delete_btn = (ImageButton) itemsContainer.getChildAt(indexOfItem).findViewById(R.id.delete_item);

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
        final ImageButton finalItem_delete_btn1 = item_delete_btn;
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalItem_delete_btn1 != null) {
                    finalItem_delete_btn1.setVisibility(View.GONE);
                }
                del_image.setVisibility(View.GONE);
                img_refresh.setVisibility(View.GONE);
                mapImageUploading.remove(key);
                mapImageUploading.put(key, new Uploadclass(uri, imgOffer, imgDone, indexOfItem, del_image, img_refresh, progress, finalItem_delete_btn, imageModel));
                mapImageUploading.get(key).execute();
                progress.setVisibility(View.VISIBLE);


            }
        });

        final Uploadclass uploadclass = new Uploadclass(uri, imgOffer, imgDone, indexOfItem, del_image, img_refresh, progress, item_delete_btn, imageModel);
        mapImageUploading.put(key, uploadclass);
        mapImageUploading.get(key).execute();
        if (item_delete_btn != null) {
            item_delete_btn.setVisibility(View.GONE);
        }

        list_Uris.add(uri);
        final LinearLayout fLytImagesGroupContainer = lytImagesGroupContainer;
        final int fIndexOfItem = indexOfItem;
        del_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int indexOfItem = fIndexOfItem;


                if (decreasedValueFormIndex1 != 0 && indexOfItem == 1) {
                    indexOfItem = indexOfItem - decreasedValueFormIndex1;
                    images_Array[indexOfItem].remove(fLytImagesGroupContainer.indexOfChild(rootImage) - 1);

                    if (images_Array[indexOfItem].size() == 0) {
                        decreasedValueFormIndex1 = 0;
                    }
                } else if (decreasedValueFormIndex2 != 0 && indexOfItem == 2) {
                    indexOfItem = indexOfItem - decreasedValueFormIndex2;
                    images_Array[indexOfItem].remove(fLytImagesGroupContainer.indexOfChild(rootImage) - 1);

                    if (images_Array[indexOfItem].size() == 0) {
                        decreasedValueFormIndex2 = 0;
                    }
                }

                Toast.makeText(v.getContext(), "current index of item : " + indexOfItem
                        , Toast.LENGTH_LONG).show();

                if (!mapImageUploading.get(key).isDone())
                    if (mapImageUploading.size() != 0) {
                        mapImageUploading.get(key).cancel(true);
                    } else

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    cloudinary.uploader().destroy(
                                            uploadclass.getPubid(), ObjectUtils.emptyMap());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                mapImageUploading.remove(key);
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

    class Uploadclass extends AsyncTask<Void, Void, HashMap> {
        String pubid;
        int x;
        boolean done, success;
        ImageView imageView, imageViewdone;
        Uri uri;
        SpinKitView progress;
        ImageButton delete_item;
        ImageButton imgdel, imgRefresh;
        Image_model imageModel;

        private String imageUrl;


        public Uploadclass(Uri imageUri, ImageView imageView, ImageView imgdn, int y, ImageButton del, ImageButton ref, SpinKitView progress, ImageButton deleteItem, Image_model image_model) {
            this.imageView = imageView;
            this.imageViewdone = imgdn;
            this.x = y;
            this.uri = imageUri;
            this.imgdel = del;
            this.imgRefresh = ref;
            this.progress = progress;
            this.delete_item = deleteItem;
            this.imageModel = image_model;
        }

        public boolean isDone() {
            return done;
        }

        public String getURL() {
            return imageUrl;
        }

        public String getPubid() {
            return pubid;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageUrl = null;
            success = false;
            done = false;
        }

        @Override
        protected HashMap doInBackground(Void... voids) {
            HashMap uploadResult = null;
            try {
                uploadResult = (HashMap) cloudinary.uploader().upload(getApplicationContext().getContentResolver()
                        .openInputStream(uri), ObjectUtils.emptyMap());


                ids.add((String) uploadResult.get("public_id"));

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
                imageUrl = (String) response.get("url");
                pubid = (String) response.get("public_id");
                imageView.setAlpha(1.0f);
                imageViewdone.setVisibility(View.VISIBLE);
                imgdel.setVisibility(View.VISIBLE);
                showMessageToast("تم رفع الصورة");
                imageModel.setPublic_id(pubid);
                imageModel.setUrl(imageUrl);
                images_Array[x].add(imageModel);
                images_publicIDs_Array[x].add(pubid);
                done = true;
                success = true;
            } else {
                imageUrl = null;
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

