import java.util.ArrayList;
import java.util.List;

import pid.InputSource;

public class SensorArray implements InputSource
{
	public int detectedCount;
	
	private List<Sensor> _sensors;
	private SensorFilter _filter;
	private float _value;
	
	public SensorArray()
	{
		this(null);
	}
	
	public SensorArray(SensorFilter filter)
	{
		_filter = filter;
		_sensors = new ArrayList<Sensor>();
	}
	
	public void addSensor(Sensor sensor)
	{
		_sensors.add(sensor);
	}
	
	public void init()
	{
		if(_filter == null)
		{
			_filter = new SensorFilter(1, 1);
		}
		Sensor[] sensorArray = new Sensor[_sensors.size()];
		_filter.init(_sensors.toArray(sensorArray));
		_filter.start();
		
	}
	
	public float update()
	{
		int errorsSum = 0;
		detectedCount = 0;
		float weightSum = 0;
		for(Sensor sensor : _sensors)
		{
			if(sensor.isDetecting())
			{
				detectedCount += 1;
				errorsSum += sensor.getError();
				weightSum += sensor.getWeight();
			}
		}
		_value = detectedCount == 0 ? 0 : errorsSum / weightSum;
		return _value;
	}

	@Override
	public float read() {
		return _value;
	}
}
