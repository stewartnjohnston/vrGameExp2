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
    int objectModelViewProjectionParam;
    float[] modelViewProjection = new float[16];
    float[] modelView = new float[16];

    public void draw(float[] view, float[] perspective, RenderNode _node)
    {
        node = _node;

        //engine.GetrenderSystem().update(1.0, view, perspective);

        float[] modelTarget = node.position.position.GetMatrix();

        Matrix.multiplyMM(modelView, 0, view, 0, modelTarget, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        drawTarget();

    }

    /** Draw the target object. */
    public void drawTarget() {
        GLES20.glUseProgram(objectProgram);
        GLES20.glUniformMatrix4fv(objectModelViewProjectionParam, 1, false, modelViewProjection, 0);

        TexturedMesh targetObjectMeshes = (TexturedMesh) node.display.textureMeshObject;
        Texture sterioTextures = (Texture) node.display.textureObject;

        sterioTextures.bind();

        targetObjectMeshes.draw();
        Util.checkGlError("drawTarget");
    }

}
