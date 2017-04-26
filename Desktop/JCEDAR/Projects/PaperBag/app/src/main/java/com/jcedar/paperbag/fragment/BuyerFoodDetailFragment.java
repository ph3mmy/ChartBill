package com.jcedar.paperbag.fragment;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.jcedar.paperbag.provider.PaperBagContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLUWAPHEMMY on 4/5/2017.
 */
public class BuyerFoodDetailFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BuyerFoodDetailFragment";
    View view;
    ImageView imgLayout, addFavImg;
    TextView prodName, prodPrice, prodDesc, prodTotalPrice;
    Spinner qtySpinner;
    Button addToCart;
    String foodName, foodDesc, foodPrice, foodId, foodSellerId, spinnerQtySel, sellerEmail, sellerToken, qtyOrd;
    int qtyLeft;
    int totalPrice;
    DatabaseReference mRef;
    List<Integer> spinnerList;
    private static final String NOT_YET_FAV = "not_favorite";
    private static final String NOT_YET_ORDERED = "not_ordered";
    private static final String IS_FAV = "favorite";
    private static final String IS_ORDERED = "ordered";
    private static final String AWAITING_DELIVERY = "awaiting";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BuyerFoodActivity)getActivity()).showUpButton();


        Bundle mArg = getArguments();
        foodId = mArg.getString("prodId");
        foodName = mArg.getString("prodName");
        foodDesc = mArg.getString("prodDesc");
        foodPrice = mArg.getString("prodPrice");
        foodSellerId = mArg.getString("prodSellerId");
        qtyLeft = mArg.getInt("prodQty");
        qtyOrd = mArg.getString("prodQtyOrd");

        Log.e(TAG, "onCreate: foodSeller id = " + foodSellerId );

        ((BuyerFoodActivity) getActivity()).getSupportActionBar().setTitle(foodName + " Details");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer_food_detail, container, false);

        mRef = FirebaseDatabase.getInstance().getReference();
        spinnerList = new ArrayList<>();

        for (int i = 1; i <= qtyLeft; i++) {
            spinnerList.add(i);
        }


        imgLayout = (ImageView) view.findViewById(R.id.buyerDetailImgRel);
        addFavImg = (ImageView) view.findViewById(R.id.ivBuyerAddFav);
        prodName = (TextView) view.findViewById(R.id.tvBuyerDetailName);
        prodDesc = (TextView) view.findViewById(R.id.tvBuyerDetailDesc);
        prodPrice = (TextView) view.findViewById(R.id.tvBuyerDetailPrice);
        prodTotalPrice = (TextView) view.findViewById(R.id.tvBuyerDetailTotalPrice);
        qtySpinner = (Spinner) view.findViewById(R.id.spinnerBuyerQty);
        addToCart = (Button) view.findViewById(R.id.btBuyerAddToCart);

        addToCart.setOnClickListener(this);
        addFavImg.setOnClickListener(this);

        prodName.setText(foodName);
        prodDesc.setText(foodDesc);
        prodPrice.setText("$" + foodPrice);

        if (qtyLeft <= 0 || spinnerList.size() == 0) {
//            addToCart.setEnabled(false);
            //addToCart.setClickable(false);
        }

        final Query storeQuery = mRef.child("Stores").orderByChild("storeSellerId").equalTo(foodSellerId);
        storeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                    sellerEmail = datasnap.child("storeSellerEmail").getValue(String.class);
                   sellerToken = datasnap.child("storeSellerToken").getValue(String.class);
//                    Log.e(TAG, "onDataChange: store test seller email = " + prodPhoto + " tokenSella = " + st);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

            final Query imgQuery = mRef.child("Product").orderByChild("productID").equalTo(foodId);
            imgQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                        String prodPhoto = datasnap.child("productPhoto").getValue(String.class);
                        imgLayout.setImageBitmap(MyUtils.decodeBase64(prodPhoto));
                    }
                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ArrayAdapter<Integer> camAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, spinnerList);
        camAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        qtySpinner.setAdapter(camAdapter);

    qtySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            final String sel = qtySpinner.getSelectedItem().toString();
            spinnerQtySel = sel;

            totalPrice = (Integer.parseInt(foodPrice)  * Integer.parseInt(spinnerQtySel));
            prodTotalPrice.setText("$" + totalPrice);
