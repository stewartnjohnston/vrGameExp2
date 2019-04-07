package net.pogo.game.engine;

import java.util.HashMap;
import java.util.Map;


public class GrowSystem extends System {

    private Map<Integer, GrowNode> targets = new HashMap<Integer, GrowNode>();

    void addEntity(Entity entity)
    {
        if(!targets.containsKey(entity.id))
        {
            if(entity.hasComponentType(ScaleComponent.class.getName()))
            {
                GrowNode node = new GrowNode();
                node.scale = (ScaleComponent) entity.getComponentType(ScaleComponent.class.getName());
                targets.put(entity.id, node);
            }
        }
    }

    void removeEntity(Entity entity)
    {
        targets.remove(entity.id);
    }


    public void draw()
    {

    }

    public void update( float time  )
    {
        for (GrowNode target : targets.values())
        {
            if(target.scale.increaseScale)
            {
                if(target.scale.scaleFactor < target.scale.scaleUpperBound)
                    target.scale.scaleFactor = target.scale.scaleFactor + target.scale.scaleVelocity;
                else
                    target.scale.increaseScale = false;
            } else
            {
                if(target.scale.scaleFactor > target.scale.scaleLowerBound)
                    target.scale.scaleFactor = target.scale.scaleFactor - target.scale.scaleVelocity;
                else
                    target.scale.increaseScale = true;
            }

        }
    }
}
