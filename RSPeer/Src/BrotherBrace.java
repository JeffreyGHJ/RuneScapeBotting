//BrotherBrace.java

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

public class BrotherBrace extends DisposableTask
{
	public Position bankExit = new Position( 3130, 3124 );
	public Area guideArea = Area.rectangular( 3125, 3123, 3129, 3125 );
	public Position monastaryExit = new Position( 3122, 3102 );
	
	
	public boolean disposable()
	{
		return monastaryExit.isPositionInteractable();
		
			//!Tab.FRIENDS_LIST.getComponent().isExplicitlyHidden()
			/*HintArrow.isPresent()
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( monastaryExit );
			*/
	}
	
	public boolean validate()
	{
		return ( ( HintArrow.isPresent() && HintArrow.getPosition() != null && HintArrow.getPosition().equals( bankExit ) )
			|| ( !Tab.ACCOUNT_MANAGEMENT.getComponent().isExplicitlyHidden() && !guideArea.contains( Players.getLocal().getPosition() ) 
			&&  Tab.FRIENDS_LIST.getComponent().isExplicitlyHidden() )
			|| ( !Tab.FRIENDS_LIST.getComponent().isExplicitlyHidden() && !Players.getLocal().getPosition().equals( monastaryExit ) ) );
	}

	public int execute()
	{
		Debug.out( "Brother Brace" );
		
		if( HintArrow.getPosition() != null )
		{
			Debug.out( "Hint Arrow is not null" );		
		}
		else
		{
			Debug.out( "Hint Arrow is null" );
		}
		
		
		if ( Npcs.getNearest( "Brother Brace" ) == null )
		{
			Debug.out( "Approaching Brother Brace" );
			Movement.walkTo( monastaryExit );
			Time.sleep( 200, 800 );
		}
		//else if brother brace is not null AND not close, move closer (bot got stuck in room after bank because of hint arrow)
		else if ( HintArrow.isPresent()
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( Npcs.getNearest( "Brother Brace" ).getPosition() ) )
		{
			Util.talkTo( "Brother Brace" );
		}
		else if ( !HintArrow.isPresent()
			&& !Tab.PRAYER.getComponent().isExplicitlyHidden()
			&& !Tab.PRAYER.isOpen() )
		{
			Debug.out( "Opening Prayer Tab" );
			Tabs.open( Tab.PRAYER );
			Time.sleepUntil(() -> Tab.PRAYER.isOpen(), 200, 4000);
		}
		else if ( HintArrow.getPosition() == null
			&& !Tab.FRIENDS_LIST.getComponent().isExplicitlyHidden()
			&& !Tab.FRIENDS_LIST.isOpen() )
		{
			Debug.out( "Opening Freinds List Tab" );
			Tabs.open( Tab.FRIENDS_LIST );
			Time.sleepUntil(() -> Tab.FRIENDS_LIST.isOpen(), 200, 4000);
		}
		else if ( HintArrow.getPosition() != null 
			&& HintArrow.getPosition().equals( monastaryExit ))
		{
			Debug.out( "Leaving Monastary" );
			Movement.walkTo( monastaryExit );
		}
		
		return 0;
	}
}
