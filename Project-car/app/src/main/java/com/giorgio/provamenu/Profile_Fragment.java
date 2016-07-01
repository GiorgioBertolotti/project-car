package com.giorgio.provamenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;

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
        if(MainActivity.stato == 20){
            ((TextView) view.findViewById(R.id.prftvname)).setText(MainActivity.loggato.getName());
            ((TextView) view.findViewById(R.id.prftvsurname)).setText(MainActivity.loggato.getSurname());
            ((TextView) view.findViewById(R.id.prftvmobile)).setText(MainActivity.loggato.getMobile());
            ((TextView) view.findViewById(R.id.prftvtipo)).setVisibility(View.INVISIBLE);
            if(MainActivity.loggato.getImg()!=null)
                ((ImageView) view.findViewById(R.id.prfivprofileimage)).setImageDrawable(new BitmapDrawable(getResources(), MainActivity.loggato.getImg()));
            FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.prffabedit);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(chooserIntent, 1);
                }
            });
        }
        if(MainActivity.stato == 42||MainActivity.stato == 45||MainActivity.stato == 52){
            ((TextView) view.findViewById(R.id.prftvname)).setText(MainActivity.selected.getName());
            ((TextView) view.findViewById(R.id.prftvsurname)).setText(MainActivity.selected.getSurname());
            ((TextView) view.findViewById(R.id.prftvmobile)).setText(MainActivity.selected.getMobile());
            if(MainActivity.selected.getImg()!=null)
                ((ImageView) view.findViewById(R.id.prfivprofileimage)).setImageDrawable(new BitmapDrawable(getResources(), MainActivity.selected.getImg()));
            ((FloatingActionButton) view.findViewById(R.id.prffabedit)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.prftvtipo)).setVisibility(View.VISIBLE);
            if(MainActivity.selected.getType_id()==1)
                ((TextView) view.findViewById(R.id.prftvtipo)).setText("Autostoppista");
            else
                ((TextView) view.findViewById(R.id.prftvtipo)).setText("Autista");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) return;
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Drawable bd = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(inputStream));
                ((ImageView) getActivity().findViewById(R.id.prfivprofileimage)).setImageDrawable(bd);
                mListener.onFragmentInteraction(null);
            }
            catch (Exception e) {return;}
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