//            categoryId = categAndKey.get(sel);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });

        String selection1 = PaperBagContract.MyProduct.PRODUCT_ID + "=?";
        String[] arg = new String[]{foodId};
        Cursor cc = getActivity().getContentResolver().query(PaperBagContract.MyFavorite.CONTENT_URI,
                PaperBagContract.MyFavorite.PROJECTION_ALL, selection1,
                arg, null);

        if (cc != null) {
            while (cc.moveToNext()) {
                        addFavImg.setImageResource(R.drawable.fav_red);
            }
            cc.close();
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btBuyerAddToCart:

                if (MyUtils.checkNetworkAvailability(getActivity())) {
                    if (!TextUtils.isEmpty(spinnerQtySel) || (qtyLeft > 0)) {
                        String qtyOrdd = String.valueOf((Integer.parseInt(qtyOrd) + Integer.parseInt(spinnerQtySel)));
                        placeOrder(foodId, sellerEmail, sellerToken, foodName, foodDesc, foodSellerId, spinnerQtySel, qtyOrdd, String.valueOf(totalPrice));
                    } else {
                        Snackbar.make(view, foodName + " is out of stock ", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "onClick: check out network= " );
                    MyUtils.networkDialog(getActivity()).show();
                }
                break;
            case R.id.ivBuyerAddFav:
                if (!TextUtils.isEmpty(spinnerQtySel)) {
                    addProductToFav(foodId, foodName, foodDesc, foodSellerId, spinnerQtySel, String.valueOf(totalPrice));
                    addFavImg.setImageResource(R.drawable.fav_red);
                } else {
                    Toast.makeText(getActivity(), foodName + " is out of stock ", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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
        setTargetFragment(BuyerFoodDetailFragment.this, 0);
        dialog.setArguments(arg);
        dialog.setCancelable(false);
        dialog.show(getFragmentManager(), "order_dialog");

    }

    private void loadSummaryFragment (String id, String Name, String desc, String ppu, String sellerId, String qtyOrd, int qtyLeft, int totalPri, String sellerEmail, String sellerToken) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        OrderSummaryFragment fragment = new OrderSummaryFragment();
        Bundle args = new Bundle();
        args.putString("prodId", id);
        args.putString("prodName", Name);
        args.putString("prodDesc", desc);
        args.putString("ppu", ppu);
        args.putString("prodSellerId", sellerId);
        args.putString("prodSellerToken", sellerToken);
        args.putString("prodSellerEmail", sellerEmail);
        args.putString("prodQtyOrd", qtyOrd);
        args.putInt("totalP", totalPri);
        args.putInt("prodQty", qtyLeft);
        fragment.setArguments(args);
//        ft.addToBackStack("order_sum_fragment");
        ft.hide(BuyerFoodDetailFragment.this);
//        ft.add(android.R.id.content, fragment);
        ft.replace(R.id.buyerContainer, fragment, "order_sum_fragment");
        ft.commit();
        ft.addToBackStack("order_sum_fragment");
    }

    private void addProductToCart(String prodId, String prodName, String prodDesc, String prodSellerId, String prodQty, String prodPrice) {

        ContentValues mValues = new ContentValues();
        mValues.put(PaperBagContract.MyProduct.PRODUCT_NAME, prodName);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_DESC, prodDesc);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_ID, prodId);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_PRICE, prodPrice);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_QTY, prodQty);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_SELLER_ID, prodSellerId);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_IS_FAV, NOT_YET_FAV);
        mValues.put(PaperBagContract.MyProduct.PRODUCT_IS_ORDERED, IS_ORDERED);
        mValues.put(PaperBagContract.MyProduct.DELIVERY_STATUS, AWAITING_DELIVERY);

