//SurvivalExpert.java
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
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
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
import org.rspeer.script.task.Task;

import scripts.Debug;
import scripts.DisposableTask;

public class SurvivalExpert extends DisposableTask
{
	public static Area guideHouse = Area.rectangular( 3087, 3101, 3097, 3112 );
	public static Position taskArea = new Position( 3103, 3096 );
	
	public boolean disposable()
	{
		return Inventory.contains( "Shrimps" );
	}
	
	public boolean validate()
	{
		return !guideHouse.contains( Players.getLocal().getPosition() )
				&& !Inventory.contains("Shrimps");
	}
	
	public int execute()
	{
		Debug.out("SurvivalExpert");
		
		if ( Tab.SKILLS.getComponent().isVisible() )
		{
			Debug.out("Skills Button visible"); 
		}
		else
		{
			Debug.out("Skills Button not visible");
		}
		
		if( Npcs.getNearest( "Survival Expert" ) == null )
		{
			walkToSurvivalExpert();
		}
		else if ( !Tab.INVENTORY.getComponent().isVisible() )
		{
			if ( !Dialog.isViewingChat() )
			{
				talkTo( Npcs.getNearest( "Survival Expert" ) );
			}
			
			continueChat();
			
			Time.sleepUntil(() -> Tab.INVENTORY.getComponent().isVisible(), 200, 4000);
		}
		else if ( !Tab.INVENTORY.isOpen() 
				&& !Tab.SKILLS.getComponent().isVisible() )
		{
			Debug.out("Opening inventory...");
			Tabs.open( Tab.INVENTORY );
			Time.sleepUntil(() -> Tab.INVENTORY.isOpen(), 200, 4000);
		}
		else if ( !Inventory.contains( "Raw shrimps" )
				&& !Inventory.contains( "Shrimps" ) )
				//&& Tab.INVENTORY.isOpen() )
		{
			Debug.out("Catching Shrimp");
			
			if ( !Tab.INVENTORY.isOpen() )
			{
				Tabs.open( Tab.INVENTORY );
				Time.sleepUntil(() -> Tab.INVENTORY.isOpen(), 200, 4000);
			}
		
			if ( Npcs.getNearest( "Fishing spot" ) == null )
			{
				Debug.out("No fishing spot nearby, walking to fishing area");
				walkToSurvivalExpert();
			}
			else
			{
				Npc fishSpot = Npcs.getNearest("Fishing spot");
				
				if ( fishSpot == null )
				{
					Debug.out( fishSpot + ": is null"); 
				}
				else
				{
					fishSpot.interact( "Net" );
					Time.sleepUntil(() -> Players.getLocal().isAnimating(), 200, 4000);
					Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 200, 4000);
				}

			}
		}
		else if ( Inventory.contains( "Raw shrimps" ) 
				&& !Inventory.contains( "Tinderbox" ) )
		{
			Debug.out("Opening skills...");
		
			Tabs.open( Tab.SKILLS );
		
			Time.sleepUntil(() -> Tab.SKILLS.isOpen(), 200, 4000);
		
			if ( !Dialog.isViewingChat() )
			{
				talkTo( Npcs.getNearest( "Survival Expert" ) );
			}
			
			continueChat();
		}
		else if ( Inventory.contains( "Raw shrimps" ) 
				&& !Inventory.contains( "Logs" )
				&& Skills.getExperience( Skill.FIREMAKING ) <= 0 )
		{
			Debug.out("Chopping Tree");
			SceneObjects.getNearest( "Tree" ).interact( "Chop down" );
			
			Time.sleepUntil(() -> Players.getLocal().isAnimating(), 4000);
			Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);
		}
		else if ( Inventory.contains( "Logs" ) )
		{
			Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);
		
			Inventory.getFirst( "Logs" ).interact( "Use" );
			Inventory.getFirst( "Tinderbox" ).interact( "Use" );
			
			Time.sleepUntil(() -> Players.getLocal().isAnimating(), 4000);
			Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);
		}
		else if ( Inventory.contains( "Raw shrimps" )
				&& Skills.getExperience( Skill.FIREMAKING ) >= 0 )
		{
			Inventory.getFirst( "Raw shrimps" ).interact( "Use" );
			
			Time.sleep( 200, 800 );
			
			SceneObjects.getNearest( "Fire" ).interact( "Use" );
			
			Time.sleepUntil(() -> Players.getLocal().isAnimating(), 4000);
			Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 4000);
		}
		
		//TalkToSurvivalExpert
		
		//OpenSkills
		
		//MakeFire
		
		//TalkToSurvivalExpert
		
		//CatchShrimp
		
		//CookShrimp
		
		//ExitArea
		
		return 0;
	}
	
	private void walkToSurvivalExpert()
	{
		Movement.walkTo( taskArea );
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
