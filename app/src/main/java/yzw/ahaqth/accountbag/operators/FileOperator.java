package yzw.ahaqth.accountbag.operators;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class FileOperator {
    public static File cacheDir;
    public static File fileDir;
    public static File externalCacheDir;
    public static File externalFileDir;
    public static File imageDir;
    public static File backupDir;
    public static File sdCardDir;
    public static File imageCacheDir;
    public static File compressedImageDir;

    public static void initialAppDir(Context context) {
        cacheDir = context.getCacheDir();
        fileDir = context.getFilesDir();
        externalCacheDir = context.getExternalCacheDir();
        imageCacheDir = new File(externalCacheDir,"image_cache");
        compressedImageDir = new File(externalCacheDir,"image_compressed");
        externalFileDir = context.getExternalFilesDir(null);
        sdCardDir = Environment.getExternalStorageDirectory();
        imageDir = new File(externalFileDir, "images/" + SetupOperator.getPhoneId());
        backupDir = new File(Environment.getExternalStorageDirectory() + File.separator + "yzw.ahaqth.accountbag" + File.separator + "Backup");
        createDirs(imageDir,imageCacheDir,compressedImageDir,backupDir);
        clearFiles(cacheDir);
        clearExternalCacheDir();
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

    public static String[] getFileList(File path, String endName) {
        String[] result = new String[0];
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null && files.length > 0) {
                List<String> fileNameList = new ArrayList<>();
                for (File file : files) {
                    if (file.isFile()) {
                        String name = file.getName();
                        if (name.endsWith(endName)) {
                            fileNameList.add(name);
                        }
                    }
                }
                result = fileNameList.toArray(new String[0]);
            }
        }
        return result;
    }

    public static String[] getFileList(String pathname, String endName) {
        File path = new File(pathname);
        return getFileList(path, endName);
    }

    public static void deleAllFiles(File dir, String endName) {
        if (dir == null || !dir.exists())
            return;
        if (dir.isFile() && dir.getName().endsWith(endName)) {
            dir.delete();
        } else {
            File[] files = dir.listFiles();
            if (files == null)
                return;
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(endName))
                    file.delete(); // 删除所有文件
                else if (file.isDirectory())
                    deleAllFiles(file, endName); // 递规的方式删除文件夹
            }
        }
    }

    public static boolean clearFiles(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        return clearFiles(new File(path));
    }

    public static boolean clearFiles(File dir) {
        boolean b = true;
        if (dir == null || !dir.exists())
            return false;
        if (dir.isFile()) {
            b = dir.delete();
        } else {
            File[] files = dir.listFiles();
            if (files == null)
                b = false;
            else {
                for (File file : files) {
                    if (file.isFile())
                        b = file.delete(); // 删除所有文件
                    else if (file.isDirectory())
                        b = clearFilesAndDir(file); // 递规的方式删除文件夹
                }
            }
        }
        return b;
    }

    public static boolean clearFilesAndDir(File dir) {
        boolean b;
        b = clearFiles(dir);
        if(b)
            b = dir.delete();// 删除目录本身
        return b;
    }

    public static void clearExternalCacheDir(){
        File[] files = externalCacheDir.listFiles();
        if(files == null || files.length == 0)
            return;
        for(File file:files){
            if(file.isFile()){
                file.delete();
            }else{
                clearFiles(file);
            }
        }
    }
}
