package com.easytravel.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import static com.easytravel.app.MainActivity.mDrawerToggle;

public class Settings_Fragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    public Settings_Fragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_settings_, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        final SeekBar seekBar = (SeekBar)view.findViewById(R.id.setskbrange);
        final TextView tvRange = (TextView)view.findViewById(R.id.settvrange2);
        seekBar.setProgress(MainActivity.loggato.getRange());
        tvRange.setText(Integer.toString(MainActivity.loggato.getRange()));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer tmp = progress;
                tvRange.setText(tmp.toString());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        getActivity().findViewById(R.id.tlbbtnsettings).setVisibility(View.INVISIBLE);
        MainActivity.notificationsVisibility = getActivity().findViewById(R.id.notification).getVisibility();
        getActivity().findViewById(R.id.notification).setVisibility(View.INVISIBLE);
        getActivity().findViewById(R.id.tlbbtnnotifications).setVisibility(View.INVISIBLE);
        mDrawerToggle.syncState();
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                mDrawerToggle.setDrawerIndicatorEnabled(true);
                mDrawerToggle.setToolbarNavigationClickListener(null);
                DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                mDrawerToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
                getActivity().findViewById(R.id.tlbbtnsettings).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.notification).setVisibility(MainActivity.notificationsVisibility);
                getActivity().findViewById(R.id.tlbbtnnotifications).setVisibility(View.VISIBLE);
                mDrawerToggle.syncState();
            }
        });
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
