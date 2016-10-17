package com.lightcontrol;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class FragmentOne extends Fragment {
    public static Switch lightSwitch;
    public static Fragment newInstance(Context context) {
        FragmentOne f = new FragmentOne();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_one, null);
        lightSwitch = (Switch) root.findViewById(R.id.switch1);

        lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    MainActivity.sendMessage("automation", "L1");
                }
                else
                {
                    MainActivity.sendMessage("automation","L0");
                }
            }
        });

        return root;
    }

}