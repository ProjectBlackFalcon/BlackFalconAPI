package com.ankamagames.dofus.util;

public class ThreadUtils {

    public static void sleep(final int second){
        try {
            Thread.sleep(1000 * second);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(final long milli){
        try {
            Thread.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
