/*
 *
 *  * ﻿Copyright 2017 Bait Inc
 *  * Licensed under the Apache License, Version 2.0 Jubilee 2017;
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package inc.bait.jubilee.model.helper.util;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class BitmapUtil {
    private static String TAG =
            LogUtil.makeLogTag(
                    BitmapUtil.class);

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {

        ByteArrayOutputStream stream =
                new ByteArrayOutputStream();
        bitmap.compress(
                Bitmap.CompressFormat.PNG,
                0,
                stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getBitmap(
            byte[] image) {

        return BitmapFactory.decodeByteArray(
                image,
                0,
                image.length);
    }
    public static Bitmap getBitmap(Context context,
                                   @RawRes int id,
                                   int a) {
        InputStream inputStream =
                context.getResources()
                        .openRawResource(
                                id);
        return BitmapFactory.decodeStream(inputStream);

    }
    public static void setBitmapFromAssets(Context context,
                                 String file,
                                 ImageView imageView) {
        ImageLoader imgLoader = ImageLoader.getInstance();
        imgLoader.init(ImageLoaderConfiguration.createDefault(context));
        file = "assets://".concat(file);
        imgLoader.displayImage(file, imageView,
                DisplayImageOptions.createSimple(),
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s,
                                                 View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s,
                                                View view,
                                                FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String s,
                                                  View view,
                                                  Bitmap bitmap) {

                    }

                    @Override
                    public void onLoadingCancelled(String s,
                                                   View view) {

                    }
                },
                new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String s,
                                                 View view,
                                                 int i,
                                                 int i1) {

                    }
                });

    }

    public static Bitmap getBitmap(
            String path) {

        try {
            return BitmapFactory.decodeFile(path);
        } catch (OutOfMemoryError |
                Exception e) {
            return null;
        }
    }

    public static Bitmap getBitmap(
            @NonNull Context context,
                                   @DrawableRes int id) {

        return BitmapFactory.decodeResource(
                context.getResources(),
                id);
    }

    public static Bitmap scaleBitmap(Bitmap src,
                                     int maxWidth,
                                     int maxHeight) {

        double scaleFactor = Math.min(
                ((double) maxWidth) /
                        src.getWidth(),
                ((double) maxHeight) /
                        src.getHeight());
        try {
            return Bitmap.createScaledBitmap(src,
                    (int) (src.getWidth() *
                            scaleFactor),
                    (int) (src.getHeight() *
                            scaleFactor),
                    false);
        } catch (Exception e) {
            return null;
        }
    }

    public static Bitmap scaleBitmap(int scaleFactor,
                                     InputStream is) {

        BitmapFactory.Options bmOptions =
                new BitmapFactory.Options();

        // Decode the image file into
        // a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        return BitmapFactory.decodeStream(is,
                null,
                bmOptions);
    }

    public static int findScaleFactor(int targetW,
                                      int targetH,
                                      InputStream is) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions =
                new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,
                null,
                bmOptions);
        int actualW = bmOptions.outWidth;
        int actualH = bmOptions.outHeight;

        // Determine how much to scale down the image
        return Math.min(actualW /
                targetW,
                actualH / targetH);
    }


    private static final int DEFAULT_JPEG_QUALITY =
            90;
    private static final int UNCONSTRAINED =
            -1;


    /*
     * Compute the sample size as a function of minSideLength
     * and maxNumOfPixels.
     * minSideLength is used to specify that minimal width or height of a
     * bitmap.
     * maxNumOfPixels is used to specify the maximal size in pixels that is
     * tolerable in terms of memory usage.
     *
     * The function returns a sample size based on the constraints.
     * Both size and minSideLength can be passed in as UNCONSTRAINED,
     * which indicates no care of the corresponding constraint.
     * The functions prefers returning a sample size that
     * generates a smaller bitmap, unless minSideLength = UNCONSTRAINED.
     *
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way.
     * For example, BitmapFactory downsamples an image by 2 even though the
     * request is 3. So we round up the sample size to avoid OOM.
    */

    private static int computeInitialSampleSize(int w,
                                                int h,
                                                int minSideLength,
                                                int maxNumOfPixels) {
        if (maxNumOfPixels == UNCONSTRAINED
                && minSideLength == UNCONSTRAINED) return 1;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ?
                1 :
                (int) Math.ceil(
                        Math.sqrt(
                                (float)
                                        (w *
                                                h) /
                                        maxNumOfPixels));

        if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            int sampleSize = Math.min(
                    w /
                            minSideLength,
                    h /
                            minSideLength);
            return Math.max(sampleSize,
                    lowerBound);
        }
    }



    public static Bitmap resizeBitmapByScale(Bitmap bitmap,
                                             float scale,
                                             boolean recycle) {
        int width = Math.round(
                bitmap.getWidth() *
                scale);
        int height = Math.round(
                bitmap.getHeight() *
                        scale);
        if (width == bitmap.getWidth()
                && height ==
                bitmap.getHeight()) return bitmap;
        Bitmap target = Bitmap.createBitmap(width,
                height,
                getConfig(bitmap));
        Canvas canvas =
                new Canvas(target);
        canvas.scale(scale,
                scale);
        Paint paint =
                new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap,
                0,
                0,
                paint);
        if (recycle) bitmap.recycle();
        return target;
    }

    private static Bitmap.Config getConfig(Bitmap bitmap) {
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        return config;
    }

    public static Bitmap resizeDownBySideLength(Bitmap bitmap,
                                                int maxLength,
                                                boolean recycle) {
        int srcWidth = bitmap.getWidth();
        int srcHeight = bitmap.getHeight();
        float scale = Math.min(
                (float) maxLength /
                        srcWidth,
                (float) maxLength /
                        srcHeight);
        if (scale >= 1.0f) return bitmap;
        return resizeBitmapByScale(bitmap,
                scale,
                recycle);
    }

    public static Bitmap resizeAndCropCenter(Bitmap bitmap,
                                             int size,
                                             boolean recycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        if (w == size &&
                h == size) return bitmap;

        // scale the image so that the shorter side equals to the target;
        // the longer side will be center-cropped.
        float scale = (float) size / Math.min(w,
                h);

        Bitmap target =
                Bitmap.createBitmap(size,
                size,
                getConfig(bitmap));
        int width = Math.round(scale *
                bitmap.getWidth());
        int height = Math.round(scale *
                bitmap.getHeight());
        Canvas canvas =
                new Canvas(target);
        canvas.translate((size -
                width) /
                2f,
                (size -
                        height) /
                        2f);
        canvas.scale(scale,
                scale);
        Paint paint =
                new Paint(Paint.FILTER_BITMAP_FLAG |
                Paint.DITHER_FLAG);
        canvas.drawBitmap(bitmap,
                0,
                0,
                paint);
        if (recycle) bitmap.recycle();
        return target;
    }

    public static void recycleSilently(Bitmap bitmap) {
        if (bitmap == null) return;
        try {
            bitmap.recycle();
        } catch (Throwable t) {
            LogUtil.w(TAG, "unable recycle bitmap", t);
        }
    }

    public static Bitmap rotateBitmap(Bitmap source,
                                      int rotation,
                                      boolean recycle) {
        if (rotation == 0) return source;
        int w = source.getWidth();
        int h = source.getHeight();
        Matrix m = new Matrix();
        m.postRotate(rotation);
        Bitmap bitmap =
                Bitmap.createBitmap(source,
                0,
                0,
                w,
                h,
                m,
                true);
        if (recycle) source.recycle();
        return bitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap createVideoThumbnail(String filePath) {
        // MediaMetadataRetriever is available on API Level 8
        // but is hidden until API Level 10
        Class<?> clazz = null;
        Object instance = null;
        try {
            clazz = Class.forName(
                    "android.media.MediaMetadataRetriever");
            instance = clazz.newInstance();

            Method method =
                    clazz.getMethod("setDataSource",
                    String.class);
            method.invoke(instance,
                    filePath);

            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= 9) {
                return (Bitmap) clazz.getMethod("captureFrame")
                        .invoke(instance);
            } else {
                byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture")
                        .invoke(instance);
                if (data != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data,
                            0,
                            data.length);
                    if (bitmap != null) return bitmap;
                }
                return (Bitmap) clazz.getMethod("getFrameAtTime")
                        .invoke(instance);
            }
        } catch (RuntimeException ex) {
            // Assume this is a corrupt video file.
        } catch (InstantiationException |
                InvocationTargetException |
                NoSuchMethodException |
                ClassNotFoundException |
                IllegalAccessException e) {
            LogUtil.e(TAG,
                    "createVideoThumbnail",
                    e);
        } finally {
            try {
                if (instance != null) {
                    clazz.getMethod("release")
                            .invoke(instance);
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public static byte[] compressToBytes(Bitmap bitmap) {
        return compressToBytes(bitmap,
                DEFAULT_JPEG_QUALITY);
    }

    public static byte[] compressToBytes(Bitmap bitmap,
                                         int quality) {
        ByteArrayOutputStream baos =
                new ByteArrayOutputStream(65536);
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                quality,
                baos);
        return baos.toByteArray();
    }

    public static boolean isSupportedByRegionDecoder(String mimeType) {
        if (mimeType == null) return false;
        mimeType = mimeType.toLowerCase();
        return mimeType.startsWith("image/") &&
                (!mimeType.equals("image/gif") &&
                        !mimeType.endsWith("bmp"));
    }

    public static boolean isRotationSupported(String mimeType) {
        if (mimeType == null) return false;
        mimeType = mimeType.toLowerCase();
        return mimeType.equals("image/jpeg");
    }

    public static Bitmap decodeSampledBitmapFromUri(String path,
                                                    int reqWidth,
                                                    int reqHeight) {

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options =
                new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,
                options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options,
                reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(path,
                options);

        return bm;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth,
                                            int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight ||
                width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }

        return inSampleSize;
    }

    public static void addImageToGallery(final String filePath,
                                         final Context context) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN,
                System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE,
                "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA,
                filePath);
        context.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values);
    }

    public static void removeImageFromGallery(String filePath,
                                              Context context) {
        context.getContentResolver()
                .delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.Media.DATA
                                + "='"
                                + filePath
                                + "'", null);
    }

}

