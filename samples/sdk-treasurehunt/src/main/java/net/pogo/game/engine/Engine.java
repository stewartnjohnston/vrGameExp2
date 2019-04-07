package net.pogo.game.engine;

import com.google.vr.sdk.samples.treasurehunt.TexturedMesh;

import java.util.List;
import java.util.ArrayList;
/*

 * @startuml

 * class Eninge

 * @enduml

 */

public class Engine
{
  private List<Entity> entities = new ArrayList<Entity>();
  private SystemManager systemManager = new SystemManager();
  private TextureContainer textureContainer = new TextureContainer();
  private TexturedMeshContainer texturedMeshContainer = new TexturedMeshContainer();
  private RenderSystem renderSystem;

  public Engine()
  {
    addALLSystems();
  }

  public void addEntity(Entity entity )
  {
    entities.add( entity );
    systemManager.addEntity(entity);
  }

  public void removeEntity(Entity entity)
  {
    // destroy nodes containing this entity's components
    // and remove them from the node lists
    systemManager.removeEntity(entity);
    entities.remove( entity );
  }

  public SystemManager GetSystemManager()
  {
    return systemManager;
  }

  public void addALLSystems()
  {
    systemManager.addSystem(new GrowSystem());
    systemManager.addSystem(new MoveSystem());
    renderSystem = new RenderSystem();
    systemManager.addSystem(renderSystem);
  }

  public RenderSystem GetrenderSystem()
  {
    return renderSystem;
  }

  public TextureContainer GetTextureContainer()
  {
    return textureContainer;
  }

  public TexturedMeshContainer GetTexturedMeshContainer()
  {
    return texturedMeshContainer;
  }
}

