package com.jcedar.tixee.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.tixee.R;
import com.jcedar.tixee.helper.MyUtils;
import com.jcedar.tixee.model.Ticket;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by OLUWAPHEMMY on 2/9/2017.
 */
public class ScanTicketActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = ScanTicketActivity.class.getSimpleName();
    private Button cameraButton, galleryButton, verifyButton;
    private ImageView scanTicket;
    String scanResultString;
    LinearLayout scanLayStat, scanLayContact;
    private static final int SELECT_PHOTO = 100;

    private static final int PHOTO_REQUEST = 10;
    private TextView scanResults, scanStatus, scanCampaign, scanConName, scanConPhone;
    private CardView cdScanResult;
    private BarcodeDetector detector;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private boolean isScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ticket);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("Verify Ticket");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        scanLayStat = (LinearLayout) findViewById(R.id.scanLayStatCamp);
        scanLayContact = (LinearLayout) findViewById(R.id.scanLayContact);
        scanTicket = (ImageView) findViewById(R.id.ivScanImage);
        verifyButton = (Button) findViewById(R.id.btScanResultVerify);
        cameraButton = (Button) findViewById(R.id.btScanTicketCamera);
        galleryButton = (Button) findViewById(R.id.btScanTicketGallery);
        scanResults = (TextView) findViewById(R.id.scan_result);
        scanStatus = (TextView) findViewById(R.id.scan_status);
        scanCampaign = (TextView) findViewById(R.id.scan_camp);
        scanConName = (TextView) findViewById(R.id.scan_con_name);
        scanConPhone = (TextView) findViewById(R.id.scan_con_phone);
        cdScanResult = (CardView) findViewById(R.id.cdScanResult);

        if (savedInstanceState != null) {
            imageUri = Uri.parse(savedInstanceState.getString(SAVED_INSTANCE_URI));
            scanResults.setText(savedInstanceState.getString(SAVED_INSTANCE_RESULT));
        }

        detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE)
                .build();
        if (!detector.isOperational()) {
            scanResults.setText("Could not set up the detector!");
            return;
        }

        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);
        verifyButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btScanTicketCamera:

                ActivityCompat.requestPermissions(ScanTicketActivity.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);


                break;
            case R.id.btScanTicketGallery:

                //launch gallery via intent
                Intent photoPic = new Intent(Intent.ACTION_PICK);
                photoPic.setType("image/*");
                startActivityForResult(photoPic, SELECT_PHOTO);
                break;
            case R.id.btScanResultVerify:
                if (!MyUtils.checkNetworkAvailability(this)) {
                    MyUtils.networkDialog(this).show();
                } else if ((scanResultString.contains("&")) || (scanResultString.contains("?")) || (scanResultString.contains("/"))) {
                    Toast.makeText(this, "This is not a valid Ticket", Toast.LENGTH_SHORT).show();
                } else
                fetchTicketDetails(scanResultString);

        }
    }

    private void fetchTicketDetails(final String scannedString) {
        final DatabaseReference tReference = FirebaseDatabase.getInstance().getReference("Ticket");
        tReference.child(scannedString.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue(String.class);
                Ticket myTick = dataSnapshot.getValue(Ticket.class);
                Log.e(TAG, "returned snapshot " + dataSnapshot + " myTick retScan " + myTick);//myTick);
                if (myTick != null) {
                    scanLayStat.setVisibility(View.VISIBLE);
                    scanCampaign.setText(myTick.getCampaignName());
                    if (myTick.getStatus().equalsIgnoreCase("Used")) {
                        scanStatus.setText(myTick.getStatus());
                        scanStatus.setTextColor(ContextCompat.getColor(ScanTicketActivity.this,android.R.color.holo_red_light));
                    }else  {
                    scanStatus.setText(myTick.getStatus());
                    isScanned = true;
                    }

                    if (!myTick.getContactName().equalsIgnoreCase("Nil")) {
                        scanConName.setText(myTick.getContactName());
                        scanConPhone.setText(myTick.getContactPhone());
                        scanLayContact.setVisibility(View.VISIBLE);
                    }

                }else {
                    Toast.makeText(ScanTicketActivity.this, "Ticket not found", Toast.LENGTH_SHORT).show();
                }
                if (isScanned) {
                    tReference.child(scannedString.trim()).child("status").setValue("Used");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(ScanTicketActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                scanLayStat.setVisibility(View.GONE);
                scanAndReturn(this, imageUri, scanResults, scanTicket);
            } catch (Exception e) {
                Toast.makeText(this, "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, e.toString());
            }
        } else if (requestCode == SELECT_PHOTO) {
            if (resultCode == RESULT_OK) {
                //doing some uri parsing
                Uri selectedImage = data.getData();
//                InputStream imageStream = null;
//                scanTicket.setImageURI(selectedImage);
                scanAndReturn(this, selectedImage, scanResults, scanTicket);

            }
        }

        }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = new File(Environment.getExternalStorageDirectory(), "picture.jpg");
        imageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (imageUri != null) {
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            outState.putString(SAVED_INSTANCE_RESULT, scanResults.getText().toString());
        }
        super.onSaveInstanceState(outState);
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        int targetW = 600;
        int targetH = 600;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    private void scanAndReturn (Context context, Uri uri, TextView scanResultss, ImageView scanTicket) {
        Bitmap bitmap = null;
        try {
            bitmap = decodeBitmapUri(context, uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (detector.isOperational() && bitmap != null) {
            scanTicket.setImageURI(uri);
            scanTicket.setVisibility(View.VISIBLE);
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<Barcode> barcodes = detector.detect(frame);
            for (int index = 0; index < barcodes.size(); index++) {
                Barcode code = barcodes.valueAt(index);
                cdScanResult.setVisibility(View.VISIBLE);
//                scanResultss.setText(scanResultss.getText() + code.displayValue + "\n");
//                scanResultString = scanResultss.getText() + code.displayValue;
                scanResultString = code.rawValue;
                scanResultss.setText(scanResultString);
//                Log.e(TAG, " scanned Barcode String " + rr);

                //Required only if you need to extract the type of barcode
                int type = barcodes.valueAt(index).valueFormat;
                switch (type) {
                    case Barcode.CONTACT_INFO:
                        Log.i(TAG, code.contactInfo.title);
                        break;
                    case Barcode.EMAIL:
                        Log.i(TAG, code.email.address);
                        break;
                    case Barcode.ISBN:
                        Log.i(TAG, code.rawValue);
                        break;
                    case Barcode.PHONE:
                        Log.i(TAG, code.phone.number);
                        break;
                    case Barcode.PRODUCT:
                        Log.i(TAG, code.rawValue);
                        break;
                    case Barcode.SMS:
                        Log.i(TAG, code.sms.message);
                        break;
                    case Barcode.TEXT:
                        Log.i(TAG, code.rawValue);
                        break;
                    case Barcode.URL:
                        Log.i(TAG, "url: " + code.url.url);
                        break;
                    case Barcode.WIFI:
                        Log.i(TAG, code.wifi.ssid);
                        break;
                    case Barcode.GEO:
                        Log.i(TAG, code.geoPoint.lat + ":" + code.geoPoint.lng);
                        break;
                    case Barcode.CALENDAR_EVENT:
                        Log.i(TAG, code.calendarEvent.description);
                        break;
                    case Barcode.DRIVER_LICENSE:
                        Log.i(TAG, code.driverLicense.licenseNumber);
                        break;
                    default:
                        Log.i(TAG, code.rawValue);
                        break;
                }
            }
            if (barcodes.size() == 0) {
                scanResultss.setText("Scan Failed: Found nothing to scan");
            }
        } else {
            scanResultss.setText("Could not set up the detector!");
        }
    }

}
