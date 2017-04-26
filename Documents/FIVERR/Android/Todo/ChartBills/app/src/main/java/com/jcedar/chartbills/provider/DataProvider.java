package com.jcedar.chartbills.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jcedar.chartbills.helper.SelectionBuilder;

/**
 * Created by OLUWAPHEMMY on 10/30/2016.
 */
public class DataProvider extends ContentProvider {

    private static final String TAG = DataProvider.class.getName();
    private UriMatcher sUriMatcher = buildUriMatcher();
    private  DatabaseHelper mHelper;
    private SQLiteDatabase mdb;

    private static final int BILL_ID = 101;
    private static final int BILL_LIST = 102;


    @Override
    public boolean onCreate() {

        mHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        mdb = mHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        final SelectionBuilder builder = buildSelection(uri, match);
        switch (match) {
            case BILL_LIST:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = ChartBillContract.Bills.SORT_ORDER_DEFAULT;
                }
                break;
            default:
                break;
        }
        return builder.where(selection, selectionArgs).query(mdb, projection, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (sUriMatcher.match(uri)){
            case BILL_ID:
                return ChartBillContract.Bills.CONTENT_ITEM_TYPE;
            case BILL_LIST:
                return ChartBillContract.Bills.CONTENT_TYPE;


            default:
                throw new IllegalArgumentException ("Unsupported Uri: " +uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        mdb = mHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        final SelectionBuilder builder = buildSelection(uri, match);
        long id;

        switch (match){
            case BILL_LIST: {
                id = mdb.insertOrThrow(DatabaseHelper.Tables.BILLS, null, contentValues);
                notifyChange(uri);
                return getUriForId(id, uri);
            }

            default:
                throw new UnsupportedOperationException ("Unsupported insert Uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        mdb = mHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int numRowsInsert = 0;
        switch (match) {
            case BILL_LIST:
                mdb.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = mdb.insert(DatabaseHelper.Tables.BILLS, null, value);
                        if (_id != -1) {
                            numRowsInsert++;
                        }
                    }
                    // To commit the transaction
                    mdb.setTransactionSuccessful();
                } finally {
                    mdb.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }

        if (numRowsInsert > 0) {
            notifyChange(uri);
        }
        return numRowsInsert;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        if (uri == ChartBillContract.BASE_CONTENT_URI){
            deleteDatabase();
            notifyChange(uri);
            return 1;
        }
        mdb = mHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        final SelectionBuilder builder = buildSelection(uri, match);
        int retValue = builder.where(selection, selectionArgs).delete(mdb);
        notifyChange(uri);
        return retValue ;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        mdb = mHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        final SelectionBuilder builder = buildSelection(uri, match);

        int retVal = builder.where(selection, selectionArgs).update(mdb, contentValues);
        notifyChange(uri);
        return retVal;
    }

    private static Uri getUriForId(long id, Uri uri) {
        if (id > 0) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            return itemUri;
        }
        //something went wrong
        throw new SQLException("Problem inserting into uri: " + uri);
    }

    private void notifyChange(Uri uri) {
        Context context = getContext();
        if( context != null) {
            ContentResolver resolver = context.getContentResolver();
            resolver.notifyChange(uri, null);
        }

        // Widgets can't register content observers so we refresh widgets separately.
        // context.sendBroadcast(ScheduleWidgetProvider.getRefreshBroadcastIntent(context, false));
    }

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ChartBillContract.CONTENT_AUTHORITY;


        matcher.addURI(authority, ChartBillContract.PATH_BILLS, BILL_LIST);
        matcher.addURI(authority, ChartBillContract.PATH_BILLS + "/#", BILL_ID);

        return matcher;
    }

    private SelectionBuilder buildSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case BILL_LIST: {
                return builder.table(DatabaseHelper.Tables.BILLS);
            }
            case BILL_ID: {
                final String id = uri.getLastPathSegment();
                return builder.table(DatabaseHelper.Tables.BILLS)
                        .where(ChartBillContract.Bills._ID + "=?", id);
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    private void deleteDatabase() {
        // TODO: wait for content provider operations to finish, then tear down
        mHelper.close();
        Context context = getContext();
        DatabaseHelper.deleteDatabase(context);
        mHelper = new DatabaseHelper(getContext());
    }
}
