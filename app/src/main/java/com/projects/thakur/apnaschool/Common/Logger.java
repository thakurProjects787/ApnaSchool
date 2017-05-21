package com.projects.thakur.apnaschool.Common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;


public class Logger {

    public static  FileHandler logger = null;
    private static String folderName = "/sdcard/Files/SchoolTrac";

    static boolean isExternalStorageAvailable = false;
    static boolean isExternalStorageWriteable = false;
    static String state = Environment.getExternalStorageState();


/*
  Write data into text file
 */
    public static void addDataIntoFile(String fileName,String message,Context context) {

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            isExternalStorageAvailable = isExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            isExternalStorageAvailable = true;
            isExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            isExternalStorageAvailable = isExternalStorageWriteable = false;
        }

        File dir = new File(folderName);
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            if(!dir.exists()) {
                Log.d("Dir created ", "Dir created ");
                dir.mkdirs();
            }

            File logFile = new File(dir,fileName);

            if (!logFile.exists())  {
                try  {
                    Log.d("File created ", "File created "+ dir);
                    logFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try {
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));

                buf.write(message + "\r\n");
                //buf.append(message);
                buf.newLine();
                buf.flush();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
      Delete file
     */
    public static void deleteFile(String fileName,Context context) {

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            isExternalStorageAvailable = isExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            isExternalStorageAvailable = true;
            isExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            isExternalStorageAvailable = isExternalStorageWriteable = false;
        }

        File dir = new File(folderName);
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            if(!dir.exists()) {
                Log.d("Dir created ", "Dir created ");
                dir.mkdirs();
            }

            File logFile = new File(dir,fileName);

            if(logFile.exists()){
                logFile.delete();
            }

        }
    }

    /*
       Read Attendece data from  file
     */
    public static List<ArrayList> getDataFromAttnFile(String fileName,Context context) {

        List<ArrayList> combinedDetails = new ArrayList<ArrayList>();

        ArrayList<String> overallDetails = new ArrayList<String>();
        ArrayList<String> eachSchoolDetails = new ArrayList<String>();

        try {
            File dir = new File(folderName);
            FileInputStream fis = new FileInputStream (new File(dir,fileName));
            //InputStream inputStream = context.openFileInput(folderPath+"/"+fileName);

            if ( fis != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();

                String studentsDetails = "";
                String teachersDetails = "";
                String classesDetails = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //stringBuilder.append(receiveString);

                    // check for eachschooldetails
                    if(!receiveString.isEmpty()) {
                        if (receiveString.split("@")[0].equals("EACHSCHOOL")) {
                                eachSchoolDetails.add(receiveString);
                        } else {

                            if(receiveString.split("@")[0].equals("STUDENT")){
                                studentsDetails = receiveString;
                            } else if(receiveString.split("@")[0].equals("TEACHER")){
                                teachersDetails= receiveString;
                            } else {
                                classesDetails= receiveString;
                            }
                        }
                    }


                }

                overallDetails.add(studentsDetails);
                overallDetails.add(teachersDetails);
                overallDetails.add(classesDetails);

                combinedDetails.add(overallDetails);
                combinedDetails.add(eachSchoolDetails);

                fis.close();
                //ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(">> ", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(">> ", "Can not read file: " + e.toString());
        }

        return combinedDetails;
    }

    /*
       Read MDM data from  file
     */
    public static List<ArrayList> getDataFromMDMFile(String fileName,Context context) {

        List<ArrayList> combinedDetails = new ArrayList<ArrayList>();

        ArrayList<String> overallDetails = new ArrayList<String>();
        ArrayList<String> eachSchoolDetails = new ArrayList<String>();

        try {
            File dir = new File(folderName);
            FileInputStream fis = new FileInputStream (new File(dir,fileName));
            //InputStream inputStream = context.openFileInput(folderPath+"/"+fileName);

            if ( fis != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();

                String studentsDetails = "";
                String ricestockDetails = "";

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //stringBuilder.append(receiveString);

                    // check for eachschooldetails
                    if(!receiveString.isEmpty()) {
                        if (receiveString.split("@")[0].equals("EACHSCHOOL")) {
                            eachSchoolDetails.add(receiveString);
                        } else {

                            if(receiveString.split("@")[0].equals("STUDENT")){
                                studentsDetails = receiveString;
                            } else if(receiveString.split("@")[0].equals("RICESTOCK")){
                                ricestockDetails= receiveString;
                            }
                        }
                    }


                }

                overallDetails.add(studentsDetails);
                overallDetails.add(ricestockDetails);

                combinedDetails.add(overallDetails);
                combinedDetails.add(eachSchoolDetails);

                fis.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(">> ", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(">> ", "Can not read file: " + e.toString());
        }

        return combinedDetails;
    }

    /*
    Read School Details data from  file
     */
    public static ArrayList<String> getDataFromSchoolFile(String fileName,Context context) {

        ArrayList<String> details = new ArrayList<String>();

        try {
            File dir = new File(folderName);
            FileInputStream fis = new FileInputStream (new File(dir,fileName));
            //InputStream inputStream = context.openFileInput(folderPath+"/"+fileName);

            if ( fis != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //stringBuilder.append(receiveString);

                    // check for eachschooldetails
                    if(!receiveString.isEmpty()) {
                        if (receiveString.split("@")[0].equals("SCHOOL")) {
                            details.add(receiveString.split("@")[1]);
                        }
                    }


                }

                fis.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(">> ", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(">> ", "Can not read file: " + e.toString());
        }

        return details;
    }

    /*
    Read Task Details data from  file
     */
    public static List<ArrayList> getDataFromTaskFile(String fileName,Context context) {

        List<ArrayList> combinedDetails = new ArrayList<ArrayList>();

        ArrayList<String> overallDetails = new ArrayList<String>();
        ArrayList<String> eachSchoolDetails = new ArrayList<String>();

        try {
            File dir = new File(folderName);
            FileInputStream fis = new FileInputStream (new File(dir,fileName));
            //InputStream inputStream = context.openFileInput(folderPath+"/"+fileName);

            if ( fis != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //stringBuilder.append(receiveString);

                    // check for eachschooldetails
                    if(!receiveString.isEmpty()) {
                        if (receiveString.split("@")[0].equals("TASK")) {
                            overallDetails.add(receiveString.split("@")[1]);
                        } else if(receiveString.split("@")[0].equals("EACHDETAILS")){
                            eachSchoolDetails.add(receiveString.split("@")[1]);
                        }
                    }


                }

                combinedDetails.add(overallDetails);
                combinedDetails.add(eachSchoolDetails);

                fis.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(">> ", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(">> ", "Can not read file: " + e.toString());
        }

        return combinedDetails;
    }

    /*
    Read School locations data from  file
     */
    public static String getDataFromlocationFile(String fileName,Context context) {

        String details = "";

        try {
            File dir = new File(folderName);
            FileInputStream fis = new FileInputStream (new File(dir,fileName));
            //InputStream inputStream = context.openFileInput(folderPath+"/"+fileName);

            if ( fis != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                //StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    //stringBuilder.append(receiveString);

                    // check for eachschooldetails
                    if(!receiveString.isEmpty()) {
                        if (receiveString.split("@")[0].equals("MAPS")) {
                            details = details + receiveString.split("@")[1] + "%";
                        }
                    }


                }

                fis.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e(">> ", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(">> ", "Can not read file: " + e.toString());
        }

        return details;
    }

}