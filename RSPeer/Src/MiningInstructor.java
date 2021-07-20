//MiningInstructor.java

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
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.listeners.ChatMessageListener;

import org.rspeer.script.Script;
import org.rspeer.script.task.Task;

import scripts.Debug;
import scripts.DisposableTask;

public class MiningInstructor extends DisposableTask
{
	public final int TIN_ROCK = 10080;
	public final int COPPER_ROCK = 10079;
	public final Position miningArea = new Position( 3079, 9504 );
	public final Position combatArea = new Position( 3103, 9505 );

	public boolean disposable()
	{
		return Inventory.contains( "Bronze dagger" ) || Equipment.contains( "Bronze dagger" );
	}

	public boolean validate()
	{
		return playerEnteredMine();
	}
	
	public int execute()
	{	
		if ( Npcs.getNearest( "Mining Instructor") == null )
		{
			Debug.out( "Approaching Mining Instructor" );
			Movement.walkTo( miningArea );
		}
		else if ( !Inventory.contains( "Bronze pickaxe" ) )
		{	
			Debug.out( "Getting pickaxe" );
			talkTo( "Mining Instructor" );
		}
		else if ( !Inventory.contains( "Copper ore" )
				&& !Inventory.contains( "Bronze bar" )
				&& !Inventory.contains( "Bronze dagger" ) )
		{
			Debug.out( "Mining Copper" );
			
			SceneObjects.getNearest( COPPER_ROCK ).interact( "Mine" );
			Time.sleepUntil(() -> Inventory.contains( "Copper ore" ), 200, 8000);
		}
		else if ( !Inventory.contains( "Tin ore" ) 
				&& !Inventory.contains( "Bronze bar" )
				&& !Inventory.contains( "Bronze dagger" ) )
		{
			Debug.out( "Mining Tin" );
			
			SceneObjects.getNearest( TIN_ROCK ).interact( "Mine" );
			Time.sleepUntil(() -> Inventory.contains( "Tin ore" ), 200, 8000);
		}
		else if ( Inventory.contains( "Copper ore" )
				&& Inventory.contains( "Tin ore" ) )
		{
			Debug.out( "Making Bronze bar" );
			
			Inventory.getFirst( "Tin ore" ).interact( "Use" );
			Time.sleep( 250, 800 );
			
			SceneObjects.getNearest( "Furnace" ).interact( "Use" );
			
			Time.sleepUntil(() -> Inventory.contains( "Bronze bar" ), 200, 8000);
		}
		else if ( !Inventory.contains( "Hammer" ) )
		{
			Debug.out( "Getting hammer" );
			talkTo( "Mining Instructor" );
		}
		else if ( Inventory.contains( "Bronze bar" )
				&& !Inventory.contains( "Bronze dagger" ) )
		{
			Debug.out( "Making Bronze dagger" );
			
			SceneObjects.getNearest( "Anvil" ).interact( "Smith" );
			Time.sleepUntil(() -> smithDaggerVisible(), 200, 8000);
			
			if ( !smithDaggerVisible() )
			{
				Debug.out( "Error opening smithing interface" );
			}
			else
			{
				Interfaces.getComponent( 312, 9 ).click();
				Time.sleepUntil(() -> Inventory.contains( "Bronze dagger" ), 200, 8000);
			}
		}
		
		return 0;
	}
	
	public boolean smithDaggerVisible()
	{
		if ( Interfaces.getComponent( 312, 9 ) == null )
		{
			return false;
		}
		else
		{
			return Interfaces.getComponent( 312, 9 ).isVisible();
		}
	}

	public boolean playerEnteredMine()
	{
		int yPos = Players.getLocal().getPosition().getY();

		if ( yPos > 5000 )
		{
			return true;
		}
		else
		{
			return false;
		}
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
    	
    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    	
    	while ( Dialog.canContinue() )
    	{
	    	Debug.out("Continuing chat...");
    		Dialog.processContinue();
	    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    		Time.sleep( 200, 2350 );
    	}
    }
    
    public void talkTo(String npcName)
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
	    	Debug.out("Continuing chat...");
    		Dialog.processContinue();
	    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    		Time.sleep( 200, 2350 );
    	}
    }
}
