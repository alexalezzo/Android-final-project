package com.example.alex.android_final_project;

import java.util.ArrayList;


/**
 * Created by Alex on 4/15/2016.
 */
public class MorseCover {
    private int SHORT_TIME = 1000;
    private int MINIMUM = 100;
    private int LONG_TIME = 2000;
    public int[] GetCode(String input)
    {
        int[] res = new int[input.length() * 2];
        for(int i = 0; i < input.length(); i++)
        {
            if(input.charAt(i) == '.')
            {
                res[2* i] = SHORT_TIME;
                res[2 * i + 1] = SHORT_TIME;
            }
            else if (input.charAt(i) == '-')
            {
                res[2* i] = LONG_TIME;
                res[2 * i + 1] = SHORT_TIME;
            }
            else if (input.charAt(i) == '|')
            {
                //res[2* i] = LONG_TIME;
                res[2 * i + 1] = 2 * LONG_TIME;
            }
            else if (input.charAt(i) == ' ')
            {
                res[2 * i + 1] = LONG_TIME;
            }
        }
        return res;
    }
    public String ParseCode(ArrayList<Long> time)
    {
        String code = "";
        int toRemove = 0;
        for(int i = 0; i < time.size(); i++)
        {
            int diffSh = Math.abs(SHORT_TIME - Math.abs(time.get(i).intValue()));
            int diffL = Math.abs(LONG_TIME - Math.abs(time.get(i).intValue()));
            int diffSL = Math.abs(2 * LONG_TIME - Math.abs(time.get(i).intValue()));
            if(time.get(i) > MINIMUM && diffSh < diffL)
            {
                code += ".";
            }
            else if(time.get(i) > MINIMUM && diffSh > diffL)
            {
                code += "-";
            }
            else if(time.get(i) < -MINIMUM && diffL < diffSh && diffL < diffSL)
            {
                code += " ";
                toRemove = i;
            }
            else if(time.get(i) < -MINIMUM && diffSL < diffSh && diffSL < diffL)
            {
                code += " | ";
                toRemove = i;
            }
        }
        for(int i = 0; i < toRemove; i++)
        {
            time.remove(0);
        }
        return code;
    }
}
