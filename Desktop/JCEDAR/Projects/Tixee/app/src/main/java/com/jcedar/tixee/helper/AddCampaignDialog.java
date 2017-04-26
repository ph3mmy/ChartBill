package com.jcedar.tixee.helper;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jcedar.tixee.R;
import com.jcedar.tixee.model.Campaign;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

/**
 * Created by OLUWAPHEMMY on 1/22/2017.
 */

public class AddCampaignDialog extends DialogFragment {

    private static final String TAG = AddCampaignDialog.class.getSimpleName();
    private EditText etCampName, etCampTicketNo;
    private Button addCampaign, imageChooserButton;
    private ImageView closeDialog;
    private TextView dialogTitle, imageChooserText, ticketAmount;
    private LinearLayout dialogAv, imageChooserLayput;

    private static final int PICK_IMAGE = 180;

    int dialogType;
    Bitmap bitmap;
    String editDialog, cName, cTicket, cKey, cAv, imageString;

    public AddCampaignDialog() {

    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;

            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        dialogType = getArguments().getInt("position");
        editDialog = getArguments().getString("edit");
        Log.e(TAG, "ret Arg = "  + editDialog);
        cName = getArguments().getString("cName");
        cTicket = getArguments().getString("cTicket");
        cAv = getArguments().getString("cAv");
        cKey = getArguments().getString("cKey");


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.dialog_campaign, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        dialogTitle = (TextView) rootView.findViewById(R.id.tvCreateCamp);
        ticketAmount = (TextView) rootView.findViewById(R.id.tvTicketAmount);
        imageChooserText = (TextView) rootView.findViewById(R.id.tvCampaignPhoto);
        addCampaign = (Button) rootView.findViewById(R.id.btAddCampaign);
        imageChooserButton = (Button) rootView.findViewById(R.id.btCampaignPhoto);
        etCampName = (EditText) rootView.findViewById(R.id.etCampaignName);
        etCampTicketNo = (EditText) rootView.findViewById(R.id.etCampaignTicket);
        closeDialog = (ImageView) rootView.findViewById(R.id.ivCloseDialog);
        dialogAv = (LinearLayout) rootView.findViewById(R.id.dialogAvailable);
        imageChooserLayput = (LinearLayout) rootView.findViewById(R.id.uploadButtonLayout);
        TextView av = (TextView) rootView.findViewById(R.id.tvAv);


        if (editDialog != null) {
            if (editDialog.equalsIgnoreCase("EDIT_DIALOG")) {

                dialogTitle.setText("EDIT CAMPAIGN");
                etCampName.setText(cName);
                etCampName.setEnabled(false);
                etCampTicketNo.setText(cTicket);
                addCampaign.setText("Edit Campaign");
                ticketAmount.setVisibility(View.VISIBLE);
                imageChooserLayput.setVisibility(View.GONE);

            }else if (editDialog.equalsIgnoreCase("ALLOC_DIALOG")) {

                dialogTitle.setText("ALLOCATE SECONDARY TICKET");
                etCampName.setText(cName);
                dialogAv.setVisibility(View.VISIBLE);
                etCampName.setEnabled(false);
                av.setText(cAv);
                etCampTicketNo.setText(cTicket);
                addCampaign.setText("Allocate Ticket");
                imageChooserLayput.setVisibility(View.GONE);

            }
        }
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        imageChooserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });


        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        addCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!MyUtils.checkNetworkAvailability(getActivity())) {
                    MyUtils.networkDialog(getActivity()).show();
                }

//                if (editDialog != null) {
                    if (editDialog.equalsIgnoreCase("EDIT_DIALOG")) {
                        String editedTicket  = etCampTicketNo.getText().toString();

                        int mm = Integer.parseInt(editedTicket);
                        int maxTick = Integer.parseInt(cTicket);

                     /*   if (mm > maxTick) {
                            etCampTicketNo.setError("You cannot allocate more than the available ticket");
                        }*/

                            EditCampaignFragmentInteractionListener listener = (EditCampaignFragmentInteractionListener) getTargetFragment();
                            listener.onEditCampaignButtonClick(cKey, editedTicket);
                            dismiss();

                    }

                    else if (editDialog.equalsIgnoreCase("ALLOC_DIALOG")) {
                        if (!MyUtils.checkNetworkAvailability(getActivity())) {
                            MyUtils.networkDialog(getActivity()).show();
                        }
                        String allocSec  = etCampTicketNo.getText().toString();
                        int allocInt = Integer.parseInt(allocSec);
                        int avv = Integer.parseInt(cAv);
                        if (allocInt > avv) {
                            etCampTicketNo.setError("You cannot allocate more than you have");
                        }else {
                            AllocateCampaignFragmentInteractionListener listener = (AllocateCampaignFragmentInteractionListener) getTargetFragment();
                            listener.onAllocateCampaignButtonClick(cKey, allocSec);
                            dismiss();
                        }
                    }

                else {

                    View focusView = null;
                    boolean cancel = false;

                    String campName = etCampName.getText().toString();
                    String campNoTicket = etCampTicketNo.getText().toString();

                    if (TextUtils.isEmpty(campName)) {
                        etCampName.setError("Campaign Name cannot be empty");
                        focusView = etCampName;
                        cancel = true;
                    }

                    if (TextUtils.isEmpty(campNoTicket)) {
                        etCampTicketNo.setError("No of ticket cannot be empty");
                        focusView = etCampTicketNo;
                        cancel = true;
                    }
                    if (cancel) {
                        focusView.requestFocus();
                    } else if (bitmap == null) {
                        imageChooserButton.setError("Please choose image");
                    }

                    else {
                        imageChooserButton.setError(null);
                        imageString = MyUtils.encodeTobase64(bitmap);
                        addNewCampaign(campName, campNoTicket, imageString);
                        dismiss();
                    }
                }
            }
        });

    }


    private void addNewCampaign (String campaignName, String ticketNo, String imageUrl) {

        DatabaseReference aFirebase = FirebaseDatabase.getInstance().getReference("Campaign");
        int mmm = Integer.parseInt(ticketNo);
        String userId = aFirebase.push().getKey();
        Campaign campaign = new Campaign(userId, campaignName, mmm, mmm, 0, imageUrl);
        aFirebase.child(userId).setValue(campaign);

/*        aFirebase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Campaign c = dataSnapshot.getValue(Campaign.class);

*//*
                if (c == null ) {
                    Toast.makeText(getActivity(), "Newly added campaign is null ", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getActivity(), "Newly added campaign is  " + c.getName() + "\n with " +c.getNumberOfTickets() + "tickets", Toast.LENGTH_SHORT).show();
*//*

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.e(TAG, " Error on adding campaign " + databaseError.toException());

            }
        });*/
    }

    private void choosePhoto () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri  = data.getData();
            try {
                imageChooserButton.setError(null);
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface EditCampaignFragmentInteractionListener {
        public void onEditCampaignButtonClick(String key, String newTicketVal);
    }
    public interface AllocateCampaignFragmentInteractionListener {
        public void onAllocateCampaignButtonClick(String key, String newTicketVal);
    }
}
