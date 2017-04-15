import pid.InputSource;

public abstract class Sensor implements InputSource
{
	protected float _weight;
	protected float _error;
	protected boolean _detecting;
	protected float _value;
	
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
	

	@Override
	public float read()
	{
		return _value;
	}
}