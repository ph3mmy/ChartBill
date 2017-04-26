package com.jcedar.paperbag.dialog;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.activity.BuyerFoodActivity;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.provider.PaperBagContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by OLUWAPHEMMY on 4/9/2017.
 */
public class OrderDialog extends DialogFragment implements View.OnClickListener{

    private static final String TAG = "OrderDialog";
    String foodId, foodName, foodDesc, foodQty, foodSellerId, foodPrice, sellerEmail, sellerToken, qtyOrderedFBase, qtyOrd;
    EditText buyerName, buyerAddress, buyerPhone;
    Button orderSubmit;
    ImageView closeDialog;
    LinearLayout formLayout, orderCompleteLayout;
    TextView closeComplete;
    View progressView;
    int orderCntFb;
    String qtyFromFBDB;
    DatabaseReference mRef;
    private static final String IS_ORDERED = "ordered";
    private static final String AWAITING_DELIVERY = "awaiting";


    public OrderDialog() {

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
        foodId = mm.getString("foodId");
        foodName = mm.getString("foodName");
        foodDesc = mm.getString("foodDesc");
        foodQty = mm.getString("foodQty");
        foodPrice = mm.getString("foodPrice");
        foodSellerId = mm.getString("foodSellerId");
        sellerEmail = mm.getString("foodSellerEmail");
        sellerToken = mm.getString("foodSellerToken");
        qtyOrd = mm.getString("foodQtyOrd");

        Log.e(TAG, "onCreate: food ID = " + foodId + " seller email = " + sellerEmail + " seller tok == " + sellerToken);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_order, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mRef = FirebaseDatabase.getInstance().getReference();

        buyerName = (EditText) rootView.findViewById(R.id.etOrderContactName);
        buyerAddress = (EditText) rootView.findViewById(R.id.etOrderContactAddress);
        buyerPhone = (EditText) rootView.findViewById(R.id.etOrderContactPhone);
        orderSubmit = (Button) rootView.findViewById(R.id.btOrderSubmit);
        closeDialog = (ImageView) rootView.findViewById(R.id.ivCloseDialog);
        orderCompleteLayout = (LinearLayout) rootView.findViewById(R.id.orderCompleteLayout);
        formLayout = (LinearLayout) rootView.findViewById(R.id.orderFormLayout);
        progressView = rootView.findViewById(R.id.order_progress);
        closeComplete = (TextView) rootView.findViewById(R.id.tvCloseDialogComplete);

        orderSubmit.setOnClickListener(this);
        closeDialog.setOnClickListener(this);
        closeComplete.setOnClickListener(this);

        mRef.child("Product").child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                qtyFromFBDB = dataSnapshot.child("productQtyOrdered").getValue(String.class);
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
            case R.id.tvCloseDialogComplete:
                 dismiss();
                break;
            case R.id.btOrderSubmit:

                String buyerNameStr = buyerName.getText().toString();
                String buyerAddressStr = buyerAddress.getText().toString();
                String buyerPhoneStr = buyerPhone.getText().toString();

                if (TextUtils.isEmpty(buyerNameStr)) {
                    buyerName.setError("Invalid Name");
                    buyerName.requestFocus();
                    Toast.makeText(getActivity(), "Enter your Name", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(buyerAddressStr)) {
                    buyerAddress.setError("Invalid Address");
                    buyerAddress.requestFocus();
                    Toast.makeText(getActivity(), "Enter Address to deliver your food", Toast.LENGTH_SHORT).show();
                }else if (TextUtils.isEmpty(buyerPhoneStr)) {
                    buyerPhone.setError("Invalid Phone");
                    buyerPhone.requestFocus();
                    Toast.makeText(getActivity(), "Enter your Phone Number", Toast.LENGTH_SHORT).show();
                } else {
                    //update db
                    //notify seller
                    //oncomplete, set orderComplete layout visible
                    String orderMsg = "Order Details " + "\n\n" +
                            "Item Name: \t\t" + foodName + "\n" +
                            "Item QTY: \t\t" + foodQty + "\n" +
                            "Item Price: \t\t" + foodPrice + "\n" +
                            "-------------------------------\n\n" +
                            "Buyer Name: \t\t" + buyerNameStr + "\n" +
                            "Delivery Address: \t\t" + buyerAddressStr + "\n" +
                            "Contact Phone: \t\t" + buyerPhoneStr + "\n" +
                            "-------------------------------\n-------------------------------\n\n" +
                            "Thanks\n" +
                            "PaperBag";

                    JSONArray recArray = new JSONArray();
                    recArray.put(sellerToken);
                    sendFCMMessage(recArray, "You have a New Order", buyerNameStr + " just placed an Order", orderMsg);

                        addProductToCart(foodId, foodName, foodDesc, foodSellerId, foodQty, foodPrice);
                        sendEmail(sellerEmail, orderMsg);
                        upFirebaseDbOrderCount(foodId, foodQty);
                        dismiss();
                        Toast.makeText(getActivity(), "Order Successfully Placed", Toast.LENGTH_SHORT).show();

                }

                break;
        }

    }

