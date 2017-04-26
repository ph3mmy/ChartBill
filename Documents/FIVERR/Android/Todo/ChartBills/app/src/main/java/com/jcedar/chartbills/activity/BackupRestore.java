package com.jcedar.chartbills.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.jcedar.chartbills.R;
import com.jcedar.chartbills.adapter.ResultsAdapter;
import com.jcedar.chartbills.provider.DatabaseHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by OLUWAPHEMMY on 3/19/2017.
 */

public class BackupRestore extends BaseDemoActivity implements View.OnClickListener {

    private GoogleApiClient api;
    private boolean mResolvingError = false;
    private DriveFile mfile;
    private static final int DIALOG_ERROR_CODE = 100;
    private static final String DATABASE_NAME = "demodb";
    //    private static final String GOOGLE_DRIVE_FILE_NAME = "sqlite_db_backup";
    private static final String META_MIME_TYPE = "application/x-sqlite3";

    private static final String TAG = BackupRestore.class.getSimpleName();
    LinearLayout backupLayout, restoreLayout, backupAndRestoreLayout, progressLayout;
    TextView progressText;
    ResultsAdapter mResultsAdapter;
    DriveFolder mfolder;
    DriveId mDId;
    ProgressDialog mProgress;
    File dbFile;
    String dbName;
    private View newProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_restore);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setActionBar(toolbar);
        toolbar.setTitle("Backup & Restore");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.my_gray));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        backupLayout = (LinearLayout) findViewById(R.id.backUpLayout);
        restoreLayout = (LinearLayout) findViewById(R.id.restoreLayout);
        newProgress = (ProgressBar)findViewById(R.id.qr_progress);
        backupAndRestoreLayout = (LinearLayout) findViewById(R.id.backupAndRestoreView);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        progressText = (TextView) findViewById(R.id.tvProgress);

        DatabaseHelper mDb = new DatabaseHelper(this);
        dbName = mDb.getDatabaseName();
        dbFile = this.getDatabasePath(dbName);

        Log.e(TAG, "onClick: name of db = " + dbName + " databse path = " + dbFile.toString());



        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Connecting to Drive...");
        mProgress.setTitle("Please Wait");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);

        backupLayout.setOnClickListener(this);
        restoreLayout.setOnClickListener(this);

        if (isConnectionAvailable()) {
            mProgress.show();
        }


    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        api = getGoogleApiClient();

        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }
        mfolder = Drive.DriveApi.getAppFolder(getGoogleApiClient());
        mDId = mfolder.getDriveId();


/*        MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("New folder").build();
        Drive.DriveApi.getRootFolder(getGoogleApiClient())
                .createFolder(getGoogleApiClient(), changeSet)
                .setResultCallback(folderCreatedCallback);*/
//        saveToDrive();
    }

