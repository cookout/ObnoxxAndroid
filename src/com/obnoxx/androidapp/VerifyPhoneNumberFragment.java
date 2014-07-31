package com.obnoxx.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;

public class VerifyPhoneNumberFragment extends Fragment {
    private static final String TAG = "VerifyPhoneNumberFragment";
    private static final int MODE_PHONE_NUMBER = 1;
    private static final int MODE_VERIFICATION_CODE = 2;
    private static final int MODE_PROGRESS_BAR = 3;

    private String mTemporaryUserCode = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_verify_phone_number, parent, false);
        setButtonHandlers(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getPhoneNumberText().getText().clear();
        setMode(MODE_PHONE_NUMBER);
    }

    private void setButtonHandlers(View v) {
        final Context appContext = this.getActivity().getApplicationContext();

        ((Button) v.findViewById(R.id.phone_number_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setMode(MODE_PROGRESS_BAR);
                        String phoneNumber = VerifyPhoneNumberFragment.this.getPhoneNumberText()
                                .getText().toString();
                        VerifyPhoneNumberTask t =
                                new VerifyPhoneNumberTask(appContext, phoneNumber) {
                                    @Override
                                    public void onPostExecute(VerifyPhoneNumberResponse result) {
                                        if (result.getStatusCode() == 200) {
                                            mTemporaryUserCode = result.getTemporaryUserCode();
                                            setMode(MODE_VERIFICATION_CODE);
                                        } else {
                                            Toast.makeText(appContext, "Error, try again",
                                                    Toast.LENGTH_SHORT).show();
                                            setMode(MODE_PHONE_NUMBER);
                                        }
                                    }
                                };
                        t.execute();
                    }
                });

        ((Button) v.findViewById(R.id.verification_code_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setMode(MODE_PROGRESS_BAR);
                        String verificationCodeText =
                                VerifyPhoneNumberFragment.this.getVerificationCodeText()
                                        .getText().toString();
                        VerifyPhoneNumberTask t =
                                new VerifyPhoneNumberTask(appContext, verificationCodeText,
                                        mTemporaryUserCode) {
                                    @Override
                                    public void onPostExecute(VerifyPhoneNumberResponse result) {
                                        if (result.getStatusCode() == 200) {
                                            setSessionId(result.getSessionId());
                                        } else {
                                            Toast.makeText(appContext, "Error, try again",
                                                    Toast.LENGTH_SHORT).show();
                                            setMode(MODE_VERIFICATION_CODE);
                                        }
                                    }
                                };
                        t.execute();
                    }
                }
        );
    }

    private EditText getPhoneNumberText() {
        return ((EditText) getActivity().findViewById(R.id.phone_number));
    }

    private EditText getVerificationCodeText() {
        return ((EditText) getActivity().findViewById(R.id.verification_code));
    }

    private void setMode(int mode) {
        ((LinearLayout) getActivity().findViewById(R.id.phone_number_layout))
                .setVisibility(mode == MODE_PHONE_NUMBER ? View.VISIBLE : View.GONE);
        ((LinearLayout) getActivity().findViewById(R.id.verification_code_layout))
                .setVisibility(mode == MODE_VERIFICATION_CODE ? View.VISIBLE : View.GONE);
        ((ProgressBar) getActivity().findViewById(R.id.progress_bar))
                .setVisibility(mode == MODE_PROGRESS_BAR ? View.VISIBLE : View.GONE);

        if (mode == MODE_PHONE_NUMBER) {
            getPhoneNumberText().requestFocus();
        } else if (mode == MODE_VERIFICATION_CODE) {
            getVerificationCodeText().requestFocus();
        }
    }

    private void setSessionId(String sessionId) {
        CurrentUser.setSessionId(this.getActivity(), sessionId);
        startActivity(new Intent(this.getActivity(), RecordSoundActivity.class));
    }
}
