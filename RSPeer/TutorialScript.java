//Tutorial Script

import org.rspeer.script.Script;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.ScriptCategory;

//GET TIME STAMPS FOR EVERYTHING

//GET BOT STATS

@ScriptMeta(
	name = "Tutorial Script",  
	desc = "Trying to get a local script running", 
	developer = "Trikkstr", 
	category = ScriptCategory.TOOL
)

public class TutorialScript extends Script 
{
	
    @Override
    public void onStart() 
	{
		System.out.println("onStart()");
    }

    @Override
    public int loop() 
	{
		/*
		*	Code in here starting from the top-down will be ran and repeated.
		*
		*	The rate of repeat is defined by the returning int, 
		*	this number represents milliseconds. 1000ms = 1 second.
        */

		System.out.println("loop()");		

        return 750; 
    }

    @Override
    public void onStop() 
	{
		System.out.println("onStop()");
    }
}

