package com.jcedar.chartbills.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcedar.chartbills.R;
import com.jcedar.chartbills.helper.FormatUtils;
import com.jcedar.chartbills.provider.ChartBillContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.support.v7.widget.RecyclerView.ViewHolder;
import static com.jcedar.chartbills.R.id.tvPaidCount;
import static com.jcedar.chartbills.helper.FormatUtils.getReadableDate;

/**
 * Created by OLUWAPHEMMY on 11/18/2016.
 */
//public class BillRecyclerAdapter extends RecyclerViewCursorAdapter<BillRecyclerAdapter.ChartBillsViewHolder>  {
public class BillRecyclerAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {

    private static final String TAG = BillRecyclerAdapter.class.getSimpleName();
    LayoutInflater inflater;
    private ClickListener clicklistener;
    private RedButtonClickListener rClicklistener;
    private GreenButtonClickListener greenClicklistener;
    private GreyButtonClickListener greyClicklistener;
    Context mContext;
    String userId;
    SharedPreferences pref;

    private  boolean isFirstPass = false;

    private static final int STATUS_UPCOMING = 1;
    private static final int STATUS_PAID = 2;
    private static final int STATUS_OVERDUE = 3;

    List<String> mmList, overList, paidList;
    HashMap<String, Integer> paidMap, upcomingMap;

    public BillRecyclerAdapter(Context context) {

        inflater = LayoutInflater.from(context);
        this.mContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mmList = new ArrayList<>();
        overList = new ArrayList<>();
        paidList = new ArrayList<>();
        paidMap = new HashMap<>();
        upcomingMap = new HashMap<>();

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {
            case STATUS_UPCOMING:
                view = inflater.inflate(R.layout.list_item_bill_upcoming, parent, false);
                return new UpcomingViewHolder(view, mContext);

            case STATUS_OVERDUE:
                view = inflater.inflate(R.layout.list_item_bill_overdue, parent, false);
                return new OverDueViewHolder(view, mContext);

            case STATUS_PAID:
                view = inflater.inflate(R.layout.list_item_bill_paid, parent, false);
                return new PaidViewHolder(view, mContext);
            default:
                break;
        }
        return null;
    }

