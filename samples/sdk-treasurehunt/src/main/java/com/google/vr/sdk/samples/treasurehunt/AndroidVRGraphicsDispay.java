package com.google.vr.sdk.samples.treasurehunt;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.google.vr.sdk.base.Eye;

import net.pogo.game.engine.GraphicsDisplay;
import net.pogo.game.engine.RenderNode;
import net.pogo.game.engine.TextureContainer;

public class AndroidVRGraphicsDispay extends GraphicsDisplay {

    private RenderNode node;
    int objectProgram;
    float scaleFactor = 1;
    int objectModelViewProjectionParam;
    float[] scaleMatrix = new float[16];
    boolean increaseScale = true;
    float[] afterScaleMatrix = new float[16];
    float[] modelViewProjection = new float[16];
    float[] modelView = new float[16];
    boolean leftEye = true;

    public void draw(float[] view, float[] perspective, RenderNode _node)
    {
        node = _node;

        //engine.GetrenderSystem().update(1.0, view, perspective);

        if(perspective[3] == 1.0f)
        {
            leftEye = true;
            perspective[3] = 0;
        }
        else if(perspective[3] == 2.0f)
        {
            leftEye = false;
            perspective[3] = 0;
        }

        float[] modelTarget = node.position.position.GetMatrix();
        if(node.scale != null)
            scaleFactor = node.scale.scaleFactor;

        Matrix.setIdentityM(scaleMatrix, 0);



        Matrix.scaleM(scaleMatrix, 0, scaleFactor, scaleFactor, scaleFactor);
        Matrix.multiplyMM(afterScaleMatrix, 0, modelTarget, 0, scaleMatrix, 0);

        Matrix.multiplyMM(modelView, 0, view, 0, afterScaleMatrix, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        drawTarget();

        if(leftEye)
           perspective[3] = 1.0f;
        else
           perspective[3] = 2.0f;
    }

    /** Draw the target object. */
    public void drawTarget() {
        GLES20.glUseProgram(objectProgram);
        GLES20.glUniformMatrix4fv(objectModelViewProjectionParam, 1, false, modelViewProjection, 0);

        TexturedMesh targetObjectMeshes = (TexturedMesh) node.display.textureMeshObject;
        Texture sterioTextures;
        if(leftEye)
        {
            sterioTextures = (Texture) node.display.textureObject_l;
        }
        else
        {
            sterioTextures = (Texture) node.display.textureObject_r;
        }

        sterioTextures.bind();

        targetObjectMeshes.draw();
        Util.checkGlError("drawTarget");
    }

}
