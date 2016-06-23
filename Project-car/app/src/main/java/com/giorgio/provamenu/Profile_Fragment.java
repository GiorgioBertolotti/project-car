package com.giorgio.provamenu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Profile_Fragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    public Profile_Fragment() {}
    public static Profile_Fragment newInstance(int someInt) {
        Profile_Fragment myFragment = new Profile_Fragment();

        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        myFragment.setArguments(args);

        return myFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_profile_, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ((TextView) view.findViewById(R.id.textView10)).setText(MainActivity.loggato.getName());
        ((TextView) view.findViewById(R.id.textView11)).setText(MainActivity.loggato.getSurname());
        ((TextView) view.findViewById(R.id.textView12)).setText(MainActivity.loggato.getMobile());
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
