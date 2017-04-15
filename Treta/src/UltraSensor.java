import lejos.nxt.UltrasonicSensor;

public class UltraSensor extends Sensor
{
	private UltrasonicSensor _sensor;
	private int _maxDistance;
	
	public UltraSensor(UltrasonicSensor lejosSensor, int maxDistance, float weight, float error)
	{
		super(weight, error);
		_sensor = lejosSensor;
		_maxDistance = maxDistance;
	}
	
	@Override
	public boolean update()
	{
		_value = _sensor.getDistance();
		return _value < _maxDistance;
	}
}