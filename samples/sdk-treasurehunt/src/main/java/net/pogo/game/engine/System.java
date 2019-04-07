package net.pogo.game.engine;

/*

 * @startuml

 * class System

 * @enduml

 */

abstract class System
{
  private boolean running;

  abstract void update(float time);
  abstract void draw();
  abstract void addEntity(Entity entity);
  abstract void removeEntity(Entity entity);
  
  public void start()
  {
     running = true;
  }
  
  public void stop()
  {
     running = false;
  }
  
  public boolean isRunning()
  {
     return running;
  }
}
