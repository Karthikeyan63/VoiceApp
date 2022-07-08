package com.developer.karthikeyank;

import java.util.Random;

public class MaterialsQ {

    public static final int Total_Question = 10;
    public static int Running_Question = 1;
    public static int Total_Score = 0;
    private static Random random;

    public static int RandomNumber(){
        random = new Random();
        return random.nextInt(9)+1;
    }
}
