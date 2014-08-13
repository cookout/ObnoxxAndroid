package com.obnoxx.androidapp.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.obnoxx.androidapp.R;
import com.obnoxx.androidapp.SoundDeliveryProvider;
import com.obnoxx.androidapp.data.Sound;
import com.obnoxx.androidapp.requests.DownloadSoundRequest;

public class ProfileFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ProfileFragment";
    ProfileListItemAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, parent, false);

        getLoaderManager().initLoader(0, null, this);
        mAdapter = new ProfileListItemAdapter(getActivity());
        this.setListAdapter(mAdapter);

        // Give the user a button for going back to the record view.
        ((Button) v.findViewById(R.id.back_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ProfileFragment.this.getActivity(),
                                RecordSoundActivity.class));
                    }
                }
        );

        // List view: Show a list of all the sounds the user has sent or received.  If sounds
        // are clicked, play them.
        ListView listView = ((ListView) v.findViewById(android.R.id.list));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                final Sound sound = mAdapter.getSoundForPosition(position);
                new DownloadSoundRequest(ProfileFragment.this.getActivity(), sound) {
                    @Override
                    public void onPostExecute(Boolean success) {
                        if (success) {
                            sound.play();
                        }
                    }
                }.execute();
            }
        });

        return v;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        // Create a new CursorLoader with the following query parameters.
        return new CursorLoader(this.getActivity(),
                SoundDeliveryProvider.getUriForCurrentUserSoundDelivieries(this.getActivity()),
                ProfileListItemAdapter.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }
}
