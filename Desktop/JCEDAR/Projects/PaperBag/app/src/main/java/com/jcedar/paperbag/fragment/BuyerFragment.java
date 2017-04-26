package com.jcedar.paperbag.fragment;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.paperbag.R;
import com.jcedar.paperbag.activity.BuyerFoodActivity;
import com.jcedar.paperbag.activity.CartAndFavActivity;
import com.jcedar.paperbag.helper.MyUtils;
import com.jcedar.paperbag.holder.CategoryBuyerHolder;
import com.jcedar.paperbag.model.Category;

/**
 * Created by OLUWAPHEMMY on 3/27/2017.
 */
public class BuyerFragment extends Fragment {

    private static final String TAG = "BuyerFragment";
    View view;
    private TextView welcomeText;
    String uuid, role, user;
    TextView errorMsg;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    LinearLayout progLayout;
    String catId;
    int countBadge, favCount;

    private DatabaseReference mFirebaseRef, mDb;

    private FirebaseRecyclerAdapter<Category, CategoryBuyerHolder> mAdapter;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_buyer, menu);
        MenuItem cartItem = menu.findItem(R.id.action_user_cart);
        cartItem.setIcon(MyUtils.buildCounterDrawable(getActivity(), countBadge, R.drawable.cart));
        MenuItem favItem = menu.findItem(R.id.action_user_fav);
        favItem.setIcon(MyUtils.buildCounterDrawableFav(getActivity(), favCount, R.drawable.fav_white_block));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_user_cart) {
            Intent cartIntent = new Intent(getActivity(), CartAndFavActivity.class);
            cartIntent.putExtra("frag", "CartFragment");
            startActivity(cartIntent);

        } else if (item.getItemId() == R.id.action_user_fav) {
            Intent cartIntent = new Intent(getActivity(), CartAndFavActivity.class);
            cartIntent.putExtra("frag", "FavFragment");
            startActivity(cartIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        uuid = mBundle.getString("uuid");
        user = mBundle.getString("user");
        role = mBundle.getString("role");

        countBadge = MyUtils.getDbItemCount(getActivity());
        favCount = MyUtils.getDbFavCount(getActivity());

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_buyer, container, false);

        setHasOptionsMenu(true);

        welcomeText = (TextView) view.findViewById(R.id.tvWelcomebuyer);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerBuyerCategory);
        progLayout = (LinearLayout) view.findViewById(R.id.categoryProgressLayout);
//        errorMsg = (TextView) view.findViewById(errorMsg);

        mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        mDb = mFirebaseRef.child("Category");

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(true);

        welcomeText.setText("Welcome " + user);


        mAdapter = new FirebaseRecyclerAdapter<Category, CategoryBuyerHolder>(Category.class,
                R.layout.row_buyer_category, CategoryBuyerHolder.class, mDb
        ) {
            @Override
            protected void populateViewHolder(CategoryBuyerHolder viewHolder, final Category model, final int position) {

                viewHolder.categTitle.setText(model.getCategoryTitle());
                viewHolder.imageLayout.setBackground(new BitmapDrawable(getResources(), MyUtils.decodeBase64(model.getCategoryImage())));
                viewHolder.imageLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent foodIntent = new Intent(getActivity(), BuyerFoodActivity.class);
                        foodIntent.putExtra("catId", model.getCategoryId());
                        foodIntent.putExtra("catName", model.getCategoryTitle());
                        startActivity(foodIntent);
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
//                    errorMsg.setVisibility(View.VISIBLE);
                    progLayout.setVisibility(View.GONE);
                } else {
//                    errorMsg.setVisibility(View.GONE);
                    progLayout.setVisibility(View.GONE);
                }
                RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        super.onItemRangeInserted(positionStart, itemCount);
                        progLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onItemRangeRemoved(int positionStart, int itemCount) {
                        super.onItemRangeRemoved(positionStart, itemCount);
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().supportInvalidateOptionsMenu();
        getActivity().invalidateOptionsMenu();
    }
}
