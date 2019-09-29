package yzw.ahaqth.accountbag.operators;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public final class FileOperator {
    public static File cacheDir;
    public static File fileDir;
    public static File externalCacheDir;
    public static File externalFileDir;
    public static File imageDir;
    public static File backupDir;

    public static void initialAppDir(Context context) {
        cacheDir = context.getCacheDir();
        fileDir = context.getFilesDir();
        externalCacheDir = context.getExternalCacheDir();
        externalFileDir = context.getExternalFilesDir(null);
        imageDir = new File(externalFileDir, "images_" + SetupOperator.getPhoneId());
        backupDir = new File(Environment.getExternalStorageDirectory() + File.separator + "yzw.ahaqth.accountbag" + File.separator + "Backup");
        createDirs(imageDir, backupDir);
    }

    public static void createDirs(File...dir) {
        if (dir == null || dir.length == 0)
            return;
        for (File d : dir) {
            if (!d.exists()) {
                d.mkdirs();
            }
        }
    }

    public static void createDirs(String...dir) {
        if (dir == null || dir.length == 0)
            return;
        File[] files = new File[dir.length];
        for (int i = 0; i < dir.length; i++) {
            files[i] = new File(dir[i]);
        }
        createDirs(files);
    }
}
