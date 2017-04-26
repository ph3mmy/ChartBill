package com.jcedar.paperbag.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.jcedar.paperbag.R;
import com.jcedar.paperbag.fragment.AllCategoryFragment;

/**
 * Created by OLUWAPHEMMY on 4/4/2017.
 */

public class AdminViewActivity extends AppCompatActivity {

    private static final String TAG = "AdminViewActivity";
    Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle mBundle = getIntent().getExtras();
        String viewType = mBundle.getString("viewType");
        Log.e(TAG, "onCreate: bundled extras = "  + viewType);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        if (viewType != null && viewType.equalsIgnoreCase("CATEGORY")) {
            toolbar.setTitle("All Categories");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Bundle arg = new Bundle();
            arg.putString("viewType", viewType);
            AllCategoryFragment fragment = new AllCategoryFragment();
            fragment.setArguments(arg);
            ft.replace(R.id.adminContainer, fragment, "all_category_fragment");
            ft.commit();
        }
        else if (viewType != null && viewType.equalsIgnoreCase("PRODUCT")) {
            toolbar.setTitle("All Food Items");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Bundle arg = new Bundle();
            arg.putString("viewType", viewType);
            AllCategoryFragment fragment = new AllCategoryFragment();
            fragment.setArguments(arg);
            ft.replace(R.id.adminContainer, fragment, "all_category_fragment");
            ft.commit();
        }
        else if (viewType != null && viewType.equalsIgnoreCase("USER")) {
            toolbar.setTitle("All Registered Users");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Bundle arg = new Bundle();
            arg.putString("viewType", viewType);
            AllCategoryFragment fragment = new AllCategoryFragment();
            fragment.setArguments(arg);
            ft.replace(R.id.adminContainer, fragment, "all_category_fragment");
            ft.commit();
        }
        else if (viewType != null && viewType.equalsIgnoreCase("STORE")) {
            toolbar.setTitle("All Registered Stores");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Bundle arg = new Bundle();
            arg.putString("viewType", viewType);
            AllCategoryFragment fragment = new AllCategoryFragment();
            fragment.setArguments(arg);
            ft.replace(R.id.adminContainer, fragment, "all_category_fragment");
            ft.commit();
        }
        else if (viewType != null && viewType.equalsIgnoreCase("COMMENT")) {
            toolbar.setTitle("All Comments");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Bundle arg = new Bundle();
            arg.putString("viewType", viewType);
            AllCategoryFragment fragment = new AllCategoryFragment();
            fragment.setArguments(arg);
            ft.replace(R.id.adminContainer, fragment, "all_category_fragment");
            ft.commit();
        }

    }
}
