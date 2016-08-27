package com.example.refaat.cloudinarytest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {



    static String postID;
    Uploadclass imageForFacebookPost;
    String gifTag = null;
    String gifUrl = null;
    HashMap<String, Image_model> imagesMap;
   // String token = "EAACEdEose0cBAI0eZAX2ZBdyq8plGkoSgONoHDpeLnKMobHXHtF8ZAdeZBSR0bVUdWMd5RZAVHn8pMld2LWZAjPfGTYMEhivBIed5IxYZAeTKlfQkj0TnXNiwJcwVGJzQ7sx236jLV4ZAKVTl1m89KeSY64b2WZCPwpFSqVvqmnLjwQZDZD";
    Button tags, prep, create, pub, show;

    Cloudinary cloudinary;

    Button addNewItem;
    LinearLayout itemsContainer;

    Switch fbSwitch;

    HashMap<Integer, UiItem> mapOfItems;

    List<String> framesId;

    Uri priceUri, lastFrameUri;

    Boolean initCode = true;
    int rc;
    boolean postGif = false;
    boolean postImage = false;
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

        // priceUri = Uri.parse("android.resource://com.example.refaat.cloudinarytest/drawable/price.png");;

        priceUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.price)
                + '/' + getResources().getResourceTypeName(R.drawable.price) + '/' + getResources().getResourceEntryName(R.drawable.price));

        lastFrameUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.lastframe)
                + '/' + getResources().getResourceTypeName(R.drawable.lastframe) + '/' + getResources().getResourceEntryName(R.drawable.lastframe));

        tags = (Button) findViewById(R.id.tags);
        prep = (Button) findViewById(R.id.preb);
        create = (Button) findViewById(R.id.create);
        pub = (Button) findViewById(R.id.pub);
        show = (Button) findViewById(R.id.show);

        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        final RadioButton radioGif = (RadioButton) findViewById(R.id.radio_gif);
        RadioButton radioImage = (RadioButton) findViewById(R.id.radio_image);

        fbSwitch = (Switch) findViewById(R.id.facebook_switch);

        fbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    radioGroup.setVisibility(View.VISIBLE);
                    radioGif.setChecked(true);
                    postGif = true;
                    // The toggle is enabled
                } else {
                    // The toggle is disabled
                    radioGroup.setVisibility(View.GONE);
                    postGif = false;
                    postImage = false;

                    for (UiItem item : mapOfItems.values()) {


                        for (Uploadclass uploadclass : item.imagesMap.values()) {
                            uploadclass.radioBox.setVisibility(View.INVISIBLE);
                            uploadclass.facebookImage.setVisibility(View.INVISIBLE);

                        }

                    }

                }
            }
        });


        Map config = new HashMap();
        config.put("cloud_name", "app3roodk");
        config.put("api_key", "936844595166333");
        config.put("api_secret", "vnIDUi3QVRk-a_wnJjFkGDslxvM");
        cloudinary = new Cloudinary(config);

        framesId = new ArrayList<>();

        imagesMap = new HashMap<>();
        mapOfItems = new HashMap<>();
        itemsContainer = (LinearLayout) findViewById(R.id.items_container);

        gifTag = UUID.randomUUID().toString();


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

    private CropImageView mCropImageView;






    private void inflateNewItem() {
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rootView = inflater.inflate(R.layout.add_offer_item, null);
        itemsContainer.addView(rootView);

        final LinearLayout theView = (LinearLayout) rootView.findViewById(R.id.the_view);

        final TextView txtAfter = (TextView) rootView.findViewById(R.id.txtb);
        final TextView txtbefore = (TextView) rootView.findViewById(R.id.txtA);
        final TextView txtprecent = (TextView) rootView.findViewById(R.id.txtpr);
        final View dash = (View) rootView.findViewById(R.id.dash);


        final EditText inputPriceBefore = (EditText) rootView.findViewById(R.id.input_pricebefore);
        final EditText inputPriceAfter = (EditText) rootView.findViewById(R.id.input_priceafter);

        inputPriceAfter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().isEmpty()) {
                    theView.setVisibility(View.VISIBLE);
                    txtAfter.setText(editable.toString());

                    if (!inputPriceBefore.getText().toString().isEmpty()) {
                        String A = inputPriceAfter.getText().toString();
                        String B = inputPriceBefore.getText().toString();


                        String x = String.format("%.0f", (1 - (Double.parseDouble(A)) / (Double.parseDouble(B))) * 100) + "%";
                        txtprecent.setText(x);
                    } else {
                        txtprecent.setText("");
                    }
                } else {
                    txtAfter.setText("");
                    if (txtAfter.getText().equals("") && txtbefore.getText().equals("")) {
                        theView.setVisibility(View.INVISIBLE);
                    }


                }


            }
        });


        inputPriceBefore.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    theView.setVisibility(View.VISIBLE);
                    txtbefore.setText(editable.toString());
                    dash.setVisibility(View.VISIBLE);

                    if (!inputPriceAfter.getText().toString().isEmpty()) {
                        //   cardHolder.discount.setText(String.format("%.0f", (1 - (Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceAfter()) / Double.parseDouble(lstOffers.get(position).getItems().get(0).getPriceBefore()))) * 100) + "%");

                        String A = inputPriceAfter.getText().toString();
                        String B = inputPriceBefore.getText().toString();


                        String x = String.format("%.0f", (1 - (Double.parseDouble(A)) / (Double.parseDouble(B))) * 100) + "%";
                        txtprecent.setText(x);
                    } else {
                        txtprecent.setText("");
                    }
                } else {
                    txtbefore.setText("");
                    dash.setVisibility(View.GONE);
                    if (txtAfter.getText().equals("") && txtbefore.getText().equals("")) {
                        theView.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });


        final UiItem item = new UiItem();
        item.setUniqueNo(Integer.parseInt(getCurrentDate(new Date()).substring(12, 16)));
        item.setRootV(rootView);
        mapOfItems.put(item.getUniqueNo(), item);

        rootView.findViewById(R.id.delete_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (final Uploadclass uploadclass : mapOfItems.get(item.getUniqueNo()).imagesMap.values()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                cloudinary.uploader().destroy(uploadclass.getPublic_id(), ObjectUtils.emptyMap());
                                mapOfItems.get(item.getUniqueNo()).imagesMap.remove(uploadclass.getUniqueKey());
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
                        .setAspectRatio(540, 400).setFixAspectRatio(true).setOutputCompressQuality(50)
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

        // final String key = UUID.randomUUID().toString();
        final UiItem item = mapOfItems.get(requestCode);

        LinearLayout lytImagesGroupContainer = null;
        ImageButton item_delete_btn = null;
        if (itemsContainer != null) {
            lytImagesGroupContainer = (LinearLayout) mapOfItems.get(requestCode).getRootV().findViewById(R.id.imgscontainer);
            lytImagesGroupContainer.addView(rootImage, lytImagesGroupContainer.getChildCount() - 1);
            item_delete_btn = (ImageButton) mapOfItems.get(requestCode).getRootV().findViewById(R.id.delete_item);
        }

        if (lytImagesGroupContainer.getChildCount() == 4) {
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

        final ImageView facebookImageView = (ImageView) rootImage.findViewById(R.id.facebook_image);
        final RadioButton radioBox = (RadioButton) rootImage.findViewById(R.id.checkbox_facebookimage);

        //showMessageToast("جاري رفع الصورة");


        final ImageButton finalItem_delete_btn = item_delete_btn;

        final Uploadclass[] uploadImage = {new Uploadclass(uri, imgOffer, imgDone, del_image, img_refresh, progress, item_delete_btn, radioBox, facebookImageView)};
        uploadImage[0].setUniqueKey(UUID.randomUUID().toString());
        uploadImage[0].setParentItem(item);
        item.imagesMap.put(uploadImage[0].getUniqueKey(), uploadImage[0]);
        item.imagesMap.get(uploadImage[0].getUniqueKey()).execute();
        if (item_delete_btn != null) {
            item_delete_btn.setVisibility(View.GONE);
        }

        final ImageButton finalItem_delete_btn1 = item_delete_btn;
        img_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalItem_delete_btn != null) {
                    finalItem_delete_btn.setVisibility(View.GONE);
                }
                del_image.setVisibility(View.GONE);
                img_refresh.setVisibility(View.GONE);
                item.imagesMap.remove(uploadImage[0].getUniqueKey());
                uploadImage[0] = new Uploadclass(uri, imgOffer, imgDone, del_image, img_refresh, progress, finalItem_delete_btn1, radioBox, facebookImageView);
                uploadImage[0].setParentItem(item);
                item.imagesMap.put(uploadImage[0].getUniqueKey(), uploadImage[0]);
                item.imagesMap.get(uploadImage[0].getUniqueKey()).execute();
                progress.setVisibility(View.VISIBLE);
            }
        });
        final LinearLayout fLytImagesGroupContainer = lytImagesGroupContainer;
        del_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            cloudinary.uploader().destroy(
                                    item.imagesMap.get(uploadImage[0].getUniqueKey()).getPublic_id(), ObjectUtils.emptyMap());
                            item.imagesMap.remove(uploadImage[0].getUniqueKey());
                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                    }
                }).start();
                fLytImagesGroupContainer.findViewById(R.id.add_new_image).setVisibility(View.VISIBLE);
                fLytImagesGroupContainer.removeView(rootImage);
            }
        });

        radioBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {

                    showDialdForImagePost(facebookImageView, radioBox, uploadImage[0]);


                    // The toggle is enabled
                } else {
                    // The toggle is disabled

                }

            }
        });
    }

    private void showDialdForImagePost(final ImageView imageView, final RadioButton radioButton, final Uploadclass uploadclass) {

        new AlertDialog.Builder(this)
                .setTitle("تحديد صوره للبوست")
                .setMessage("هل تريد اختيار هذه الصوره للنشر على الفيس بوك ؟")
                .setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete

                        hideCheckBoxes(imageView);

                        imageForFacebookPost = uploadclass;
                        postImage = true;
                        postGif = false;

                    }
                })
                .setNegativeButton("إعاده إختيار", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        radioButton.setChecked(false);
                        imageForFacebookPost = null;
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void hideCheckBoxes(ImageView imageView) {
        imageView.setVisibility(View.VISIBLE);
        for (UiItem item : mapOfItems.values()) {

            for (Uploadclass uploadclass : item.imagesMap.values()) {

                uploadclass.radioBox.setVisibility(View.INVISIBLE);
                uploadclass.radioBox.setChecked(false);


            }


        }

    }


    private void showMessageToast(String msg) {
        try {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {

        }
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_gif:
                if (checked)

                    postGif = true;
                postImage = false;
                imageForFacebookPost = null;
                for (UiItem item : mapOfItems.values()) {

                    for (Uploadclass uploadclass : item.imagesMap.values()) {


                        uploadclass.radioBox.setVisibility(View.INVISIBLE);
                        uploadclass.facebookImage.setVisibility(View.INVISIBLE);

                    }

                }

                break;
            case R.id.radio_image:
                if (checked)

                    postImage = true;
                postGif = false;
                Toast.makeText(getApplicationContext(), "من فضلك حدد صوره للبوست", Toast.LENGTH_LONG).show();


                boolean requstfocusForFirstItem = true;
                for (UiItem item : mapOfItems.values()) {

                    if (requstfocusForFirstItem) {
                        item.getRootV().requestFocus();
                        requstfocusForFirstItem = false;
                    }
                    for (Uploadclass uploadclass : item.imagesMap.values()) {
                        uploadclass.radioBox.setVisibility(View.VISIBLE);

                    }

                }


                break;
        }
    }

    public void publishtoServer(View view) throws InterruptedException {

        if (postGif) { // prepare for posting gif
            int i = 1;
            int itemNo = 0;
            int imageNo = 0;

            for (UiItem item : mapOfItems.values()) {

                LinearLayout priceView = (LinearLayout) item.getRootV().findViewById(R.id.the_view);
                Bitmap bitmap = ConvertToBitmap(priceView);
                final Uri priceUri = getImageUri(this, bitmap);

                for (final Uploadclass uploadclass : item.imagesMap.values()) {

                    final int finalItemNo = itemNo;
                    final int finalImageNo = imageNo;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Transformation transformation = null;
                            transformation = new Transformation().height(400).crop("scale")
                                    .gravity("south_east").underlay(uploadclass.getPublic_id()).chain()
                                    .gravity("north_west").height(80).overlay("3roodk_Logo_etnsc5").crop("scale");
                        /*
                                    (String) cloudinary.uploader().upload(
                                            getApplicationContext().getContentResolver().openInputStream(priceUri),
                                            ObjectUtils.emptyMap()).get("public_id")*/
                            try {
                                String frameId = (String) cloudinary.uploader().upload(
                                        getApplicationContext().getContentResolver().openInputStream(priceUri),
                                        //               ObjectUtils.asMap("public_id", "frame"+getCurrentDate(new Date()).substring(12, 16),"transformation", transformation, "tags", gifTag)).get("public_id");
                                        ObjectUtils.asMap("public_id", "frame-" + String.valueOf(finalItemNo) + "-" + String.valueOf(finalImageNo), "transformation", transformation, "tags", gifTag)).get("public_id");
                                framesId.add(frameId);
                                getContentResolver().delete(priceUri, null, null);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                    imageNo++;
                }

                if (i++ == mapOfItems.size()) {


                    final Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Transformation transformation = new Transformation();
                                        transformation.width(540).height(400);
                                        String lastFrameId = (String) cloudinary.uploader().upload(
                                                getApplicationContext().getContentResolver().openInputStream(lastFrameUri),
                                                ObjectUtils.asMap("transformation", transformation, "public_id", "zframe", "tags", gifTag)).get("public_id");
                                        framesId.add(lastFrameId);
                                        createGif();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();

                        }
                    }, 5000);


                }

                itemNo++;
            }

        } else if (postImage && imageForFacebookPost != null) {

            LinearLayout priceView = (LinearLayout) imageForFacebookPost.getParentItem().getRootV().findViewById(R.id.the_view);
            Bitmap bitmap = ConvertToBitmap(priceView);
            final Uri priceUri = getImageUri(this, bitmap);

            new Thread(new Runnable() {
                @Override
                public void run() {

                    Transformation transformation = null;
                    transformation = new Transformation().height(400).width(540).crop("scale")
                            .gravity("south_east").underlay(imageForFacebookPost.getPublic_id()).chain()
                            .gravity("north_west").height(80).overlay("3roodk_Logo_etnsc5").crop("scale");
                        /*
                                    (String) cloudinary.uploader().upload(
                                            getApplicationContext().getContentResolver().openInputStream(priceUri),
                                            ObjectUtils.emptyMap()).get("public_id")*/
                    try {
                        String frameUrl = (String) cloudinary.uploader().upload(
                                getApplicationContext().getContentResolver().openInputStream(priceUri),
                                //               ObjectUtils.asMap("public_id", "frame"+getCurrentDate(new Date()).substring(12, 16),"transformation", transformation, "tags", gifTag)).get("public_id");
                                //  ObjectUtils.asMap("public_id", "frame-" + String.valueOf(finalItemNo) + "-" + String.valueOf(finalImageNo), "transformation", transformation, "tags", gifTag)).get("public_id");
                                ObjectUtils.asMap("transformation", transformation)).get("url");
                        getContentResolver().delete(priceUri, null, null);
                        String param = "url";
                        postFacebok(param, frameUrl);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }).start();


            //prepare for posting photo


        } else{


if (!fbSwitch.isChecked())
    Toast.makeText(this, "من فضلك فعل النشر على الفيس بوك أولا", Toast.LENGTH_SHORT).show();
            if (imageForFacebookPost==null)
                Toast.makeText(this, "من فضلك قم بتحديد صوره للبوست!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap ConvertToBitmap(LinearLayout theView) {
        theView.setDrawingCacheEnabled(true);
        theView.buildDrawingCache();
        Bitmap map = theView.getDrawingCache();

        return map;
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
*/

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        Bitmap bitmap = Bitmap.createScaledBitmap(inImage, 151, 86, false);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    /*
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
    }
    */
    public void createGif() {

        new Thread(new Runnable() {
            @Override
            public void run() {


                // cloudinary.url().type("multi").imageTag("bank");


                try {

                    Transformation transformation = new Transformation();
                    transformation.delay(1600);
                    Map delay = new HashMap();
                    delay.put("transformation", transformation);
                    gifUrl = (String) cloudinary.uploader().multi(gifTag, delay).get("url");
                    if (gifUrl != null) {
                        deleteFrames();
                        String param = "link";
                        postFacebok(param, gifUrl);

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }).start();
        //       pub.setVisibility(View.VISIBLE);
    }

    void deleteFrames() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (String public_id : framesId) {

                    try {
                        cloudinary.uploader().destroy(public_id, ObjectUtils.emptyMap());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }



    /*
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
                }*/

    public void postFacebok(String param, String value) {


        String pass = null;
        if (param.equals("url"))
            pass = "photos";
        else
            pass = "feed";


        String token = "EAAD6PMxRRuwBABAPSQhUEGE5BnQ1E6pzJotCeU7Hpk7R5DTH0R7kEOHMC9oAZCqwwzmEZAOzqYFHMi0lhSiuEwv15SwmSkzvOrZBwuR1ygsYwkiadpa1VwlZAJaJMGZB00XBuTiV9Khcluzvg4haqZAnQwdxZA2fZCKFVWQqycH4eAZDZD";
        String applicationId = "275139032860396";
        String userId = "1579805665655253";
        Collection<String> permissions =null;
        Collection<String> declinedPermissions = null;
        AccessTokenSource accessTokenSource = null;
        Date expirationTime = null;
        Date lastRefreshTime = null;




        AccessToken accessToken = new AccessToken(token, applicationId, userId, permissions, declinedPermissions, accessTokenSource, expirationTime, lastRefreshTime);




        String pgeId = "1591626347747816";
        Bundle params = new Bundle();
        params.putString(param, value);
        params.putString("message", "poooooooost" + getCurrentDate(new Date()));
    //    params.putString("scheduled_publish_time","1476983634");



        /* make the API call */

        new GraphRequest(
                accessToken,
                "/" + pgeId + "/" + pass,
                params,
                HttpMethod.POST,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                            String xx = response.toString();
//                            postID = (String) response.getJSONObject().get("id");
                         //   showMsg("Done");
                            showMsg(xx);


                    }
                }
        ).executeAsync();


    }

    private void showMsg(String id) {

        Toast.makeText(this, "res = " + id, Toast.LENGTH_LONG).show();
        show.setVisibility(View.VISIBLE);
        show.requestFocus();
    }

/*
    public void postFacebok() {
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

        //show.setVisibility(View.VISIBLE);
    }
*/

    public void showPost(View view) {
        startActivity(getOpenFacebookIntent(this));
    }

    public Intent getOpenFacebookIntent(Context context) {

        showMessageToast("شوف اول بوست لما الصفحه تفتح");

        try {
            context.getPackageManager().getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/1591626347747816/"));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/best.android.games4u/"));
        }
    }


    class UiItem {
        HashMap<String, Uploadclass> imagesMap = new HashMap<>();
        private int uniqueNo;
        private View rootV;

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
        ImageView facebookImage;
        RadioButton radioBox;
        boolean done, success;
        ImageView imageView, imageViewdone;
        Uri uri;
        SpinKitView progress;
        ImageButton delete_item;
        ImageButton imgdel, imgRefresh;
        private String public_id;
        private String Url;
        private String uniqueKey;
        private UiItem parentItem;

        public Uploadclass(Uri imageUri, ImageView imageView, ImageView imgdn, ImageButton del, ImageButton ref, SpinKitView progress, ImageButton deleteItem, RadioButton rb, ImageView fbImage) {
            this.imageView = imageView;
            this.imageViewdone = imgdn;
            this.uri = imageUri;
            this.imgdel = del;
            this.imgRefresh = ref;
            this.progress = progress;
            this.delete_item = deleteItem;
            this.radioBox = rb;
            this.facebookImage = fbImage;
        }

        public UiItem getParentItem() {
            return parentItem;
        }

        public void setParentItem(UiItem parentItem) {
            this.parentItem = parentItem;
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




