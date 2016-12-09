package com.easytravel.app;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class Settings_Fragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    public Settings_Fragment() {}
    public static Settings_Fragment newInstance(int someInt) {
        Settings_Fragment myFragment = new Settings_Fragment();

        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        myFragment.setArguments(args);

        return myFragment;
    }
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
        switch (MainActivity.stato){
            case 21:{
                view.findViewById(R.id.setetip).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.settvip).setVisibility(View.INVISIBLE);
                final SeekBar seekBar = (SeekBar)view.findViewById(R.id.setskbrange);
                final TextView tvRange = (TextView)view.findViewById(R.id.settvrange2);
                seekBar.setProgress(MainActivity.loggato.getRange());
                tvRange.setText(MainActivity.loggato.getRange().toString());
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
                break;
            }
            case 1:{
                view.findViewById(R.id.setskbrange).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.settvrange).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.settvrange2).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.settvoldpassword).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.setetoldpassword).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.settvnewpassword).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.setetnewpassword).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.settvconfirm).setVisibility(View.INVISIBLE);
                view.findViewById(R.id.setetconfirm).setVisibility(View.INVISIBLE);
                ((EditText)view.findViewById(R.id.setetip)).setText(MainActivity.ipServer);
                Button btnsave = (Button)view.findViewById(R.id.setbtnsalva);
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                p.addRule(RelativeLayout.BELOW, R.id.setetip);
                p.addRule(RelativeLayout.CENTER_HORIZONTAL);
                btnsave.setLayoutParams(p);
            }
        }
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
