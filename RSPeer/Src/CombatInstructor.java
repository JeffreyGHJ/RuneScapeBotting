//CombatInstructor.java

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
import scripts.Util;

public class CombatInstructor extends DisposableTask
{
	public final Position combatArea = new Position( 3103, 9505 );
	//public final Position gate = new Position( 3024, 9488 );
	public final Position insidePit = new Position( 3110, 9518 );
	public final Position ladder = new Position( 3111, 9526 );

	public boolean disposable()
	{
		return Players.getLocal().getPosition().getY() < 5000
				&& ( Inventory.contains( "Shortbow" ) || Equipment.contains( "Shortbow" ) );
	}

	public boolean validate()
	{
		return Inventory.contains( "Bronze dagger" ) || Equipment.contains( "Bronze dagger" );
	}
	
	public int execute()
	{	
		if ( ( Npcs.getNearest( "Combat Instructor" ) == null || !Npcs.getNearest( "Combat Instructor" ).isPositionInteractable() )
			&& ( !Inventory.contains( "Bronze sword" ) && !Equipment.contains( "Bronze sword" ) ) )
		{
			Debug.out( "Approaching Combat Instructor" );
			Movement.walkTo( combatArea );
			Time.sleepUntil(() -> Npcs.getNearest( "Combat Instructor" ) != null, 200, 8000 );
		}
		else if ( !Tab.EQUIPMENT.getComponent().isVisible()
				&& !Interfaces.isOpen( 84 )
				&& !Equipment.contains( "Bronze sword" )
				&& !Inventory.contains( "Bronze sword" ) )
		{
			Debug.out( "Unlocking Equipment Tab" );
			Util.talkTo( "Combat Instructor" );
		}
		else if ( Tab.EQUIPMENT.getComponent().isVisible()
				&& !Equipment.contains( "Bronze sword" ) 
				&& !Equipment.contains( "Bronze dagger" )
				&& !Equipment.contains( "Shortbow" )
				&& !Tab.EQUIPMENT.isOpen() )
		{
			Debug.out( "Opening Equipment");
			Tabs.open( Tab.EQUIPMENT );
			Time.sleepUntil(() -> Tab.EQUIPMENT.isOpen(), 200, 4000);
			Time.sleep( 250, 800 );
		}
		else if ( Tab.EQUIPMENT.isOpen()
				&& !Interfaces.isOpen(84) 
				&& !Equipment.contains( "Bronze sword" ) 
				&& !Equipment.contains( "Bronze dagger" )
				&& !Equipment.contains( "Shortbow" ) )
		{
			Debug.out( "Opening Equipment stats" );
			Interfaces.getComponent( 387, 1 ).click();	//Equipment Stats button
			Time.sleepUntil(() -> Interfaces.isOpen( 84 ), 200, 4000); //Equipment Stats close button
			Time.sleep( 250, 800 );
		}
		else if ( Interfaces.isOpen( 84 )
				&& !Equipment.contains( "Bronze dagger" ) 
				&& !Inventory.contains( "Bronze sword" ) 
				&& !Inventory.contains( "Shortbow" ) )
		{
			Debug.out( "Equipping Dagger" );
			Inventory.getFirst( "Bronze dagger" ).click();
			Time.sleepUntil(() -> Equipment.contains( "Bronze dagger" ), 200, 4000);
		}
		else if ( Interfaces.isOpen( 84 )
				&& Equipment.contains( "Bronze dagger" ) )
		{
			Debug.out( "Closing Equipment stats" );
			Interfaces.getComponent( 84, 4 ).click();
			Time.sleepUntil(() -> !Interfaces.getComponent( 84, 4 ).isVisible(), 200, 4000);
			Time.sleep( 250, 800 );
		}
		else if ( HintArrow.isPresent()
				&& HintArrow.getPosition().equals( Npcs.getNearest( "Combat Instructor" ).getPosition() )
				&& !Inventory.contains( "Bronze sword" )
				&& !Equipment.contains( "Bronze sword" ) )
		{
			Debug.out( "Dagger equipped, obtaining sword and sheild" );
			Util.talkTo( "Combat Instructor" );
		}
		else if ( (Inventory.contains( "Bronze sword" ) || Inventory.contains( "Wooden sheild" ) )
				&& !( Equipment.contains( "Shortbow" ) || Inventory.contains( "Shortbow" ) ) )
		{
			Debug.out( "New Equipment!" );
			
			if ( Inventory.getFirst( "Bronze sword" ) != null )
			{
				Debug.out( "Equipping Bronze sword" );
				Inventory.getFirst( "Bronze sword" ).interact( "Wield" );
				Time.sleepUntil(() -> !Inventory.contains( "Bronze sword" ), 200, 4000);
			}
			
			if ( Inventory.getFirst( "Wooden shield" ) != null )
			{
				Debug.out( "Equipping Wooden shield" );
				Inventory.getFirst( "Wooden shield" ).interact( "Wield" );
				Time.sleepUntil(() -> !Inventory.contains( "Wooden shield" ), 200, 4000);
			}
		}
		else if ( !HintArrow.isPresent()
				&& Tab.COMBAT.getComponent().isVisible()
				&& Equipment.contains( "Bronze sword" )
				&& !Inventory.contains( "Shortbow" )		
				&& !Equipment.contains( "Shortbow" ) )
		{
			Tabs.open( Tab.COMBAT );
			Time.sleepUntil(() -> Tab.COMBAT.isOpen(), 200, 4000);
		}
		else if ( HintArrow.isPresent()
				&& HintArrow.getPosition().equals( insidePit )
				&& !Inventory.contains( "Shortbow" )
				&& !Equipment.contains( "Shortbow" ) )
		{
			Debug.out( "Killing a rat" );
			Movement.walkTo( insidePit );
		}
		else if ( HintArrow.isPresent()
			&& insidePit.isPositionInteractable()
			&& HintArrow.getPosition().isPositionInteractable()
			&& Players.getLocal().getTarget() == null )
		{
			Debug.out( "Selecting rat" );
			Npc rat = Npcs.getNearest(n -> n.getName().equals("Giant rat") && n.getTarget() == null );
			
			if ( rat.getTarget() == null )
			{
				rat.interact( "Attack" );
				Time.sleepUntil(() -> rat.getTarget().equals( Players.getLocal() ), 200, 8000 );
			}
		}
		else if ( Players.getLocal().getTarget() != null )
		{
			Debug.out( "Player is in combat with: " + Players.getLocal().getTarget() );
			Time.sleepUntil(() -> Players.getLocal().getTarget() == null, 750, 10000);
		}
		else if ( HintArrow.isPresent() 
				&& HintArrow.getPosition().equals( Npcs.getNearest( "Combat Instructor" ).getPosition() ) 
				&& !Npcs.getNearest( "Combat Instructor" ).isPositionInteractable() )
		{
			Debug.out( "Returning to Combat Instructor" );
			Movement.walkTo( Npcs.getNearest( "Combat Instructor" ) );
		}
		else if ( HintArrow.isPresent() 
				&& HintArrow.getPosition().equals( Npcs.getNearest( "Combat Instructor" ).getPosition() ) 
				&& Npcs.getNearest( "Combat Instructor" ).isPositionInteractable() )
		{
			Debug.out( "Following Hint Arrow" );
			Util.talkTo( "Combat Instructor" );
		}
		else if ( Inventory.contains( "Shortbow" ) || Inventory.contains( "Bronze arrow" ) )
		{
			if ( Inventory.contains( "Shortbow" ) )
			{
				Debug.out( "Equipping Shortbow" );
				Inventory.getFirst( "Shortbow" ).interact( "Wield" );
				Time.sleepUntil(() -> Equipment.contains( "Shortbow" ), 400, 4000);
			}
			
			if ( Inventory.contains( "Bronze arrow" ) )
			{
				Debug.out( "Equipping Bronze arrow" );
				Inventory.getFirst( "Bronze arrow" ).interact( "Wield" );
				Time.sleepUntil(() -> Equipment.contains( "Bronze arrow" ), 400, 4000);
			}
		}
		else if ( !shouldLeave()
				&& Equipment.contains( "Shortbow" ) 
				&& Equipment.contains( "Bronze arrow" ) 
				&& Players.getLocal().getTarget() == null )
		{
			Debug.out( "Selecting rat" );
			Npc rat = Npcs.getNearest(n -> n.getName().equals("Giant rat") && n.getTarget() == null );
			
			if ( rat.getTarget() == null )
			{
				rat.interact( "Attack" );
				Time.sleepUntil(() -> rat.getTarget().equals( Players.getLocal() ), 200, 8000 );
			}
		}
		else if ( HintArrow.isPresent()
				&& HintArrow.getPosition().equals( ladder )
				&& SceneObjects.getNearest( "Ladder" ) == null )
		{
			Debug.out( "Approaching ladder" );
			Movement.walkTo( ladder );
			Time.sleep( 0, 500 );
		}
		else if ( HintArrow.isPresent()
				&& HintArrow.getPosition().equals( ladder )
				&& SceneObjects.getNearest( "Ladder" ) != null )
		{
			Debug.out( "Leaving Combat Instructor" );
			SceneObjects.getNearest( "Ladder" ).interact( "Climb-up" );
			Time.sleepUntil(() -> Players.getLocal().getPosition().getY() < 5000, 200, 8000);
		}
		
		return 0;
	}
	
	public boolean shouldLeave()
	{
		if ( HintArrow.isPresent() )
		{
			return HintArrow.getPosition().equals( ladder );
		}
		else
		{
			return false;
		}
	}
}
