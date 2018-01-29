package com.fruitbasket.originf16;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
     //////////////////UI///////////////////////////////
    public static TextView tv;
    private String sRecordStatus = "Init Record";
    public static TextView tvTime100MilliSecond;
    private static String INIT_100_MILL_SECOND = "00:00:0";
    private String s100MillSecond = INIT_100_MILL_SECOND;

    //////////////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv=(TextView)findViewById(R.id.textView2);
        //time init
        tvTime100MilliSecond=(TextView)findViewById(R.id.time_100millisecond);
        tvTime100MilliSecond.setText(s100MillSecond);
        tv.setText(sRecordStatus);
        GlobalConfig.fAbsolutepath.mkdirs();//创建文件夹

        GlobalConfig.stWaveFileUtil.initIQFile();//创建IQTXT文件
        startRecordAction();//开始录音，不用点按钮
        GlobalConfig.stPhaseProxy.init();//处理相位数据
        //initIos();
        if(GlobalConfig.bPlayThreadFlag) {
            ThreadInstantPlay threadInstantPlay = new ThreadInstantPlay();//开启新线程 放音
            //Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
            threadInstantPlay.start();//开启新线程 放音
        }
        else{
            GlobalConfig.isRecording=true;
        }
    }

    class ThreadInstantPlay extends Thread
    {
        @Override
        public void run()
        {
            //Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
            AudioTrackPlay Player= new AudioTrackPlay();
            GlobalConfig.isRecording=true;
            Player.play();

            while (GlobalConfig.isRecording==true){}
            Player.stop();
        }
    }

    private void initIos()
    {
        //GlobalConfig.stWaveFileUtil.readTxtDataToShort(GlobalConfig.stWaveFileUtil.getIosRecordFileName(),GlobalConfig.vIosData);
        for( int i=0; i<GlobalConfig.FRAME_NUM; i++)
        {
            String sFileName = GlobalConfig.stWaveFileUtil.getIosRecordFileNameByFrame(i+1);
            GlobalConfig.stWaveFileUtil.readTxtDataToShort(sFileName,GlobalConfig.vvIosData[i]);
        }
    }
    public void startRecordAction(){
        try {
            //创建临时文件,注意这里的格式为.pcm
            GlobalConfig.fPcmRecordFile = File.createTempFile(GlobalConfig.sRecordPcmFileName, ".pcm", GlobalConfig.fAbsolutepath);
            GlobalConfig.fPcmRecordFile2 = File.createTempFile(GlobalConfig.sRecordPcmFileName2, ".pcm", GlobalConfig.fAbsolutepath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GlobalConfig.stPhaseAudioRecord.initRecord();//真正的启动录音
        sRecordStatus="-------------start Record-----------------";
        tv.setText(sRecordStatus);
    }

    //runs after pressing the record button
    public void startRecording(View view) throws IOException {
        /*short[] recordData1 = new short[512];
        short[] recordData2 = new short[512];
        short iTmp = 0;
        java.util.Random random=new java.util.Random();// 定义随机类
        for(int i = 0;i < 512;i++)
        {
            iTmp = (short)(random.nextInt(65536) - 32767);
            recordData1[i] = iTmp;
            recordData2[i] = iTmp;
        }

        Log.e("Jni", "aaa");
        long lTime = 0;
        PhaseProcessI ppi = new PhaseProcessI(GlobalConfig.MAX_FRAME_SIZE , GlobalConfig.NUM_FREQ, GlobalConfig.START_FREQ, GlobalConfig.FREQ_INTERVAL);
        Log.e("Jni", ppi.getJniString());
        lTime = System.currentTimeMillis();
        float       f               = ppi.getDistanceChange(ppi.nativePerson, recordData1, recordData1.length);

        float[] iqDatas = ppi.getBaseBand(ppi.nativePerson, GlobalConfig.NUM_FREQ);
        for(int i = 0;i < iqDatas.length;i++) {
            if(i % 64 == 0)
            {
                Log.i("bobo", "Line" + String.valueOf(i / 64) + " ");
            }
            Log.i("bobo", "IQ" + String.valueOf(i % 64) + "=" + String.valueOf(iqDatas[i]));
        }


        long lDic1 = System.currentTimeMillis() - lTime;
        lTime = System.currentTimeMillis();
        float       distancechange  = PhaseProxy.stPhaseProcess.GetDistanceChange(recordData2);
        long lDic2 = System.currentTimeMillis() - lTime;
        Log.i("Jni", String.valueOf(f) + "," + String.valueOf(distancechange) + "," + String.valueOf(lDic1) + "," + String.valueOf(lDic2));*/


        startRecordAction();
        //UI
        view.setClickable(false);
        Button btn=(Button)findViewById(R.id.button);
        btn.setClickable(true);
        //judgeEddian();
    }


    public void stopRecordingAction() throws IOException {//关闭之后需要修改的设置
        Log.i("timer","audio Stopped");
        sRecordStatus="!!!!!!!!!!!!!stop Record!!!!!!!!!!!!";
        tv.setText(sRecordStatus);
        //play stop
        //record release
        GlobalConfig.isRecording = false;
        GlobalConfig.stPhaseAudioRecord.stopRecording();
        GlobalConfig.stPhaseProxy.destroy();
        GlobalConfig.stWaveFileUtil.destroy();
    }

    //runs when the stop button is pressed
    public void stopRecording(View view) throws IOException {
        Log.i("audio","Stopped");
        stopRecordingAction();
        //UI
        view.setClickable(false);
        Button btn=(Button)findViewById(R.id.button2);
        btn.setClickable(true);
    }

    //any code below this comment can be neglected
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
