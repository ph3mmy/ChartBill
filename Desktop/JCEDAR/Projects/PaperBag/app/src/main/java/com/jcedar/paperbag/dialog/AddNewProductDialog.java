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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.helper.MyUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by OLUWAPHEMMY on 3/29/2017.
 */

public class AddNewProductDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "AddNewProductDialog";
    private static final String APP_TEMP_FOLDER = "PaperBag";
    private ImageView closeDialog, removeIcon, galleryChooser, cameraChooser, imgViewPrev;
    private EditText productName, productDesc, productQty, productPrice;
    private Button addNewProduct;
    private RelativeLayout coverPhotoLayout;
    private LinearLayout chooserLayout;
    Spinner productCateg;
    DatabaseReference mRef;
    String encImage;

    private static final int PICK_IMAGE = 180;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private static final int PHOTO_REQUEST = 10;
    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_RESULT = "result";
    private String uuuid, categoryId;
    Bitmap bitmap;
    private HashMap<String, String> categAndKey;
    private String categorySel;


    public AddNewProductDialog() {

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
        super.onCreate(savedInstanceState);
        Bundle mm = getArguments();
        uuuid = mm.getString("uuid");
        Log.e(TAG, "onCreate: seller ID = " + uuuid);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_add_new_product, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mRef = FirebaseDatabase.getInstance().getReference();
        categAndKey = new HashMap<>();

        productName = (EditText) rootView.findViewById(R.id.etDialogProductName);
        productDesc = (EditText) rootView.findViewById(R.id.etDialogProductDesc);
        productQty = (EditText) rootView.findViewById(R.id.etDialogProductQty);
        productPrice = (EditText) rootView.findViewById(R.id.etDialogProductPrice);

        closeDialog = (ImageView) rootView.findViewById(R.id.ivCloseDialog);
        removeIcon = (ImageView) rootView.findViewById(R.id.dialogAddRemoveImage);
        imgViewPrev = (ImageView) rootView.findViewById(R.id.imgViewPreview);
        galleryChooser = (ImageView) rootView.findViewById(R.id.dialogGalleryImageButton);
        cameraChooser = (ImageView) rootView.findViewById(R.id.dialogCameraImageButton);

        addNewProduct = (Button) rootView.findViewById(R.id.btAddNewProduct);
        coverPhotoLayout = (RelativeLayout) rootView.findViewById(R.id.dialogAddNewPreviewLayout);
        chooserLayout = (LinearLayout) rootView.findViewById(R.id.dialogAddNewImageChooserLayout);
        productCateg = (Spinner) rootView.findViewById(R.id.spinnerProductCategory);

        closeDialog.setOnClickListener(this);
        removeIcon.setOnClickListener(this);
        galleryChooser.setOnClickListener(this);
        cameraChooser.setOnClickListener(this);
        addNewProduct.setOnClickListener(this);


        mRef.child("Category").addValueEventListener(new ValueEventListener() {
            final List<String> category = new ArrayList<String>();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String categTitle = dataSnapshot1.child("categoryTitle").getValue(String.class);
                    String key = dataSnapshot1.getKey();
                    if (!category.contains(categTitle)) {
                        category.add(categTitle);
                        categAndKey.put(categTitle, key);
                    }
                }

                if (getActivity() != null) {
                    ArrayAdapter<String> camAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, category);
                    camAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    productCateg.setAdapter(camAdapter);
                }
                productCateg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        final String sel = productCateg.getSelectedItem().toString();
                        categorySel = sel;
                        categoryId = categAndKey.get(sel);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCloseDialog:
                dismiss();
                break;
            case R.id.dialogAddRemoveImage:
                coverPhotoLayout.setVisibility(View.GONE);
                encImage = "";
                bitmap = null;
                chooserLayout.setVisibility(View.VISIBLE);

                break;
            case R.id.dialogGalleryImageButton:
                chooseGalleyPhoto();

                break;
            case R.id.dialogCameraImageButton:

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
            case R.id.btAddNewProduct:

                // Store values from editTexts.
                String productNamStr = productName.getText().toString();
                String productDescStr = productDesc.getText().toString();
                String productQtyStr = productQty.getText().toString();
                String productPriceStr = productPrice.getText().toString();

                boolean cancel = false;
                View focusView = null;


                if (productCateg.getChildCount() == 0) {
                    Toast.makeText(getActivity(), "Category not yet populated", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(productNamStr)) {
                    productName.setError("Product Name is required");
                    focusView = productName;
                    cancel = true;
                }
                if (TextUtils.isEmpty(productDescStr)) {
                    productDesc.setError("Description is required");
                    focusView = productDesc;
                    cancel = true;
                }
                if (TextUtils.isEmpty(productQtyStr)) {
                    productQty.setError("Quantity available must be set");
                    focusView = productQty;
                    cancel = true;
                }
                if (TextUtils.isEmpty(productPriceStr)) {
                    productPrice.setError("Quantity available must be set");
                    focusView = productPrice;
                    cancel = true;
                }

                if (cancel) {
                    // There was an error; don't attempt login and focus the first
                    // form field with an error.
                    focusView.requestFocus();
                } else if (bitmap == null || TextUtils.isEmpty(encImage)) {
                    showToast("Product Cover Photo not set");
                } else {
                    Log.e(TAG, "onClick: final else imageStr = " + encImage);
                    String timeAdded = String.valueOf(System.currentTimeMillis());
                    AddProductFragmentInteractionListener listener = (AddProductFragmentInteractionListener) getTargetFragment();
                    listener.onAddProductButtonClick(productNamStr, encImage, productDescStr, productPriceStr, productQtyStr, "0",
                            timeAdded, uuuid, "0", categorySel, categoryId);
                    dismiss();

                }

                break;
            default:
                break;
        }

    }

    private void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();

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
                    showToast("Permission Denied!");
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
            //                galleryChooser.setError(null);
            setImageAndHideLayout(imagUri);
                /*bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imagUri);
                BitmapDrawable dd = new BitmapDrawable(getResources(), bitmap);
                coverPhotoLayout.setBackground(dd);
                chooserLayout.setVisibility(View.GONE);
                coverPhotoLayout.setVisibility(View.VISIBLE);
                encImage = MyUtils.encodeTobase64(bitmap);*/
        } else if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
//        } else if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
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
            imgViewPrev.setImageDrawable(dd);
//            coverPhotoLayout.setBackground(dd);
            chooserLayout.setVisibility(View.GONE);
            coverPhotoLayout.setVisibility(View.VISIBLE);
            encImage = MyUtils.encodeTobase64(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface AddProductFragmentInteractionListener {
        public void onAddProductButtonClick(String productName, String productPhoto, String productDesc, String productPrice,
                                            String productQty, String productQtyOrdered, String productDateAdded, String productSellerId,
                                            String productCommentCount, String productCategoryTitle, String productCategoryId);
    }

}
