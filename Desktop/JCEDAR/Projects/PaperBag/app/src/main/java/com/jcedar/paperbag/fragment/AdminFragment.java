package com.jcedar.paperbag.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.jcedar.paperbag.activity.AdminViewActivity;
import com.jcedar.paperbag.dialog.AddNewCategoryDialog;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.holder.NewCommentHolder;
import com.jcedar.paperbag.holder.NewUserHolder;
import com.jcedar.paperbag.model.Category;
import com.jcedar.paperbag.model.Comment;
import com.jcedar.paperbag.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLUWAPHEMMY on 3/27/2017.
 */
public class AdminFragment extends Fragment implements View.OnClickListener, AddNewCategoryDialog.AddNewCategoryListener {

    private static final String TAG = "AdminFragment";
    View view;
    TextView welcome, userCount, storeCount, categCount, productCount, commentCount, errorMsg;
    String uuid, role, user;
    DatabaseReference mRef, mDb;
    List<String> users, stores, categoryList, foodItemList, commentList;
    LinearLayout addNewLayout, allFoodLayout, allCategoryLayout, allUserLayout, allStoreLayout, allCommentLayout;
    RelativeLayout adminNewCommentLayout, adminNewUserLayout;
    RecyclerView newUserRecycler, newCommentRecycler;
    private LinearLayoutManager linearLayoutManager;

    private FirebaseRecyclerAdapter<User, NewUserHolder> mAdapter;
    private FirebaseRecyclerAdapter<Comment, NewCommentHolder> commentAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        uuid = mBundle.getString("uuid");
        user = mBundle.getString("user");
//        role = mBundle.getString("role");

        Log.e(TAG, "onCreate: bundle user = " + user + " uuid = " + uuid);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_admin, container, false);

        users = new ArrayList<>();
        stores = new ArrayList<>();
        categoryList = new ArrayList<>();
        foodItemList = new ArrayList<>();
        commentList = new ArrayList<>();

        mRef = FirebaseDatabase.getInstance().getReference();

        welcome = (TextView) view.findViewById(R.id.tvWelcomeAdmin);
        errorMsg = (TextView) view.findViewById(R.id.tvNewUserErrorMsg);
        userCount = (TextView) view.findViewById(R.id.tvAdminUserCount);
        storeCount = (TextView) view.findViewById(R.id.tvAdminStoreCount);
        categCount = (TextView) view.findViewById(R.id.tvAdminCategoryCount);
        productCount = (TextView) view.findViewById(R.id.tvAdminProductCount);
        commentCount = (TextView) view.findViewById(R.id.tvAdminCommentCount);
        addNewLayout = (LinearLayout) view.findViewById(R.id.tvAdminAddCategory);
        allCategoryLayout = (LinearLayout) view.findViewById(R.id.allCategoryLayout);
        allFoodLayout = (LinearLayout) view.findViewById(R.id.allFoodLayout);
        allUserLayout = (LinearLayout) view.findViewById(R.id.allUserLayout);
        allStoreLayout = (LinearLayout) view.findViewById(R.id.allStoresLayout);
        allCommentLayout = (LinearLayout) view.findViewById(R.id.allCommentLayout);
        adminNewCommentLayout = (RelativeLayout) view.findViewById(R.id.adminNewCommentLayout);
        adminNewUserLayout = (RelativeLayout) view.findViewById(R.id.adminNewUserLayout);
        newUserRecycler = (RecyclerView) view.findViewById(R.id.recyclerAdminNewUser);
//        newCommentRecycler = (RecyclerView) view.findViewById(R.id.recyclerAdminNewComments);
        welcome.setText("Welcome " + user);

        mRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String campTitle = dataSnapshot1.child("name").getValue(String.class);
                    String key = dataSnapshot1.getKey();

                    if (!users.contains(campTitle)) {
                        users.add(campTitle);
                    }

                }
                userCount.setText("" + users.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("Stores").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String campTitle = dataSnapshot1.child("storeName").getValue(String.class);
                    String key = dataSnapshot1.getKey();
                    Log.e(TAG, "campaign name " + campTitle + " key " + key);

                    if (!stores.contains(campTitle)) {
                        stores.add(campTitle);
                    }

                }
                storeCount.setText("" + stores.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRef.child("Category").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String categTitle = dataSnapshot1.child("categoryId").getValue(String.class);
                    String key = dataSnapshot1.getKey();
                    Log.e(TAG, "campaign name " + categTitle + " key " + key);

                    if (!categoryList.contains(categTitle)) {
                        categoryList.add(categTitle);
                    }

                }
                categCount.setText("" + categoryList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("Product").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String categTitle = dataSnapshot1.child("productName").getValue(String.class);
                    String key = dataSnapshot1.getKey();

                    if (!foodItemList.contains(categTitle)) {
                        foodItemList.add(categTitle);
                    }

                }
                productCount.setText("" + foodItemList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mRef.child("Comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    String categTitle = dataSnapshot1.child("commentID").getValue(String.class);
                    String key = dataSnapshot1.getKey();

                    if (!commentList.contains(categTitle)) {
                        commentList.add(categTitle);
                    }

                }
                commentCount.setText("" + commentList.size());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addNewLayout.setOnClickListener(this);
        allCategoryLayout.setOnClickListener(this);
        allFoodLayout.setOnClickListener(this);
        allStoreLayout.setOnClickListener(this);
        allUserLayout.setOnClickListener(this);
        allCommentLayout.setOnClickListener(this);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        newUserRecycler.setHasFixedSize(true);

        Query query = mRef.child("User").orderByChild("activationId").equalTo("0");

        mAdapter = new FirebaseRecyclerAdapter<User, NewUserHolder>(User.class,
                R.layout.row_admin_new_user, NewUserHolder.class, query
        ) {
            @Override
            protected void populateViewHolder(NewUserHolder viewHolder, final User model, final int position) {

                viewHolder.userName.setText(model.getName());
                viewHolder.userEmail.setText(model.getEmail());
                viewHolder.userRole.setText(getUserRole(model.getRole()));
                viewHolder.imgApprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        approveUser(position, model);
                    }
                });

                viewHolder.imgDisapprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "User " + model.getName() + " disapproved", Toast.LENGTH_SHORT).show();
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
                    newUserRecycler.scrollToPosition(positionStart);
                }
            }
        });

        adminNewCommentLayout.setOnClickListener(this);
        adminNewUserLayout.setOnClickListener(this);

        newUserRecycler.setLayoutManager(linearLayoutManager);
        newUserRecycler.setAdapter(mAdapter);

        Log.e(TAG, "onCreateView: admin adapetr children = " + mAdapter.getItemCount());
