package com.example.alex.android_final_project;

/**
 * Created by Alex on 4/15/2016.
 */
public class Morse
{
    char[] alpha = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '0', ' ' };
    String[] dottie = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.",
            "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.",
            "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-",
            "-.--", "--..", ".----", "..---", "...--", "....-", ".....",
            "-....", "--...", "---..", "----.", "-----", "|" };

    public String toMorse(char [] translates) {
        String s = "";
        for (int i = 0; i < translates.length; i++) {

            for (int j = 0; j < alpha.length; j++) {
                if (alpha[j] == (translates[i])) {
                    s += dottie[j] + " ";
                    break;
                }
            }
        }


        return s;
    }

    public String toEnglish(String input)
    {
        String s="";
        String curr = "";
        for(int i = 0; i < input.length(); i++)
        {
            if(input.charAt(i) == ' ')
            {
                for(int j = 0; j < dottie.length; j++)
                {
                    if(dottie[j].equals(curr))
                    {
                        s += alpha[j];
                        break;
                    }
                }
                curr = "";
            }
            else
                curr += input.charAt(i);
        }


        return s;
    }
}


