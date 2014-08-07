package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.obnoxx.androidapp.R;

/**
 * UI that asks the user his phone number, to begin logging him in.
 */
public class LoginPhoneNumberFragment extends Fragment {
    private OnPhoneNumberSelectedListener mCallback;

    public interface OnPhoneNumberSelectedListener {
        public void onPhoneNumberSelected(String phoneNumber);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnPhoneNumberSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getClass().getCanonicalName()
                    + " must implement OnPhoneNumberSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_phone_number_fragment, parent, false);
        setButtonHandlers(v);
        return v;
    }

    private void setButtonHandlers(View v) {
        final Context appContext = this.getActivity().getApplicationContext();

        v.findViewById(R.id.phone_number_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText phoneNumberText =
                                ((EditText) getActivity().findViewById(R.id.phone_number));
                        mCallback.onPhoneNumberSelected(phoneNumberText.getText().toString());
                    }
                }
        );
    }
}
