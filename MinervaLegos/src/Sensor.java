public abstract class Sensor
{
	protected float _weight;
	protected boolean _detecting;
	
	public Sensor(float weight)
	{
		_weight = weight;
	}
	
	public final float getWeight()
	{
		return _weight;
	}
	
	public final boolean isDetecting()
	{
		return _detecting;
	}
	
	public abstract boolean update();
}