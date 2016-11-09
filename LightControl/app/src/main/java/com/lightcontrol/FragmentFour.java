package com.lightcontrol;



import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class FragmentFour extends  Fragment{

    public static Switch geoSwitch;

    public static Fragment newInstance(Context context) {
        FragmentThree f = new FragmentThree();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_four, null);

        geoSwitch = (Switch) root.findViewById(R.id.switch4);

        geoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.sendMessage("automation", "G1");
                    Intent intent = new Intent(getContext(),MapsActivity.class);
                    startActivity(intent);
                } else {
                    MainActivity.sendMessage("automation", "G0");
                }
            }
        });


        return root;
    }
}
