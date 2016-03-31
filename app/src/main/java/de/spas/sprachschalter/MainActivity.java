package de.spas.sprachschalter;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private static final String LOG_TAG = "MainActivity";
    private SpeechRecognizer speech;
    private Intent recognizerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(this);

        startListening();
    }

    private void startListening() {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,getResources().getConfiguration().locale.getLanguage());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(LOG_TAG, "ready");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(LOG_TAG, "Beginning");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(LOG_TAG, "Endofspeech");

    }

    @Override
    public void onError(int errorCode) {
        String errorMessage = getErrorText(errorCode);
        Log.d(LOG_TAG, "FAILED " + errorMessage);

    }

    @Override
    public void onResults(Bundle bundle) {
        recognizeKeyword(bundle);
        speech.startListening(recognizerIntent);
    }

    private void recognizeKeyword(Bundle bundle) {
        ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String[] keywordsOn = getResources().getStringArray(R.array.keywords_on);
        String[] keywordsOff = getResources().getStringArray(R.array.keywords_off);

        for(String s : keywordsOn) {
            if(matches.contains(s)) {
                on();
            }
        }
        for(String s : keywordsOff) {
            if(matches.contains(s)) {
                off();
            }
        }
    }

    private void off() {
        ((ImageView)findViewById(R.id.bulb)).setImageResource(R.drawable.bulb_off);
    }

    private void on() {
        ((ImageView)findViewById(R.id.bulb)).setImageResource(R.drawable.bulb_on);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        recognizeKeyword(bundle);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        ((ImageView)findViewById(R.id.bulb)).setImageResource(R.drawable.bulb_off);
    }

    @Override
    protected void onResume() {
        super.onResume();
        speech.startListening(recognizerIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        speech.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(speech!=null) speech.destroy();
    }


    public static String getErrorText(int errorCode) {
        String message;
        switch (errorCode) {
            case SpeechRecognizer.ERROR_AUDIO:
                message = "Audio recording error";
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                message = "Client side error";
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                message = "Insufficient permissions";
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                message = "Network error";
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                message = "Network timeout";
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                message = "No match";
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                message = "RecognitionService busy";
                break;
            case SpeechRecognizer.ERROR_SERVER:
                message = "error from server";
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                message = "No speech input";
                break;
            default:
                message = "Didn't understand, please try again.";
                break;
        }
        return message;
    }
}
