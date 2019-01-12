package net.pogo.game.engine;

import static java.lang.System.out;

abstract public class GraphicsDisplay {

    protected TextureContainer textureContainer;
    protected TexturedMeshContainer texturedMeshContainer;

    abstract public void draw(float[] view, float[] perspective, RenderNode node);

}
