package com.google.vr.sdk.samples.treasurehunt.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by blake on 20/05/17.
 */

public class Circuit {

    public List<Wall> walls;
    public float walk = 0;
    public float R, R1, R2;


    public Circuit()
    {
        CreateSimple4Ciruit();
    }

    public void CreateSimple4Ciruit()
    {
        walls = new ArrayList<Wall>(4);

        // Create first wall
        Wall wall;


        // Create first wall
        wall = new Wall(3, 0.0f);
        wall.TranslateWall(-50.0f, 0.0f, -50.0f);
        walls.add(wall);

        // Second wall
        wall = new Wall(4, 90.0f);
        wall.TranslateWall(65.0f, 0.0f, 35.0f);
        walls.add(wall);

        // Third wall
        wall = new Wall(5, 0.0f);
        wall.TranslateWall(50.0f, 0.0f, 50.0f);
        walls.add(wall);

        // Fourth wall
        wall = new Wall(6, 90.0f);
        wall.TranslateWall(-65.0f, 0.0f, -35.0f);
        walls.add(wall);

        walls.add(wall);


    }

    public float[][] GetTransforms(int leaveSpace)
    {
        int numBricks = leaveSpace + GetNumberOfBricks();
        float bricks[][] = new float[numBricks][16];

        int brickCount = leaveSpace;

        for (Wall wall: walls)
        {
            float newBricks[][] = wall.GetTransforms();
            int newBrickCount = wall.bricks.size();
            for (int i = 0; i < newBrickCount; i++)
            {
                for (int j = 0; j < 16; j++)
                {
                    bricks[brickCount][j] = newBricks[i][j];
                }
                brickCount++;
            }

        }
        return bricks;
    }

    public int GetNumberOfBricks()
    {
        int brickCount = 0;
        for (Wall wall: walls)
        {
            brickCount = brickCount + wall.bricks.size();
        }
        return brickCount;
    }
}
