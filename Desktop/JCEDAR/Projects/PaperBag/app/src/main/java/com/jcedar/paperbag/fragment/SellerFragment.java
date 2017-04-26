package com.jcedar.paperbag.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.activity.SellerDetailActivity;
import com.jcedar.paperbag.dialog.AddNewProductDialog;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.holder.ProductHolder;
import com.jcedar.paperbag.model.Product;

/**
 * Created by OLUWAPHEMMY on 3/27/2017.
 */
public class SellerFragment extends Fragment implements View.OnClickListener,
        AddNewProductDialog.AddProductFragmentInteractionListener {

    private static final String TAG = "SellerFragment";
    View view;
    private TextView welcomeText;
    private FloatingActionButton newProductFab;
    String uuid, role, user;
    TextView errorMsg, sellerProdCount;

    private DatabaseReference mFirebaseRef, mDb;

    private RecyclerView recyclerView;
    private GridLayoutManager linearLayoutManager;
    LinearLayout progLayout;

    private FirebaseRecyclerAdapter<Product, ProductHolder> mAdapter;

    public SellerFragment () {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        uuid = mBundle.getString("uuid");
        user = mBundle.getString("user");
        role = mBundle.getString("role");

        Log.e(TAG, "onCreate: bundle user = " + user + " uuid = " + uuid);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_seller, container, false);
        mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        mDb = mFirebaseRef.child("Product");


        welcomeText = (TextView) view.findViewById(R.id.tvWelcomeSeller);
        newProductFab = (FloatingActionButton) view.findViewById(R.id.mainFab);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerSeller);
        sellerProdCount = (TextView) view.findViewById(R.id.tvSellerCount);
        progLayout = (LinearLayout) view.findViewById(R.id.categoryProgressLayout);
        errorMsg = (TextView) view.findViewById(R.id.errorMsg);

        linearLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setHasFixedSize(true);

        welcomeText.setText("Welcome " + user);
        newProductFab.setOnClickListener(this);
        Query query = mFirebaseRef.child("Product").orderByChild("productSellerId").equalTo(uuid);


        mAdapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(Product.class,
                R.layout.row_item_seller, ProductHolder.class, query
        ) {
            @Override
            protected void populateViewHolder(ProductHolder viewHolder, final Product model, final int position) {

                viewHolder.productName.setText(model.getProductName());
                viewHolder.orderedCount.setText("Ordered: " + model.getProductQtyOrdered());
                viewHolder.productPrice.setText("$" + model.getProductPrice());
                viewHolder.imgPrev.setImageBitmap(MyUtils.decodeBase64(model.getProductPhoto()));
//                viewHolder.imgPreview.setBackground(new BitmapDrawable(getResources(), )));
                viewHolder.delImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteProduct(position, model);
                    }
                });

                viewHolder.sellerListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent detailIntent = new Intent(getActivity(), SellerDetailActivity.class);
                        detailIntent.putExtra("productId", model.getProductID());
                        detailIntent.putExtra("productName", model.getProductName());
                        detailIntent.putExtra("productQtyOrdered", model.getProductQtyOrdered());
                        detailIntent.putExtra("productDate", model.getProductDateAdded());
//                        detailIntent.putExtra("productPhoto", model.getProductPhoto());
                        startActivity(detailIntent);
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
//        mAdapter.notifyDataSetChanged();


        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    errorMsg.setVisibility(View.VISIBLE);
                    progLayout.setVisibility(View.GONE);
                } else {
                    progLayout.setVisibility(View.GONE);
                    errorMsg.setVisibility(View.GONE);
                }
                RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        progLayout.setVisibility(View.GONE);
                        if (itemCount == 0) {
                            errorMsg.setVisibility(View.VISIBLE);
                        } else
                            errorMsg.setVisibility(View.GONE);
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

                sellerProdCount.setText( "My Products ( " + mAdapter.getItemCount() + " )");
                mAdapter.registerAdapterDataObserver(mObserver);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mainFab:
                if (MyUtils.checkNetworkAvailability(getActivity())) {
                    openAddNewDialog();
                }else {
                    MyUtils.networkDialog(getActivity()).show();
                }
                break;
        }
    }

    private void deleteProduct(final int position, final Product model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deleting this item means it will no more be available to buyers")
                .setTitle("Are you sure you want to delete this item ?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Product");
        Product czm = mAdapter.getItem(position);
        String key = mDb.child(czm.getProductID()).getKey();
        final String mm = model.getProductID();

        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.child(mm).getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.e(TAG, "czm key in delete " + key + " mm del " + mm);
/*        String data = "TRUE";
        notifyActivity(data, getActivity());*/
        if (mAdapter != null) {
            mAdapter.notifyItemRemoved(position);
        }
        mAdapter.notifyDataSetChanged();
        Toast.makeText(getActivity(), "Product successfully deleted", Toast.LENGTH_SHORT).show();

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

    private void openAddNewDialog() {
        AddNewProductDialog dialog = new AddNewProductDialog();
        Bundle mm = new Bundle();
        mm.putString("uuid", uuid);
        dialog.setArguments(mm);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "add_product");


    }

    /**send broadcast to mainactivity after update*/
    private  void notifyActivity(String data, String fragmentName){
        Intent intent = new Intent("my-event");
        intent.putExtra("message", data);
        intent.putExtra("fragment", fragmentName);
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public void onAddProductButtonClick(final String productName, String productPhoto, String productDesc, String productPrice,
                                        String productQty, String productQtyOrdered, String productDateAdded, String productSellerId, String productCommentCount,
                                        String productCategoryTitle, String productCategoryId) {
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Adding New Product...");
        mDialog.setCancelable(false);
        mDialog.show();

        DatabaseReference productRef = mFirebaseRef.child("Product");
        String productId = productRef.push().getKey();
        Product mProd = new Product(productId, productName, productPhoto, productDesc, productPrice, productQty, productQtyOrdered,
                productDateAdded, productSellerId, productCommentCount, productCategoryTitle, productCategoryId);
        productRef.child(productId).setValue(mProd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "New Product " + productName + " successfully added", Toast.LENGTH_SHORT).show();
                mDialog.hide();
                mDialog.dismiss();
                notifyActivity("TRUE", "SellerFragment");

            }
        });

    }
}
