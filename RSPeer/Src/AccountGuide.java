//AccountGuide.java

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

public class AccountGuide extends DisposableTask
{
	public Position banker = new Position( 3122, 3124 );
	public Position pollBooth = new Position( 3119, 3121 );
	public Position innerDoor = new Position( 3125, 3124 );
	public Position bankExit = new Position( 3130, 3124 );
	
	public boolean disposable()
	{
		return ( HintArrow.isPresent()
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( bankExit ) )
			|| ( !Tab.ACCOUNT_MANAGEMENT.getComponent().isExplicitlyHidden() 
				&& (Players.getLocal().getPosition().getX() > 3129 || Tab.PRAYER.getComponent().isVisible()) );
	}
	
	public boolean validate()
	{
		return ( Equipment.contains( "Shortbow" ) || Inventory.contains( "Shortbow" ) )
			&& Players.getLocal().getPosition().getY() < 5000;
	}

	public int execute()
	{
		Debug.out( "Account Guide" );
		
		if ( HintArrow.isPresent() 
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( banker ) )
		{
			Debug.out( "Meeting the banker" );
			
			if ( Npcs.getNearest( "Banker" ) == null )
			{
				Movement.walkTo( banker );
				Time.sleep( 200, 800 );
			}
			else
			{
				Util.talkTo( "Banker" );
			}
		
		}
		else if ( HintArrow.isPresent() 
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( pollBooth )
			//&& !Interfaces.get( 0 ).isOpen() )
			&& !Dialog.canContinue() )
		{
			Debug.out( "Opening Poll booth interface" );
			
			SceneObjects.getNearest( "Poll booth" ).interact( "Use" );
			Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
		}
		else if ( HintArrow.isPresent() 
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( pollBooth )
			&& Dialog.canContinue() )
		{
			Debug.out( "Continuing chat..." );
			Dialog.processContinue();
			Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
		}
		else if ( HintArrow.isPresent()
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( innerDoor )
			&& !Npcs.getNearest( "Account Guide" ).isPositionInteractable() )
		{
			Debug.out( "Approaching Account Guide" );
			Movement.walkTo( Npcs.getNearest( "Account Guide" ) );
		}
		else if ( HintArrow.isPresent()
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( Npcs.getNearest( "Account Guide" ).getPosition() ) 
			&& !Dialog.isOpen() )
		{
			Util.talkTo( "Account Guide" );
		}
		else if ( !HintArrow.isPresent()
			&& Tab.ACCOUNT_MANAGEMENT.getComponent().isVisible()
			&& !Tabs.open( Tab.ACCOUNT_MANAGEMENT ) )
		{
			Debug.out( "Opening Account Management tab" );
			Tabs.open( Tab.ACCOUNT_MANAGEMENT );
		}
		
		return 0;
	}
}
