package com.ack.familyfootprints.onboarding;
//import android.app.Fragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ack.familyfootprints.R;

/**
 * Created by Lucky Goyal on 8/1/2017.
 */
public class ScreenSliderFragment extends Fragment {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    public ScreenSliderFragment() {

    }
    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSliderFragment create(int pageNumber) {
        ScreenSliderFragment fragment = new ScreenSliderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        int layoutResId;
        switch (mPageNumber) {
            case 0:
                layoutResId = R.layout.frame_layout_demo1;
                break;
            case 1:
                layoutResId = R.layout.frame_layout_demo2;
                break;
            case 2:
                layoutResId = R.layout.frame_layout_demo3;
                break;
            default:
                layoutResId = R.layout.frame_layout_demo1;
        }
        View rootView = getActivity().getLayoutInflater().inflate(layoutResId, container, false);
        // Set the current page index as the View's tag (useful in the PageTransformer)
        rootView.setTag(mPageNumber);
        return rootView;
    }

}
