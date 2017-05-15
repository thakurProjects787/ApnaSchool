package com.projects.thakur.apnaschool.Common;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.projects.thakur.apnaschool.Auth.StartUpActivity;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.*;
import jxl.write.Number;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class CreateExcelReport {

    static public String folderName = "/sdcard/Files/ApnaSchool";

    //logger
    static public Logger logger;

     /*
        Generate Attendence Report
      */
    static public boolean generateAttndReport(String fileName,Context context) {

        //logger.addRecordToLog("MSG : Write Data");

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("Excel : ", "Storage not available or read only");
            //logger.addRecordToLog("MSG : Storage not available or read only");
            return false;
        }

        boolean success = false;

        File dir = new File(folderName);
        if(!dir.exists()) {
            Log.d("Dir created ", "Dir created ");
            //logger.addRecordToLog("MSG : Dir created");
            dir.mkdirs();
        }

        File reportFile = new File(dir,fileName);
        // delete old files
        if(reportFile.exists()){
            reportFile.delete();
        }

        /*
          Get all details
         */
        List<ArrayList> combinedDetails = logger.getDataFromAttnFile("dataCal.txt",context);

        if(combinedDetails.get(0).size() == 0 ){
            return false;
        }

        String studentsDetails = combinedDetails.get(0).get(0).toString().split("@")[1];
        String teachersDetails = combinedDetails.get(0).get(1).toString().split("@")[1];
        String[] classesDetails = combinedDetails.get(0).get(2).toString().split("@")[1].split("%");

        ArrayList<String> eachSchools = combinedDetails.get(1);





        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("en", "EN"));

        //1. Create an Excel file
        WritableWorkbook myFirstWbook = null;
        try {

            myFirstWbook = Workbook.createWorkbook(new File(dir,fileName));

            // create an Excel sheet
            WritableSheet excelSheet1 = myFirstWbook.createSheet("Combined Status", 0);

            WritableFont arial12ptBold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat arial12BoldFormat = new WritableCellFormat(arial12ptBold);
            arial12BoldFormat.setWrap(true);
            arial12BoldFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            arial12BoldFormat.setBackground(Colour.YELLOW);

            // submitted background
            WritableFont submittedformatpt = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat submittedformat = new WritableCellFormat(submittedformatpt);
            submittedformat.setWrap(true);
            submittedformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            submittedformat.setBackground(Colour.GREEN);

            // not submitted background
            WritableFont notsubmittedformatpt = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat notsubmittedformat = new WritableCellFormat(notsubmittedformatpt);
            notsubmittedformat.setWrap(true);
            notsubmittedformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            notsubmittedformat.setBackground(Colour.RED);


            WritableCellFormat integerformat = new WritableCellFormat(NumberFormats.INTEGER);
            integerformat.setWrap(true);
            integerformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

            WritableFont arial12pt = new WritableFont(WritableFont.ARIAL, 12);
            WritableCellFormat arial12format = new WritableCellFormat(arial12pt);
            arial12format.setWrap(true);
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

            // column , row - format
            Label label = new Label(0, 3, "Category", arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(1, 3, "Total" , arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(2, 3, "Present" , arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(3, 3, "Absents" , arial12BoldFormat);
            excelSheet1.addCell(label);

            // add students vaules
            label = new Label(0, 4, "Student's" , arial12BoldFormat);
            excelSheet1.addCell(label);

            Number number = new Number(1, 4, Double.parseDouble(studentsDetails.split("#")[0]) ,integerformat);
            excelSheet1.addCell(number);

            number = new Number(2, 4, Double.parseDouble(studentsDetails.split("#")[1]) ,integerformat);
            excelSheet1.addCell(number);

            number = new Number(3, 4, Double.parseDouble(studentsDetails.split("#")[2]) ,integerformat);
            excelSheet1.addCell(number);

            // add Teachers vaules
            label = new Label(0, 5, "Teachers's" , arial12BoldFormat);
            excelSheet1.addCell(label);

            number = new Number(1, 5, Double.parseDouble(teachersDetails.split("#")[0]) ,integerformat);
            excelSheet1.addCell(number);

            number = new Number(2, 5, Double.parseDouble(teachersDetails.split("#")[1]) ,integerformat);
            excelSheet1.addCell(number);

            number = new Number(3, 5, Double.parseDouble(teachersDetails.split("#")[2]) ,integerformat);
            excelSheet1.addCell(number);


            // add classes details
            int rowno = 6;
            for (String eachclass: classesDetails) {
                rowno = rowno + 1;

                label = new Label(0, rowno, eachclass.split("#")[0] , arial12BoldFormat);
                excelSheet1.addCell(label);

                number = new Number(1, rowno, Double.parseDouble(eachclass.split("#")[1]) ,integerformat);
                excelSheet1.addCell(number);

                number = new Number(2, rowno, Double.parseDouble(eachclass.split("#")[2]) ,integerformat);
                excelSheet1.addCell(number);

                number = new Number(3, rowno, Double.parseDouble(eachclass.split("#")[3]) ,integerformat);
                excelSheet1.addCell(number);

            }

            rowno = 6;

            // Update Each school details
            WritableSheet eachSchoolSheet = myFirstWbook.createSheet("Each School Details", 1);

            // column , row - format
            Label label2 = new Label(0, 3, "School ID" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(1, 3, "School Details" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(2, 3, "Students" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(3, 3, "Teachers" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(4, 3, "Classes" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            int sch_rowno = 4;

            Iterator<String> iterator = eachSchools.iterator();
            while (iterator.hasNext()) {
               String details = iterator.next();

                sch_rowno = sch_rowno + 1;

                details = details.replace("EACHSCHOOL@","");

                String school_id = "";
                String school_Details = "";

                String teachers_Details = "";
                String students_details = "";
                String classess_details = "";

                WritableCellFormat cellFormat;

                // check submitted schools and pending schools details
                if(details.split("&")[1].equals("NS")){

                    school_id = details.split("&")[0].split("#")[0];
                    school_Details = "Name : "+details.split("&")[0].split("#")[1]+" \nDistrict : " + details.split("&")[0].split("#")[2] + "\nEmail ID : " + details.split("&")[0].split("#")[3];


                    teachers_Details = "Not Submitted";
                    students_details = "Not Submitted";
                    classess_details = "Not Submitted";
                    cellFormat = notsubmittedformat;
                } else {

                    school_id = details.split("&")[0].split("#")[0];
                    school_Details = "Name : "+details.split("&")[0].split("#")[1]+" \nDistrict : " + details.split("&")[0].split("#")[2] + "\nEmail ID : " + details.split("&")[0].split("#")[3];

                    teachers_Details = details.split("&")[1].replace("#"," - ");
                    students_details = details.split("&")[2].replace("#"," - ");
                    classess_details = details.split("&")[3].replace("%","\n").replace("#"," - ");

                    cellFormat = submittedformat;
                }



                label2 = new Label(0, sch_rowno, school_id , cellFormat);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(1, sch_rowno, school_Details , arial12format);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(2, sch_rowno, teachers_Details, arial12format);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(3, sch_rowno, students_details, arial12format);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(4, sch_rowno, classess_details, arial12format);
                eachSchoolSheet.addCell(label2);


            }

            // write all data into workbook
            myFirstWbook.write();

            success = true;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }

        // Send MAIL
        String userBody="Dear User"+"\n \n Please Find Attendence Excel sheet in attachments.\n " +
                "\n\n Thanks \n School Trace. ";

        String userSub="Attendence Details!";

        String[] emaildetails={new StartUpActivity().userDetails.getSchool_emailID()};

        new SendMail(userSub, userBody, emaildetails,folderName+"/"+fileName, context).send();

        return success;

    }

    /*
        Generate MDM Report
      */
    static public boolean generateMDMReport(String fileName,Context context) {

        //logger.addRecordToLog("MSG : Write Data");

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("Excel : ", "Storage not available or read only");
            //logger.addRecordToLog("MSG : Storage not available or read only");
            return false;
        }

        boolean success = false;

        File dir = new File(folderName);
        if(!dir.exists()) {
            Log.d("Dir created ", "Dir created ");
            //logger.addRecordToLog("MSG : Dir created");
            dir.mkdirs();
        }

        File reportFile = new File(dir,fileName);
        // delete old files
        if(reportFile.exists()){
            reportFile.delete();
        }

        /*
          Get all details
         */
        List<ArrayList> combinedDetails = logger.getDataFromMDMFile("dataCal.txt",context);

        if(combinedDetails.get(0).size() == 0 ){
            return false;
        }

        String studentsDetails = combinedDetails.get(0).get(0).toString().split("@")[1];
        String riceStockDetails = combinedDetails.get(0).get(1).toString().split("@")[1];

        ArrayList<String> eachSchools = combinedDetails.get(1);





        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("en", "EN"));

        //1. Create an Excel file
        WritableWorkbook myFirstWbook = null;
        try {

            myFirstWbook = Workbook.createWorkbook(new File(dir,fileName));

            // create an Excel sheet
            WritableSheet excelSheet1 = myFirstWbook.createSheet("Combined Status", 0);

            WritableFont arial12ptBold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat arial12BoldFormat = new WritableCellFormat(arial12ptBold);
            arial12BoldFormat.setWrap(true);
            arial12BoldFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            arial12BoldFormat.setBackground(Colour.YELLOW);

            // submitted background
            WritableFont submittedformatpt = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat submittedformat = new WritableCellFormat(submittedformatpt);
            submittedformat.setWrap(true);
            submittedformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            submittedformat.setBackground(Colour.GREEN);

            // not submitted background
            WritableFont notsubmittedformatpt = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat notsubmittedformat = new WritableCellFormat(notsubmittedformatpt);
            notsubmittedformat.setWrap(true);
            notsubmittedformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            notsubmittedformat.setBackground(Colour.RED);


            WritableCellFormat integerformat = new WritableCellFormat(NumberFormats.INTEGER);
            integerformat.setWrap(true);
            integerformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

            WritableFont arial12pt = new WritableFont(WritableFont.ARIAL, 12);
            WritableCellFormat arial12format = new WritableCellFormat(arial12pt);
            arial12format.setWrap(true);
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

            // column , row - format
            Label label = new Label(0, 3, " ", arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(1, 3, "Total" , arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(2, 3, "Present" , arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(3, 3, "Absents" , arial12BoldFormat);
            excelSheet1.addCell(label);

            // add students vaules
            label = new Label(0, 4, "Student's" , arial12BoldFormat);
            excelSheet1.addCell(label);

            Number number = new Number(1, 4, Double.parseDouble(studentsDetails.split("#")[0]) ,integerformat);
            excelSheet1.addCell(number);

            number = new Number(2, 4, Double.parseDouble(studentsDetails.split("#")[1]) ,integerformat);
            excelSheet1.addCell(number);

            number = new Number(3, 4, Double.parseDouble(studentsDetails.split("#")[2]) ,integerformat);
            excelSheet1.addCell(number);


            // Rice Stock details
            // column , row - format
            label = new Label(0, 6, " ", arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(1, 6, "Today Used" , arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(2, 6, "Stock" , arial12BoldFormat);
            excelSheet1.addCell(label);


            // add students vaules
            label = new Label(0, 7, "RICE" , arial12BoldFormat);
            excelSheet1.addCell(label);

            number = new Number(1, 7, Double.parseDouble(riceStockDetails.split("#")[0]) ,integerformat);
            excelSheet1.addCell(number);

            number = new Number(2, 7, Double.parseDouble(riceStockDetails.split("#")[1]) ,integerformat);
            excelSheet1.addCell(number);






            // Update Each school details
            WritableSheet eachSchoolSheet = myFirstWbook.createSheet("Each School Details", 1);

            // column , row - format
            Label label2 = new Label(0, 3, "School ID" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(1, 3, "School Details" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(2, 3, "Students" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(3, 3, "Rice" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(4, 3, "MDM Menu" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            int sch_rowno = 4;

            Iterator<String> iterator = eachSchools.iterator();
            while (iterator.hasNext()) {
                String details = iterator.next();

                sch_rowno = sch_rowno + 1;

                details = details.replace("EACHSCHOOL@","");

                String school_id = "";
                String school_Details = "";

                String students_details = "";
                String ricestock_Details = "";
                String mdmmenu_details = "";

                WritableCellFormat cellFormat;

                // check submitted schools and pending schools details
                if(details.split("&")[1].equals("NS")){

                    school_id = details.split("&")[0].split("#")[0];
                    school_Details = "Name : "+details.split("&")[0].split("#")[1]+" \nDistrict : " + details.split("&")[0].split("#")[2] + "\nEmail ID : " + details.split("&")[0].split("#")[3];


                    ricestock_Details = "Not Submitted";
                    students_details = "Not Submitted";
                    mdmmenu_details = "Not Submitted";
                    cellFormat = notsubmittedformat;
                } else {

                    school_id = details.split("&")[0].split("#")[0];
                    school_Details = "Name : "+details.split("&")[0].split("#")[1]+" \nDistrict : " + details.split("&")[0].split("#")[2] + "\nEmail ID : " + details.split("&")[0].split("#")[3];

                    students_details = "T : "+details.split("&")[1].split("#")[0]+"\nP : "+details.split("&")[1].split("#")[1]+"\nA : "+details.split("&")[1].split("#")[2];
                    ricestock_Details = "Used : "+details.split("&")[2].split("#")[0]+"\nStock : "+details.split("&")[2].split("#")[1];
                    mdmmenu_details = details.split("&")[3].replace("#","\n");

                    cellFormat = submittedformat;
                }



                label2 = new Label(0, sch_rowno, school_id , cellFormat);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(1, sch_rowno, school_Details , arial12format);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(2, sch_rowno, students_details, arial12format);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(3, sch_rowno, ricestock_Details, arial12format);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(4, sch_rowno, mdmmenu_details, arial12format);
                eachSchoolSheet.addCell(label2);


            }

            // write all data into workbook
            myFirstWbook.write();

            success = true;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }

        // Send MAIL
        String userBody="Dear User"+"\n \n Please Find MDM Excel sheet in attachments.\n " +
                "\n\n Thanks \n School Trace. ";

        String userSub="MDM Report";

        String[] emaildetails={new StartUpActivity().userDetails.getSchool_emailID()};

        new SendMail(userSub, userBody, emaildetails,folderName+"/"+fileName, context).send();

        return success;

    }

    /*
        Generate Question Task Report
      */
    static public boolean generateQuestionTaskReport(String fileName,Context context) {

        //logger.addRecordToLog("MSG : Write Data");

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("Excel : ", "Storage not available or read only");
            //logger.addRecordToLog("MSG : Storage not available or read only");
            return false;
        }

        boolean success = false;

        File dir = new File(folderName);
        if(!dir.exists()) {
            Log.d("Dir created ", "Dir created ");
            //logger.addRecordToLog("MSG : Dir created");
            dir.mkdirs();
        }

        File reportFile = new File(dir,fileName);
        // delete old files
        if(reportFile.exists()){
            reportFile.delete();
        }

        /*
          Get all details
         */
        List<ArrayList> combinedDetails = logger.getDataFromTaskFile("TaskData.txt",context);

        if(combinedDetails.get(0).size() == 0 ){
            return false;
        }

        String taskDetails = combinedDetails.get(0).get(0).toString();

        ArrayList<String> eachSchools = combinedDetails.get(1);

        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale(new Locale("en", "EN"));

        //1. Create an Excel file
        WritableWorkbook myFirstWbook = null;
        try {

            myFirstWbook = Workbook.createWorkbook(new File(dir,fileName));

            // create an Excel sheet
            WritableSheet excelSheet1 = myFirstWbook.createSheet("Combined Status", 0);

            WritableFont arial12ptBold = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat arial12BoldFormat = new WritableCellFormat(arial12ptBold);
            arial12BoldFormat.setWrap(true);
            arial12BoldFormat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            arial12BoldFormat.setBackground(Colour.YELLOW);

            // submitted background
            WritableFont submittedformatpt = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat submittedformat = new WritableCellFormat(submittedformatpt);
            submittedformat.setWrap(true);
            submittedformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            submittedformat.setBackground(Colour.GREEN);

            // not submitted background
            WritableFont notsubmittedformatpt = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
            WritableCellFormat notsubmittedformat = new WritableCellFormat(notsubmittedformatpt);
            notsubmittedformat.setWrap(true);
            notsubmittedformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            notsubmittedformat.setBackground(Colour.RED);


            WritableCellFormat integerformat = new WritableCellFormat(NumberFormats.INTEGER);
            integerformat.setWrap(true);
            integerformat.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

            WritableFont arial12pt = new WritableFont(WritableFont.ARIAL, 12);
            WritableCellFormat arial12format = new WritableCellFormat(arial12pt);
            arial12format.setWrap(true);
            arial12format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);

            // column , row - format
            Label label = new Label(2, 4, "Heading" , arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(2, 5, "Details" , arial12BoldFormat);
            excelSheet1.addCell(label);

            label = new Label(2, 6, "Date" , arial12BoldFormat);
            excelSheet1.addCell(label);

            // add students vaules
            label = new Label(3, 4, taskDetails.split("&")[0] , arial12format);
            excelSheet1.addCell(label);

            label = new Label(3, 5, taskDetails.split("&")[2] , arial12format);
            excelSheet1.addCell(label);

            label = new Label(3, 6, taskDetails.split("&")[1] , arial12format);
            excelSheet1.addCell(label);



            // Update Each school details
            WritableSheet eachSchoolSheet = myFirstWbook.createSheet("Each School Details", 1);

            // column , row - format
            Label label2 = new Label(0, 3, "School ID" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(1, 3, "School Details" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            label2 = new Label(2, 3, "Answer" , arial12BoldFormat);
            eachSchoolSheet.addCell(label2);

            int sch_rowno = 4;

            Iterator<String> iterator = eachSchools.iterator();
            while (iterator.hasNext()) {
                String details = iterator.next();

                sch_rowno = sch_rowno + 1;

                String school_id = "";
                String school_Details = "";

                String answer = "";

                WritableCellFormat cellFormat;

                // check submitted schools and pending schools details
                if(details.split("#")[1].equals("NA")){

                    school_id = details.split("#")[0].split("&")[0];
                    school_Details = "Name : "+details.split("#")[0].split("&")[1]+" \nPlace : " + details.split("#")[0].split("&")[2] + "\nDistrict : " + details.split("#")[0].split("&")[3];


                    answer = "Not Submitted";

                    cellFormat = notsubmittedformat;
                } else {

                    school_id = details.split("#")[0].split("&")[0];
                    school_Details = "Name : "+details.split("#")[0].split("&")[1]+" \nPlace : " + details.split("#")[0].split("&")[2] + "\nDistrict : " + details.split("#")[0].split("&")[3];

                    answer = details.split("#")[1];

                    cellFormat = submittedformat;
                }



                label2 = new Label(0, sch_rowno, school_id , cellFormat);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(1, sch_rowno, school_Details , arial12format);
                eachSchoolSheet.addCell(label2);

                label2 = new Label(2, sch_rowno, answer, arial12format);
                eachSchoolSheet.addCell(label2);


            }

            // write all data into workbook
            myFirstWbook.write();

            success = true;


        } catch (IOException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        } finally {

            if (myFirstWbook != null) {
                try {
                    myFirstWbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
        }

        // Send MAIL
        String userBody="Dear User"+"\n \n Please Find Task Excel sheet in attachments.\n " +
                "\n\n Thanks \n School Trace. ";

        String userSub="Task Report";

        String[] emaildetails={new StartUpActivity().userDetails.getSchool_emailID()};

        new SendMail(userSub, userBody, emaildetails,folderName+"/"+fileName, context).send();

        return success;

    }


    static public boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    static public boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

}
