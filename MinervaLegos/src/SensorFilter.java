
public class SensorFilter extends Thread
{
	private Sensor[] _sensors;
	private int _sampleSize;
	private int _positiveCnt;
	private boolean _inited;

	public SensorFilter(int sampleSize, int positiveCnt)
	{
		_sampleSize = sampleSize;
		_positiveCnt = positiveCnt;
	}

	public void init(Sensor[] sensors)
	{
		_sensors = sensors;
		_inited = true;
	}

	@Override
	public void run()
	{
		while (true)
		{
			if (!_inited)
				return;

			for (Sensor sensor : _sensors)
			{
				sensor._detecting = run(sensor);
			}
		}
	}

	private boolean run(Sensor sensor)
	{
		int cnt = 0;
		for (int i = 0; i < _sampleSize; i++)
		{
			if (sensor.update())
			{
				if (++cnt >= _positiveCnt)
				{
					return true;
				}
			}
		}
		return false;
	}
}