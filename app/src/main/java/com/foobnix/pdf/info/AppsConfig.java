package com.foobnix.pdf.info;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppsConfig {
    //FIXME:
    public static boolean IS_LOG =
            false;/*
            Build.MODEL.startsWith("Android SDK")
            || Build.DEVICE.contains("emulator")
            || Build.MODEL.contains("sdk_gphone64_x86_64");*/
    public final static ExecutorService executorService = Executors.newFixedThreadPool(100); //2
}
