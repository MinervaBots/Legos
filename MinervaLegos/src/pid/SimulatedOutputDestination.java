package pid;

public class SimulatedOutputDestination implements OutputDestination
{
	public float value;

	public SimulatedOutputDestination(float value)
	{
		this.value = value;
	}

	@Override
	public void write(float value)
	{
		this.value = value;
	}
}
