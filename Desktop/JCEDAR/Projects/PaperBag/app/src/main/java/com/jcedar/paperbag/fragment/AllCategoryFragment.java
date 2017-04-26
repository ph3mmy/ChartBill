package com.jcedar.paperbag.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.helper.FormatUtils;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.holder.CategoryHolder;
import com.jcedar.paperbag.holder.NewCommentHolder;
import com.jcedar.paperbag.holder.NewUserHolder;
import com.jcedar.paperbag.holder.ProductHolder;
import com.jcedar.paperbag.holder.StoreHolder;
import com.jcedar.paperbag.model.Category;
import com.jcedar.paperbag.model.Comment;
import com.jcedar.paperbag.model.Product;
import com.jcedar.paperbag.model.Stores;
import com.jcedar.paperbag.model.User;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by OLUWAPHEMMY on 4/4/2017.
 */

public class AllCategoryFragment extends Fragment {

    private static final String TAG = "AllCategoryFragment";
    private View view;
    RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private GridLayoutManager gridLayoutManager;
    private DatabaseReference mFirebaseRef, mDb;
    String viewType;
             String storeName, storeOwner;
    private FirebaseRecyclerAdapter<Category, CategoryHolder> mAdapter;
    private FirebaseRecyclerAdapter<Product, ProductHolder> productAdapter;
    private FirebaseRecyclerAdapter<User, NewUserHolder> userAdapter;
    private FirebaseRecyclerAdapter<Comment, NewCommentHolder> commentAdapter;
    private FirebaseRecyclerAdapter<Stores, StoreHolder> storeAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arg = getArguments();
        viewType = arg.getString("viewType");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_category, container, false);

        mFirebaseRef = FirebaseDatabase.getInstance().getReference();

        recyclerView = (RecyclerView) view.findViewById(R.id.allCategoryRecycler);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);

        if (viewType.equalsIgnoreCase("CATEGORY")) {
            mDb = mFirebaseRef.child("Category");
            mAdapter = new FirebaseRecyclerAdapter<Category, CategoryHolder>(Category.class,
                    R.layout.row_all_category, CategoryHolder.class, mDb
            ) {
                @Override
                protected void populateViewHolder(CategoryHolder viewHolder, final Category model, final int position) {

                    viewHolder.categTitle.setText(model.getCategoryTitle());
                    viewHolder.dateAdded.setText(FormatUtils.getRecyclerReadableDate(Long.parseLong(model.getCategoryDateAdded())));


                    final List<String> categList = new ArrayList<>();
                    Query query = mFirebaseRef.child("Product").orderByChild("productCategoryId").equalTo(model.getCategoryId());
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                String campTitle = dataSnapshot1.child("productCategoryId").getValue(String.class);
                                String key = dataSnapshot1.getKey();

                                if (!categList.contains(campTitle)) {
                                    categList.add(campTitle);
                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
//                    return categList.size();


                    viewHolder.catgSize.setText("" + categList.size());
                    viewHolder.coverImg.setImageBitmap(MyUtils.decodeBase64(model.getCategoryImage()));
//                viewHolder.imgPreview.setBackground(new BitmapDrawable(getResources(), )));
                    viewHolder.delImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteCategory(position, model);
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
        }
        else if (viewType.equalsIgnoreCase("PRODUCT")) {
            mDb = mFirebaseRef.child("Product");
            productAdapter = new FirebaseRecyclerAdapter<Product, ProductHolder>(Product.class,
                    R.layout.row_item_seller, ProductHolder.class, mDb
            ) {
                @Override
                protected void populateViewHolder(ProductHolder viewHolder, final Product model, final int position) {

                    viewHolder.productName.setText(model.getProductName());
                    viewHolder.orderedCount.setText("Ordered: " + model.getProductQtyOrdered());
                    viewHolder.productPrice.setText("$" + model.getProductPrice());
                    viewHolder.forAdminLayout.setVisibility(View.VISIBLE);
                    viewHolder.forAdminStore.setVisibility(View.GONE);
                    viewHolder.forAdminDate.setText("Added on " + FormatUtils.getRecyclerReadableDate(Long.parseLong(model.getProductDateAdded())));
                    viewHolder.imgPrev.setImageBitmap(MyUtils.decodeBase64(model.getProductPhoto()));
//                viewHolder.imgPreview.setBackground(new BitmapDrawable(getResources(), )));
                    viewHolder.delImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteProduct(position, model);
                        }
                    });

                }
            };

            productAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int jokeCount = productAdapter.getItemCount();
                    int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                    // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                    // to the bottom of the list to show the newly added message.
                    if (lastVisiblePosition == -1 ||
                            (positionStart >= (jokeCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                        recyclerView.scrollToPosition(positionStart);
                    }
                }
            });
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(productAdapter);
        }
        else if (viewType.equalsIgnoreCase("USER")) {
            mDb = mFirebaseRef.child("User");
            userAdapter = new FirebaseRecyclerAdapter<User, NewUserHolder>(User.class,
                    R.layout.row_admin_new_user, NewUserHolder.class, mDb
            ) {
                @Override
                protected void populateViewHolder(NewUserHolder viewHolder, final User model, final int position) {

                    viewHolder.userName.setText(model.getName());
                    viewHolder.userEmail.setText(model.getEmail());
                    viewHolder.userRole.setText(getUserRole(model.getRole()));
                    viewHolder.adminDelUserLayout.setVisibility(View.VISIBLE);
                    viewHolder.mLayout.setVisibility(View.GONE);
                    viewHolder.delUser.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            deleteUser(position, model);
                        }
                    });

                }
            };

            userAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int jokeCount = userAdapter.getItemCount();
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
            recyclerView.setAdapter(userAdapter);
        }
        else if (viewType.equalsIgnoreCase("COMMENT")) {
            mDb = mFirebaseRef.child("Comment");
            commentAdapter = new FirebaseRecyclerAdapter<Comment, NewCommentHolder>(Comment.class,
                    R.layout.row_admin_new_comment, NewCommentHolder.class, mDb
            ) {
                @Override
                protected void populateViewHolder(NewCommentHolder viewHolder, final Comment model, final int position) {


                    viewHolder.productName.setText(model.getProductName());
                    viewHolder.productRating.setText(model.getRating() + "/5.0");
                    viewHolder.productComment.setText(model.getComment());
                    viewHolder.commentor.setText(model.getCommentor());
                    viewHolder.mLayoutComm.setVisibility(View.GONE);

                }
            };

            commentAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int jokeCount = commentAdapter.getItemCount();
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
            recyclerView.setAdapter(commentAdapter);
        }
        else if (viewType.equalsIgnoreCase("STORE")) {
            mDb = mFirebaseRef.child("Stores");
            storeAdapter = new FirebaseRecyclerAdapter<Stores, StoreHolder>(Stores.class,
                    R.layout.row_admin_stores, StoreHolder.class, mDb
            ) {
                @Override
                protected void populateViewHolder(StoreHolder viewHolder, final Stores model, final int position) {

                    viewHolder.storeName.setText(model.getStoreName());
                    viewHolder.storeAddress.setText(model.getStoreAddress());
                    viewHolder.storePhone.setText(model.getStoreContactPhone());
                    viewHolder.storeUser.setText(model.getStoreSellerName());
                    viewHolder.storeDel.setVisibility(View.GONE);

                }
            };

            storeAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int jokeCount = storeAdapter.getItemCount();
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
            recyclerView.setAdapter(storeAdapter);
        }


        return view;
    }

    private int getCategorySize (String categId) {
        final List<String> categList = new ArrayList<>();
        Query query = mFirebaseRef.child("Product").orderByChild("productCategoryId").equalTo(categId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String campTitle = dataSnapshot1.child("productCategoryId").getValue(String.class);
                    String key = dataSnapshot1.getKey();

                    if (!categList.contains(campTitle)) {
                        categList.add(campTitle);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return categList.size();

    }


    private String getUserRole (String role) {
        String rolex = null;
        if (role.equalsIgnoreCase("1")) {
            rolex = "Seller";
        }else if (role.equalsIgnoreCase("2")) {
            rolex = "Buyer";
        }
        return rolex;
    }

    private String getStoreName (String sellerId) {
        DatabaseReference storeRef = mFirebaseRef.child("Stores").child(sellerId);
        storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                storeName = dataSnapshot.child("storeName").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return storeName;
    }
/*    private String getStoreOwner (String sellerId) {
        String mm = null;
        DatabaseReference storeRef = mFirebaseRef.child("User").child(sellerId).child("name");
        storeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    mm = snap.getValue(String.class);
                }
//                storeOwner = dataSnapshot.getValue(String.class);
//                storeOwner = dataSnapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return mm;
    }*/

    private void deleteCategory(final int position, final Category model) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deleting this Food category will make all food in this category Unavailable to buyers")
                .setTitle("Are you sure you want to delete this Food Category ?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Category");
                        Category czm = mAdapter.getItem(position);
//        String key = mDb.child(czm.getProductID()).getKey();
                        final String mm = model.getCategoryId();
                        Query productDelQuery  = mFirebaseRef.child("Product").orderByChild("productCategoryId").equalTo(mm);

                        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dataSnapshot.child(mm).getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });/*
                        if (mAdapter != null) {
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Category successfully deleted", Toast.LENGTH_SHORT).show();*/
//                        dialogInterface.dismiss();


                        productDelQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dataSnapshot.getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        if (mAdapter != null) {
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), "Category and it food successfully deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();

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
    private void deleteProduct(final int position, final Product model) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deleting this Food will make it no more available to buyers")
                .setTitle("Are you sure you want to delete this Food Item ?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Product");
                        Category czm = mAdapter.getItem(position);
//        String key = mDb.child(czm.getProductID()).getKey();
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
                        if (mAdapter != null) {
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), model.getProductName() + " successfully deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
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

    private void deleteUser(final int position, final User model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Deleting this user will delete their created store and products")
                .setTitle("Are you sure you want to delete this user ?")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("User");
                        Category czm = mAdapter.getItem(position);
//        String key = mDb.child(czm.getProductID()).getKey();
                        final String mm = model.getUserID();

                        mDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                dataSnapshot.child(mm).getRef().removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //delete products associated with user
                        Query userDelQuery  = mFirebaseRef.child("Product").orderByChild("productSellerId").equalTo(mm);
                        userDelQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot datasnap1 : dataSnapshot.getChildren()) {
                                    datasnap1.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        Query userStoreDelQuery  = mFirebaseRef.child("Stores").orderByChild("storeSellerId").equalTo(mm);
                        userStoreDelQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot datasnap1 : dataSnapshot.getChildren()) {
                                    datasnap1.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        if (mAdapter != null) {
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity(), model.getName() + " and associated stores and products successfully deleted", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
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

}
