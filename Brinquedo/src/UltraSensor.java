import lejos.hardware.sensor.NXTUltrasonicSensor;

public class UltraSensor implements Sensor
{
	private float _weight;
	private NXTUltrasonicSensor _sensor;
	private int _maxDistance;
	private float[] _dist;
	
	public UltraSensor(NXTUltrasonicSensor lejosSensor, int maxDistance, float weight)
	{
		_sensor = lejosSensor;
		_weight = weight;
		_maxDistance = maxDistance;
		_dist = new float[_sensor.getDistanceMode().sampleSize()];
	}
	
	@Override
	public float getWeight()
	{
		return _weight;
	}
	
	@Override
	public boolean isDetecting()
	{
		_sensor.getDistanceMode().fetchSample(_dist, 0);
		return _dist[0] < _maxDistance;
	}
}