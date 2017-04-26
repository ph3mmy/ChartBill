package com.jcedar.paperbag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.activity.BuyerFoodActivity;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.holder.ProductHolder;
import com.jcedar.paperbag.model.Product;


/**
 * Created by OLUWAPHEMMY on 4/5/2017.
 */

public class BuyerFoodFragment extends Fragment implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "BuyerFoodFragment";
    View view;
    RecyclerView recyclerView;
    GridLayoutManager linearLayoutManager;
    DatabaseReference mFirebaseRef, mDb;
    private FirebaseRecyclerAdapter<Product, ProductHolder> mAdapter;
    TextView errorMsg;
    String categoryId;
    LinearLayout progLayout;
    String categoryName;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        categoryId = arg.getString("catId");
        categoryName = arg.getString("catName");


        ((BuyerFoodActivity) getActivity()).getSupportActionBar().setTitle(categoryName + " Menu");

//        getActivity().getSupportFragmentManager().addOnBackStackChangedListener(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_buyer_food, container, false);

//        ((BuyerFoodActivity)getActivity()).showUpButton();

        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerBuyer);
        errorMsg = (TextView)view.findViewById(R.id.errorMsg);
        progLayout = (LinearLayout) view.findViewById(R.id.categoryProgressLayout);

        mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        Query mDb = mFirebaseRef.child("Product").orderByChild("productCategoryId").equalTo(categoryId);

        linearLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setHasFixedSize(true);


        mAdapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(Product.class,
                R.layout.row_item_seller, ProductHolder.class, mDb
        ) {
            @Override
            protected void populateViewHolder(ProductHolder viewHolder, final Product model, final int position) {

                viewHolder.productName.setText(model.getProductName());
                viewHolder.orderedCount.setText(model.getProductDesc());
                viewHolder.orderedCount.setMaxLines(20);
                viewHolder.productPrice.setText("$" + model.getProductPrice());
                viewHolder.imgPrev.setImageBitmap(MyUtils.decodeBase64(model.getProductPhoto()));
//                viewHolder.imgPreview.setBackground(new BitmapDrawable(getResources(), )));
                viewHolder.delImg.setVisibility(View.GONE);
                viewHolder.sellerListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int prodQty = Integer.parseInt(model.getProductQty());
                        int prodQtyOrdered = Integer.parseInt(model.getProductQtyOrdered());
                        int qtyLeft = prodQty - prodQtyOrdered;
                        Log.e(TAG, "onClick: qty left == " + qtyLeft );
                        loadDetailFragment(model.getProductID(), model.getProductName(), model.getProductDesc(),
                                model.getProductPrice(), model.getProductSellerId(), qtyLeft, model.getProductQtyOrdered());
                    }
                });

            }
        };

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int jokeCount = mAdapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (jokeCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }

        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mAdapter);


        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    progLayout.setVisibility(View.GONE);
                    errorMsg.setVisibility(View.VISIBLE);
                } else {
                    errorMsg.setVisibility(View.GONE);
                    progLayout.setVisibility(View.GONE);
                }
                RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);/*
                        if (itemCount == 0) {
                            errorMsg.setVisibility(View.VISIBLE);
                        } else
                            errorMsg.setVisibility(View.GONE);*/
                        progLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        super.onItemRangeRemoved(positionStart, itemCount);
                        progLayout.setVisibility(View.GONE);
                        if (itemCount == 0) {
                            errorMsg.setVisibility(View.VISIBLE);
                        } else
                            errorMsg.setVisibility(View.GONE);
                    }

                };

                mAdapter.registerAdapterDataObserver(mObserver);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void loadDetailFragment (String id, String Name, String Desc, String Price, String sellerId, int qtyLeft, String qtyOrd) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        BuyerFoodDetailFragment fragment = new BuyerFoodDetailFragment();
        Bundle args = new Bundle();
        args.putString("prodId", id);
        args.putString("prodName", Name);
        args.putString("prodDesc", Desc);
        args.putString("prodPrice", Price);
        args.putString("prodSellerId", sellerId);
        args.putInt("prodQty", qtyLeft);
        args.putString("prodQtyOrd", qtyOrd);
        fragment.setArguments(args);
        ft.addToBackStack("buyer_food_fragment");
        ft.hide(BuyerFoodFragment.this);
//        ft.add(android.R.id.content, fragment);
        ft.replace(R.id.buyerContainer, fragment);
        ft.commit();
    }

    @Override
    public void onBackStackChanged() {
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() < 1) {
            ((BuyerFoodActivity)getActivity()).finish();
//            ((BuyerFoodActivity)getActivity()).hideUpButton();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((BuyerFoodActivity) getActivity()).supportInvalidateOptionsMenu();
        ((BuyerFoodActivity) getActivity()).getSupportActionBar().setTitle(categoryName + " Menu");
    }

}
