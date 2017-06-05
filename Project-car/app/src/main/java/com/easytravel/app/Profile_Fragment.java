package com.easytravel.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;

import static com.easytravel.app.MainActivity.mDrawerToggle;

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
            ((TextView) view.findViewById(R.id.prftvnamesurname)).setText(MainActivity.loggato.getName()+" "+MainActivity.loggato.getSurname());
            ((TextView) view.findViewById(R.id.prftvmail)).setText(MainActivity.loggato.getMail());
            ((TextView) view.findViewById(R.id.prftvmobile)).setText(MainActivity.loggato.getMobile());
            ((RatingBar) view.findViewById(R.id.prfrbrating)).setRating(MainActivity.loggato.getRating().floatValue());
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
            ((TextView) view.findViewById(R.id.prftvnamesurname)).setText(MainActivity.selected.getName()+" "+MainActivity.selected.getSurname());
            ((TextView) view.findViewById(R.id.prftvmail)).setText(MainActivity.selected.getMail());
            ((TextView) view.findViewById(R.id.prftvmobile)).setText(MainActivity.selected.getMobile());
            ((RatingBar) view.findViewById(R.id.prfrbrating)).setRating(MainActivity.selected.getRating().floatValue());
            if(MainActivity.selected.getImg()!=null)
                ((ImageView) view.findViewById(R.id.prfivprofileimage)).setImageDrawable(new BitmapDrawable(getResources(), MainActivity.selected.getImg()));
            ((FloatingActionButton) view.findViewById(R.id.prffabedit)).setVisibility(View.INVISIBLE);
            ((TextView) view.findViewById(R.id.prftvtipo)).setVisibility(View.VISIBLE);
            if(MainActivity.selected.getType_id()==1)
                ((TextView) view.findViewById(R.id.prftvtipo)).setText("Autostoppista");
            else
                ((TextView) view.findViewById(R.id.prftvtipo)).setText("Autista");
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
        getActivity().findViewById(R.id.prfrbrating).setEnabled(false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data == null) return;
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bmpo = BitmapFactory.decodeStream(inputStream);
                int w = 200, h = 200;
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(w, h, conf);
                Canvas canvas = new Canvas(bmp);
                canvas.drawColor(Color.WHITE);
                float rel = (float)bmpo.getWidth()/bmpo.getHeight();
                if(bmpo.getHeight()>bmpo.getWidth()){
                    int nw = Math.round(rel*w);
                    Bitmap scaled = Bitmap.createScaledBitmap(bmpo, nw, h, true);
                    canvas.drawBitmap(scaled,(w-nw)/2,0,null);
                }
                if(bmpo.getHeight()<bmpo.getWidth()){
                    int nh = Math.round(h/rel);
                    Bitmap scaled = Bitmap.createScaledBitmap(bmpo, w, nh, true);
                    canvas.drawBitmap(scaled,0,(h-nh)/2,null);
                }
                if(bmpo.getHeight()==bmpo.getWidth()){
                    Bitmap scaled = Bitmap.createScaledBitmap(bmpo, w, h, true);
                    canvas.drawBitmap(scaled,0,0,null);
                }
                Drawable drawable = new BitmapDrawable(getResources(), bmp);
                ((ImageView) getActivity().findViewById(R.id.prfivprofileimage)).setImageDrawable(drawable);
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
