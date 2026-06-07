package com.example.sparkchaindemo.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileUtils {
    private static final int MAX_LOG_LENGTH = 4000;
    public static void writeFile(String path, byte[] bytes) {
        boolean append = false;
        try {
            File file = new File(path);
            if (file.exists()) {
                append = true;
            }else {
                file.createNewFile();
            }
            FileOutputStream out = new FileOutputStream(path,append);
            FileChannel fileChannel = out.getChannel();
            fileChannel.write(ByteBuffer.wrap(bytes));
            fileChannel.force(true);
            fileChannel.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        }
    }

    public static String getSDPath(){
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if(sdCardExist)
        {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.toString();
    }

    public static void longLog(String tag, String message) {
        if (message.length() > MAX_LOG_LENGTH) {
            int chunkCount = message.length() / MAX_LOG_LENGTH;
            for (int i = 0; i <= chunkCount; i++) {
                int max = MAX_LOG_LENGTH * (i + 1);
                if (max >= message.length()) {
                    Log.d(tag, message.substring(MAX_LOG_LENGTH * i));
                } else {
                    Log.d(tag, message.substring(MAX_LOG_LENGTH * i, max));
                }
            }
        } else {
            Log.d(tag, message);
        }
    }


    public static boolean deleteDirectory(String filePath) {
        if (!filePath.endsWith(File.separator))
            filePath = filePath + File.separator;
        File dirFile = new File(filePath);
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                flag = file.delete();
                if (!flag) break;
            }
            else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        return dirFile.delete();
    }



    public static void saveToFile(String fileName,String inputText) {
        File file = new File(fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(inputText.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
