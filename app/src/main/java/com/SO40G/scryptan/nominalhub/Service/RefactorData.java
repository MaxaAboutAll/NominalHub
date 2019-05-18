package com.SO40G.scryptan.nominalhub.Service;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

public class RefactorData extends AppCompatActivity {

    private String TAG = "RefactorData";

    public void writeFile(String dataJSON, String FILENAME) {
        try {
            OutputStream outputStream = openFileOutput(FILENAME, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);
            osw.write(dataJSON);
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String FILENAME) {
        try {
            String returning = "";
            FileReader fileReader = new FileReader(FILENAME+".txt");
            Scanner scanner = new Scanner(fileReader);
            while (scanner.hasNextLine()) {
                returning += scanner.nextLine();
            }
            return returning;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
