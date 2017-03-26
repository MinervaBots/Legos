import java.util.ArrayList;
import java.util.List;

public class Sensoring
{
	private List<Sensor> _sensors;
	
	public Sensoring()
	{
		_sensors = new ArrayList<Sensor>();
	}
	
	public void AddSensor(Sensor sensor)
	{
		_sensors.add(sensor);
	}
	
	public float Update()
	{
		int errorsSum = 0;
		int detectedCount = 0;
		for(Sensor sensor : _sensors)
		{
			if(sensor.isDetecting())
			{
				detectedCount++;
				errorsSum += sensor.getWeight();
			}
		}
		return errorsSum / detectedCount;
	}
}
