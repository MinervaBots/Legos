package pid;

import java.util.Random;

public class PIDTuner
{
	PIDMode ATuneModeRemember;

	float kpmodel = 1.5f, taup = 100;
	private float[] _theta;
	private float _outputStart;
	float aTuneStep = 50, aTuneNoise = 1, aTuneStartValue = 100;
	int aTuneLookBack = 20;

	private boolean _tuning;
	private long _modelTime;
	private boolean _useSimulation;

	private PIDController _PidController;
	private InputSource _inputSource;
	private OutputDestination _outputDestination;
	private PIDAutoTune _PidAutoTune;
	private Random _random;

	public PIDTuner(PIDController pidController, InputSource inputSource, OutputDestination outputDestination)
	{
		_theta = new float[50];
		_inputSource = inputSource; // Fonte de entrada caso não esteja em
									// simulação
		_outputDestination = outputDestination;
		_PidController = pidController;

		_PidAutoTune = new PIDAutoTune(pidController.getControllerDirection());
		_random = new Random(0);

		_tuning = false;

		if (_inputSource instanceof SimulatedInputSource)
		{
			if (_outputDestination instanceof SimulatedOutputDestination)
			{
				_outputStart = ((SimulatedOutputDestination) _outputDestination).value;
				_useSimulation = true;
			}
			else
			{
				throw new IllegalArgumentException(
						"Se você deseja simular o PID, tanto a entrada como a saída devem ser do tipo Simulated");
			}
		}
	}

	public void setup()
	{
		if (_useSimulation)
		{
			for (byte i = 0; i < 50; i++)
			{
				_theta[i] = _outputStart;
			}
			_modelTime = 0;
		}
		// Setup the pid
		_PidController.controllerMode(PIDMode.AUTOMATIC);

		if (_tuning)
		{
			_tuning = false;
			changeAutoTune();
			_tuning = true;
		}
	}

	public void update()
	{
		long now = System.currentTimeMillis();

		if (_tuning)
		{
			boolean runtime = _PidAutoTune.runtime(_inputSource.read());
			// _output = _PidAutoTune.getOutput();

			if (runtime)
			{
				_tuning = false;
			}
			if (!_tuning)
			{
				// Acabamos, seta os parametros
				_PidController.tunings(_PidAutoTune.getProportional(), _PidAutoTune.getIntegrative(),
						_PidAutoTune.getDerivative());
				printData();
				autoTuneHelper(false);
			}
		}
		else
		{
			_PidController.input(_inputSource.read()).run();
		}

		if (_useSimulation)
		{
			_theta[30] = ((SimulatedOutputDestination) _outputDestination).value;
			if (now >= _modelTime)
			{
				_modelTime += 100;
				calculateModel();
			}
		}
		else
		{
			_outputDestination.write(_PidAutoTune.getOutput());
		}
	}

	public void calculateModel()
	{
		// cycle the dead time
		for (byte i = 0; i < 49; i++)
		{
			_theta[i] = _theta[i + 1];
		}
		// compute the input
		float currentInput = _inputSource.read();
		float randomFactor = _random.nextInt(20) - 10;
		float newInput = (kpmodel / taup) * (_theta[0] - _outputStart) + currentInput * (1 - 1 / taup)
				+ randomFactor / 100;

		((SimulatedInputSource) _inputSource).value = newInput;
	}

	void changeAutoTune()
	{
		if (!_tuning)
		{
			// Set the output to the desired starting frequency.
			_outputDestination.write(aTuneStartValue);
			// output = aTuneStartValue;
			_PidAutoTune.setNoiseBand(aTuneNoise);
			_PidAutoTune.setOutputStep(aTuneStep);
			_PidAutoTune.setLookbackSec((int) aTuneLookBack);
			autoTuneHelper(true);
			_tuning = true;
		}
		else
		{
			// Cancel autotune
			_PidAutoTune.cancel();
			_tuning = false;
			autoTuneHelper(false);
		}
	}

	void autoTuneHelper(boolean start)
	{
		if (start)
			ATuneModeRemember = _PidController.getControllerMode();
		else
			_PidController.controllerMode(ATuneModeRemember);
	}

	public void printData()
	{
		System.out.println("Tune finalizado");
		System.out.println(_PidController.toString());
	}

	public enum Mode
	{
		OFF, ON, ON_SIMULATED,
	}
}
