package au.edu.comp5216.sydney.edu.au.mediaaccess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewImage extends AppCompatActivity {

    ImageView iv2;
    public static int IMAGE_STORED_RESULT_CODE_SUCCESS = 1001;
    public static int IMAGE_STORED_RESULT_CODE_FAIL = 1002;
    //Context context = this;
    //Bitmap btmp = getIntent().getExtra;
//    byte[] bis;
//            //byte[] bis = getIntent().getByteArrayExtra("bitmap");
////
////    Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
//    Bitmap greybitmap;
    private String image_cache,fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        image_cache = getIntent().getStringExtra("img");
        fileName = getIntent().getStringExtra("file_name");
        //String f = getIntent().getStringExtra("picpath");
        iv2 = (ImageView) findViewById(R.id.imageView2);
        iv2.setImageURI(Uri.parse(image_cache));

    }

    public void onReturnMainClick( View view ){
        //byte[] bis = getIntent().getByteArrayExtra("bitmap");
//        if(bis != null || bis.length!=0){
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
//            saveImageToGallery(context,bitmap);
//        }
//        Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
//        saveImageToGallery(context,bitmap);
        //Customcamera.this.finish();
        //Customcamera.this.finish();
        //startActivity(this,MainActivity.class);
        //finish();
        try {
            String uri = MediaStore.Images.Media.insertImage(this.getContentResolver(),image_cache, fileName, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri contentUri = Uri.parse(uri);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(getRealPathFromURI(this,contentUri)))));
            } else {
                final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                sendBroadcast(intent);
            }
            this.setResult(IMAGE_STORED_RESULT_CODE_SUCCESS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            this.setResult(IMAGE_STORED_RESULT_CODE_FAIL);
        }
        this.finish();
    }

    public void onGreyingClick(View view){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter cf = new ColorMatrixColorFilter(matrix);
        iv2.setColorFilter(cf);
        iv2.setImageAlpha(128);
    }

    public void onUndoClick(View view){
        iv2.setColorFilter(null);
        iv2.setImageAlpha(255);
    }

//    public  void saveImageToGallery(Context context, Bitmap bmp) {
//        // 首先保存图片
//        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
//        String fileName = System.currentTimeMillis() + ".jpg";
//        File file = new File(appDir, fileName);
//        try {
//            FileOutputStream fos = new FileOutputStream(file);
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//            fos.flush();
//            fos.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // 其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),
//                    file.getAbsolutePath(), fileName, null);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 最后通知图库更新
////        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
//        //list = getAllImagePath();
//    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String result = "";
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            cursor.moveToFirst();
            int column_index = cursor.getColumnIndex(proj[0]);
            result = cursor.getString(column_index);
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        clearCache();
        super.onDestroy();
    }

    public void clearCache(){
        File cacheDir = this.getCacheDir();

        File file = new File(cacheDir+"/"+fileName);

        if (file.exists()) {
            file.delete();
        }
    }

}
