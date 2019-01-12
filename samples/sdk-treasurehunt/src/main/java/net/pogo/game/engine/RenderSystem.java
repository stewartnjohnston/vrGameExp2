package net.pogo.game.engine;

import java.util.HashMap;
import java.util.Map;
import static java.lang.System.out;


public class RenderSystem extends System
{
  private Map<Integer, RenderNode> targets = new HashMap<Integer, RenderNode>();
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
  public void draw()
  {

    for (RenderNode target : targets.values())
    {
        String xxx = target.position.position.toString();
        display.draw(view, perspective, target);
        out.println("RenderSystem::update() target " + target.position.position.toString());
    }
   }
}
