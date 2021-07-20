package scripts;

import org.rspeer.runetek.event.types.RenderEvent;

import java.util.LinkedList;
import java.awt.*;

//MAKE THIS AN INTERFACE TO ENSURE THAT DEBUG MESSAGES ARE INITIALIZED
//MAKE ALL CALLS TO OUT PRINT TO A LOG FILE
public class Debug
{
	final static Font font = new Font("Monospaced", Font.PLAIN, 12);
	final static float spacing = font.getSize2D();
	final static int numLines = 8;
	final static int lineHeight = (int)spacing + 2;
	final static int canvasX = 4;
	final static int canvasY = 306;
	final static int canvasLength = 512;
	final static int canvasHeight = numLines * lineHeight + 1;
	final static int text_offset = lineHeight - 2;
	final static Color DARK_GRAY = new Color(35, 35, 35, 255);
	final static Color GRAY = new Color(58, 58, 58, 255);
	final static Color LIGHT_GRAY = new Color(188, 188, 188, 255);
	static int y_offset;
	static boolean init = false;
	//final double canvasSizeRatio = 1.0;

	private static String[] messages = new String[numLines];

	public static void Initialize()
	{
		//System.out.println("Debug Module Constructed!");

		/* initialize messages array */
		for( int i = 0; i < numLines; i++ )
		{
			messages[i] = "";
		}
		
		init = true;
		
		out( "Debug Module Initialized!" );
	}

	public static void Update( RenderEvent e )
	{
		if(init == false)
			Initialize();
	
		Graphics2D g = (Graphics2D)e.getSource();
		
		drawCanvas(g);
		drawLines(g);
	}

	private static void drawCanvas( Graphics2D g )
	{
		/* this will be the canvas */
		g.setColor( Color.BLACK );
		g.fillRect(canvasX, canvasY, canvasLength, canvasHeight);
	} 

	private static void drawLines( Graphics2D g )
	{
		for ( int i = 0; i < numLines; i++ )
		{			
			y_offset = lineHeight * i;				

			if(i % 2 == 0)
				g.setColor( DARK_GRAY );
			else
				g.setColor( GRAY );

			g.fillRect(canvasX, canvasY + y_offset + 1, canvasLength, lineHeight - 1);

			g.setColor( LIGHT_GRAY );
			g.drawString(messages[i], canvasX + 4, canvasY + y_offset + text_offset);
		}
	}

	public static void out( String msg )
	{
		//This algorithm can be improved - instead of copying each message
		//into the next index, just rotate which index is printed first
		for ( int i = 0; i < numLines - 1; i++ )
		{
			messages[i] = messages[i+1];
		} 

		messages[numLines - 1] = msg;
	}
}
