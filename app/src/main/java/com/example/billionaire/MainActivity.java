package com.example.billionaire;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int[] imagesarr = new int[]{
            R.drawable.c1,
            R.drawable.c2,
            R.drawable.c3,
            R.drawable.c4,
            R.drawable.c5,
            R.drawable.c6,
    };
    //add by HGY
    private int[] imageViewInt = new int[]{
            R.drawable.buttom,
            R.drawable.buttom,
            R.drawable.back,
            R.drawable.buttom,
            R.drawable.buttom,
            R.drawable.bomb,
            R.drawable.buttom,
            R.drawable.back,
            R.drawable.buttom,
            R.drawable.buttom,
            R.drawable.forward,
            R.drawable.bomb,
            R.drawable.buttom,
            R.drawable.buttom,
    };
    private ImageView[] imageViews = new ImageView[14];
    private Button buttonOk;
    private ImageView image1;
    private TextView textResult;
    private int status = 0;
    private  int vrandom = 0;
    private int vcount=5;

//    private boolean beginStatus = false;
    // 生命值
    private int strength = 2;
    private int recentPosition = 0;

    //    deleted by young
    private int initPosition = 0;

//    private LayerDrawable userLayerDrawable;
//    private LayerDrawable bomberLayerDrawable;

    // 掷骰子部分使用例程方式
    // 乱
    Handler mHandler = new Handler()
    {
        @Override//
        public void handleMessage(Message msg) {
            if (msg.what == 0x111){
                vrandom = ( (int) (Math.random()*200+1))%6;
                image1.setImageResource(imagesarr[vrandom]);
            }else {
                imageViews[recentPosition].setImageResource(imageViewInt[recentPosition]);
                switch (msg.what) {
                    case 0x333:
                        imageViews[++recentPosition].setImageResource(R.drawable.userposition);
                        if (recentPosition==13) {
                            Toast.makeText(MainActivity.this, "游戏胜利", Toast.LENGTH_SHORT).show();
                            textResult.setText("游戏胜利！");
                        }break;
                    case 0x444:imageViews[++recentPosition].setImageResource(R.drawable.booom);break;
                    case 0x555:imageViews[++recentPosition].setImageResource(R.drawable.userback);break;
                    case 0x666:
                        if (++recentPosition > 9)
                            imageViews[recentPosition].setImageResource(R.drawable.userforward);
                        else
                            imageViews[recentPosition].setImageResource(R.drawable.userback);
                        break;
                    case 0x777:imageViews[--recentPosition].setImageResource(R.drawable.userposition);
                }

            }
            if (msg.what == 0x801) {
                textResult.setText("恭喜你801，成功过关!" );
            }else if (msg.what == 0x448) {
                textResult.setText("游戏失败" );
                Log.i("最后一步448 handler 的位置是:", (recentPosition + 1) + "");
                Toast.makeText(getApplicationContext(), "游戏失败", Toast.LENGTH_SHORT).show();
            }else{
                textResult.setText("摇骰子，还剩下" + vcount + "次机会, 生命值为" + strength);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        beginStatus = true;
        buttonOk = (Button) findViewById(R.id.buttonOK);
        image1 = (ImageView) findViewById(R.id.image1);

        textResult = (TextView) findViewById(R.id.textResult);

        for (int i = 0; i < imageViews.length; i++) imageViews[i] = (ImageView)findViewById(R.id.i001 + i);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vcount-->0 && strength > 0 && recentPosition!=13) {
//                    status2=100;
                    status=0;

                    new Thread()
                    {
                        public void run()
                        {
                            while ( status < 15 )
                            {
                                mHandler.sendEmptyMessage(0x111);   // 发送消息
                                status = doWork1();
                            }
                            //最后一步单独处理,这是前边的步骤
                            for (int i = 0; i < vrandom; i++) {
                                doWork();
                                if (recentPosition==12)break;

                                mHandler.sendEmptyMessage(0x333);

                            }


                            if (recentPosition!=13) {
                                doWork();
                                // 最后一步
                                Log.i("最后一步的位置是:", (recentPosition + 1) + "");

                                Object lastTag = imageViews[recentPosition + 1].getTag();
                                if (lastTag==null) {
                                    mHandler.sendEmptyMessage(0x333);
                                }else {
                                    //最后遇到了炸弹
                                    if (lastTag.toString().equals("bomb")) {
                                        strength -= 1;
                                        mHandler.sendEmptyMessage(0x444);
                                        doWork();
                                    }else if (lastTag.toString().equals("back")){
                                        mHandler.sendEmptyMessage(0x555);
                                        doWork();
                                        mHandler.sendEmptyMessage(0x777);
                                        doWork();
                                        mHandler.sendEmptyMessage(0x777);
                                    }else if (lastTag.toString().equals("forward")){
                                        mHandler.sendEmptyMessage(0x666);
                                        doWork();
                                        mHandler.sendEmptyMessage(0x333);
                                        doWork();
                                        mHandler.sendEmptyMessage(0x333);
                                    }
                                }
                            }
                        }
                    }.start();
                }
                if (recentPosition==13) {
                    Toast.makeText(MainActivity.this, "游戏胜利", Toast.LENGTH_SHORT).show();
                    buttonOk.setText("游戏胜利！");
                } else if (strength == 0 || vcount == 0) {
                    Toast.makeText(getApplicationContext(), "游戏失败", Toast.LENGTH_SHORT).show();
                    buttonOk.setText("游戏失败");
                }
            }
        });

    }


    // 模拟一个耗时的操作
    public int doWork1()
    {   // 为数组元素赋值  // data[hasData++] = (int) (Math.random() * 100);
        status++;
        try
        {
            Thread.sleep(50);  //	*休眠50毫秒,即让该线程休眠 50毫秒，再继续执行该线程。
        }
        catch (InterruptedException e)//捕获该异常  保证程序正常的中断。
        {
            e.printStackTrace();
        }
        return status;
    }

    public void doWork() {
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
    }


//    /**
//     * int originalResourse = imageViews[recentPosition].getTag()==null?R.drawable.buttom:("back".equals(imageViews[recentPosition].getTag())?R.drawable.back:(recentPosition>9?R.drawable.forward:R.drawable.back));
//     * @param position
//     * @return
//     */
//    private int getResource(int position){
//        if ("back".equals(imageViews[position].getTag())) {
//            return R.drawable.back;
//        }else if ("forward".equals(imageViews[position].getTag())){
//            if (position<9) return R.drawable.back;
//            else return R.drawable.forward;
////        }else if ("bomb".equals(imageViews[position].getTag())) {
////            return R.drawable.bomb;//炸弹，应该是一次性
//        }else return R.drawable.buttom;
//    }


//    /**
//     * 或者每次移动之后修改前/后一个
//     * 这种方式效率比较低
//     * @param position
//     */
//    private void reInit(int position) {
//        for (int i = 0; i < position; i++) {
//            imageViews[i].setImageResource(getResource(i));
//        }
//    }
}

