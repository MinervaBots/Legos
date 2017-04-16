package pid;

public class PIDAutoTune
{
	private float _output;
	private float _setPoint;
	private float _noiseBand;
	private PIDControlType _controlType;
	private boolean _running;

	private long _peak1, _peak2;

	private int _sampleTime;
	private int _nLookBack;
	private int _peakType;
	private float[] _lastInputs;
	private float[] _peaks;
	private int _peakCount;
	private boolean _justChanged;
	private float _absMax, _absMin;
	private float _outputStep;
	private float _outputStart;
	private long _lastTime;

	private float _gainMargin /* Ku */, _oscillationPeriod /* Tu */;

	private PIDDirection _controllerDirection;

	public PIDAutoTune(PIDDirection controllerDirection)
	{
		_lastInputs = new float[101];
		_peaks = new float[10];
		_controlType = PIDControlType.PID; // default: PID
		_noiseBand = 0.5f;
		_running = false;
		_outputStep = 30;
		setLookbackSec(10);

		_lastTime = System.currentTimeMillis();
		_controllerDirection = controllerDirection;
	}

	public void cancel()
	{
		_running = false;
	}

	public boolean runtime(float input)
	{
		// boolean justEvaluated = false;
		if (_peakCount > 9 && _running)
		{
			_running = false;
			finishUp();
			return true;
		}
		long now = System.currentTimeMillis();
		if (now - _lastTime < _sampleTime)
			return false;

		_lastTime = now;
		float referanceValue = input;
		// justEvaluated = true;

		if (!_running)
		{
			// initialize working variables the first time around
			_peakType = 0;
			_peakCount = 0;
			_justChanged = false;
			_absMax = referanceValue;
			_absMin = referanceValue;
			_setPoint = referanceValue;
			_running = true;
			_outputStart = _output;
			_output = _outputStart + _outputStep;
		}
		else
		{
			if (referanceValue > _absMax)
				_absMax = referanceValue;
			if (referanceValue < _absMin)
				_absMin = referanceValue;
		}

		// oscillate the output base on the input's relation to the setpoint

		float corretedStep = ((_controllerDirection == PIDDirection.DIRECT) ? _outputStep : -_outputStep);
		if (referanceValue > _setPoint + _noiseBand)
		{
			_output = _outputStart - corretedStep;
		}
		else if (referanceValue < _setPoint - _noiseBand)
		{
			_output = _outputStart + _outputStep;
		}

		boolean isMax = true, isMin = true;
		// isMax = true;
		// isMin = true;
		// id peaks
		for (int i = _nLookBack - 1; i >= 0; i--)
		{
			float val = _lastInputs[i];
			if (isMax)
				isMax = referanceValue > val;
			if (isMin)
				isMin = referanceValue < val;
			_lastInputs[i + 1] = _lastInputs[i];
		}
		_lastInputs[0] = referanceValue;
		if (_nLookBack < 9)
		{
			// we don't want to trust the maxes or mins until the inputs array
			// has been filled
			return false;
		}

		if (isMax)
		{
			if (_peakType == 0)
				_peakType = 1;
			if (_peakType == -1)
			{
				_peakType = 1;
				_justChanged = true;
				_peak2 = _peak1;
			}
			_peak1 = now;
			_peaks[_peakCount] = referanceValue;

		}
		else if (isMin)
		{
			if (_peakType == 0)
				_peakType = -1;
			if (_peakType == 1)
			{
				_peakType = -1;
				_peakCount++;
				_justChanged = true;
			}

			if (_peakCount < 10)
				_peaks[_peakCount] = referanceValue;
		}

		if (_justChanged && _peakCount > 2)
		{
			// we've transitioned. check if we can autotune based on the last
			// peaks
			float avgSeparation = (Math.abs(_peaks[_peakCount - 1] - _peaks[_peakCount - 2])
					+ Math.abs(_peaks[_peakCount - 2] - _peaks[_peakCount - 3])) / 2;
			if (avgSeparation < 0.05 * (_absMax - _absMin))
			{
				finishUp();
				_running = false;
				return true;

			}
		}
		_justChanged = false;
		return false;
	}

	private void finishUp()
	{
		_output = _outputStart;
		// we can generate tuning parameters!
		_gainMargin = 4 * (2 * _outputStep) / ((_absMax - _absMin) * (float) Math.PI);
		_oscillationPeriod = (float) (_peak1 - _peak2) / 1000;
	}

	public float getProportional()
	{
		float weight = _weights[_controlType.getValue()][0];
		return weight * _gainMargin;
	}

	public float getIntegrative()
	{
		float weight = _weights[_controlType.getValue()][1];
		return weight * (_gainMargin / _oscillationPeriod);
	}

	public float getDerivative()
	{
		float weight = _weights[_controlType.getValue()][2];
		return weight * _gainMargin * _oscillationPeriod;
	}

	public void setOutputStep(float Step)
	{
		_outputStep = Step;
	}

	public float GetOutputStep()
	{
		return _outputStep;
	}

	public void setControlType(PIDControlType type) // 0=PI, 1=PID
	{
		_controlType = type;
	}

	public PIDControlType getControlType()
	{
		return _controlType;
	}

	public void setNoiseBand(float Band)
	{
		_noiseBand = Band;
	}

	public float getNoiseBand()
	{
		return _noiseBand;
	}

	public float getOutput()
	{
		return _output;
	}

	public void setLookbackSec(int value)
	{
		if (value < 1)
			value = 1;

		if (value < 25)
		{
			_nLookBack = value * 4;
			_sampleTime = 250;
		}
		else
		{
			_nLookBack = 100;
			_sampleTime = value * 10;
		}
	}

	public int getLookbackSec()
	{
		return _nLookBack * _sampleTime / 1000;
	}

	public enum PIDControlType
	{
		P(0), PI(1), PID(2);

		/*
		 * PD(2), PESSEN_INTEGRAL_RULE(4), SOME_OVERSHOT(5), NO_OVERSHOT(6); //
		 */

		private final int value;

		private PIDControlType(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}
	}

	float[][] _weights = { { 0.5f, 0, 0 }, // P
			{ 0.45f, 0.54f, 0 }, // PI
			{ 0.60f, 1.2f, 3 / 40 }, // PID
			/*
			 * {0.6f, 1/2, 1/8}, {0.7f, 1/2.5f, 3/20}, {0.33f, 1/2, 1/3}, {0.2f,
			 * 1/2, 1/3},
			 */
	};
}