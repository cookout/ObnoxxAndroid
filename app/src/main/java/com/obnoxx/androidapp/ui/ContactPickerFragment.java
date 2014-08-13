package com.obnoxx.androidapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.obnoxx.androidapp.R;

public class ContactPickerFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ContactPickerFragment";
    private static final String SAVED_INSTANCE_STATE_MODEL = "model";
    public static final String RESULT_RESULTS = "results";

    private Bundle mInitialInstanceState;
    private ContactPickerModel mModel = null;
    private ContactPickerListAdapter mAdapter = null;

    /**
     * Sets the default selection for the contact picker, probably from Android
     * system settings.  If this Fragment is then created without a saved
     * instance state, this view will default to the contacts specified in this
     * initial state.
     */
    public void setArguments(Bundle initialInstanceState) {
        mInitialInstanceState = initialInstanceState;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, parent, false);

        getLoaderManager().initLoader(0, null, this);
        mModel = new ContactPickerModel(savedInstanceState == null ?
                mInitialInstanceState :
                savedInstanceState.getBundle(SAVED_INSTANCE_STATE_MODEL));
        mAdapter = new ContactPickerListAdapter(this.getActivity(), mModel);
        this.setListAdapter(mAdapter);

        ((Button) v.findViewById(R.id.back_button)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(RESULT_RESULTS, mModel.getSavedInstanceState());
                        ContactPickerFragment.this.getActivity().setResult(
                                Activity.RESULT_OK, returnIntent);
                        ContactPickerFragment.this.getActivity().finish();
                    }
                });

        return v;
    }

    @Override
    public Loader onCreateLoader(int i, Bundle bundle) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = '"
                + ("1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME
                + " COLLATE LOCALIZED ASC";
        return new CursorLoader(this.getActivity(), uri, projection, selection, selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(SAVED_INSTANCE_STATE_MODEL, mModel.getSavedInstanceState());
    }
}
