package net.pogo.game.engine;

import net.pogo.game.common.Transform;

import java.util.Random;

public class ScaleComponent {
    public float scaleFactor = 1.0f;
    public float scaleUpperBound = 1.3f;
    public float scaleLowerBound = 0.7f;
    public float scaleVelocity = 0.0001f;
    boolean increaseScale = true;

    public void randomCalculateBonds(float maxUpper, float minLower, float maxVelocity, float minVelocity)
    {
        float fraction = getRandomFraction();

        scaleUpperBound = 1.3f +  ((maxUpper - 1.0f) * fraction);

        fraction = getRandomFraction();
        scaleLowerBound = 0.6f - ((1.0f - minLower) * fraction);

        fraction = getRandomFraction();
        float velocitySpread = maxVelocity - minVelocity;

        scaleVelocity = minVelocity + (velocitySpread * fraction);
    }

    public void calculateBonds(float maxUpper, float minLower, float maxVelocity, float minVelocity)
    {
        float fraction = getRandomFraction();

        scaleUpperBound = maxUpper;

        scaleLowerBound = minLower;

        scaleVelocity = maxVelocity * (1 + fraction);
    }

    public float getRandomFraction()
    {
        Random rand = new Random();
        int n = rand.nextInt(100);
        float fraction = 1.0f/(float)n;
        return fraction;
    }

}
