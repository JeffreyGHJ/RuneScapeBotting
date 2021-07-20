//MagicInstructor.java

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
import org.rspeer.runetek.api.component.tab.Spell.Modern;
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

public class MagicInstructor extends DisposableTask
{
	public Position monastaryExit = new Position( 3122, 3102 );
	public Position instructorArea = new Position( 3140, 3087 );
	
	
	public boolean disposable()
	{
		return !Tab.EMOTES.getComponent().isExplicitlyHidden();
	}
	
	public boolean validate()
	{
		return monastaryExit.isPositionInteractable();
	}

	public int execute()
	{
		Debug.out( "Magic Instructor" );	
		
		if ( Npcs.getNearest( "Magic Instructor" ) == null ) 
		{
			Movement.walkTo( instructorArea );
			Time.sleep( 200, 800 );
		}
		else if ( Npcs.getNearest( "Magic Instructor" ).isPositionInteractable()
			&& HintArrow.getPosition() != null
			&& HintArrow.getPosition().equals( Npcs.getNearest( "Magic Instructor" ).getPosition() )
			&& !Dialog.isViewingChatOptions() )
		{
			Util.talkTo( "Magic Instructor" );
		}
		else if ( !Tab.MAGIC.getComponent().isExplicitlyHidden()
			&& HintArrow.getPosition() == null
			&& !Tab.MAGIC.isOpen() )
		{
			Debug.out( "Opening Magic tab" );
			Tabs.open( Tab.MAGIC );
			Time.sleepUntil(() -> Tab.MAGIC.isOpen(), 200, 4000 );
		}
		else if ( HintArrow.getPosition() != null
			&& !HintArrow.getPosition().equals( Npcs.getNearest( "Magic Instructor" ).getPosition() ) )
		{
			Debug.out( "Casting Wind Strike on a chicken" );
			Interfaces.getComponent( 218, 6 ).interact( "Cast" );
			Time.sleep( 250, 800 );
			
			Npc chicken = Npcs.getNearest(n -> n.getName().equals("Chicken") && n.getTarget() == null );
			
			if ( chicken.getTarget() == null )
			{
				chicken.interact( "Cast" );
				Time.sleepUntil(() -> HintArrow.getPosition() != null 
									&& HintArrow.getPosition().equals( 
										Npcs.getNearest( "Magic Instructor" ).getPosition() ), 500, 8000 );
			}
		}
		else if ( Dialog.isViewingChatOptions() )
		{
			Dialog.process( "Yes." );
			ContinueChat();
			Dialog.process( "No, I'm not planning to do that." );
			ContinueChat();
		}
			
		return 0;
	}
	
	public void ContinueChat()
    {
    	Debug.out("Continuing chat...");
  
  		if ( Interfaces.getComponent(162, 45).isVisible() ) //Click to continue
		{
			Debug.out("Handling notification...");
			Interfaces.getComponent(162, 45).click();
			Time.sleepUntil(() -> !Interfaces.getComponent(162, 45).isVisible(), 200, 4000);
		}
    	
    	Time.sleepUntil(() -> Dialog.canContinue(), 200, 4000);
    	
    	if(Dialog.canContinue())
    	{
    		Dialog.processContinue();
    	}
    	
    	Time.sleepUntil(() -> Dialog.canContinue(), 200,  4000);
    	
    	if(Dialog.canContinue() /*&& isStopping == false*/)
    	{
    		ContinueChat();
    	}
    }
}
