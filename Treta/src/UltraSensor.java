import lejos.nxt.UltrasonicSensor;

public class UltraSensor extends Sensor
{
	private UltrasonicSensor _sensor;
	private int _maxDistance;
	
	public UltraSensor(UltrasonicSensor lejosSensor, int maxDistance, float weight)
	{
		super(weight);
		_sensor = lejosSensor;
		_maxDistance = maxDistance;
	}
	
	@Override
	public boolean update()
	{
		return _sensor.getDistance() < _maxDistance;
	}
}