    public void setClicklistener (ClickListener clicklistener){
        this.clicklistener = clicklistener;
    }
    public void setRClicklistener (RedButtonClickListener rClicklistener){
        this.rClicklistener = rClicklistener;
    }
    public void setGreyButtonClicklistener (GreyButtonClickListener greyClicklistener){
        this.greyClicklistener = greyClicklistener;
    }
    public void setGreenClicklistener (GreenButtonClickListener greenClicklistener){
        this.greenClicklistener = greenClicklistener;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {

        userId = cursor.getString(
                cursor.getColumnIndex(ChartBillContract.Bills._ID));

        String desc = cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_DESCRIPTION));
        long dueDate = Long.parseLong(cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_DUE_DATE)));
        String repeatFreq = cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_REPEAT_FREQ));
        String billAmount = cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_AMOUNT));
        String billStatus = cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_STATUS));
        String memo = cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_MEMO));

        int yy = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_DUE_YEAR)));
        int mm = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_DUE_MONTH)));
        int ww = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_DUE_WEEK)));

        if ((!mmList.contains(userId)) && (billStatus.equalsIgnoreCase("upcoming"))) {
            if (!TextUtils.isEmpty(billAmount)) {
                upcomingMap.put(userId, Integer.valueOf(billAmount));
            }else {
                upcomingMap.put(userId, 0);
            }
            mmList.add(userId);
        }
        else if ((!overList.contains(userId)) && (billStatus.equalsIgnoreCase("overdue"))) {
            overList.add(userId);
        }
        else if ((!paidMap.containsKey(userId)) && (billStatus.equalsIgnoreCase("paid"))) {
            if (!TextUtils.isEmpty(billAmount)) {
                paidMap.put(userId, Integer.valueOf(billAmount));
            }else {
                paidMap.put(userId, 0);
            }
            paidList.add(userId);
        }

        String defCurrency = pref.getString(mContext.getResources().getString(R.string.pref_key_currency),
                mContext.getResources().getString(R.string.pref_currency_default));

        switch (getItemViewType(cursor.getPosition())) {
            case STATUS_PAID:

                PaidViewHolder paidHolder = (PaidViewHolder) holder;
                /*if (TextUtils.isEmpty(memo)) {
                    paidHolder.memoImage.setVisibility(View.GONE);
                }*/
                hideMemoIcon(memo, paidHolder.memoImage);
                hideRepeatIcon(repeatFreq, paidHolder.repeatImage);
                if (!TextUtils.isEmpty(billAmount)) {
                    paidHolder.amount.setText(defCurrency + billAmount);
                } else {
                    paidHolder.amount.setText("-");
                }
                paidHolder.description.setText(desc);
                if (userId.equalsIgnoreCase(paidList.get(0))) {
                    paidHolder.tvHeader.setVisibility(View.VISIBLE);
                } else {
                    paidHolder.tvHeader.setVisibility(View.GONE);
                }
                paidHolder.tvPaidCount.setText(paidMap.size()+"");
                int mp =0;
                for (int i = 0; i < paidMap.size(); i++) {
                    mp = + paidMap.get(userId);
                }
                paidHolder.tvpaidPrice.setText(defCurrency + mp);


                break;
            case STATUS_OVERDUE:
                OverDueViewHolder overDueHolder = (OverDueViewHolder) holder;
                /*if (TextUtils.isEmpty(memo)) {
                    overDueHolder.memoImage.setVisibility(View.GONE);
                }*/
                hideMemoIcon(memo, overDueHolder.memoImage);
                hideRepeatIcon(repeatFreq, overDueHolder.repeatImage);
                if (!TextUtils.isEmpty(billAmount)) {
                    overDueHolder.amount.setText(defCurrency + billAmount);
                } else {
                    overDueHolder.amount.setText("-");
                }
                overDueHolder.description.setText(desc);
                overDueHolder.dueDate.setText(getReadableDate(dueDate));
                if (userId.equalsIgnoreCase(overList.get(0))) {
                    overDueHolder.overDueSection.setVisibility(View.VISIBLE);
                } else {
                    overDueHolder.overDueSection.setVisibility(View.GONE);
                }

                isFirstPass = true;

                break;
            case STATUS_UPCOMING:
                UpcomingViewHolder upcomingHolder = (UpcomingViewHolder) holder;
                /*if (TextUtils.isEmpty(memo)) {
                    upcomingHolder.memoImage.setVisibility(View.GONE);
                }*/
                hideMemoIcon(memo, upcomingHolder.memoImage);
                hideRepeatIcon(repeatFreq, upcomingHolder.repeatImage);
                if (!TextUtils.isEmpty(billAmount)) {
                    upcomingHolder.amount.setText(defCurrency + billAmount);
                } else {
                    upcomingHolder.amount.setText("-");
                }
                if (userId.equalsIgnoreCase(mmList.get(0))) {
                upcomingHolder.tvHeader.setVisibility(View.VISIBLE);
            } else {
                upcomingHolder.tvHeader.setVisibility(View.GONE);
            }
                upcomingHolder.description.setText(desc);
                upcomingHolder.dueDate.setText(FormatUtils.getUpcomingReadableDate(dueDate, ww, mm, yy));

                upcomingHolder.mtvPaidCount.setText(paidMap.size()+"");
                int mU =0;
                for (int i = 0; i < upcomingMap.size(); i++) {
                    mp = + upcomingMap.get(userId);
                }
                upcomingHolder.tvpaidPrice.setText(defCurrency + mU);

                break;
        }
        Log.e(TAG, "onBindViewHolder: mmList = "  + mmList.size() + " overdue list = " + overList.size());

    }

    @Override
    public int getItemViewType(int position) {

        int viewType = 0;
        Cursor cursor = this.getItem(position);
        String billStatus = cursor.getString(cursor.getColumnIndex(ChartBillContract.Bills.BILL_STATUS));

        if (billStatus.equalsIgnoreCase("upcoming")) {
            viewType = STATUS_UPCOMING;
        } else if (billStatus.equalsIgnoreCase("overdue")) {
            viewType = STATUS_OVERDUE;
        } else if (billStatus.equalsIgnoreCase("paid")) {
            viewType = STATUS_PAID;
        }
        return viewType;
    }


    private class PaidViewHolder extends ViewHolder implements View.OnClickListener{
        ImageView greenButtonImage, repeatImage, memoImage;
        TextView description, amount, tvpaidPrice, tvPaidCount;
        LinearLayout tvHeader;
        Context context;

        public PaidViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;

            greenButtonImage = (ImageView) itemView.findViewById(R.id.ivGreenButton);
            repeatImage = (ImageView) itemView.findViewById(R.id.ivRecyclerRepeat);
            memoImage = (ImageView) itemView.findViewById(R.id.ivRecyclerMemo);

            tvHeader = (LinearLayout) itemView.findViewById(R.id.tvPaidDescription);

            description = (TextView) itemView.findViewById(R.id.tvPaidDesc);
            amount = (TextView) itemView.findViewById(R.id.tvPaidAmount);
            tvPaidCount = (TextView) itemView.findViewById(R.id.tvPaidCount);
            tvpaidPrice = (TextView) itemView.findViewById(R.id.tvPaidTotalPrice);

            itemView.setOnClickListener(this);
            greenButtonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (greenClicklistener != null) {
                        greenClicklistener.GreenButtonItemClickListener(greenButtonImage, getAdapterPosition());
                    }
                }
            });

        }

        @Override
        public void onClick(View view) {
            if (clicklistener != null) {
                clicklistener.itemClickListener(itemView, getAdapterPosition());
            }
        }

    }

    private class UpcomingViewHolder extends ViewHolder implements View.OnClickListener{

        ImageView greyButtonImage, repeatImage, memoImage;
        TextView description, dueDate, amount, tvUpcomingSec, tvpaidPrice, mtvPaidCount;
        LinearLayout tvHeader;
        Context context;

        public UpcomingViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;

            greyButtonImage = (ImageView) itemView.findViewById(R.id.ivGreyButton);
            repeatImage = (ImageView) itemView.findViewById(R.id.ivRecyclerRepeat);
            memoImage = (ImageView) itemView.findViewById(R.id.ivRecyclerMemo);

            tvHeader = (LinearLayout) itemView.findViewById(R.id.tvPaidDescription);

            description = (TextView) itemView.findViewById(R.id.tvUpcomingDesc);
            dueDate = (TextView) itemView.findViewById(R.id.tvUpcomingDate);
            amount = (TextView) itemView.findViewById(R.id.tvPaidAmount);
//            amount = (TextView) itemView.findViewById(R.id.tvPaidAmount);
            mtvPaidCount = (TextView) itemView.findViewById(tvPaidCount);
            tvpaidPrice = (TextView) itemView.findViewById(R.id.tvPaidTotalPrice);
//            tvUpcomingSec = (TextView) itemView.findViewById(R.id.tvUpcomingSection);

            itemView.setOnClickListener(this);
            greyButtonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (greyClicklistener != null) {
                        greyClicklistener.GreyButtonItemClickListener(greyButtonImage, getAdapterPosition());
                    }
                }
            });

        }

        @Override
        public void onClick(View view) {
            if (clicklistener != null) {
                clicklistener.itemClickListener(itemView, getAdapterPosition());
            }
        }

    }

    private class OverDueViewHolder extends ViewHolder implements View.OnClickListener{

        ImageView redButtonImage, repeatImage, memoImage;
        TextView description, dueDate, amount, overDueSection;
        Context context;

        public OverDueViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;

            redButtonImage = (ImageView) itemView.findViewById(R.id.ivRedButton);
            repeatImage = (ImageView) itemView.findViewById(R.id.ivRecyclerRepeat);
            memoImage = (ImageView) itemView.findViewById(R.id.ivRecyclerMemo);

            description = (TextView) itemView.findViewById(R.id.tvOverdueDesc);
            dueDate = (TextView) itemView.findViewById(R.id.tvOverdueDate);
            amount = (TextView) itemView.findViewById(R.id.tvOverdueAmount);
            overDueSection = (TextView) itemView.findViewById(R.id.tvOverDueSection);


            itemView.setOnClickListener(this);
            redButtonImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (rClicklistener != null) {
                        rClicklistener.RedButtonItemClickListener(redButtonImage, getAdapterPosition());
                    }
                }
            });


        }

        @Override
        public void onClick(View view) {
            if (clicklistener != null) {
                clicklistener.itemClickListener(itemView, getAdapterPosition());
            }

        }

    }

    public interface ClickListener  {
        public void itemClickListener(View view, int position);
    }

    public interface RedButtonClickListener  {
        public void RedButtonItemClickListener(View view, int position);
    }

    public interface GreyButtonClickListener  {
        public void GreyButtonItemClickListener(View view, int position);
    }

    public interface GreenButtonClickListener  {
        public void GreenButtonItemClickListener(View view, int position);
    }

    private void hideRepeatIcon (String repeatFreq, ImageView holderImage) {
        if (repeatFreq.equalsIgnoreCase("does not repeat")) {
            holderImage.setVisibility(View.GONE);
        } else {
            holderImage.setVisibility(View.VISIBLE);
        }
    }

    private void hideMemoIcon (String memo, ImageView holderImage) {
        if (TextUtils.isEmpty(memo)) {
            holderImage.setVisibility(View.GONE);
        } else {
            holderImage.setVisibility(View.VISIBLE);
        }
    }

}
