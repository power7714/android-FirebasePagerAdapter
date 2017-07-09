package com.yourpackagename;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by CurTro Studios on 7/6/2017.
 */

public abstract class FirebasePagerAdapter<T> extends PagerAdapter implements FirebaseAdapter, ChangeEventListener {

    private static String TAG = "firebasepageradapter";
    protected final Activity mActivity;
    protected final ObservableSnapshotArray<T> mSnapshots;
    protected final int mLayout;

    /**
     * @param activity    The {@link Activity} containing the {@link android.support.v4.view.ViewPager}
     * @param modelLayout This is the layout used to represent a single list item. You will be
     *                    responsible for populating an instance of the corresponding view with the
     *                    data from an instance of modelClass.
     * @param snapshots   The data used to populate the adapter
     */
    public FirebasePagerAdapter(Activity activity,
                               ObservableSnapshotArray<T> snapshots,
                               @LayoutRes int modelLayout) {
        mActivity = activity;
        mSnapshots = snapshots;
        mLayout = modelLayout;

        startListening();
    }

    /**
     * @param parser a custom {@link SnapshotParser} to convert a {@link DataSnapshot} to the model
     *               class
     * @param query  The Firebase location to watch for data changes. Can also be a slice of a
     *               location, using some combination of {@code limit()}, {@code startAt()}, and
     *               {@code endAt()}. <b>Note, this can also be a {@link DatabaseReference}.</b>
     * @see #FirebasePagerAdapter(Activity, ObservableSnapshotArray, int)
     */
    public FirebasePagerAdapter(Activity activity,
                               SnapshotParser<T> parser,
                               @LayoutRes int modelLayout,
                               Query query) {
        this(activity, new FirebaseArray<>(query, parser), modelLayout);
    }

    /**
     * @see #FirebasePagerAdapter(Activity, SnapshotParser, int, Query)
     */
    public FirebasePagerAdapter(Activity activity,
                               Class<T> modelClass,
                               @LayoutRes int modelLayout,
                               Query query) {
        this(activity, new ClassSnapshotParser<>(modelClass), modelLayout, query);
    }

    @Override
    public int getCount() {
        return mSnapshots.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void startListening() {
        if (!mSnapshots.isListening(this)) {
            mSnapshots.addChangeEventListener(this);
        }
    }

    @Override
    public void cleanup() {
        mSnapshots.removeChangeEventListener(this);
    }

    @Override
    public T getItem(int position) {
        return mSnapshots.getObject(position);
    }

    @Override
    public DatabaseReference getRef(int position) {
        return mSnapshots.get(position).getRef();
    }

    @Override
    public void onChildChanged(ChangeEventListener.EventType type, DataSnapshot snapshot, int index, int oldIndex) {
        notifyDataSetChanged();
    }

    @Override
    public void onDataChanged() {

    }

    @Override
    public void onCancelled(DatabaseError error) {
        Log.w(TAG, error.toException());
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mActivity.getLayoutInflater().inflate(mLayout, container, false);

        //T model = getItem(position);
        T model = getItem(position);

        // Call out to subclass to marshall this model into the provided view
        populateView(view, model, position);
        container.addView(view);
        return view;
    }

    /**
     * Each time the data at the given Firebase location changes,
     * this method will be called for each item that needs to be displayed.
     * The first two arguments correspond to the mLayout and mModelClass given to the constructor of
     * this class. The third argument is the item's position in the list.
     * <p>
     * Your implementation should populate the view using the data contained in the model.
     *
     * @param v        The view to populate
     * @param model    The object containing the data used to populate the view
     * @param position The position in the list of the view being populated
     */
    protected abstract void populateView(View v, T model, int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }

}
