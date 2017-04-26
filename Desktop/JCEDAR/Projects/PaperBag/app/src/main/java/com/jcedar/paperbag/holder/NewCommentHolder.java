package com.jcedar.paperbag.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcedar.paperbag.R;

import static com.jcedar.paperbag.R.id.ivDisapprove;


public class NewCommentHolder extends RecyclerView.ViewHolder {

    public TextView productName, productRating, productComment, commentor;
    public ImageView imgApprove, imgDisapprove;
    public LinearLayout mLayoutComm;

    public NewCommentHolder(View itemView) {
        super(itemView);

        productName = (TextView) itemView.findViewById(R.id.tvAdminRowCommentProduct);
        productRating = (TextView) itemView.findViewById(R.id.tvAdminRowCommentRating);
        productComment = (TextView) itemView.findViewById(R.id.tvAdminRowComment);
        commentor = (TextView) itemView.findViewById(R.id.tvAdminRowCommentor);
        imgApprove = (ImageView) itemView.findViewById(R.id.ivApprove);
        imgDisapprove = (ImageView) itemView.findViewById(ivDisapprove);
        mLayoutComm = (LinearLayout) itemView.findViewById(R.id.tvAdminRowCommentAction);

    }
}
