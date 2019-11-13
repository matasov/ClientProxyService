package com.matas.liteconstruct.service.business;

import java.util.HashMap;
import java.util.Map;

public class LiveLineLevelsConstants {

  public static final Map<Integer, Integer> PAYMENTS = new HashMap() {
    {
      put(0, 10);//sum=20 next=10 tech=10 result=0
      put(1, 10);//sum=20 next=20 tech=0 result=0
      put(2, 20);//sum=40 next=28 tech=10 ref=2 result=0
      put(3, 28);//sum=56 next=40 tech=10 ref=0 out=6 result=0
      put(4, 40);//sum=80 next=55 tech=20 ref=5 out=0 result=0
      put(5, 55);//sum=110 next=80 tech=20 ref=0 out=10 result=0
      put(6, 80);//sum=160 next=120 tech=30 ref=10 out=0 result=0
      put(7, 120);//sum=240 next=180 tech=40 ref=0 out=20 result=0
      put(8, 180);//sum=360 next=300 tech=40 ref=20 out=0 result=0
      put(9, 300);//sum=600 next=500 tech=60 ref=0 out=40 result=0
      put(10, 500);
      put(11, 800);
      put(12, 1300);
      put(13, 2100);
      put(14, 3000);
      put(15, 5000);
      put(16, 7000);
      put(17, 10000);
      put(18, 15000);
      put(19, 20000);
    }
  };
  
  public static final Map<Integer, Integer> PAYOUTS = new HashMap() {
    {
      put(0, 0);
      put(1, 0);
      put(2, 0);
      put(3, 6);
      put(4, 0);
      put(5, 10);
      put(6, 0);
      put(7, 20);
      put(8, 0);
      put(9, 40);
      put(10, 0);
      put(11, 100);
      put(12, 0);
      put(13, 800);
      put(14, 0);
      put(15, 1500);
      put(16, 0);
      put(17, 3000);
      put(18, 0);
      put(19, 30000);
    }
  };
  
  public static final Map<Integer, Integer> PAYOUT_REFERRALS = new HashMap() {
    {
      put(0, 0);
      put(1, 0);
      put(2, 2);
      put(3, 0);
      put(4, 5);
      put(5, 0);
      put(6, 10);
      put(7, 0);
      put(8, 20);
      put(9, 0);
      put(10, 100);
      put(11, 0);
      put(12, 200);
      put(13, 0);
      put(14, 400);
      put(15, 0);
      put(16, 1000);
      put(17, 0);
      put(18, 3000);
      put(19, 0);
    }
  };
  
  public static final Map<Integer, Integer> TECHS_COUNT = new HashMap() {
    {
      put(0, 0);
      put(1, 0);
      put(2, 1);
      put(3, 1);
      put(4, 2);
      put(5, 2);
      put(6, 3);
      put(7, 4);
      put(8, 4);
      put(9, 6);
      put(10, 10);
      put(11, 20);
      put(12, 30);
      put(13, 40);
      put(14, 60);
      put(15, 150);
      put(16, 300);
      put(17, 200);
      put(18, 700);
      put(19, 1000);
    }
  };
}