//        String selection1 = PaperBagContract.MyProduct.PRODUCT_ID + "=?" + " AND " + PaperBagContract.MyProduct.PRODUCT_IS_ORDERED + "=?";
        String selection1 = PaperBagContract.MyProduct.PRODUCT_ID + "=?";
        String[] arg = new String[]{prodId};

        Uri uri = PaperBagContract.MyProduct.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,
                PaperBagContract.MyProduct.PROJECTION_ALL,
                selection1,
                arg,
                null);
        Cursor c = cursorLoader.loadInBackground();

        if (!c.moveToNext()) {
            getActivity().getContentResolver().insert(uri, mValues);
            Toast.makeText(getActivity(), prodName + " successfully added to cart", Toast.LENGTH_SHORT).show();
        } else {
            String isAlreadyOrdered = c.getString(c.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_IS_ORDERED));
            if (!isAlreadyOrdered.equalsIgnoreCase(IS_ORDERED)) {
                ContentValues mmValues = new ContentValues();
                mmValues.put(PaperBagContract.MyProduct.PRODUCT_IS_ORDERED, IS_ORDERED);
                getActivity().getContentResolver().update(uri, mmValues, selection1, arg );
                Toast.makeText(getActivity(), prodName + " successfully added to cart", Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(getActivity(), prodName + " already added to cart", Toast.LENGTH_SHORT).show();
            }
        }
        c.close();

    }

    private void addProductToFav(String prodId, String prodName, String prodDesc, String prodSellerId, String prodQty, String prodPrice) {

        ContentValues mValues = new ContentValues();
        mValues.put(PaperBagContract.MyFavorite.PRODUCT_NAME, prodName);
        mValues.put(PaperBagContract.MyFavorite.PRODUCT_DESC, prodDesc);
        mValues.put(PaperBagContract.MyFavorite.PRODUCT_ID, prodId);
        mValues.put(PaperBagContract.MyFavorite.PRODUCT_PRICE, prodPrice);
        mValues.put(PaperBagContract.MyFavorite.PRODUCT_QTY, prodQty);
        mValues.put(PaperBagContract.MyFavorite.PRODUCT_SELLER_ID, prodSellerId);
        mValues.put(PaperBagContract.MyFavorite.PRODUCT_IS_ORDERED, NOT_YET_ORDERED);
        mValues.put(PaperBagContract.MyFavorite.DELIVERY_STATUS, NOT_YET_ORDERED);

//        String selection1 = PaperBagContract.MyProduct.PRODUCT_ID + "=?" + " AND " + PaperBagContract.MyProduct.PRODUCT_IS_ORDERED + "=?";
        String selection1 = PaperBagContract.MyFavorite.PRODUCT_ID + "=?";
        String[] arg = new String[]{prodId};

        Uri uri = PaperBagContract.MyFavorite.CONTENT_URI;
        CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,
                PaperBagContract.MyFavorite.PROJECTION_ALL,
                selection1,
                arg,
                null);
        Cursor c = cursorLoader.loadInBackground();

        if (!c.moveToNext()) {
            getActivity().getContentResolver().insert(uri, mValues);
            Toast.makeText(getActivity(), prodName + " successfully added to favorite", Toast.LENGTH_SHORT).show();
        } else {
                Toast.makeText(getActivity(), prodName + " already added to favorite", Toast.LENGTH_SHORT).show();

        }
        c.close();
        ((BuyerFoodActivity) getActivity()).supportInvalidateOptionsMenu();

    }

    @Override
    public void onResume() {
        super.onResume();

        ((BuyerFoodActivity) getActivity()).supportInvalidateOptionsMenu();
        ((BuyerFoodActivity) getActivity()).getSupportActionBar().setTitle(foodName + " Details");
    }


}
