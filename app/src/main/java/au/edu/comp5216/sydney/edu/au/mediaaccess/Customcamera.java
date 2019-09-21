package au.edu.comp5216.sydney.edu.au.mediaaccess;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static au.edu.comp5216.sydney.edu.au.mediaaccess.ViewImage.IMAGE_STORED_RESULT_CODE_FAIL;
import static au.edu.comp5216.sydney.edu.au.mediaaccess.ViewImage.IMAGE_STORED_RESULT_CODE_SUCCESS;

public class Customcamera extends Activity implements SurfaceHolder.Callback{
    private Camera mCamera;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    MarshmallowPermission marshmallowPermission = new MarshmallowPermission(this);
    public final int VIEM_ITEM_REQUEST_CODE = 647;
    private static final int MY_PERMISSIONS_REQUEST_OPEN_CAMERA = 101;
    private int cameraId=0;//声明cameraId属性，ID为1调用前置摄像头，为0调用后置摄像头。此处因有特殊需要故调用前置摄像头
    //定义照片保存并显示的方法
    private Camera.PictureCallback mpictureCallback=new Camera.PictureCallback(){

        @Override
        public void onPictureTaken(byte[] data,Camera camera){

            //File tempfile=new File("/sdcard/emp.png");//新建一个文件对象tempfile，并保存在某路径中
            String cache_dir = getBaseContext().getCacheDir().getPath();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            String file_name = "IMG_" + timeStamp + ".jpg";
            File temp = new File(cache_dir+"/"+file_name);
            try{ FileOutputStream fos =new FileOutputStream(temp);
                fos.write(data);//将照片放入文件中
                fos.close();//关闭文件
                Intent intent=new Intent(Customcamera.this,ViewImage.class);//新建信使对象

                intent.putExtra("img",temp.getAbsolutePath());//打包文件给信使
                intent.putExtra("file_name",file_name);
                //finish();
                startActivityForResult(intent,VIEM_ITEM_REQUEST_CODE);//打开新的activity，即打开展示照片的布局界面
                //Customcamera.this.finish();//关闭现有界面
                //finish();
            }
            catch (IOException e){e.printStackTrace();}
            //finish();
        }
    };


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom);
        mPreview=findViewById(R.id.preview);//初始化预览界面
        mHolder=mPreview.getHolder();
        mHolder.addCallback(this);
        //点击预览界面聚焦
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });
    }
    //定义“拍照”方法
    public void capture(View view){
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);//设置照片格式
        parameters.setPreviewSize(800,400);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //摄像头聚焦
        mCamera.autoFocus(new Camera.AutoFocusCallback(){
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){mCamera.takePicture(null,null, mpictureCallback);}
            }
        });

    }
    //activity生命周期在onResume是界面应是显示状态
    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera==null){//如果此时摄像头值仍为空
            mCamera=getCamera();//则通过getCamera()方法开启摄像头
            if(mHolder!=null){
                setStartPreview(mCamera,mHolder);//开启预览界面
            }
        }
    }
//    //activity暂停的时候释放摄像头
//    @Override
//    protected void onPause() {
//        super.onPause();
//        releaseCamera();
//    }
    //onResume()中提到的开启摄像头的方法
    private Camera getCamera(){
        Camera camera;//声明局部变量camera
        try{
            camera=Camera.open(cameraId);}//根据cameraId的设置打开前置摄像头
        catch (Exception e){
            camera=null;
            e.printStackTrace(); }
        return camera;
    }
    //开启预览界面
    private void setStartPreview(Camera camera,SurfaceHolder holder){
        try{
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(90);//如果没有这行你看到的预览界面就会是水平的
            camera.startPreview();}
        catch (Exception e){
            e.printStackTrace(); }
    }
    //定义释放摄像头的方法
    private void releaseCamera(){
        if(mCamera!=null){//如果摄像头还未释放，则执行下面代码
            mCamera.stopPreview();//1.首先停止预览
            mCamera.setPreviewCallback(null);//2.预览返回值为null
            mCamera.release(); //3.释放摄像头
            mCamera=null;//4.摄像头对象值为null
        }
    }
    //定义新建预览界面的方法
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera,mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();//如果预览界面改变，则首先停止预览界面
        setStartPreview(mCamera,mHolder);//调整再重新打开预览界面
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();//预览界面销毁则释放相机
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==VIEM_ITEM_REQUEST_CODE&&resultCode == IMAGE_STORED_RESULT_CODE_SUCCESS){
            Toast.makeText(this, "Picture saved!",
                    Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else if(requestCode==VIEM_ITEM_REQUEST_CODE&&resultCode == IMAGE_STORED_RESULT_CODE_FAIL){
            Toast.makeText(this, "Picture fail to save!",
                    Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
}
