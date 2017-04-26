package com.jcedar.paperbag.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;


/**
 * Created by OLUWAPHEMMY on 10/30/2016.
 */
public class PaperBagContract {

    //authority of data provider
    public static final String CONTENT_AUTHORITY = "com.jcedar.paperbag.provider";

    //authority of base content uri
    public static final Uri BASE_CONTENT_URI =  Uri.parse("content://" + CONTENT_AUTHORITY);

    //path or table names
    public static final String PATH_CART = "my_product";
    public static final String PATH_FAV= "my_fav";
    private static final String CALLER_IS_SYNCADAPTER = "caller_is_sync_adapter";
//    public static final String PATH_SEARCH = "search";


    public static class MyProduct implements MyProductColumns, BaseColumns {
        /** Content URI for  students table */
        public static final Uri CONTENT_URI  =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CART).build();

        /** The mime type of a single item */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd.com.jcedar.paperbag.provider.my_product";

        /** The mime type of a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd.com.jcedar.paperbag.provider.my_product";

        public static Uri buildCurrencyUri(long myProductId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(myProductId)).build();
        }


        /** A projection of all tables in students table */
        public static final String[] PROJECTION_ALL = {
                _ID,  PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESC, PRODUCT_QTY, PRODUCT_PRICE, PRODUCT_SELLER_ID,
                PRODUCT_IS_FAV, PRODUCT_IS_ORDERED, DELIVERY_STATUS
                //, UPDATED

        };

        /** The default sort order for queries containing students */
        public static final String SORT_ORDER_DEFAULT = PRODUCT_NAME +" DESC";

    }

public static class MyFavorite implements MyProductColumns, BaseColumns {
        /** Content URI for  students table */
        public static final Uri CONTENT_URI  =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV).build();

        /** The mime type of a single item */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd.com.jcedar.paperbag.provider.my_fav";

        /** The mime type of a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd.com.jcedar.paperbag.provider.my_fav";

        public static Uri buildCurrencyUri(long myProductId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(myProductId)).build();
        }


        /** A projection of all tables in students table */
        public static final String[] PROJECTION_ALL = {
                _ID,  PRODUCT_NAME, PRODUCT_ID, PRODUCT_DESC, PRODUCT_PRICE, PRODUCT_SELLER_ID, PRODUCT_QTY,
                 PRODUCT_IS_ORDERED, DELIVERY_STATUS
                //, UPDATED

        };

        /** The default sort order for queries containing students */
        public static final String SORT_ORDER_DEFAULT = PRODUCT_NAME +" DESC";

    }


    public interface SyncColumns{
        String UPDATED = "updated";
    }

    interface MyProductColumns{
        String PRODUCT_NAME= "product_name";
        String PRODUCT_ID = "product_id";
        String PRODUCT_DESC = "product_desc";
        String PRODUCT_QTY = "product_qty";
        String PRODUCT_PRICE = "product_price";
        String PRODUCT_SELLER_ID = "seller_id";
        String PRODUCT_IS_FAV = "favorite_status";
        String PRODUCT_IS_ORDERED= "order_status";
        String DELIVERY_STATUS= "delivery_status";
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                PaperBagContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(PaperBagContract.CALLER_IS_SYNCADAPTER));
    }

}
