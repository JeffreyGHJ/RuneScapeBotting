//MasterChef.java

package scripts;

import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.api.scene.HintArrow;
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
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.event.listeners.RenderListener;

import org.rspeer.script.Script;
//import org.rspeer.script.task.Task;

import scripts.Debug;
import scripts.DisposableTask;

public class MasterChef extends DisposableTask
{
	public Area chefsArea = Area.rectangular(0,0,0,0);
	public Position chefsAreaTile = new Position( 3078, 3084 );
	public Position chefExit = new Position( 3072, 3090 );
	public InterfaceComponent clickContinue = Interfaces.getComponent(162, 45);
	
	public boolean disposable()
	{
		return Players.getLocal().getPosition().equals( chefExit )
			|| Tab.QUEST_LIST.getComponent().isVisible();
	}

	public boolean validate()
	{
		return ((
					Inventory.contains( "Shrimps" ) 
					&& !Inventory.contains( "Bread" )) 
					|| 
				( 	//OR
					HintArrow.getPosition() != null 
					&& HintArrow.getPosition().equals( chefExit ) 
				));
	}
	
	public int execute()
	{
		Debug.out("Master Chef");
	
		if ( Npcs.getNearest( "Master Chef" ) == null )//!chefsArea.contains( Players.getLocal().getPosition() ) )
		{
			//walkToChef();
			Movement.walkTo( chefsAreaTile );
			Debug.out( "Walking to Master Chef" );
		}
		else if ( !Npcs.getNearest( "Master Chef" ).isPositionInteractable() )
		{
			Movement.walkTo( chefsAreaTile );
			Debug.out( "Walking to Master Chef" );
		}
		else if ( !Inventory.contains( "Pot of flour" ) 
				&& !Inventory.contains( "Bread dough" )
				&& !Inventory.contains( "Bread" ) )
		{
			talkTo( Npcs.getNearest( "Master Chef" ) );
			continueChat();
		}
		else if ( Inventory.contains( "Pot of flour" )
				&& Inventory.contains( "Bucket of water" ) )
		{
			Debug.out( "Making dough" );
			Inventory.getFirst( "Pot of flour" ).interact( "Use" );
			Inventory.getFirst( "Bucket of water" ).interact( "Use" );
			
			Time.sleepUntil(() -> Inventory.contains( "Bread dough" ), 4000 );
			Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000); 
		}
		else if ( Inventory.contains( "Bread dough" ) )
		{
			Debug.out( "Making dough into bread" );
			
			Inventory.getFirst( "Bread dough" ).interact( "Use" );
			SceneObjects.getNearest( "Range" ).interact( "Use" );
			
			Time.sleepUntil(() -> !Inventory.contains( "Bread dough" ), 4000 );
			Time.sleepUntil(() -> Players.getLocal().isAnimating(), 1000); 
			Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000); 
		}
		else if ( Inventory.contains( "Bread" ) )
		{
			Movement.walkTo( chefExit );
			Debug.out( "Exiting chefs house" );
		}
		
		return 0;
	}
	
	public void talkTo(Npc npc)
    {
    	Debug.out("Attempting to talk to " + npc +"...");
    
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
    }
    
    public void continueChat()
    {
    	Debug.out("Continuing chat...");
    	
    	if ( clickContinue.isVisible() )
    	{
    		clickContinue.click();
    		Time.sleepUntil(() -> !clickContinue.isVisible(), 2000);
    	}
    	
    	Time.sleepUntil(() -> Dialog.canContinue(), 4000);
    	
    	if(Dialog.canContinue())
    	{
    		Dialog.processContinue();
    	}
    	
    	Time.sleepUntil(() -> Dialog.canContinue(), 4000);
    	
    	if(Dialog.canContinue() /*&& isStopping == false*/)
    	{
    		Time.sleep( 200, 2350 );
    		continueChat();
    	}
    }
}







