package br.com.actia.gpsgm_168.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Is necessary to display image and the main thread isn't responsible for this.
 */
public class ImageWorker {
	Context contex;
	int width;
	int height;
	
	/**
	 * Load in a ImageView
	 * @param path image path
	 * @param imageView a view
	 * @param contex the context
	 * @param width image width
	 * @param height image height
	 */
	@SuppressLint("NewApi")
	public void loadBitmap(String path, ImageView imageView,Context contex,int width,int height) {
		this.contex=contex;
		this.width=width;
		this.height=height;
	    if (cancelPotentialWork(path, imageView)) {
	        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
	        final AsyncDrawable asyncDrawable =
	                new AsyncDrawable(contex.getResources(), BitmapFactory.decodeFile(path), task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,path);
	    }
	}
	
	/**
	 * Cancel the work
	 * @param data a data cancel
	 * @param imageView a view
	 * @return false if the same work is already in progress
	 */
	public static boolean cancelPotentialWork(String data, ImageView imageView) {
	    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final String bitmapData = bitmapWorkerTask.data;
	        // If bitmapData is not yet set or it differs from the new data
	        if (bitmapData == null || bitmapData != data) {
	            // Cancel previous task
	            bitmapWorkerTask.cancel(true);
	        } else {
	            // The same work is already in progress
	            return false;
	        }
	    }
	    // No task associated with the ImageView, or an existing task was cancelled
	    return true;
	}
	
	/**
	 * Start the worker
	 * @param imageView a view
	 * @return
	 */
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		   if (imageView != null) {
		       //final Drawable drawable = imageView.getBackground();
			   final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getBitmapWorkerTask();
		       }
		    }
		    return null;
		}
	
	static class AsyncDrawable extends BitmapDrawable {
	    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

	    public AsyncDrawable(Resources res, Bitmap bitmap,
	            BitmapWorkerTask bitmapWorkerTask) {
	        super(res, bitmap);
	        bitmapWorkerTaskReference =
	            new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
	    }

	    public BitmapWorkerTask getBitmapWorkerTask() {
	        return bitmapWorkerTaskReference.get();
	    }
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    String data =null;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... params) {
	        data = params[0];
	        return decodeSampledBitmapFromResource(data, width, height);
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
		@Override
	    protected void onPostExecute(Bitmap bitmap) {
	    	 if (isCancelled()) {
	             bitmap = null;
	         }

	         if (imageViewReference != null && bitmap != null) {
	             final ImageView imageView = imageViewReference.get();
	             final BitmapWorkerTask bitmapWorkerTask =
	                     getBitmapWorkerTask(imageView);
	             if (this == bitmapWorkerTask && imageView != null) {
	                 imageView.setImageBitmap(bitmap);
	             }
	         }
	    }
	}
	/**
	 * Decode bit map
	 * @param pathId path
	 * @param reqWidth with 
	 * @param reqHeight height
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String pathId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(pathId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(pathId, options);
	}
	
	/**
	 * 
	 * @param options options
	 * @param reqWidth width
	 * @param reqHeight height
	 * @return calculate size
	 */
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
}


