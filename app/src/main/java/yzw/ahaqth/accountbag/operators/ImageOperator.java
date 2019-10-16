package yzw.ahaqth.accountbag.operators;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import yzw.ahaqth.accountbag.modules.ImageRecord;
import yzw.ahaqth.accountbag.tools.EncryptAndDecrypt;

public final class ImageOperator {
    static String TAG = "殷宗旺";

    public static File imageCopyToCache(Context context, Uri uri) throws IOException {
        File outFile = new File(FileOperator.imageCacheDir, System.currentTimeMillis() + ".jpg");
        if (outFile.exists())
            outFile.delete();
        outFile.createNewFile();
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
        byte[] buffer = new byte[1024 * 8];
        int count;
        while ((count = inputStream.read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, count);
            fileOutputStream.flush();
        }
        fileOutputStream.close();
        inputStream.close();
        return outFile;
    }

//    public static File imageCopyToCache(Bitmap bitmap) throws IOException {
//        File outFile = new File(FileOperator.imageCacheDir, System.currentTimeMillis() + ".jpg");
//        if (outFile.exists())
//            outFile.delete();
//        outFile.createNewFile();
//        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
//        fileOutputStream.flush();
//        fileOutputStream.close();
//        return outFile;
//    }

    public static boolean saveImageFile(ImageRecord imageRecord) {
        String fileName = imageRecord.getImageFileName();
        File cacheFile = new File(imageRecord.getPath(), fileName);
        fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".jmima";
        File saveFile = new File(FileOperator.imageDir, fileName);
        if(EncryptAndDecrypt.encryptImage(cacheFile.getAbsolutePath(),saveFile.getAbsolutePath())) {
            cacheFile.delete();
            imageRecord.setPath(FileOperator.imageDir.getAbsolutePath());
            imageRecord.setImageFileName(fileName);
            return true;
        }else
            return false;

    }

    public static String getRealImagePath(ImageRecord imageRecord){
        String fileName = imageRecord.getImageFileName();
        fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".tmp";
        return FileOperator.imageCacheDir + File.separator + fileName;
    }

    public static Bitmap getRealImage(ImageRecord imageRecord){
        Bitmap bitmap = null;
        if(imageRecord.isSaved()){
            String fileName = imageRecord.getImageFileName();
            fileName = fileName.substring(0,fileName.lastIndexOf(".")) + ".tmp";
            String originPath = imageRecord.getPath() + File.separator + imageRecord.getImageFileName();
            String outPath = FileOperator.imageCacheDir + File.separator + fileName;
            if(EncryptAndDecrypt.decryptImage(originPath,outPath)){
                bitmap = BitmapFactory.decodeFile(outPath);
            }
        }else{
           String imageFile = imageRecord.getPath()+File.separator+imageRecord.getImageFileName();
           bitmap = BitmapFactory.decodeFile(imageFile);
        }
        return bitmap;
    }

    public static void deleImageFile(ImageRecord imageRecord) {
        File image = new File(imageRecord.getPath(), imageRecord.getImageFileName());
        if (image.exists())
            image.delete();
    }
}
