package com.junyou.hbks.utils;

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

    //0-1
    public static int randomsOne(){
        Random random = new Random();
        int i = random.nextInt(2);
        return i;
    }

    private class Rand{

        public int randomResult(){
            int randNum  = randoms();
//            Log.i("TAG","随机数(1-100): " + randNum);
            if ( randNum >=0 && randNum <= 27 ){
                //27%  再接再厉
                return 1;
            }else if(randNum > 27 && randNum <= 77){
                //50% 一个小时
                return 0;
            }else if(randNum >77 && randNum <= 93){
                //16% 三个小时
                return 2;
            }else if(randNum >93 && randNum <=96){
                //3% 一个月
                return 3;
            }else if(randNum >96 && randNum <=98){
                //2% 三个月
                return 4;
            }else if (randNum >99 && randNum <= 100){
                //1% 终身
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
