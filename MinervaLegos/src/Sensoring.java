import java.util.ArrayList;
import java.util.List;

public class Sensoring
{
	public int detectedCount;
	
	private List<Sensor> _sensors;
	private SensorFilter _filter;
	
	public Sensoring()
	{
		this(null);
	}
	
	public Sensoring(SensorFilter filter)
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
		return detectedCount == 0 ? 0 : errorsSum / weightSum;
	}
}
