package com.jcedar.paperbag.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.jcedar.paperbag.holder.NewCommentHolder;
import com.jcedar.paperbag.model.Comment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLUWAPHEMMY on 4/4/2017.
 */

public class SellerDetailActivity extends AppCompatActivity {

    private static final String TAG = "SellerDetailActivity";
    String prodId, prodDate, prodQtyOrdered, prodPhoto, prodName;
    TextView tvProdName, tvProdDate, tvProdQty, tvCommentrating;
    ImageView imCover;
    DatabaseReference mref;
    List<String> mList;
    RecyclerView recyclerView;
    private FirebaseRecyclerAdapter<Comment, NewCommentHolder> commentAdapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar!=null) {
            toolbar.setTitle("Food Details");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

        Bundle args = getIntent().getExtras();
        prodName = args.getString("productName");
        prodId = args.getString("productId");
        prodDate = args.getString("productDate");
        prodQtyOrdered = args.getString("productQtyOrdered");
//        prodPhoto = args.getString("productPhoto");

        tvProdName = (TextView) findViewById(R.id.sellerDetailName);
        tvProdDate = (TextView) findViewById(R.id.sellerDetailDate);
        tvProdQty = (TextView) findViewById(R.id.sellerDetailQtyOrdered);
        tvCommentrating = (TextView) findViewById(R.id.sellerDetailCommentRating);
        imCover = (ImageView) findViewById(R.id.sellerDetailPhoto);
        recyclerView = (RecyclerView) findViewById(R.id.sellerDetailsRecycler);

        linearLayoutManager = new LinearLayoutManager(this);

        tvProdName.setText("Name: \t" + prodName);
        tvProdDate.setText("Date Added: \t" + FormatUtils.getRecyclerReadableDate(Long.parseLong(prodDate)));
        tvProdQty.setText("Qty Ordered: \t" + prodQtyOrdered);
//        imCover.setImageBitmap(MyUtils.decodeBase64(prodPhoto));

        mref = FirebaseDatabase.getInstance().getReference();
        mList = new ArrayList<>();

        final Query imgQuery = mref.child("Product").orderByChild("productID").equalTo(prodId);
        imgQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                    String prodPhoto = datasnap.child("productPhoto").getValue(String.class);
        imCover.setImageBitmap(MyUtils.decodeBase64(prodPhoto));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query query = mref.child("Comment").orderByChild("productId").equalTo(prodId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot datasnap : dataSnapshot.getChildren()) {
                    String commentId = datasnap.child("commentID").getValue(String.class);
                    if (!mList.contains(commentId)) {
                        mList.add(commentId);
                    }
                }

                tvCommentrating.setText("Comments & Ratings ( " + mList.size() + " )");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query comQuery = mref.child("Comment").orderByChild("productId").equalTo(prodId);

        commentAdapter = new FirebaseRecyclerAdapter<Comment, NewCommentHolder>(Comment.class,
                R.layout.row_admin_new_comment, NewCommentHolder.class, comQuery
        ) {
            @Override
            protected void populateViewHolder(NewCommentHolder viewHolder, final Comment model, final int position) {
                viewHolder.productName.setText(model.getProductName());
                viewHolder.productRating.setText(model.getRating() + "/5.0");
                viewHolder.productComment.setText(model.getComment());
                viewHolder.commentor.setText("by " + model.getCommentor());
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
}
