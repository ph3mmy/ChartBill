package com.jcedar.paperbag.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.jcedar.paperbag.R;
import com.jcedar.paperbag.fragment.CartFragment;

/**
 * Created by OLUWAPHEMMY on 4/7/2017.
 */

public class CartAndFavActivity extends AppCompatActivity {


    private static final String TAG = "CartAndFavActivity";
    private Toolbar toolbar;
    String frag;


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buyer, menu);
        MenuItem cartItem = (MenuItem) findViewById(R.id.action_user_cart);
        MenuItem favItem = (MenuItem) findViewById(R.id.action_user_fav);
        return true;
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_and_fav);

        toolbar = (Toolbar) findViewById(R.id.toolbar);


        Bundle mBundle = getIntent().getExtras();
        frag = mBundle.getString("frag");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (frag.equalsIgnoreCase("CartFragment")) {
            toolbar.setTitle("My Cart");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            CartFragment fragment = new CartFragment();
            Bundle bd = new Bundle();
            bd.putString("frag", frag);
            Log.e(TAG, "onCreate: type of fragment arg = " + frag );
            fragment.setArguments(bd);
            ft.replace(R.id.cartContainer, fragment);
            ft.commit();
        } else if (frag.equalsIgnoreCase("FavFragment")) {
            toolbar.setTitle("My Favorites");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            CartFragment fragment = new CartFragment();
            Bundle bd = new Bundle();
            Log.e(TAG, "onCreate: type of fragment arg = " + frag );
            bd.putString("frag", frag);
            fragment.setArguments(bd);
            ft.replace(R.id.cartContainer, fragment);
            ft.commit();

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }
}
