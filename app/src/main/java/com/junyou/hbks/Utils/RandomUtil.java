package com.junyou.hbks.Utils;

import android.util.Log;
import java.util.Random;

public class RandomUtil {

    public int startRandom(){
        Rand rand = new Rand();
        int randNum = rand.randomResult();
        if (randNum >= 0){
//            Log.i("TAG", "结果(0-5)： " + randNum);
            return randNum;
        }
        return 1;
    }

    private class Rand{

        public int randomResult(){
            int randNum  = randoms();
//            Log.i("TAG","随机数(1-100): " + randNum);
            if ( randNum >=0 && randNum <= 50 ){
                //50%  再接再厉
                return 1;
            }else if(randNum > 50 && randNum <= 80){
                //30% 一个小时
                return 0;
            }else if(randNum >80 && randNum <= 90){
                //10% 三个小时
                return 2;
            }else if(randNum >90 && randNum <=95){
                //5% 一个月
                return 3;
            }else if(randNum >95 && randNum <=98){
                //3% 三个月
                return 4;
            }else if (randNum >98 && randNum <= 100){
                //2% 终身
                return 5;
            }else{
                return 1;
            }
        }

        //产生 1-100 随机数
        private  int randoms(){
            Random random = new Random();
            int i=random.nextInt(100) + 1;
            return i;
        }
    }
}
