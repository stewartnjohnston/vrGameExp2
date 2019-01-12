package net.pogo.game.engine;

import android.content.Context;

import com.google.vr.sdk.samples.treasurehunt.Texture;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;


public class TextureContainer {

    private final static Logger logger = Logger.getLogger(TextureContainer.class.getName());

    private Map<String, Texture> textures = new HashMap<String, Texture>();
    Context context;
/*
    public TextureContainer(Context _context)
    {
        context =_context;
    }
*/
    public Boolean loadTexture(String fileName)
    {
        if(textures.containsKey(fileName))
            //if the texture already exists in the container no need to reload it.
            return true;

        try {
            Texture texture = new Texture( context, fileName);
            textures.put(fileName, texture);
            return true;
        }
        catch (Exception ex)
        {
            logger.log(Level.INFO, "TexturedMeshContainer.loadWaveFrontObject() Error="  + ex.getMessage());
        }
        return false;
    }

    public Boolean loadTexture(String fileName, Texture texture)
    {
        if(textures.containsKey(fileName))
            //if the texture already exists in the container no need to reload it.
            return true;

        try {
            textures.put(fileName, texture);
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
        return textures.containsKey(key);
    }


    public Texture getTexture(String key)
    {
        return textures.get(key);
    }


}
