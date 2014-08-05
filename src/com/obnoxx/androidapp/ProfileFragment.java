package com.obnoxx.androidapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, parent, false);

        Uri uri = Uri.parse(SoundDeliveryProvider.DELIVERIES_FOR_URI.toString() + "/" +
                CurrentUser.getUser(this.getActivity()).getId());
        Cursor deliveries = getActivity().managedQuery(
                uri, ProfileListItemAdapter.PROJECTION, null, null,
                ProfileListItemAdapter.SOUND_DELIVERY_DATE_TIME_COLUMN_NAME + " DESC");
        final ProfileListItemAdapter adapter =
                new ProfileListItemAdapter(getActivity(), deliveries);

        // Give the user a button for going back to the record view.
        ((Button) v.findViewById(R.id.back_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProfileFragment.this.getActivity(),
                                RecordSoundActivity.class));
                    }
                });

        // List view: Show a list of all the sounds the user has sent or received.  If sounds
        // are clicked, play them.
        ListView listView = ((ListView) v.findViewById(R.id.sound_delivery_list));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                final Sound sound = adapter.getSoundForPosition(position);
                new DownloadSoundTask(ProfileFragment.this.getActivity(), sound) {
                    @Override
                    public void onPostExecute(Boolean success) {
                        if (success) {
                            sound.play();
                        }
                    }
                }.execute();
            }
        });

        // Force refresh our data, for now.
        // TODO(jonemerson): Think about how we really want to do this.
        new GetSoundsOperation(this.getActivity()) {
            @Override
            public void onComplete(GetSoundsResponse response) {
                Log.i(TAG, "Get sounds complete - " + response.getSoundDeliveries().size() +
                        " deliveries, " + response.getSounds().size() + " sounds");
                adapter.notifyDataSetChanged();
            }
        };

        return v;
    }
}
