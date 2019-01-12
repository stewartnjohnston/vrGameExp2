package com.google.vr.sdk.samples.treasurehunt.common;

import android.opengl.Matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by blake on 20/05/17.
 */

public class Wall {

    public List<Transform> bricks;
    float dist = 3.0f;  // defalt distance between bricks
    float scale = 1.0f;  // default scale of wall
    float rotationAngle = 0.0f; // default rotation agle
    float rotationAngle1 = 0.0f; // default rotation agle

    public Wall()
    {
        CreateWall();
    }
    public Wall(int size)
    {
        CreateWall(size);
    }
    public Wall(int size, float RotationAngle)
    {
        rotationAngle = RotationAngle;
        CreateWall(size);
    }

    public void CreateWall()
    {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(4);
        randomInt++;
        randomInt++;
        CreateWall(randomInt);
    }

    public int pickRandomCube(int size)
    {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(size);
        return randomInt;
    }
    public void CreateWall(int size)
    {

        int randomCube = pickRandomCube(size * size);
        int i = 0;

        bricks = new ArrayList<Transform>(size * size);
        float X;
        float Y;
        float Z = 0.0f;

        float shift = (((float)size - 1.0f)/2.0f);

        for (int x = 0; x < size; x++)
        {
            for (int y = 0; y < size; y++)
            {
                if ( i == randomCube )
                {
                    Z = 2.0f;
                }
                i++;
                X = dist * (x - shift);
                Y = dist * (y - shift);

                Transform brick = new Transform();
                brick.Scale(scale);
                brick.Rotate(rotationAngle, 0.0f, 1.0f, 0.0f);
                brick.Translate( X , Y ,Z);
                bricks.add(brick);
                Z = 0.0f;
             }
        }
    }

    public float[][] GetTransforms()
    {
        int numBricks = bricks.size();
        float bricksFloat[][] = new float[numBricks][16];

        int brickCount = 0;
        for (Transform brick: bricks)
        {
            bricksFloat[brickCount] = brick.GetMatrix().clone();
            brickCount++;
        }
        return bricksFloat;
    }


    public void TranslateWall(float x, float y, float z)
    {
        for (Transform brick: bricks)
        {
            brick.Translate(x, y, z);
        }
    }
    public void RotateWall(float angle, float x, float y, float z)
    {
        for (Transform brick: bricks)
        {
            brick.Rotate(angle, x, y, z);
        }
    }
}
