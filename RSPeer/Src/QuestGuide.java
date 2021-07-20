//QuestGuide.java

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
import org.rspeer.runetek.event.types.ChatMessageEvent;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.listeners.ChatMessageListener;

import org.rspeer.script.Script;
import org.rspeer.script.task.Task;

import scripts.Debug;
import scripts.DisposableTask;

public class QuestGuide extends DisposableTask
{
	public Position chefExit = new Position( 3072, 3090 );
	public Position questGuideHouse = new Position( 3086, 3126 );
	public Position ladderPosition = new Position( 3088, 3119 );
	public InterfaceComponent gameMessage = Interfaces.getComponent(263, 1, 0);

	public boolean disposable()
	{
		return playerEnteredMine() || Inventory.contains( "Bronze pickaxe" );
	}

	public boolean validate()
	{
		return ( Inventory.contains( "Bread" ) && !playerEnteredMine() );
	}
	
	public int execute()
	{
		if ( Interfaces.getComponent(263, 1, 0).getText().contains( "Fancy a run?" ) )
		{
			Movement.toggleRun( false );
			
			Time.sleepUntil(() -> !Movement.isRunEnabled(), 2000);
			
			if ( !Movement.isRunEnabled() )
			{
				Debug.out( "Enabling Run" );
				Movement.toggleRun( true );
				Time.sleepUntil(() -> Movement.isRunEnabled(), 2000);
			}
		}
		else if ( !Movement.isRunEnabled() )
		{
			Movement.toggleRun( true );
			Time.sleepUntil(() -> Movement.isRunEnabled(), 2000);
		}
		else if ( Npcs.getNearest( "Quest Guide" ) == null )
		{
			Debug.out( "Walking to Quest Guide" );
			Movement.walkTo( questGuideHouse );	
		}
		else if ( !Npcs.getNearest( "Quest Guide" ).isPositionInteractable() )
		{
			Debug.out( "Walking to Quest Guide" );
			Movement.walkTo( Npcs.getNearest( "Quest Guide" ) );
		}
		else if ( Npcs.getNearest( "Quest Guide" ).isPositionInteractable()
			&& shouldTalkToQuestGuide() )
		{
			Debug.out( "Talking to Quest Guide" );
			talkTo( Npcs.getNearest( "Quest Guide" ) );
			continueChat();
		}
		else if ( Tab.QUEST_LIST.getComponent().isVisible() 
				&& !shouldTalkToQuestGuide()
				&& !Tab.QUEST_LIST.isOpen() )
		{
			Debug.out( "Opening quest list" );
			Tabs.open( Tab.QUEST_LIST );
			Time.sleepUntil(() -> Tab.QUEST_LIST.isOpen(), 2000);
		}
		else if ( shouldLeave() )
		{
			Debug.out( "Leaving Quest Guide" );
			SceneObjects.getNearest( "Ladder" ).interact( "Climb-down" );
			Time.sleepUntil(() -> playerEnteredMine(), 8000);
		}	
	
		return 0;
	}
	
	public boolean shouldTalkToQuestGuide()
	{
		if ( ( HintArrow.getPosition() == null ) 
			|| Npcs.getNearest("Quest Guide").equals( null ) )
		{
			return false;
		}
		else
		{
			return HintArrow.getPosition().equals( Npcs.getNearest("Quest Guide").getPosition() );
		}
	}
	
	public boolean shouldLeave()
	{
		if ( !HintArrow.isPresent() )
		{
			Debug.out("shouldLeave(): HintArrow == null");
			return false;
		}
		else 
		{
			Debug.out("shouldLeave(): HintArrow == ladderPosition");
			return HintArrow.getPosition().equals( ladderPosition );
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
    }
    
    public void continueChat()
    {
    	Debug.out("Continuing chat...");
    	
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
