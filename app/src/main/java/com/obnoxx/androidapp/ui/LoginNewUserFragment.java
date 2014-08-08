package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.obnoxx.androidapp.R;

/**
 * UI that allows a new user to select his user name.
 */
public class LoginNewUserFragment extends Fragment {
    private OnUserNameSelectedListener mCallback;

    public interface OnUserNameSelectedListener {
        public void onUserNameSelected(String verificationCode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnUserNameSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getClass().getCanonicalName()
                    + " must implement OnUserNameSelectedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.login_new_user_fragment, parent, false);
        setButtonHandlers(v);
        return v;
    }

    private void setButtonHandlers(View v) {
        v.findViewById(R.id.create_user_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onUserNameSelected(
                                getUserNameText().getText().toString());
                    }
                }
        );
    }

    private EditText getUserNameText() {
        return ((EditText) getActivity().findViewById(R.id.user_name));
    }
}
