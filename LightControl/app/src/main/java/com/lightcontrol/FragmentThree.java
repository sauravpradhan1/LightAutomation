package com.lightcontrol;


import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class FragmentThree extends Fragment {


    public static Switch schSwitch;

    private Integer sH,sM,eH,eM;
    public static Fragment newInstance(Context context) {
        FragmentThree f = new FragmentThree();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_three, null);

        schSwitch = (Switch) root.findViewById(R.id.switch3);
        schSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    MainActivity.sendMessage("automation", "S1");
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            sH = selectedHour;
                            sM = selectedMinute;
                            Toast.makeText(getContext(), selectedHour + ":" + selectedMinute, Toast.LENGTH_LONG).show();
                            Calendar mcurrentTime = Calendar.getInstance();
                            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    eH = selectedHour;
                                    eM = selectedMinute;
                                    Toast.makeText(getContext(), selectedHour + ":" + selectedMinute, Toast.LENGTH_LONG).show();
                                    JSONObject js = new JSONObject();
                                    try {
                                        js.put("START_HOUR", sH);
                                        js.put("START_MINUTE", sM);
                                        js.put("END_HOUR", eH);
                                        js.put("END_MINUTE", eM);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    MainActivity.sendMessage("scheduler", js, getActivity());
                                    schSwitch.setChecked(true);
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Ending Time");
                            mTimePicker.show();
                        }
                    }, hour, minute, true);
                    mTimePicker.setTitle("Select Starting Time");
                    mTimePicker.show();
                } else {
                    MainActivity.sendMessage("automation", "S0");
                }
            }
        });
        return root;
    }

}