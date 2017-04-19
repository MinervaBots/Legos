import lejos.hardware.sensor.EV3IRSensor;

public class InfraRedSensor extends Sensor
{
	private EV3IRSensor _sensor;
	private int _maxDistance;
	private float[] _dist;

	public InfraRedSensor(EV3IRSensor lejosSensor, int maxDistance, float weight, float error)
	{
		super(weight, error);
		_sensor = lejosSensor;
		_maxDistance = maxDistance;
		_dist = new float[_sensor.getDistanceMode().sampleSize()];
	}

	@Override
	public boolean update()
	{
		_sensor.getDistanceMode().fetchSample(_dist, 0);
		_value = _dist[0];
		return _value < _maxDistance;
	}
}
