package com.demo.tavish.hemantapp.Utils;


import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class ExportToXLS {

    private static final String TAG = "ExportToXLS";

    public static boolean exportToXLS(Context context, HashMap<String, Cursor> CursorMap, String filename){

        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)&&getAvailableStorage(context)>1000000) {
            return false;
        }
       /* File file;
        File sdCardDir = Environment.getExternalStorageDirectory();
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsoluteFile()+"/tavishHemant");
       *//* File imgFile = new File(mFolder.getAbsolutePath());*//*
        //file = new File(mFolder, String.valueOf(imgFile));
        if (!dir.exists()) {
            sdCardDir.mkdirs();
        }

        if (dir.exists()) {
            dir.delete();
        }*/
        File file;
        File sdCardDir = Environment.getExternalStorageDirectory();
        file = new File(sdCardDir, filename);
        if (!sdCardDir.exists()) {
            sdCardDir.mkdirs();
        }

        if (file.exists()) {
            file.delete();
        }



        try{
            WritableWorkbook wwb;
            FileOutputStream os = new FileOutputStream(file);
            wwb = Workbook.createWorkbook(os);

            int n = 0;
            for(Map.Entry<String, Cursor> entry : CursorMap.entrySet()){
                System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
                WritableSheet sheet = wwb.createSheet(entry.getKey(), n);
                WriteToSheet(entry.getValue(), sheet);
                n++;
            }

            wwb.write();
            wwb.close();
            wwb = null;

            String[] paths = new String[1];
            paths[0] = file.getPath();
            MediaScannerConnection.scanFile(context, paths, null, null);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (WriteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void WriteToSheet(Cursor c, WritableSheet sheet) throws WriteException{
        Label label;
        for (int i = 0; i < c.getColumnCount(); i++) {
            label = new Label(i, 0, c.getColumnName(i), getHeader());
            sheet.addCell(label);
        }

        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);

            for (int j = 0; j<c.getColumnCount(); j++) {
                Label cell = new Label(j,i+1, c.getString(j));
                sheet.addCell(cell);
            }
        }

        if (c != null) {
            c.close();
            c = null;
        }
    }


    public static long getAvailableStorage(Context context) {

        String root = context.getExternalFilesDir(null).getPath();
        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }

    public static WritableCellFormat getHeader() {
        WritableFont font = new WritableFont(WritableFont.TIMES, 10,
                WritableFont.BOLD);
        try {
            font.setColour(Colour.BLUE);
        } catch (WriteException e1) {
            e1.printStackTrace();
        }
        WritableCellFormat format = new WritableCellFormat(font);
        try {
            format.setAlignment(jxl.format.Alignment.CENTRE);
            format.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
            format.setBorder(Border.ALL, BorderLineStyle.THIN,
                    Colour.BLACK);
            format.setBackground(Colour.YELLOW);
        } catch (WriteException e) {
            e.printStackTrace();
        }
        return format;
    }
}

