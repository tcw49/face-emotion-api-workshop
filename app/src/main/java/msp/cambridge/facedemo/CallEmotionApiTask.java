package msp.cambridge.facedemo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Asychronous task for querying the Emotion API endpoint to recognize the emotions in the
 * submitted image.
 *
 * @author Henry Thompson
 */
public class CallEmotionApiTask extends AsyncTask<Void, Void, List<RecognizeResult>> {
    /** The bytes representing the JPEG image to be submitted to the emotion API endpoint. */
    private final byte[] _image;

    /** The Client for contacting the Emotion API endpoint. */
    private final EmotionServiceClient _emotionClient;

    /** The Callback so that the caller can be notified when the requests task is done. */
    private final OnEmotionRequestComplete _listener;

    /**
     * If not null, indicates that an exception was thrown during calling the endpoint and that
     * this was the exception thrown.
     */
    private Exception mException = null;

    /**
     * Interface for the callback after the CallEmotionApi asynchronous task completes.
     */
    public interface OnEmotionRequestComplete {
        /**
         * Called when the asychronous task has successfully called the Emotion API endpoint.
         * @param results The list of all emotions recognised in the image. An empty list
         *                indicates that the Emotion API was unable to detect any faces.
         * @param image The bytes representing the image that was sent to the Emotion API to
         *              generate the response in <code>results</code>.
         */
        void onEmotionResult(@NonNull List<RecognizeResult> results, @NonNull byte[] image);

        /**
         * Called when an exception occurs during the asynchronous task to the Emomtion API
         * endpoint.
         * @param exception The exception thrown during the API request.
         */
        void onError(@NonNull Exception exception);
    }

    /**
     * @param image The bytes representing the image to be analysed by the Emotion API.
     * @param emotionClient The Client to be used for contacting the Emotion API.
     * @param listener The callback used to indicate when the asynchronous task is finished.
     */
    public CallEmotionApiTask(@NonNull final byte[] image,
                              @NonNull final EmotionServiceClient emotionClient,
                              @NonNull final OnEmotionRequestComplete listener) {
        _image = image;
        _emotionClient = emotionClient;
        _listener = listener;
    }

    @Override
    protected List<RecognizeResult> doInBackground(Void... args) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(_image);
            return _emotionClient.recognizeImage(inputStream);
        }
        catch (Exception e) {
            mException = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<RecognizeResult> result) {
        if (mException == null) {
            _listener.onEmotionResult(result, _image);
        }
        else {
            _listener.onError(mException);
        }
    }
}