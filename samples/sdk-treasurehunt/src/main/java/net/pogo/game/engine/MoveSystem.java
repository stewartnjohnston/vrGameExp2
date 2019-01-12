package net.pogo.game.engine;

import java.util.*;


public class MoveSystem extends System
{
  
  private Map<Integer, MoveNode> targets = new HashMap<Integer, MoveNode>();

  void addEntity(Entity entity)
  {
     if(!targets.containsKey(entity.id))
     {
        if(entity.hasComponentType(VelocityComponent.class.getName()))
        {
           MoveNode node = new MoveNode();
           node.velocity = (VelocityComponent) entity.getComponentType(VelocityComponent.class.getName());
           node.position = (PositionComponent) entity.getComponentType(PositionComponent.class.getName());
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
  
    for (MoveNode target : targets.values())
    {
        float dx = target.velocity.velocity.data[0] * time;
        float dy = target.velocity.velocity.data[1] * time;
        float dz = target.velocity.velocity.data[2] * time;

        target.position.position.Translate(dx,dy,dz);
    }
  }
}
