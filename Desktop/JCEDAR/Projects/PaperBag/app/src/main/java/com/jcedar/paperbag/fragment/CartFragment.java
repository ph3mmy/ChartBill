package com.jcedar.paperbag.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.activity.BuyerFoodActivity;
import com.jcedar.paperbag.adapter.PaperBagRecyclerAdapter;
import com.jcedar.paperbag.dialog.CommentDialog;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.model.Comment;
import com.jcedar.paperbag.provider.PaperBagContract;

/**
 * Created by OLUWAPHEMMY on 4/7/2017.
 */
public class CartFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        PaperBagRecyclerAdapter.ClickListener, CommentDialog.CommentFragmentListener{

    private static final String TAG = "CartFragment";
    View view;
    TextView cartCounter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private static final String CART_FRAGMENT = "CartFragment";
    private static final int CART_LOADER = 0;
    private static final int FAV_LOADER = 1;
    PaperBagRecyclerAdapter mAdapter;
    DatabaseReference mRef;
    String fragType, uuid, commentator, sellerEmail, sellerToken;
    FirebaseAuth mAuth;


    private boolean isCart;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        fragType = mBundle.getString("frag");
        mAuth = FirebaseAuth.getInstance();
        uuid = mAuth.getCurrentUser().getUid();

        isCart = CART_FRAGMENT.equalsIgnoreCase(fragType);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_cart, container, false);

        getLoaderManager().initLoader(CART_LOADER, null, this);
        getLoaderManager().initLoader(FAV_LOADER, null, this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerCart);
        cartCounter = (TextView) view.findViewById(R.id.tvCartCounter);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new PaperBagRecyclerAdapter(getActivity(), fragType);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setClicklistener(this);


        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.child("User").child(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    commentator = dataSnapshot.child("name").getValue(String.class);
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (isCart) {
            return new CursorLoader(getActivity(),
                    PaperBagContract.MyProduct.CONTENT_URI,
                    PaperBagContract.MyProduct.PROJECTION_ALL,
                    null,
                    null,
                    null);
        } else {
            return new CursorLoader(getActivity(),
                    PaperBagContract.MyFavorite.CONTENT_URI,
                    PaperBagContract.MyFavorite.PROJECTION_ALL,
                    null,
                    null,
                    null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mAdapter.notifyDataSetChanged();
        if (isCart) {
            cartCounter.setText("My Cart Items ( " + mAdapter.getItemCount() + " )");
        } else {
            cartCounter.setText("My Favorite Items ( " + mAdapter.getItemCount() + " )");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);
    }

    @Override
    public void itemClickListener(View view, int position) {
        Cursor foodCursor  = mAdapter.getItem(position);
        String foodId = foodCursor.getString(foodCursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_ID));
        String foodBaseId = foodCursor.getString(foodCursor.getColumnIndex(PaperBagContract.MyProduct._ID));
        String foodName = foodCursor.getString(foodCursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_NAME));
        String foodSellerId = foodCursor.getString(foodCursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_SELLER_ID));
        String foodDesc = foodCursor.getString(foodCursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_DESC));
        String foodSellerTp = foodCursor.getString(foodCursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_PRICE));
        String foodQty = foodCursor.getString(foodCursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_QTY));

        final ProgressDialog mDia = new ProgressDialog(getActivity());
        if (TextUtils.isEmpty(sellerEmail) || sellerEmail.equalsIgnoreCase(null)) {
            mDia.setTitle("Fetching Data");
            mDia.setMessage("Please Wait...");
            mDia.setIndeterminate(true);
            mDia.setCancelable(false);
            mDia.show();
        }

        int qty, totalP;
        totalP = Integer.parseInt(foodSellerTp);
        qty = Integer.parseInt(foodQty);
        String ppu = String.valueOf((totalP/qty));

        final Query storeQuery = mRef.child("Stores").orderByChild("storeSellerId").equalTo(foodSellerId);
        storeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                    sellerEmail = datasnap.child("storeSellerEmail").getValue(String.class);
                    sellerToken = datasnap.child("storeSellerToken").getValue(String.class);
                    if (!TextUtils.isEmpty(sellerEmail)) {
                        mDia.hide();
                        mDia.dismiss();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        switch (view.getId()) {
            case R.id.cartTvRemove:
                removeCartItem(foodBaseId);
                break;
            case R.id.cartTvFavOrder:
                if (TextUtils.isEmpty(sellerEmail)) {
                    mDia.show();
                } else {
                    mDia.hide();
                    mDia.dismiss();

                    Intent intent = new Intent(getActivity(), BuyerFoodActivity.class);
                    intent.putExtra("prodId", foodId);
                    intent.putExtra("fav", "FAV");
                    intent.putExtra("prodName", foodName);
                    intent.putExtra("prodDesc", foodDesc);
                    intent.putExtra("ppu", ppu);
                    intent.putExtra("prodSellerId", foodSellerId);
                    intent.putExtra("prodSellerToken", sellerToken);
                    intent.putExtra("prodSellerEmail", sellerEmail);
                    intent.putExtra("totalP", totalP);
                    intent.putExtra("prodQty", qty);
                    startActivity(intent);
                }

                break;
            case R.id.cartTvOrderComplete:
                //show comment and rating dialog
                if (MyUtils.checkNetworkAvailability(getActivity())) {
                    commentDialog(foodId, foodName, commentator);
                } else {
                    MyUtils.networkDialog(getActivity()).show();
                }
                break;
            default:
                break;
        }

    }

    private void removeCartItem (final String foodBaseId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deleting this order implies that you have finalized transaction and delivery with the seller")
                .setTitle("Are you sure you want to delete this Order ?")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isCart) {
                            getActivity().getContentResolver().delete(PaperBagContract.MyProduct.CONTENT_URI,
                                    PaperBagContract.MyProduct._ID + "=?", new String[]{foodBaseId});
                            getLoaderManager().restartLoader(CART_LOADER, null, CartFragment.this);
                        } else {
                            getActivity().getContentResolver().delete(PaperBagContract.MyFavorite.CONTENT_URI,
                                    PaperBagContract.MyFavorite._ID + "=?", new String[]{foodBaseId});
                            getLoaderManager().restartLoader(FAV_LOADER, null, CartFragment.this);
                        }
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Removal successful", Toast.LENGTH_SHORT).show();
                    }
                })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCancelable(false);
                    dialog.show();
    }

    private void commentDialog (String foodId, String foodName, String commentor) {
        CommentDialog dialog = new CommentDialog();
        Bundle arg = new Bundle();
        arg.putString("foodId", foodId);
        arg.putString("foodName", foodName);
        arg.putString("commentator", commentor);
        dialog.setArguments(arg);
        dialog.setCancelable(false);
        dialog.setTargetFragment(CartFragment.this, 0);
        dialog.show(getFragmentManager(), "comment_dialog");

    }

    @Override
    public void onCommentButtonClick(String productId, String productName, String rating, String comment, String commentator) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("Comment");

        String key = mRef.push().getKey();
        Comment comment1 = new Comment(key, productId, productName, rating, comment, commentator, "0");
        mRef.child(key).setValue(comment1);
        Toast.makeText(getActivity(), "Comment submitted successfully and transaction finalized", Toast.LENGTH_SHORT).show();
        getActivity().getContentResolver().delete(PaperBagContract.MyProduct.CONTENT_URI,
                PaperBagContract.MyProduct.PRODUCT_ID + "=?", new String[]{productId});
        mAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(CART_LOADER, null, CartFragment.this);
    }

}
