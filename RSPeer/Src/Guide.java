//Guide.java
package scripts;

import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.api.scene.HintArrow;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Tab;
import org.rspeer.runetek.api.component.tab.Tabs;
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

public class Guide extends DisposableTask
{
	public static Area guideHouse = Area.rectangular( 3087, 3101, 3097, 3112 );
	public Position outside = new Position(3098, 3107);
	
	public boolean disposable()
	{
		return !guideHouse.contains( Players.getLocal().getPosition() );
			//&& Tab.OPTIONS.getComponent().isVisible()
	}

	public boolean validate()
	{
		return guideHouse.contains( Players.getLocal().getPosition() );//!optionsOpen();
	}
	
	public int execute()
	{
		if( !Dialog.isViewingChat() && !optionsVisible() )
		{
			TalkTo( Npcs.getNearest("Gielinor Guide") );
		}
		else if( !optionsVisible() )
		{
			ContinueChat();
			SelectChatOption("I am an experienced player.");
			ContinueChat();
			Time.sleepUntil(() -> optionsVisible(), 200, 4000);
		}
		else if ( !optionsOpen() )
		{
			OpenOptions();
		}
		else if ( Npcs.getNearest("Gielinor Guide").getPosition().equals( HintArrow.getPosition() ) )
		{
			TalkTo( Npcs.getNearest("Gielinor Guide") );
			ContinueChat();
		}
		else
		{
			//Debug.out("Guide: " + Npcs.getNearest("Gielinor Guide").getPosition() + " --- Arrow: " + HintArrow.getPosition() );
			ExitGuideHouse();
		}
		
		return 0;
	}
	
	public void TalkTo(Npc npc)
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
    	
    	Time.sleepUntil(() -> Dialog.isViewingChat(), 200, 4000);
    	
    	if(Dialog.isViewingChat())
    	{
    		Debug.out("TalkTo(): SUCCESS!");
    	}
    	else
    	{
    		Debug.out("TalkTo(): FAIL!");
    	}
    }
    
    public void ContinueChat()
    {
    	Debug.out("Continuing chat...");
    	
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
    
    public void SelectChatOption(String option)
    {
    	Debug.out("Selecting chat option...");
    
    	Time.sleepUntil(() -> Dialog.isViewingChatOptions(), 4000);
    	
    	if(Dialog.isViewingChatOptions())
    	{
    		if(Dialog.process(option))
    		{
    			Debug.out("Chat option selected successfully!");
    		}
    		else
    		{
    			Debug.out("SelectChatOption(): FAIL!");
    		}
    	}
    }
    
    public void OpenOptions()
    {
    	Debug.out("Opening options tab...");
    	
    	InterfaceComponent options = Tab.OPTIONS.getComponent();
    	
    	if(options != null && options.isVisible() && !Tab.OPTIONS.isOpen())
    	{
    		Tabs.open(Tab.OPTIONS);
    	}
    	
    	Time.sleepUntil(() -> Tab.OPTIONS.isOpen(), 4000);
    	
    	if(Tab.OPTIONS.isOpen())
    	{
    		Debug.out("OpenOptions(): SUCCESS!");
    	}
    	else
    	{
    		Debug.out("OpenOptions(): FAIL!");
    	}
    }
    
    public boolean optionsVisible()
    {
    	InterfaceComponent options = Tab.OPTIONS.getComponent();
		return( options.isVisible() );
    }
    
    public boolean optionsOpen()
    {
    	return( Tab.OPTIONS.isOpen() );
    }
    
    public void ExitGuideHouse()
    {
    	Debug.out("Exiting guide's house...");
    	Movement.walkTo( outside );
    }
}
