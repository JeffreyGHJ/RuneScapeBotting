//DisposeableTask.java
package scripts;

import org.rspeer.script.task.Task;

public abstract class DisposableTask extends Task
{
	public abstract boolean disposable();
}
