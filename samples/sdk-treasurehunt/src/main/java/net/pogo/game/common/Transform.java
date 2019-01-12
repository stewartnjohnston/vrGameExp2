package net.pogo.game.common;

 import android.opengl.Matrix;
//import com.intellij.util.*;


/**
 * Created by blake on 20/05/17.
 */


public class Transform {

    public static final int matrixSize = 16;

    public float [] matrix = new float[matrixSize];

    public void SetUnity()
    {
        Matrix.setIdentityM(matrix, 0);
    }

    public Transform()
    {
        SetUnity();
    }

    public void Translate(float x, float y, float z)
    {

        Matrix.translateM(matrix, 0 , x ,y, z);
    }

    public void Rotate(float angle, float x, float y, float z)
    {
        Matrix.rotateM(matrix, 0 , angle, x ,y, z);

    }

    public  void Scale(float scale)
    {
        Matrix.scaleM(matrix, 0, scale, scale, scale);
    }

    public float[] GetMatrix()
    {
        return matrix;
    }
/*
    public String toString() {
        String matrixString = new String();

        for (int i = 0; i < matrixSize; i++) {
            matrixString += ",   " + String.format("%.02f", matrix[i]);
        }
        return matrixString;

    }
*/
    public void setDebug() {
        String matrixString = new String();

        for (int i = 0; i < matrixSize; i++) {
            matrix[i] = i + 100;
        }
    }

}
