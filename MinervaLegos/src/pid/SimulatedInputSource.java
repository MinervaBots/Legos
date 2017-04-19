package pid;

public class SimulatedInputSource implements InputSource
{
	public float value;

	public SimulatedInputSource(float value)
	{
		this.value = value;
	}

	@Override
	public float read()
	{
		return value;
	}
}
