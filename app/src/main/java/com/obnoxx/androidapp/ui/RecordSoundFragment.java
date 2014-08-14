package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.obnoxx.androidapp.R;
import com.obnoxx.androidapp.SoundRecorder;
import com.obnoxx.androidapp.SoundRecordingException;
import com.obnoxx.androidapp.data.ContactGroup;
import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.requests.AddSoundRequest;

/**
 * Implements the sound recording interface.  Lets the user record a sound,
 * play it back, and choose people to share it with.
 */
public class RecordSoundFragment extends Fragment {
    private static final String TAG = "RecordSoundFragment";
    public static final String STATE_CONTACT_GROUP = "s";
    public static final int RESULT_CONTACTS_PICKED = 1;

    private ContactGroup mContactGroup = null;
    private SoundRecorder mSoundRecorder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mSoundRecorder = new SoundRecorder(activity.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.record_sound_fragment, parent, false);

        mContactGroup = (savedInstanceState == null) ?
                ContactGroup.get(this.getActivity()) :
                (ContactGroup) savedInstanceState.getParcelable(STATE_CONTACT_GROUP);
        ((ContactGroupView) v.findViewById(R.id.contact_group_view))
                .setText(mContactGroup.toString());
        setButtonHandlers(v);

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_CONTACT_GROUP, mContactGroup);
    }

    private void setButtonHandlers(View v) {
        ((Button) v.findViewById(R.id.record_button)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.play_button)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.send_button)).setOnClickListener(onClickListener);
        ((Button) v.findViewById(R.id.profile_button)).setOnClickListener(onClickListener);
        ((ContactGroupView) v.findViewById(R.id.contact_group_view))
                .setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.record_button:
                    try {
                        mSoundRecorder.start();
                    } catch (SoundRecordingException e) {
                        Log.e(TAG, "Could not start recording", e);
                    }
                    break;

                case R.id.play_button:
                    play();
                    break;

                case R.id.send_button:
                    send();
                    break;

                case R.id.profile_button:
                    startActivity(new Intent(RecordSoundFragment.this.getActivity(),
                            ProfileActivity.class));
                    break;

                case R.id.contact_group_view:
                    Intent intent = new Intent(RecordSoundFragment.this.getActivity(),
                            ContactPickerActivity.class);
                    intent.putExtra(ContactPickerActivity.INITIAL_CONTACT_GROUP, mContactGroup);
                    startActivityForResult(intent, RESULT_CONTACTS_PICKED);
                    break;
            }
        }
    };

    private void play() {
        Toast.makeText(this.getActivity(), "Playing...", Toast.LENGTH_SHORT).show();
        Sound sound = mSoundRecorder.getLastSound();
        if (sound != null) {
            sound.play();
        }
    }

    private void send() {
        Sound sound = mSoundRecorder.getLastSound();
        if (sound != null) {
            AddSoundRequest t = new AddSoundRequest(this.getActivity(), sound, mContactGroup);
            t.execute();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RecordSoundFragment.RESULT_CONTACTS_PICKED &&
                resultCode == Activity.RESULT_OK) {
            mContactGroup.copyFrom((ContactGroup)
                    data.getParcelableExtra(ContactPickerFragment.RESULT_CONTACT_GROUP));
            ((ContactGroupView) this.getView().findViewById(R.id.contact_group_view))
                    .setText(mContactGroup.toString());
            mContactGroup.save(this.getActivity());
        }
    }
}
