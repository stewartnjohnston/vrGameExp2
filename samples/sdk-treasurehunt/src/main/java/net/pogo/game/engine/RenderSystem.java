package net.pogo.game.engine;

import java.util.HashMap;
import java.util.Map;
import static java.lang.System.out;


public class RenderSystem extends System
{
  private Map<Integer, RenderNode> targets = new HashMap<Integer, RenderNode>();
  private Map<Integer, RenderNode> cockpitNodes = new HashMap<Integer, RenderNode>();
  public GraphicsDisplay display;
  public float[] view;
  public float[] perspective;


  void addEntity(Entity entity)
  {
     if(!targets.containsKey(entity.id))
     {
      if(entity.hasComponentType(DisplayComponent.class.getName()))
        {
           RenderNode node = new RenderNode();
           node.display = (DisplayComponent) entity.getComponentType(DisplayComponent.class.getName());
           node.position = (PositionComponent) entity.getComponentType(PositionComponent.class.getName());
           node.scale = (ScaleComponent) entity.getComponentType(ScaleComponent.class.getName());
           targets.put(entity.id, node);
        }
     }
  }
  void removeEntity(Entity entity)
  {
     targets.remove(entity.id);
  }

    public void update( float time)
    {


    }

    public void addCockpitEntity(Entity entity)
    {
        if(!cockpitNodes.containsKey(entity.id))
        {
            if(entity.hasComponentType(DisplayComponent.class.getName()))
            {
                RenderNode node = new RenderNode();
                node.display = (DisplayComponent) entity.getComponentType(DisplayComponent.class.getName());
                node.position = (PositionComponent) entity.getComponentType(PositionComponent.class.getName());
                cockpitNodes.put(entity.id, node);
            }
        }
    }

  public void drawCockpit()
  {
    for (RenderNode target : cockpitNodes.values())
    {
        display.draw(view, perspective, target);
    }
  }

  public void draw()
  {
    for (RenderNode target : targets.values())
    {
        display.draw(view, perspective, target);
    }
   }
}
