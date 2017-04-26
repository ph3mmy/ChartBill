package com.jcedar.paperbag.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.jcedar.paperbag.R;
import com.jcedar.paperbag.helper.MyUtils;

/**
 * Created by OLUWAPHEMMY on 4/16/2017.
 */
public class CommentDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "CommentDialog";
    String foodId, foodName, commentator, rating;
    ImageView closeDialog;
    EditText etComment;
    RatingBar ratingBar;
    Button btSubmitComment;
    private float mm = 0;


    public CommentDialog() {

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
        Bundle mm = getArguments();
        foodId = mm.getString("foodId");
        foodName = mm.getString("foodName");
        commentator = mm.getString("commentator");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.dialog_comment_rating, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        closeDialog = (ImageView) rootView.findViewById(R.id.ivCloseCommentDialog);
        etComment = (EditText) rootView.findViewById(R.id.etComment);
        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBarComment);
        btSubmitComment = (Button) rootView.findViewById(R.id.btCommentSubmit);

//        DrawableCompat.setTint(ratingBar.getProgressDrawable(), Color.YELLOW);
//        DrawableCompat.setTint(ratingBar.getProgressDrawable(), Color.YELLOW);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mm = ratingBar.getRating();
                rating = mm +"/5.0";
            }
        });

        closeDialog.setOnClickListener(this);
        btSubmitComment.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivCloseCommentDialog:
                dismiss();
                break;
            case R.id.btCommentSubmit:
                if (MyUtils.checkNetworkAvailability(getActivity())) {
                    String comment = etComment.getText().toString();
                    String ratee = String.valueOf(mm);
                    if (TextUtils.isEmpty(comment)) {
                        etComment.setError("Comment cannot be empty");
                        etComment.requestFocus();
                    } else if (mm == 0) {
                        Toast.makeText(getActivity(), "please give a rating first", Toast.LENGTH_SHORT).show();
                    } else {
                        CommentFragmentListener listener = (CommentFragmentListener) getTargetFragment();
                        listener.onCommentButtonClick(foodId, foodName, ratee, comment, commentator);
                        dismiss();
                    }
                }else {
                    MyUtils.networkDialog(getActivity()).show();
                }

                break;
            default:
                break;
        }
    }

    public interface CommentFragmentListener {
        public void onCommentButtonClick (String productId, String productName, String rating,
                                          String comment, String commentator);
    }
}
