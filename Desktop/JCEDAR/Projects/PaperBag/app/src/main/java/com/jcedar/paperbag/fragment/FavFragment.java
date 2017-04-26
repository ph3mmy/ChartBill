package com.jcedar.paperbag.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcedar.paperbag.R;

/**
 * Created by OLUWAPHEMMY on 4/7/2017.
 */
public class FavFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FavFragment";
    View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_fav, container, false);
        return view;
    }

    @Override
    public void onClick(View view) {

    }
}
