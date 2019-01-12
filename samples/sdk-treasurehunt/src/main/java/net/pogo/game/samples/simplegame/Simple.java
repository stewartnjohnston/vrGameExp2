package net.pogo.game.samples.simplegame;

import java.util.Arrays;
import net.pogo.game.engine.*;
import java.util.logging.*;

public class Simple
{ 
   private final static Logger logger = Logger.getLogger(Simple.class.getName());
   //Logger logger = Logger.getLogger(MyClass.class.getName());
   static Engine engine;
   
   public static void main(String[] args)
   {
     logger.log(Level.INFO,"Hello World from Simple = ");
     System.out.println("Hello World Simple = ");
     
     engine = new Engine();
     createSpaceship();
     

     int count = 1;
     while (count < 100)
     {
           engine.GetSystemManager().update(1);
           count++;
     }

     System.out.println("Hello World Simple done ");
   }
   
  public static void createSpaceship()
  {
     Entity spaceship = new Entity();
 
     PositionComponent position = new PositionComponent();
      position.position.SetUnity();
      position.position.Translate(1,1,1);
     spaceship.add( position );
  
     DisplayComponent display = new DisplayComponent();
     spaceship.add( display );
  
      VelocityComponent velocity = new VelocityComponent();
      velocity.velocity.data[0]=1;
      velocity.velocity.data[1]=2;
      velocity.velocity.data[2]=3;
      spaceship.add( velocity );
 
     engine.addEntity( spaceship );
  }

}
