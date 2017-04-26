package com.jcedar.tixee.helper;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.jcedar.tixee.R;

/**
 * Created by OLUWAPHEMMY on 2/6/2017.
 */

public class EditTicketDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = EditTicketDialog.class.getSimpleName();
    private EditText etTicketId, etTicketType, etConName, etConNo;
    private Spinner statusSpinner;
    private Button btEditTicket, btDeleteTicket;
    private ImageView closeDialog;

    String[] status = {"Valid", "Invalid"};

    String editDialog, cName, cTicket, cKey, mm;

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

        editDialog = getArguments().getString("edit");
        Log.e(TAG, "ret Arg = "  + editDialog);
        cName = getArguments().getString("cName");
        cTicket = getArguments().getString("cTicketType");
        cKey = getArguments().getString("cKey");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_edit_ticket, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        etTicketId = (EditText) view.findViewById(R.id.etDialogTicketID);
        etTicketType = (EditText) view.findViewById(R.id.etDialogTicketType);
        etConName = (EditText) view.findViewById(R.id.etDialogTicketConName);
        etConNo = (EditText) view.findViewById(R.id.etDialogTicketConNumber);
        statusSpinner = (Spinner) view.findViewById(R.id.spinnerStatus);
        btEditTicket = (Button) view.findViewById(R.id.btEditTicket);
        closeDialog = (ImageView) view.findViewById(R.id.ivCloseDialog);

        etTicketType.setText(cTicket);
        etTicketId .setText(cName);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item, status
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        statusSpinner.setAdapter(spinnerAdapter);

        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mm = statusSpinner.getSelectedItem().toString();
                Log.e(TAG, "status spinner selected = " + mm);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btEditTicket.setOnClickListener(this);
        closeDialog.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btEditTicket:

                if (!MyUtils.checkNetworkAvailability(getActivity())) {
                    MyUtils.networkDialog(getActivity()).show();
                }

                String nTT = etTicketType.getText().toString();
                String conNM = etConName.getText().toString();
                String conNo = etConNo.getText().toString();

                if (TextUtils.isEmpty(nTT)) {
                    etTicketType.setError("You must Enter Ticket Type");
                } else {

                    AdminEditTicketFragmentInteractionListener listener = (AdminEditTicketFragmentInteractionListener) getTargetFragment();
                    listener.onAdminEditTicketButtonClick(cKey, mm, nTT, conNM, conNo);
                    dismiss();
                }
                break;
            case R.id.ivCloseDialog:
                dismiss();
        }
    }

    public interface AdminEditTicketFragmentInteractionListener {
        public void onAdminEditTicketButtonClick(String key, String status, String ticketType, String conName, String conPhone);
    }
}
