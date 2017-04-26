package com.jcedar.paperbag.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jcedar.paperbag.R;
import com.jcedar.paperbag.fragment.BuyerFoodFragment;
import com.jcedar.paperbag.fragment.OrderSummaryFragment;
import com.jcedar.paperbag.helper.MyUtils;

/**
 * Created by OLUWAPHEMMY on 4/5/2017.
 */

public class BuyerFoodActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "BuyerFoodActivity";
    private  int countBadge, favCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_food);

        Bundle arg = getIntent().getExtras();
        String categoryId = arg.getString("catId");
        String categoryName = arg.getString("catName");
        String fav = arg.getString("fav");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(categoryName + " Menu");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    finish();
                } else
                    getSupportFragmentManager().popBackStack();
            }
        });


        countBadge = MyUtils.getDbItemCount(this);
        favCount = MyUtils.getDbFavCount(this);


        BuyerFoodFragment fragment = new BuyerFoodFragment();
        Bundle args = new Bundle();
        args.putString("catId", categoryId);
        args.putString("catName", categoryName);
        fragment.setArguments(args);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.buyerContainer, fragment, "buyer_food_fragment");
        ft.commit();
        ft.addToBackStack("buyer_food_fragment");

        if (fav != null && fav.equalsIgnoreCase("FAV")) {
            String foodId = arg.getString("prodId");
            String foodName = arg.getString("prodName");
            String foodDesc = arg.getString("prodDesc");
            String ppu = arg.getString("ppu");
            String foodSellerId = arg.getString("prodSellerId");
            String sellerToken = arg.getString("prodSellerToken");
            String sellerEmail = arg.getString("prodSellerEmail");
            int totalP = arg.getInt("totalP");
            int qty = arg.getInt("prodQty");

                loadSummaryFragment(foodId, foodName, foodDesc, ppu, foodSellerId, qty, totalP, sellerEmail, sellerToken);

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buyer, menu);

        MenuItem cartItem = menu.findItem(R.id.action_user_cart);
        cartItem.setIcon(MyUtils.buildCounterDrawable(this, countBadge, R.drawable.cart));
        MenuItem favItem = menu.findItem(R.id.action_user_fav);
        favItem.setIcon(MyUtils.buildCounterDrawableFav(this, favCount, R.drawable.fav_white));

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
        countBadge = MyUtils.getDbItemCount(this);
        favCount = MyUtils.getDbFavCount(this);
        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSupportFragmentManager().popBackStack();
//                return true;
            break;
            case R.id.action_user_cart:
                Intent cartIntent = new Intent(this, CartAndFavActivity.class);
                cartIntent.putExtra("frag", "CartFragment");
                startActivity(cartIntent);
//                return true;
            break;
            case R.id.action_user_fav:
                Intent favIntent = new Intent(this, CartAndFavActivity.class);
                favIntent.putExtra("frag", "FavFragment");
                startActivity(favIntent);

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void hideUpButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onBackStackChanged() {

        supportInvalidateOptionsMenu();
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
           finish();
        } else
            getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        supportInvalidateOptionsMenu();
        FragmentManager fm = getSupportFragmentManager();
        if (((BuyerFoodFragment)getSupportFragmentManager().findFragmentByTag("buyer_food_fragment")) != null
                && ((BuyerFoodFragment)getSupportFragmentManager().findFragmentByTag("buyer_food_fragment")).isVisible()) {
            finish();
        } else
            super.onBackPressed();

    }
    private void loadSummaryFragment (String id, String Name, String desc, String ppu, String sellerId, int qtyLeft, int totalPri, String sellerEmail, String sellerToken) {
        FragmentManager manager = getSupportFragmentManager();
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
        args.putInt("totalP", totalPri);
        args.putInt("prodQty", qtyLeft);
        fragment.setArguments(args);
//        ft.addToBackStack("order_sum_fragment");
//        ft.hide(CartFragment.this);
//        ft.add(android.R.id.content, fragment);
        ft.replace(R.id.buyerContainer, fragment, "order_sum_fragment");
        ft.commit();
//        ft.addToBackStack("order_sum_fragment");
    }
}