    private void updateDeliveryColumn (String foodID){
        Uri uri = PaperBagContract.MyProduct.CONTENT_URI;

        String sel = PaperBagContract.MyProduct.PRODUCT_ID + "?";
        String[] arg = new String[] {foodID};
        ContentValues mValues = new ContentValues();
        getActivity().getContentResolver().update(uri, mValues, sel, arg);

    }

    private void addProductToCart(String prodId, String prodName, String prodDesc, String prodSellerId, String prodQty, String prodPrice) {

        ContentValues mValues = new ContentValues();
        mValues.put(PaperBagContract.MyProduct.PRODUCT_NAME, prodName);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_DESC, prodDesc);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_ID, prodId);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_PRICE, prodPrice);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_QTY, prodQty);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_SELLER_ID, prodSellerId);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_IS_ORDERED, IS_ORDERED);
        mValues.put(PaperBagContract.MyProduct.DELIVERY_STATUS, AWAITING_DELIVERY);

        Uri uri = PaperBagContract.MyProduct.CONTENT_URI;

            getActivity().getContentResolver().insert(uri, mValues);
            Toast.makeText(getActivity(), prodName + " successfully added to cart", Toast.LENGTH_SHORT).show();

        ((BuyerFoodActivity) getActivity()).invalidateOptionsMenu();

    }


    private void sendEmail(String sellerEmail, String msg){
        String[] TO = {sellerEmail};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("message/rfc822");
//        emailIntent.setType("text/plain");

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
//        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "New Order from PaperBag");
        emailIntent.putExtra(Intent.EXTRA_TEXT, msg);



        try {
            startActivity(Intent.createChooser(emailIntent, "Select email app to complete order..."));
            /*Intent chooserIntent = Intent.createChooser(new Intent(), "Select Email App:");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, mailChooserIntent.toArray(new Parcelable[mailChooserIntent.size()]));
            startActivity(chooserIntent);*/


        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),"There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void upFirebaseDbOrderCount(final String productId, final String qtyOrdered) {
        final DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        int newQty = Integer.parseInt(qtyFromFBDB) + Integer.parseInt(qtyOrdered);
        Log.e(TAG, "upFirebaseDbOrderCount == " + newQty );
        mRef.child("Product").child(productId).child("productQtyOrdered").setValue(String.valueOf(newQty));

    }

    public interface SendFCMFragmentListener{
         void onSendFCMMessageClick (JSONArray recipients, String title, String body, String message);

    }

    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    OkHttpClient mClient = new OkHttpClient();
    public void sendFCMMessage(final JSONArray recipients, final String title, final String body, final String message) {
//    public void sendFCMMessage(final JSONArray recipients, final String title, final String body, final String icon, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
                String result = null;
                try {
                    JSONObject root = new JSONObject();
                    JSONObject notification = new JSONObject();
                    notification.put("body", body);
                    notification.put("title", title);
//                    notification.put("icon", icon);

                    JSONObject data = new JSONObject();
                    data.put("message", message);
                    root.put("notification", notification);
                    root.put("data", data);
                    root.put("registration_ids", recipients);

                    result = postToFCM(root.toString());
                    Log.e(TAG, "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                if (TextUtils.isEmpty(result)) {

                    return;
                }
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    Log.e(TAG, "onPostExecute: " + "Message Success: " + success + "Message Failed: " + failure );
//                    Toast.makeText(getActivity(), "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
//                    Toast.makeText(getActivity(), "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    String postToFCM(String bodyString) throws IOException {
        final MediaType JSON
                = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, bodyString);
        Request request = new Request.Builder()
                .url(FCM_MESSAGE_URL)
                .post(body)
                .addHeader("Authorization", "key=" + MyUtils.Legacy_SERVER_KEY)
                .build();
        Response response = mClient.newCall(request).execute();
        return response.body().string();
    }

}
