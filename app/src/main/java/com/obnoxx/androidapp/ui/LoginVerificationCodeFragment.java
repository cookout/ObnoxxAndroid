package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.obnoxx.androidapp.R;

/**
 * UI that prompts the user for the verification code we just texted him.
 */
public class LoginVerificationCodeFragment extends Fragment {
    private OnVerificationCodeSelectedListener mCallback;

    public interface OnVerificationCodeSelectedListener {
        public void onVerificationCodeSelected(String verificationCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnVerificationCodeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getClass().getCanonicalName()
                    + " must implement OnVerificationCodeSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_verification_code_fragment, parent, false);
        setButtonHandlers(v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getVerificationCodeText().getText().clear();
    }

    private void setButtonHandlers(View v) {
        v.findViewById(R.id.verification_code_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText verificationCodeText =
                                ((EditText) getActivity().findViewById(R.id.verification_code));
                        mCallback.onVerificationCodeSelected(
                                verificationCodeText.getText().toString());
                    }
                }
        );

        ((Button) v.findViewById(R.id.verification_code_back_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LoginVerificationCodeFragment.this.getActivity().finish();
                    }
                });
    }

    private EditText getVerificationCodeText() {
        return ((EditText) getActivity().findViewById(R.id.verification_code));
    }
}
