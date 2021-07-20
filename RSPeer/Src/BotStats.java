//Bot Stats
//
//	Updated 8/21/19
//
package scripts;

import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.ScriptCategory;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.event.listeners.RenderListener;
import java.awt.*;

import scripts.Debug;
import scripts.StatusBox;

//GET TIME STAMPS FOR EVERYTHING

//GET BOT STATS

@ScriptMeta(
	name = "Bot Stats",  
	desc = "Collecting and displaying bot information and statistics", 
	developer = "Trikkstr", 
	category = ScriptCategory.TOOL
)

public class BotStats extends Script implements RenderListener
{
	long loops = 0;

	public Debug debugger; //= new Debug();
	public StatusBox statusBox;
	
    @Override
    public void onStart() 
	{
		System.out.println("onStart()");

		//Construct and initialize GUI elements
		debugger = new Debug();
		//debugger.Debug();

		statusBox = new StatusBox();
		statusBox.StartTimer();
    }

    @Override
    public int loop() 
	{
		loops++;	

		//debugger.out(loops+"");
	
        return 1000; 
    }

    @Override
    public void onStop() 
	{
		System.out.println("onStop()");
    }

	//Also try passing RenderEvent e to the GUI classes and let them init their
	//own graphics objects - would not have to import java.awt in this class
	@Override
    public void notify(RenderEvent e) 
	{	
        //Initialize our graphics
        Graphics graphics = e.getSource();
        Graphics2D g = (Graphics2D)graphics;

        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//g.setFont(font);
	
		statusBox.Update(e);
		debugger.Update(e); //BEST LIKE THIS
	}
}

