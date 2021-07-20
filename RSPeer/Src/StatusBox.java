package scripts;

import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.event.types.RenderEvent;
import java.util.concurrent.TimeUnit;
import java.awt.*;

public class StatusBox
{
	//final private Font font = new Font("Tahoma", Font.PLAIN, 10);
	final static private Font font = new Font("Monospaced", Font.PLAIN, 12);
	final static private float spacing = font.getSize2D();

	final static private int canvasX = 0;
	final static private int canvasY = 20;
	final static private int canvasLength = 240;
	final static private int canvasHeight = 60;

	final static private int header = 35;
	final static private int indent = 10;

	private static String task = "";

    private static StopWatch stopWatch;

	//final double ratio = 1.0;
	//final Color DARK_GRAY = new Color(35, 35, 35, 255);
	//final int lineHeight = (int)spacing + 2;

	//THE CONSTRUCTOR METHOD
	public static void StartTimer()
	{
		stopWatch = StopWatch.start();
		System.out.println("StatusBox Module Constructed!");
	}

	public static void Update( RenderEvent e )
	{
		Graphics2D g = (Graphics2D)e.getSource();
		
		drawCanvas(g);
		drawStats(g);
	}

	private static void drawCanvas( Graphics2D g )
	{
		/*Black background*/
		g.setColor( Color.BLACK );
 		g.fillRect( canvasX, canvasY, canvasLength, canvasHeight);	

		/*White outline*/
		g.setColor( Color.WHITE);
		g.drawRect( canvasX, canvasY, canvasLength, canvasHeight);
	} 

	private static void drawStats( Graphics2D g )
	{
		g.drawString("StatusBox Module v0.1", indent, header);
        g.drawString("Task: " + task, indent, header + spacing);
        g.drawString("Runtime: " + stopWatch.toElapsedString(), indent, header + spacing * 2);
	}


	//Dynamically Add info to the statusBox canvas
	/*
	public void out( String msg )
	{
	
	}
	*/
}
