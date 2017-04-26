package com.jcedar.paperbag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.activity.BuyerFoodActivity;
import com.jcedar.paperbag.dialog.OrderDialog;
import com.jcedar.paperbag.helper.MyUtils;

import org.json.JSONArray;

/**
 * Created by OLUWAPHEMMY on 4/14/2017.
 */

public class OrderSummaryFragment extends Fragment implements OrderDialog.SendFCMFragmentListener {

    private static final String TAG = "OrderSummaryFragment";
    private TextView orderName, orderPpu, orderQty, orderTotalPrice;
    private ImageView orderSumImage;
    private Button orderSumCheckout;
    private String itemName;
    private String itemPrice;
    private int itemQty;
    private int itemTotalPrice;
    private String foodId;
    private String itemDesc;
    private String itemSellerEmail;
    private String itemSellerToken;
    private String itemSellerId, itemOrd;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ((BuyerFoodActivity) getActivity()).getSupportActionBar().setTitle("Order Summary");

        Bundle mBundle = getArguments();
        itemName = mBundle.getString("prodName");
        foodId = mBundle.getString("prodId");
        itemPrice = mBundle.getString("ppu");
        itemDesc = mBundle.getString("prodDesc");
        itemOrd = mBundle.getString("prodQtyOrd");
        itemQty = mBundle.getInt("prodQty");
        itemTotalPrice = mBundle.getInt("totalP");
        itemSellerId = mBundle.getString("prodSellerId");
        itemSellerEmail = mBundle.getString("prodSellerEmail");
        itemSellerToken = mBundle.getString("prodSellerToken");


/*        args.putString("prodSellerId", sellerId);
        args.putString("prodSellerToken", sellerToken);
        args.putString("prodSellerEmail", sellerEmail);*/

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_summary, container, false);

        orderName = (TextView) view.findViewById(R.id.tvOrderSumName);
        orderPpu = (TextView) view.findViewById(R.id.tvOrderSumPricePU);
        orderQty = (TextView) view.findViewById(R.id.tvOrderSumQty);
        orderTotalPrice = (TextView) view.findViewById(R.id.tvOrderSumTotal);
        orderSumImage = (ImageView) view.findViewById(R.id.orderSumImgRel);
        orderSumCheckout = (Button) view.findViewById(R.id.btOrderSumCheckout);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();

        orderName.setText(itemName);
        orderPpu.setText("$" + itemPrice);
        orderQty.setText("" +itemQty);
        orderTotalPrice.setText("$" + itemTotalPrice);

        final Query imgQuery = mRef.child("Product").orderByChild("productID").equalTo(foodId);
        imgQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                    String prodPhoto = datasnap.child("productPhoto").getValue(String.class);
                    orderSumImage.setImageBitmap(MyUtils.decodeBase64(prodPhoto));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        orderSumCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qtyStr = String.valueOf(itemQty);
                String totalPriceStr = String.valueOf(itemTotalPrice);
                placeOrder(foodId, itemSellerEmail, itemSellerToken, itemName, itemDesc, itemSellerId,  qtyStr, itemOrd,totalPriceStr);
            }
        });

        return view;
    }


    private void placeOrder (String foodId, String sellerEmail, String sellerToken, String foodName, String foodDesc,
                             String foodSellerId, String foodQty, String foodQtyOrd, String foodPrice) {

        OrderDialog dialog = new OrderDialog();
        Bundle arg = new Bundle();
        arg.putString("foodId", foodId);
        arg.putString("foodName", foodName);
        arg.putString("foodDesc", foodDesc);
        arg.putString("foodQty", foodQty);
        arg.putString("foodQtyOrd", foodQtyOrd);
        arg.putString("foodSellerId", foodSellerId);
        arg.putString("foodPrice", foodPrice);
        arg.putString("foodSellerEmail", sellerEmail);
        arg.putString("foodSellerToken", sellerToken);
        setTargetFragment(OrderSummaryFragment.this, 0);
        dialog.setArguments(arg);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "order_dialog");

    }

/*    public static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    OkHttpClient mClient = new OkHttpClient();
    public void sendFCMMessage(final JSONArray recipients, final String title, final String body, final String message) {
//    public void sendFCMMessage(final JSONArray recipients, final String title, final String body, final String icon, final String message) {

        new AsyncTask<String, String, String>() {
            @Override
            protected String doInBackground(String... params) {
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

                    String result = postToFCM(root.toString());
                    Log.d(TAG, "Result: " + result);
                    return result;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject resultJson = new JSONObject(result);
                    int success, failure;
                    success = resultJson.getInt("success");
                    failure = resultJson.getInt("failure");
                    Toast.makeText(getActivity(), "Message Success: " + success + "Message Failed: " + failure, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Message Failed, Unknown error occurred.", Toast.LENGTH_LONG).show();
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
    }*/


    @Override
    public void onSendFCMMessageClick(JSONArray recipients, String title, String body, String message) {
        Log.e(TAG, "onSendFCMMessageClick: recipient = " + recipients + " title = " + title + " body = " + body + " message = " + message );
//        sendFCMMessage(recipients, title, body, message);
    }
}
