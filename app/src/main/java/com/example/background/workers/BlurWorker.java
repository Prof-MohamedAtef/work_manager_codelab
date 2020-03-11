package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.background.Constants;
import com.example.background.R;

import java.io.FileNotFoundException;

public class BlurWorker extends Worker {
    private static final String TAG = BlurWorker.class.getSimpleName();

    private Context mContext;

    public BlurWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        mContext=getApplicationContext();
        WorkerUtils.makeStatusNotification("Doing <WORK_NAME>", mContext);
        WorkerUtils.sleep();

        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);

        try {
//            REPLACE
//        Bitmap picture= BitmapFactory.decodeResource(
//           mContext.getResources(), R.drawable.test
//        );
//            WITH

            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }

            ContentResolver resolver = mContext.getContentResolver();
            // Create a bitmap
            Bitmap picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));

            Uri outputUri= WorkerUtils.writeBitmapToFile(mContext,WorkerUtils.blurBitmap(picture,mContext));
            WorkerUtils.makeStatusNotification("URI is: "+outputUri,mContext);
            Data outputData = new Data.Builder()
                    .putString(Constants.KEY_IMAGE_URI, outputUri.toString())
                    .build();
            return Result.success(outputData);
        } catch (Throwable throwable) {
            Log.e(TAG, "Error applying blur", throwable);
            return Result.failure();
        }
    }
}
