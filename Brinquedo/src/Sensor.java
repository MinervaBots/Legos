public abstract class Sensor
{
	protected float _weight;
	protected float _error;
	protected boolean _detecting;
	
	public Sensor(float weight, float error)
	{
		_weight = weight;
		_error = error;
	}

	public final float getWeight()
	{
		return _weight;
	}

	public final float getError()
	{
		return _error;
	}
	
	public final boolean isDetecting()
	{
		return _detecting;
	}
	
	public abstract boolean update();
}