package com.jcedar.chartbills.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Created by OLUWAPHEMMY on 10/30/2016.
 */
public class ChartBillContract {

    //authority of data provider
    public static final String CONTENT_AUTHORITY = "com.jcedar.chartbills.provider";

    //authority of base content uri
    public static final Uri BASE_CONTENT_URI =  Uri.parse("content://" + CONTENT_AUTHORITY);

    //path or table names
    public static final String PATH_BILLS = "bills";
    private static final String CALLER_IS_SYNCADAPTER = "caller_is_sync_adapter";
//    public static final String PATH_SEARCH = "search";


    public static class Bills implements BillsColumns, BaseColumns {
        /** Content URI for  students table */
        public static final Uri CONTENT_URI  =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_BILLS).build();

        /** The mime type of a single item */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/vnd.com.jcedar.chartbills.provider.bills";

        /** The mime type of a single item */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/vnd.com.jcedar.chartbills.provider.bills";

        public static Uri buildCurrencyUri(long currencyId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(currencyId)).build();
        }


        /** A projection of all tables in students table */
        public static final String[] PROJECTION_ALL = {
                _ID,  BILL_DESCRIPTION, BILL_DUE_DATE, BILL_REPEAT_FREQ, BILL_AMOUNT, BILL_MEMO, BILL_STATUS,
                BILL_DUE_DAY, BILL_DUE_WEEK, BILL_DUE_MONTH, BILL_DUE_YEAR, BILL_ALARM_ID
                //, UPDATED

        };

        /** The default sort order for queries containing students */
        public static final String SORT_ORDER_DEFAULT = BILL_DUE_DATE +" DESC";

    }


    public interface SyncColumns{
        String UPDATED = "updated";
    }

    interface BillsColumns{
        String BILL_DESCRIPTION = "description";
        String BILL_DUE_DATE = "due_date";
        String BILL_REPEAT_FREQ = "repeat_freq";
        String BILL_AMOUNT = "amount";
        String BILL_MEMO = "bill_memo";
        String BILL_STATUS = "status";
        String BILL_DUE_DAY = "due_day";
        String BILL_DUE_WEEK = "due_week";
        String BILL_DUE_MONTH = "due_month";
        String BILL_DUE_YEAR = "due_year";
        String BILL_ALARM_ID = "alarm_id";
    }

    public static Uri addCallerIsSyncAdapterParameter(Uri uri) {
        return uri.buildUpon().appendQueryParameter(
                ChartBillContract.CALLER_IS_SYNCADAPTER, "true").build();
    }

    public static boolean hasCallerIsSyncAdapterParameter(Uri uri) {
        return TextUtils.equals("true",
                uri.getQueryParameter(ChartBillContract.CALLER_IS_SYNCADAPTER));
    }

}
