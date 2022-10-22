package com.example.myfirstapp.genericUtil;
import java.util.Random;
/**
 * Created by adarsh on 19/01/17.
 */

public class RandomNumGenerator {

        Random random;
        public RandomNumGenerator()
        {
            random = new Random();
        }

        public String getRandomNumber()
        {
            return new Long( random.nextLong() ).toString().substring(3, 11);
        }
}
