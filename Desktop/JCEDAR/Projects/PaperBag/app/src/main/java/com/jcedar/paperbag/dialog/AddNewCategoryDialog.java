package com.jcedar.paperbag.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jcedar.paperbag.R;
import com.jcedar.paperbag.helper.MyUtils;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by OLUWAPHEMMY on 3/31/2017.
 */
public class AddNewCategoryDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "AddNewCategoryDialog";
    private static final String APP_TEMP_FOLDER = "PaperBag";
    private ImageView closeDialog, categoryGallery, categoryCamera, imgCategPreview, imgRemove;
    private EditText categoryTitle;
    private Button addCatgory;
    private RelativeLayout imageLayout;
    private LinearLayout catChooserLayout;
    private String encImage;

    private static final int PICK_IMAGE = 180;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final int PHOTO_REQUEST = 10;
    private Bitmap bitmap;



    public AddNewCategoryDialog() {

    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);/*
        Bundle mm = getArguments();
        uuuid = mm.getString("uuid");
        Log.e(TAG, "onCreate: seller ID = " + uuuid);*/

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_add_new_category, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        closeDialog = (ImageView) rootView.findViewById(R.id.ivCloseDialog);
        categoryCamera = (ImageView) rootView.findViewById(R.id.categoryCameraImageButton);
        categoryGallery = (ImageView) rootView.findViewById(R.id.categoryGalleryImageButton);
        imgCategPreview = (ImageView) rootView.findViewById(R.id.imgPreviewCategory);
        imgRemove = (ImageView) rootView.findViewById(R.id.categoryRemoveImage);

        categoryTitle = (EditText) rootView.findViewById(R.id.etDialogCategoryTitle);
        addCatgory = (Button) rootView.findViewById(R.id.btAddNewCategory);
        imageLayout = (RelativeLayout) rootView.findViewById(R.id.categoryPreviewLayout);
        catChooserLayout = (LinearLayout) rootView.findViewById(R.id.categoryChooserLayout);

        closeDialog.setOnClickListener(this);
        categoryCamera.setOnClickListener(this);
        categoryGallery.setOnClickListener(this);
        imgRemove.setOnClickListener(this);
        addCatgory.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivCloseDialog:
                dismiss();
                break;
            case R.id.categoryCameraImageButton:
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_PERMISSION);

                    } else {

                        ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_WRITE_PERMISSION);
                    }

                } else {
                    photoFromCamera();
                }

                break;
            case R.id.categoryGalleryImageButton:
                chooseGalleyPhoto();

                break;
            case R.id.categoryRemoveImage:
                imageLayout.setVisibility(View.GONE);
                encImage = "";
                bitmap = null;
                catChooserLayout.setVisibility(View.VISIBLE);

                break;
            case R.id.btAddNewCategory:

                String categTitleStr = categoryTitle.getText().toString();

                if (TextUtils.isEmpty(categTitleStr)) {
                    categoryTitle.setError("Enter Category Title");
                    Toast.makeText(getActivity(), "Category Title is required", Toast.LENGTH_SHORT).show();
                    categoryTitle.requestFocus();
                }  else if (bitmap == null || TextUtils.isEmpty(encImage)) {
                    Toast.makeText(getActivity(), "Category Image not set", Toast.LENGTH_SHORT).show();
                } else {
                    String dateAdded = String.valueOf(System.currentTimeMillis());
                    AddNewCategoryListener listener = (AddNewCategoryListener) getTargetFragment();
                    listener.onAddCategoryButtonClick(categTitleStr, encImage, dateAdded);
                    dismiss();

                }

                break;
            default:
                break;
        }

    }


    public void photoFromCamera() {

        try {

            File root = new File(Environment.getExternalStorageDirectory(), APP_TEMP_FOLDER);

            if (!root.exists()) {

                root.mkdirs();
            }

            File sdImageMainDirectory = new File(root, "product_image.jpg");
            imageUri = Uri.fromFile(sdImageMainDirectory);
            Log.e(TAG, "photoFromCamera: URI = " + imageUri );
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, PHOTO_REQUEST);

        } catch (Exception e) {

            Toast.makeText(getActivity(), "Error occured. Please try again later.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    takePicture();
                    photoFromCamera();
                } else {
                    Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void chooseGalleyPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bitmap = null;
        encImage = null;
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imagUri = data.getData();
            setImageAndHideLayout(imagUri);
        } else if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            launchMediaScanIntent();
            try {
                setImageAndHideLayout(imageUri);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Failed to load Image", Toast.LENGTH_SHORT)
                        .show();
                Log.e(TAG, e.toString());
            }
        }
    }

    private void launchMediaScanIntent() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void setImageAndHideLayout(Uri imgUri) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imgUri);
            BitmapDrawable dd = new BitmapDrawable(getResources(), bitmap);
//            Bitmap mCom = Bitmap.createScaledBitmap(bitmap, 200, 200, true);
            catChooserLayout.setVisibility(View.GONE);
            imgCategPreview.setImageDrawable(dd);
//            coverPhotoLayout.setBackground(dd);
            imageLayout.setVisibility(View.VISIBLE);
            encImage = MyUtils.encodeTobase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface AddNewCategoryListener {
        public void onAddCategoryButtonClick (String categoryTitle, String categoryImage, String categoryDateAdded);
    }
}
