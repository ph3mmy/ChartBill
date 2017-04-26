package com.jcedar.paperbag.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcedar.paperbag.R;
import com.jcedar.paperbag.provider.PaperBagContract;


/**
 * Created by OLUWAPHEMMY on 11/18/2016.
 */
public class PaperBagRecyclerAdapter extends RecyclerViewCursorAdapter<PaperBagRecyclerAdapter.PaperBagViewHolder>  {

    private LayoutInflater inflater;
    private  ClickListener clicklistener;
    private Context mContext;
    private String fragType;

    public PaperBagRecyclerAdapter(Context context, String fragType) {

        inflater = LayoutInflater.from(context);
        this.mContext = context;
        this.fragType = fragType;


    }

    @Override
    public PaperBagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.cart_list_item, parent, false);
        return new PaperBagViewHolder(view, mContext, fragType);
    }

    public void setClicklistener (ClickListener clicklistener){
        this.clicklistener = clicklistener;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public void onBindViewHolder(PaperBagViewHolder holder, Cursor cursor) {

        String foodBaseId = cursor.getString(
                cursor.getColumnIndex(PaperBagContract.MyProduct._ID));
        String foodId = cursor.getString(cursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_ID));

        String name = cursor.getString(cursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_NAME));
        String desc = cursor.getString(cursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_DESC));
        String qty = cursor.getString(cursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_QTY));
        String price = cursor.getString(cursor.getColumnIndex(PaperBagContract.MyProduct.PRODUCT_PRICE));


            holder.tvCartName.setText(name);
            holder.tvCartDesc.setText(desc);
            holder.tvCartPrice.setText("$" + price);
        holder.tvCartQty.setText(qty);
//        }


    }

    class PaperBagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tvCartName, tvCartDesc, tvCartQty, tvCartPrice, tvCartOrder, tvCartRemove, tvCartMarkAsComplete;
        LinearLayout orderLayout, markAsCompleteLayout;
        Context context;
        String fragType;

        public PaperBagViewHolder(View itemView, Context context, String fragType) {
            super(itemView);
            this.context = context;
            this.fragType = fragType;
/*
            Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/proxima-nova-semibold.ttf");
            Typeface tfSym= Typeface.createFromAsset(context.getAssets(), "fonts/proxima-nova-cond-semibold.ttf");*/

            tvCartName = (TextView) itemView.findViewById(R.id.cartTvName);
//            tvSymbol.setTypeface(tfSym);
            tvCartDesc = (TextView) itemView.findViewById(R.id.cartTvDesc);
//            tvCurrencyName.setTypeface(tf);
            tvCartQty = (TextView) itemView.findViewById(R.id.cartTvQty);
            tvCartPrice = (TextView) itemView.findViewById(R.id.cartTvPrice);
            tvCartOrder = (TextView) itemView.findViewById(R.id.cartTvFavOrder);
            tvCartRemove = (TextView) itemView.findViewById(R.id.cartTvRemove);
            tvCartMarkAsComplete = (TextView) itemView.findViewById(R.id.cartTvOrderComplete);



            orderLayout = (LinearLayout) itemView.findViewById(R.id.cartOrderLay);
            markAsCompleteLayout = (LinearLayout) itemView.findViewById(R.id.cartDeliveryStatus);

            if (fragType.equalsIgnoreCase("FavFragment")) {
                markAsCompleteLayout.setVisibility(View.GONE);
                tvCartOrder.setVisibility(View.VISIBLE);
            }

            tvCartOrder.setOnClickListener(this);
            tvCartRemove.setOnClickListener(this);
            tvCartMarkAsComplete.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cartTvRemove:
                    view = tvCartRemove;
                    break;
                case R.id.cartTvFavOrder:
                    view = tvCartOrder;
                    break;
                case R.id.cartTvOrderComplete:
                    view = tvCartMarkAsComplete;
                    break;
                default:
                    break;
            }
            if (clicklistener != null) {
                clicklistener.itemClickListener(view, getAdapterPosition());
            }

        }
    }


    public interface ClickListener  {
        public void itemClickListener(View view, int position);
//        public void itemClickListener(Cursor cursor);
    }
    public interface OrderClickListener  {
        public void itemOrderClickListener(View view, int position);
//        public void itemClickListener(Cursor cursor);
    }
    public interface RemoveClickListener  {
        public void itemRemoveClickListener(View view, int position);
    }
    public interface MarkCompleteClickListener  {
        public void itemMarkCompleteClickListener(View view, int position);
    }


}
