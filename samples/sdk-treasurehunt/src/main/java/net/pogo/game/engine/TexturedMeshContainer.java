package net.pogo.game.engine;

import android.content.Context;

import com.google.vr.sdk.samples.treasurehunt.TexturedMesh;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TexturedMeshContainer {

    private final static Logger logger = Logger.getLogger(TexturedMeshContainer.class.getName());

    private Map<String, TexturedMesh> texturedMeshs = new HashMap<String, TexturedMesh>();
    Context context;
    private int objectPositionParam;
    private int objectUvParam;
/*
    public TexturedMeshContainer(Context _context, int _objectPositionParam, int _objectUvParam)
    {
        context =_context;
        objectPositionParam = _objectPositionParam;
        objectUvParam = _objectUvParam;
    }
*/
    public Boolean loadWaveFrontObject(String fileName)
    {
        if(texturedMeshs.containsKey(fileName))
            //if the texturedMesh already exists in the container no need to reload it.
            return true;

        try {
            TexturedMesh texturedMesh = new TexturedMesh( context, fileName, objectPositionParam, objectUvParam);
            texturedMeshs.put(fileName, texturedMesh);
            return true;
        }
        catch (Exception ex)
        {
            logger.log(Level.INFO, "TexturedMeshContainer.loadWaveFrontObject() Error="  + ex.getMessage());
        }
        return false;
    }

    public Boolean loadWaveFrontObject(String fileName, TexturedMesh texturedMesh)
    {
        if(texturedMeshs.containsKey(fileName))
            //if the texturedMesh already exists in the container no need to reload it.
            return true;

        try {
            texturedMeshs.put(fileName, texturedMesh);
            return true;
        }
        catch (Exception ex)
        {
            logger.log(Level.INFO, "TexturedMeshContainer.loadWaveFrontObject() Error="  + ex.getMessage());
        }
        return false;
    }
    public Boolean containsKey(String key)
    {
        return texturedMeshs.containsKey(key);
    }

    public TexturedMesh getTexturedMesh(String key)
    {
        return texturedMeshs.get(key);
    }
    public boolean deleteWaveFrontObject(int key)
    {
        return true;
    }


}
