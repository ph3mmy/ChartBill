package com.jcedar.tixee.helper;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.jcedar.tixee.R;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by OLUWAPHEMMY on 2/8/2017.
 */

public class ShowBarcodeDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = ShowBarcodeDialog.class.getSimpleName();
    ImageView imageViewQr, qrClose;
    private TextView qrCamTitle, qrTicketStatus, qrTicketContact, qrTicketPhone;
    private ImageView qrSave, qrShare;
    public final static int QRcodeWidth = 500;
    String cName, tickConName, tickConPhone, tickType, imageUrl;
    Bitmap bitmap;
    private String tickId;
    private int width, height;
    private LinearLayout backG;
    private RelativeLayout showBack;
    private View rootView;
    private View mProgress;
    private TextView qrDialogTitle;


    public ShowBarcodeDialog() {

    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            width = ViewGroup.LayoutParams.MATCH_PARENT;
            height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


/*        editDialog = getArguments().getString("edit");
        Log.e(TAG, "ret Arg = "  + editDialog);*/
        cName = getArguments().getString("camName");
        tickConName = getArguments().getString("conName");
        tickConPhone = getArguments().getString("conPhone");
        tickType = getArguments().getString("tickType");
        tickId = getArguments().getString("tickId");
        imageUrl = getArguments().getString("imageUrl");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setTitle(null);
        rootView = inflater.inflate(R.layout.dialog_gen_ticket, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

/*        qrCamTitle = (TextView) rootView.findViewById(R.id.tvQrCamTitle);
        qrDialogTitle = (TextView) rootView.findViewById(R.id.tvTicketGenTitleDialog);
//        qrTicketStatus = (TextView) rootView.findViewById(R.id.tvQrTickStatus);
        qrTicketContact = (TextView) rootView.findViewById(R.id.tvQrTicketCon);
        qrTicketPhone = (TextView) rootView.findViewById(R.id.tvQrTickPhone);*/
//        qrSave = (ImageView) rootView.findViewById(R.id.qrSaveButton);
        backG = (LinearLayout) rootView.findViewById(R.id.backGroundLayout);
        showBack = (RelativeLayout) rootView.findViewById(R.id.showBackgroundLayout);
//        qrShare = (ImageView) rootView.findViewById(R.id.qrShareButton);
        imageViewQr = (ImageView) rootView.findViewById(R.id.imageQr);
//        qrClose = (ImageView) rootView.findViewById(R.id.qrCloseDialog);
        mProgress = (ProgressBar) rootView.findViewById(R.id.qr_progress);

/*
        qrCamTitle.setText(cName);
//        qrTicketStatus.setText(tickType);

        if (tickConName.equalsIgnoreCase("nil")) {
            qrTicketContact.setText("");
        } else
            qrTicketContact.setText(tickConName);
        if (tickConPhone.equalsIgnoreCase("nil")) {
            qrTicketPhone.setText("");
        } else
            qrTicketPhone.setText(tickConPhone);*/

/*        try {
            imageViewQr.setImageBitmap(TextToImageEncode(tickId));
        } catch (WriterException e) {
            e.printStackTrace();
        }*/

        new MyBarCode(tickId, getActivity()).execute();

     /*   qrClose.setOnClickListener(this);
        qrSave.setOnClickListener(this);
        qrShare.setOnClickListener(this);*/

        return rootView;
    }


    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
/*        switch (view.getId()) {
            case R.id.qrSaveButton:
//                createFolderWithImage();

                Bitmap bm = captureScreen();
                saveCapturedImage(bm);

                break;
            case R.id.qrShareButton:

                shareTicketIntent();

                break;
            case R.id.qrCloseDialog:
                dismiss();
        }*/
    }


    public void shareTicketIntent() {
        // Fetch Bitmap Uri locally
        Intent shareIntent;
        Uri bmpUri = Uri.fromFile(saveCapturedImage(captureScreen()));
        // Construct share intent as described above based on bitmap
        if (bmpUri != null) {
            shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            startActivity(Intent.createChooser(shareIntent, "Share Ticket"));
        } else {
            Toast.makeText(getActivity(), "Invalid Image cannot be shared", Toast.LENGTH_SHORT).show();
        }
    }


    private File saveCapturedImage(Bitmap bm) {

        File mFolder = new File(Environment.getExternalStorageDirectory(), "Tixee");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File f = null;

        if (!mFolder.exists()) {
            mFolder.mkdir();
        } else {
            f = new File(mFolder + File.separator + "Ticket_" + timeStamp + ".png");
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(f));
                getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(f)));
                Toast.makeText(getActivity(), "Ticket successfully saved", Toast.LENGTH_SHORT).show();
                dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return f;
    }

    private Bitmap captureScreen() {

//        View v1 = ((ViewGroup) (((MainActivity) getActivity()).findViewById(android.R.id.content)));
        View v1 = rootView.findViewById(R.id.showBackgroundLayout);
        v1.setDrawingCacheEnabled(true);
        Bitmap bm = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);
        rootView.setDrawingCacheEnabled(true);
//        Bitmap bitmapDialog = Bitmap.createBitmap(v1.getDrawingCache());
        Bitmap bitmapDialog = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false);
        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmapDialog, 0, 0, paint);
        //Activity and dialog captured

        return bitmapDialog;
    }


    class MyBarCode extends AsyncTask<String, Void, Void> {

        String encString = null;
        //        ProgressDialog progressBar;
        Context context;
        Bitmap bm = null;

        public MyBarCode(String enc, Context context) {
            this.encString = enc;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                bm = TextToImageEncode(encString);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            imageViewQr.setImageBitmap(bm);
            Bitmap mBit = MyUtils.decodeBase64(imageUrl);
            BitmapDrawable dd = new BitmapDrawable(getResources(), mBit);
            showBack.setBackground(dd);
            backG.setVisibility(View.VISIBLE);
//            qrShare.setVisibility(View.VISIBLE);
//            qrSave.setVisibility(View.VISIBLE);
//            qrDialogTitle.setText("Generated Ticket");
            showBack.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int touch = motionEvent.getAction();
                    Log.e(TAG, "touch event in in showdialog " + touch);
                    shareTicketIntent();
                    return true;
                }
            });
            mProgress.setVisibility(View.GONE);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private  void addOrRemoveProperty (View view, int property, boolean flag) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        if (flag) {
            layoutParams.addRule(property);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        } else {
            layoutParams.removeRule(property);
        }
        view.setLayoutParams(layoutParams);
    }

}