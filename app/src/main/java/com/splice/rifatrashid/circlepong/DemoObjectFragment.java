package com.splice.rifatrashid.circlepong;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Seize on 1/18/2016.
 */
public class DemoObjectFragment extends Fragment {

    public static final String ARG_OBJECT = "object";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.style_chooser_page, container, false);
        Bundle args = getArguments();
        int index = args.getInt(ARG_OBJECT);
        int descRef = R.string.playgameDesc;
        int imageRef = R.drawable.img_gameplay;
        final Typeface FONT_WALSHEIM_WEB = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gt-walsheim-web.ttf");
        ImageView ic_icon = (ImageView) rootView.findViewById(R.id.imageView2);
        switch (index) {
            case 0:
                break;
            case 1:
                descRef = R.string.playgameDesc;
                imageRef = R.drawable.img_gameplay;
                break;
            case 2:
                descRef = R.string.googleplayDesc;
                imageRef = R.drawable.img_googleplay;
                break;
            case 3:
                descRef = R.string.achievementsDesc;
                imageRef = R.drawable.img_achievements;
                break;
            case 4:
                descRef = R.string.leaderboardsDesc;
                imageRef = R.drawable.img_leaderboards;
                break;
        }
        if (index >= 1) {
            rootView.findViewById(R.id.txt4).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.txt5).setVisibility(View.INVISIBLE);
            ic_icon.setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.img).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.titleText).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.sub_Text).setVisibility(View.VISIBLE);
        } else {
            ic_icon.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.txt4).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.txt5).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.img).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.titleText).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.sub_Text).setVisibility(View.INVISIBLE);
        }
        TextView tv = (TextView) rootView.findViewById(R.id.sub_Text);
        TextView introText = (TextView) rootView.findViewById(R.id.txt4);
        introText.setTypeface(FONT_WALSHEIM_WEB);
        TextView miniIntroText = (TextView) rootView.findViewById(R.id.txt5);
        miniIntroText.setTypeface(FONT_WALSHEIM_WEB);

        tv.setText(getResources().getString(descRef));
        tv.setTypeface(FONT_WALSHEIM_WEB);
        ImageView iv = (ImageView) rootView.findViewById(R.id.img);
        iv.setImageResource(imageRef);
        return rootView;
    }
}
