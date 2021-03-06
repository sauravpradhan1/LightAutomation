package com.lightcontrol;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

public class FragmentTwo extends Fragment {

    public static Switch pirSwitch;
    public static Fragment newInstance(Context context) {
        FragmentTwo f = new FragmentTwo();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_two, null);
        pirSwitch = (Switch) root.findViewById(R.id.switch2);

        pirSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    MainActivity.sendMessage("automation", "P1");
                }else{
                    MainActivity.sendMessage("automation","P0");
                }
            }
        });
        return root;
    }

}