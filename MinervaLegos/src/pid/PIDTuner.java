package pid;
import java.util.Random;

public class PIDTuner
{
	PIDMode ATuneModeRemember;
	float input = 80, output = 50;

	float kpmodel = 1.5f, taup = 100;
	private float[] _theta;
	float outputStart = 5;
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
		_inputSource = inputSource; // Fonte de entrada caso não esteja em simulação
		_outputDestination = outputDestination;
		_PidController = pidController;/*new PIDController()
				.setPoint(setpoint)
				.tunings(kd, ki, kd)
				.controllerDirection(PIDDirection.DIRECT);*/
		
		_PidAutoTune = new PIDAutoTune(pidController.getControllerDirection());
		_random = new Random(0);
		
		_tuning = false;
		_useSimulation = true;
	}
	
	public void setup()
	{
		if(_useSimulation)
		{
			for(byte i = 0; i < 50; i++)
			{
				_theta[i]=outputStart;
			}
			_modelTime = 0;
		}
		//Setup the pid
		_PidController.controllerMode(PIDMode.AUTOMATIC);

		if(_tuning)
		{
			_tuning = false;
			changeAutoTune();
			_tuning = true;
		}
	}
	
	public void update()
	{
		long now = System.currentTimeMillis();

		if(!_useSimulation)
		{
			//Puxa a entrada do mundo real
			input = _inputSource.read();
		}
		
		if(_tuning)
		{
			boolean runtime = _PidAutoTune.runtime(input);
			output = _PidAutoTune.getOutput();
			
			if (runtime)
			{
				_tuning = false;
			}
			if(!_tuning)
			{
				// Acabamos, seta os parametros
				_PidController.tunings(_PidAutoTune.getProportional(), _PidAutoTune.getIntegrative(), _PidAutoTune.getDerivative());
				printData();
				autoTuneHelper(false);
			}
		}
		else
		{
			_PidController.input(input).run();
		}
		
		if(_useSimulation)
		{
			_theta[30] = output;
			if(now >= _modelTime)
			{
				_modelTime +=100; 
				calculateModel();
			}
		}
		else
		{
			_outputDestination.write(output);
		}
	}
	
	public void calculateModel()
	{
		//cycle the dead time
		for(byte i = 0; i < 49; i++)
		{
			_theta[i] = _theta[i+1];
		}
		//compute the input
		float randomFactor = _random.nextInt(20) - 10;
		input = (kpmodel / taup) *(_theta[0]-outputStart) + input*(1-1/taup) + randomFactor/100;
	}
	
	void changeAutoTune()
	{
		if(!_tuning)
		{
			//Set the output to the desired starting frequency.
			output = aTuneStartValue;
			_PidAutoTune.setNoiseBand(aTuneNoise);
			_PidAutoTune.setOutputStep(aTuneStep);
			_PidAutoTune.setLookbackSec((int)aTuneLookBack);
			autoTuneHelper(true);
			_tuning = true;
		}
		else
		{
			//Cancel autotune
			_PidAutoTune.cancel();
			_tuning = false;
			autoTuneHelper(false);
		}
	}

	void autoTuneHelper(boolean start)
	{
		if(start)
			ATuneModeRemember = _PidController.getControllerMode();
		else
			_PidController.controllerMode(ATuneModeRemember);
	}
	
	public void printData()
	{
		System.out.println("Tune finalizado");
		System.out.println(_PidController.toString());
	}
}