/*    ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                showMessage("Error while trying to create the folder");
                return;
            }


            folderId = result.getDriveFolder().getDriveId();
            showMessage("Created a folder: " + result.getDriveFolder().getDriveId());
        }
    };*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backUpLayout:
                doDriveBackup();

                break;
            case R.id.restoreLayout:
//                restoreDB(this);
//                new RestoreAsync(this, dbName, api, dbFile, mfile).execute();
//                new RestoreAsync(this, dbName, ).execute();
                restoreDriveBackup();
                break;
            default:
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void backUpDB(Context context) {
        try {
            File chartBillPath = new File(Environment.getExternalStorageDirectory().getPath() + "/.chartbills/");

            if (!chartBillPath.exists()) {
                chartBillPath.mkdirs();
            }

            File data = Environment.getDataDirectory();
            DatabaseHelper mDb = new DatabaseHelper(this);

            Log.e(TAG, "backUpDB: database Name = " + mDb.getDatabaseName() + " data directory = ");
            String dbName = mDb.getDatabaseName();

            if (chartBillPath.canWrite()) {
                String backupDBPath = String.format("%s.bak", dbName);

                File currentDB = context.getDatabasePath(dbName);
                File backupDB = new File(chartBillPath, backupDBPath);
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "Backup Successful", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Oops, cannot create backup", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restoreDB(Context context) {

        try {
//            File sd = Environment.getExternalStorageDirectory();
            File sdm = Environment.getExternalStorageDirectory();
            String sdmStr = Environment.getExternalStorageDirectory().toString();
            Log.e(TAG, "restoreDB: external directory = " + sdm + " string of storage = " + sdmStr);
            String filePath = Environment.getExternalStorageDirectory().getPath() + "/.chartbills/";
            File sd = new File(filePath);
            File data = Environment.getDataDirectory();
            DatabaseHelper mDb = new DatabaseHelper(this);
            String dbName = mDb.getDatabaseName();

            if (sd.canWrite()) {
                File backedUpDb = context.getDatabasePath(dbName);
                String backedupDBPath = String.format("%s.bak", dbName);

                File currentDb = new File(sd, backedupDBPath);
                FileChannel src = new FileInputStream(currentDb).getChannel();
                FileChannel dst = new FileOutputStream(backedUpDb).getChannel();

                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(context, "Restore Successful", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private boolean isConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No Internet Connectivity detected! Check your Internet Connectivity settings")
                    .setCancelable(false)
                    .setPositiveButton("Check Settings", new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

            return false;

        } else {
            return true;
        }

    }

    //restore back to phone
    public void restoreDriveBackup() {

        backupAndRestoreLayout.setVisibility(View.GONE);
        progressLayout.setVisibility(View.VISIBLE);
        progressText.setText("Restoring Bill from Drive..... Please wait");
//        newProgress.setVisibility(View.VISIBLE);

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, dbName))
                .build();

        Drive.DriveApi.query(api, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {

                DriveId driveId = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                metadataBufferResult.getMetadataBuffer().release();

                mfile = Drive.DriveApi.getFile(api, driveId);
//                mfile = Drive.DriveApi.getAppFolder(api);

                mfile.open(api, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDown, long bytesExpected) {
                        //mToast("Downloading... (" + bytesDown + "/" + bytesExpected + ")");;
/*                        mProgress.setMessage("Restoring...");
                        mProgress.setCancelable(false);
                        mProgress.show();*/
                    }
                })
                        .setResultCallback(restoreContentsCallback);
            }
        });
    }

    final private ResultCallback<DriveApi.DriveContentsResult> restoreContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        backupAndRestoreLayout.setVisibility(View.VISIBLE);
                        progressLayout.setVisibility(View.GONE);
                        showMessage("Unable to open file, try again.");
                        return;
                    }
                 /*   mProgress.setMessage("Restoring...");
                    mProgress.show();*/
                    String path = dbFile.getPath();
                    Log.e(TAG, "onResult: path inside restore = " + path);

                    if (!dbFile.exists()) {
                        dbFile.delete();
                    }

                    dbFile = new File(path);

                    DriveContents contents = result.getDriveContents();
                    //debug("driveId:(2)" + contents.getDriveId());

                    try {
                        FileOutputStream fos = new FileOutputStream(dbFile);
                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                        BufferedInputStream in = new BufferedInputStream(contents.getInputStream());

                        byte[] buffer = new byte[1024];
                        int n, cnt = 0;


                        //debug("before read " + in.available());

                        while ((n = in.read(buffer)) > 0) {
                            bos.write(buffer, 0, n);
                            cnt += n;
                            bos.flush();
                        }

                        //debug(" read done: " + cnt);

                        bos.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

//                    mToast(act.getResources().getString(R.string.restoreComplete));
                    backupAndRestoreLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    showMessage("Bills Restore complete");
//                    mProgress.dismiss();
/*
                    utilsM.dbOpen();
                    mRecyclerView.invalidate();
                    mAdapter.notifyDataSetChanged();*/
                    contents.discard(api);
                    finish();

                }
            };



    public  void doDriveBackup () {
        Drive.DriveApi.newDriveContents(api).setResultCallback(backupContentsCallback);
    }
     final private ResultCallback<DriveApi.DriveContentsResult> backupContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            backupAndRestoreLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            progressText.setText("Backing Up.....");
            if (!result.getStatus().isSuccess()) {
                backupAndRestoreLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                showMessage("Error while trying to create new file contents");
                return;
            }

            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(dbName) // Google Drive File name
                    .setMimeType(META_MIME_TYPE)
                    .setStarred(true).build();
            // create a file on root folder
            Drive.DriveApi.getAppFolder(api)
                    .createFile(api, changeSet, result.getDriveContents())
                    .setResultCallback(backupFileCallback);
        }
    };
    final private ResultCallback<DriveFolder.DriveFileResult> backupFileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                backupAndRestoreLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                showMessage("Error while trying to create the file, try again.");

                return;
            }
            mfile = result.getDriveFile();
            mfile.open(api, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
                @Override
                public void onProgress(long bytesDownloaded, long bytesExpected) {
                    progressText.setText("Creating backup file... ("+bytesDownloaded+"/"+bytesExpected+")");
//                    mProgress.show();
                }
            }).setResultCallback(backupContentsOpenedCallback);
            Log.e(TAG, "onResult: file id of file in drive = " + result.getDriveFile().getDriveId());
        }
    };
    final private ResultCallback<DriveApi.DriveContentsResult> backupContentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                backupAndRestoreLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                showMessage("Error opening file, try again.");
                return;
            }
            backupAndRestoreLayout.setVisibility(View.GONE);
            progressLayout.setVisibility(View.VISIBLE);
            progressText.setText("finishing Backup creation.....");

            DriveContents contents = result.getDriveContents();
            BufferedOutputStream bos = new BufferedOutputStream(contents.getOutputStream());
            byte[] buffer = new byte[1024];
            int n;

            try {
                FileInputStream is = new FileInputStream(dbFile);
                BufferedInputStream bis = new BufferedInputStream(is);

                while ((n = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, n);
//                    mProgress.show();
                }
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            contents.commit(api, null).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    backupAndRestoreLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    showMessage("Back up completed");
                    finish();
//                    mToast(act.getResources().getString(R.string.backupComplete));
//                    mProgress.dismiss();
                }
            });
        }


        class RestoreAsync extends AsyncTask<Void, Void, Void> {

            private Context mContext;
            private ProgressDialog pd;
            private String dbName;
            private GoogleApiClient api;
            private File dbFile;
            private DriveFile mfile;

            public RestoreAsync(Context context, String dbName, GoogleApiClient api, File dbFile, DriveFile mFile) {
                this.mContext = context;
                this.dbName = dbName;
                this.api = api;
                this.dbFile = dbFile;
                this.mfile = mFile;
                pd = new ProgressDialog(mContext);
                pd.setTitle("Please Wait");
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd.setMessage("Restoring Bills....");
                pd.setCancelable(false);
                pd.show();

            }

            @Override
            protected Void doInBackground(Void... voids) {

                Query query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, dbName))
                        .build();

                Drive.DriveApi.query(api, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {

                        DriveId driveId = metadataBufferResult.getMetadataBuffer().get(0).getDriveId();
                        metadataBufferResult.getMetadataBuffer().release();

                        mfile = Drive.DriveApi.getFile(api, driveId);
//                mfile = Drive.DriveApi.getAppFolder(api);

                        mfile.open(api, DriveFile.MODE_READ_ONLY, new DriveFile.DownloadProgressListener() {
                            @Override
                            public void onProgress(long bytesDown, long bytesExpected) {
                                //mToast("Downloading... (" + bytesDown + "/" + bytesExpected + ")");;
                                pd.setMessage("Restoring.." + bytesDown + "/" + bytesExpected);
                            }
                        })
                                .setResultCallback(restoreContentsCallback);
                    }

                    final private ResultCallback<DriveApi.DriveContentsResult> restoreContentsCallback =
                            new ResultCallback<DriveApi.DriveContentsResult>() {
                                @Override
                                public void onResult(DriveApi.DriveContentsResult result) {
                                    if (!result.getStatus().isSuccess()) {
                                        return;
                                    }
                                    String path = dbFile.getPath();
                                    Log.e(TAG, "onResult: path inside restore = " + path);

                                    if (!dbFile.exists()) {
                                        dbFile.delete();
                                    }

                                    dbFile = new File(path);

                                    DriveContents contents = result.getDriveContents();
                                    //debug("driveId:(2)" + contents.getDriveId());

                                    try {
                                        FileOutputStream fos = new FileOutputStream(dbFile);
                                        BufferedOutputStream bos = new BufferedOutputStream(fos);
                                        BufferedInputStream in = new BufferedInputStream(contents.getInputStream());

                                        byte[] buffer = new byte[1024];
                                        int n, cnt = 0;


                                        //debug("before read " + in.available());

                                        while ((n = in.read(buffer)) > 0) {
                                            bos.write(buffer, 0, n);
                                            cnt += n;
                                            bos.flush();
                                        }

                                        //debug(" read done: " + cnt);

                                        bos.close();

                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

//                    mToast(act.getResources().getString(R.string.restoreComplete));
/*
                    utilsM.dbOpen();
                    mRecyclerView.invalidate();
                    mAdapter.notifyDataSetChanged();*/
                                    contents.discard(api);

                                }

                            };
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pd.setMessage("Restore Completed");
                pd.dismiss();
                showMessage("Restore Successful");
                mContext.startActivity(new Intent(mContext, SettingsActivity.class));
            }

            public void showMessage(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }


        }

        class BackupAsync extends AsyncTask<Void, Void, Void> {

            private Context mContext;
            private ProgressDialog pd;
            private String dbName;
            private GoogleApiClient api;
            private File dbFile;
            private DriveFile mfile;
            private static final String META_MIME_TYPE = "application/x-sqlite3";

            public BackupAsync(Context context, String dbName, GoogleApiClient api, File dbFile, DriveFile mfile) {
                this.mContext = context;
                this.dbName = dbName;
                this.api = api;
                this.dbFile = dbFile;
                this.mfile = mfile;
                pd = new ProgressDialog(mContext);
                pd.setTitle("Please Wait");
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                pd.setMessage("Backing up your Bills....");
                pd.setCancelable(false);
                pd.show();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                Drive.DriveApi.newDriveContents(api).setResultCallback(backupContentsCallback);
                return null;
            }

            final private ResultCallback<DriveApi.DriveContentsResult> backupContentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {

                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle(dbName) // Google Drive File name
                            .setMimeType(META_MIME_TYPE)
                            .setStarred(true).build();
                    // create a file on root folder
                    Drive.DriveApi.getAppFolder(api)
                            .createFile(api, changeSet, result.getDriveContents())
                            .setResultCallback(backupFileCallback);
                }
            };

            final private ResultCallback<DriveFolder.DriveFileResult> backupFileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create the file, try again.");
                        if (pd.isShowing()) {
                            pd.dismiss();
                        }
                        return;
                    }
                    mfile = result.getDriveFile();
                    mfile.open(api, DriveFile.MODE_WRITE_ONLY, new DriveFile.DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long bytesExpected) {
                            pd.setMessage("Creating backup file... (" + bytesDownloaded + "/" + bytesExpected + ")");
//                    mProgress.show();
                        }
                    }).setResultCallback(backupContentsOpenedCallback);
                    Log.e(TAG, "onResult: file id of file in drive = " + result.getDriveFile().getDriveId());
                }
            };
            final private ResultCallback<DriveApi.DriveContentsResult> backupContentsOpenedCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {

                        showMessage("Error opening file, try again.");
                        return;
                    }

//            mProgress.show();

                    DriveContents contents = result.getDriveContents();
                    BufferedOutputStream bos = new BufferedOutputStream(contents.getOutputStream());
                    byte[] buffer = new byte[1024];
                    int n;

                    try {
                        FileInputStream is = new FileInputStream(dbFile);
                        BufferedInputStream bis = new BufferedInputStream(is);

                        while ((n = bis.read(buffer)) > 0) {
                            bos.write(buffer, 0, n);
//                    mProgress.show();
                        }
                        bos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    contents.commit(api, null).setResultCallback(new ResultCallback<com.google.android.gms.common.api.Status>() {
                        @Override
                        public void onResult(com.google.android.gms.common.api.Status status) {
                            showMessage("Back up completed");
//                    mToast(act.getResources().getString(R.string.backupComplete));
                        }
                    });
                }
            };

            public void showMessage(String message) {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
        }
    };
}


