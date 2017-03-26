import lejos.nxt.UltrasonicSensor;

public class UltraSensor implements Sensor
{
	private float _weight;
	private UltrasonicSensor _sensor;
	private int _maxDistance;
	
	public UltraSensor(UltrasonicSensor lejosSensor, int maxDistance, float weight)
	{
		_sensor = lejosSensor;
		_weight = weight;
		_maxDistance = maxDistance;
	}
	
	@Override
	public float getWeight()
	{
		return _weight;
	}
	
	@Override
	public boolean isDetecting()
	{
		return _sensor.getDistance() < _maxDistance;
	}
}