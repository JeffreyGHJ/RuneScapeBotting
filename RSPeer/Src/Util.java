//Util.java

package scripts;

import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.api.scene.HintArrow;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.component.InterfaceComponent;

import org.rspeer.script.Script;
import org.rspeer.script.task.Task;

import scripts.Debug;
import scripts.DisposableTask;

import java.util.*;

public class Util
{
	public static void talkTo(Npc npc)
    {
    	Debug.out("Attempting to talk to " + npc +"...");
    	
    	//TO-DO: CLEAR ANY CLICK TO CONTINUE DIALOG
    	//Also add this to the overloaded function that accepts a string
    
    	if(npc != null)
    	{
    	    npc.interact("Talk-to");
    	}
    	else
    	{
    	    Debug.out("TalkTo(): npc is null");
    	}
    	
    	Time.sleepUntil(() -> Dialog.isViewingChat(), 4000);
    	
    	if(Dialog.isViewingChat())
    	{
    		Debug.out("TalkTo(): SUCCESS!");
    	}
    	else
    	{
    		Debug.out("TalkTo(): FAIL!");
    	}
    	
    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    	
    	while ( Dialog.canContinue() )
    	{
	    	Debug.out("Continuing chat...");
    		Dialog.processContinue();
	    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    		Time.sleep( 200, 1850 );
    	}
    }
    
    public static void talkTo(String npcName)
    {
    	Npc npc = Npcs.getNearest( npcName );
   
    	if(npc != null)
    	{
    	    Debug.out("Attempting to talk to " + npc +"...");
    	    npc.interact("Talk-to");
    	}
    	else
    	{
    	    Debug.out("TalkTo(): npc is null");
    	}
    	
    	Time.sleepUntil(() -> Dialog.isViewingChat(), 200, 4000);
    	
    	if(Dialog.isViewingChat())
    	{
    		Debug.out("TalkTo(): SUCCESS!");
    	}
    	else
    	{
    		Debug.out("TalkTo(): FAIL!");
    	}
    	
    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    	
    	while ( Dialog.canContinue() )
    	{
	   		if ( Interfaces.getComponent(162, 45).isVisible() ) //Click to continue
			{
				Debug.out("Handling notification...");
				Interfaces.getComponent(162, 45).click();
				Time.sleepUntil(() -> !Interfaces.getComponent(162, 45).isVisible(), 200, 4000);
			}
    	
	    	Debug.out("Continuing chat...");
    		Dialog.processContinue();
	    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    		Time.sleep( 200, 2350 );
    	}
    }
    
    public static void attackNpc( String npcName )
	{
		Debug.out( "Attempting to attack: " + npcName );
	
		if ( Players.getLocal().getTarget() != null )
		{
			Debug.out( "Player is in combat with: " + Players.getLocal().getTarget() );
			return;
		}
		
		Npc npc = Npcs.getNearest( n -> n.getName().equals( npcName ) 
											&& n.getTarget() == null
											&& n.isPositionInteractable() );
		if ( npc != null 
			&& !npc.isPositionInteractable() 
			&& npc.getTarget() == null )
		{
			Debug.out( "Attempting to reach: " + npc );
			Movement.walkTo( npc.getPosition() );
			Time.sleep( 0, 700 );
		} 
		else if ( npc != null && npc.getTarget() == null )
		{
			Debug.out( "Attacking: " + npc );
			npc.interact( "Attack" );
			Time.sleepUntil(() -> npc.getTarget() != null
								&& npc.getTarget().equals( Players.getLocal() ), 400, 8000 );
		}
		else
		{
			Debug.out( "Failed: No valid \"" + npcName + "\" found" );
			return;
		}
		
	}
	
	public static void lootPosition( Position position, List<String> items )
	{
		Debug.out( "Picking up drops" );
		Time.sleepUntil(() -> Pickables.getAt( position ).length > 0, 200, 4000);
		
		Pickable[] drops = Pickables.getLoaded(n -> n.getPosition().equals(position)
													&& items.contains( n.getName() ) );
													
		Debug.out( "drops[]: " + drops );
		
		if ( drops.length > 0 )
		{
			Debug.out( "There are " + drops.length + " items to pick up" );
			Pickable drop = drops[0];
			
			Debug.out( "drops[0]: " + drops[0] );
			Debug.out( "drop: " + drop );
	
			drop.interact( "Take" );
			Time.sleepUntil(() -> drop == null, 200, 4000);
		}
		else
		{
			Debug.out( "There are no items to pick up" );
			//dropLocation = null;
		}
	}
}