/*
        if (mAdapter.getItemCount() == 0) {
            errorMsg.setVisibility(View.VISIBLE);
        } else {
            errorMsg.setVisibility(View.GONE);
        }*/
        return view;
    }

    @Override
    public void onClick(View view) {
        Intent adminIntent = new Intent(getActivity(), AdminViewActivity.class);
        switch (view.getId()) {
            case R.id.tvAdminAddCategory:
                if (MyUtils.checkNetworkAvailability(getActivity())) {
                    showAdminAddCategDialog();
                } else {
                    MyUtils.networkDialog(getActivity());
                }
                break;
            case R.id.adminNewCommentLayout:
                displayCommentRecycler();
                break;
            case R.id.adminNewUserLayout:
                errorMsg.setVisibility(View.GONE);
                newUserRecycler.setAdapter(mAdapter);
                break;
            case R.id.allCategoryLayout:
                adminIntent.putExtra("viewType", "CATEGORY");
                startActivity(adminIntent);

                break;
            case R.id.allFoodLayout:
                adminIntent.putExtra("viewType", "PRODUCT");
                startActivity(adminIntent);
                break;
            case R.id.allUserLayout:
                adminIntent.putExtra("viewType", "USER");
                startActivity(adminIntent);

                break;
            case R.id.allStoresLayout:
                adminIntent.putExtra("viewType", "STORE");
                startActivity(adminIntent);

                break;
            case R.id.allCommentLayout:
                adminIntent.putExtra("viewType", "COMMENT");
                startActivity(adminIntent);

                break;
        }
    }

    private void displayCommentRecycler() {


        Query query = mRef.child("Comment").orderByChild("approvalCode").equalTo("0");

        commentAdapter = new FirebaseRecyclerAdapter<Comment, NewCommentHolder>(Comment.class,
                R.layout.row_admin_new_comment, NewCommentHolder.class, query
        ) {
            @Override
            protected void populateViewHolder(NewCommentHolder viewHolder, final Comment model, final int position) {

                viewHolder.productName.setText(model.getProductName());
                viewHolder.productRating.setText(model.getRating());
                viewHolder.productComment.setText(model.getComment());
                viewHolder.commentor.setText(model.getCommentor());
                viewHolder.imgApprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        approveComment(position, model);
                    }
                });

                viewHolder.imgDisapprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getActivity(), "Comment "  + " disapproved", Toast.LENGTH_SHORT).show();
                    }
                });


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
                    newUserRecycler.scrollToPosition(positionStart);
                }
            }
        });

        if (commentAdapter.getItemCount() == 0) {
            errorMsg.setText("No New comment awaiting approval");
            errorMsg.setVisibility(View.GONE);
        } else {
            errorMsg.setVisibility(View.GONE);
        }

        newUserRecycler.setAdapter(commentAdapter);
    }

    private void approveComment(int position, Comment model) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("Comment");
        User czm = mAdapter.getItem(position);
        String key = mDb.child(czm.getActivationId()).getKey();
        final String mm = model.getCommentID();
        mDb.child(mm).child("approvalCode").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "Comment successfully approved", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
            }
        });
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

    private void showAdminAddCategDialog() {
        AddNewCategoryDialog dialog = new AddNewCategoryDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), "add_category");

    }

    private void approveUser(int position, final User model) {
        DatabaseReference mDb = FirebaseDatabase.getInstance().getReference("User");
        User czm = mAdapter.getItem(position);
        String key = mDb.child(czm.getActivationId()).getKey();
        final String mm = model.getUserID();
        mDb.child(mm).child("activationId").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "User " + model.getName() + " approved", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
            }
        });

        Log.e(TAG, "czm key in delete " + key + " mm del " + mm);
/*        String data = "TRUE";
        notifyActivity(data, getActivity());*/
/*        if (mAdapter != null) {
            mAdapter.notifyItemRemoved(position);
        }*/
//        Toast.makeText(getActivity(), "Product successfully deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddCategoryButtonClick(final String categoryTitle, String categoryImage, String categoryDateAdded) {
        final ProgressDialog mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Adding New Category...");
        mDialog.setCancelable(false);
        mDialog.show();

        DatabaseReference productRef = mRef.child("Category");
        String categoryId = productRef.push().getKey();
        Category category = new Category(categoryId, categoryTitle, categoryImage, categoryDateAdded);
        productRef.child(categoryId).setValue(category).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getActivity(), "New Category " + categoryTitle + " successfully added", Toast.LENGTH_SHORT).show();
                mDialog.hide();
                mDialog.dismiss();
            }
        });
    }
}
