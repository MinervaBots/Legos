import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import pid.*;
import pid.PIDDirection;
import pid.PIDTuner;
import pid.PIDTuner.Mode;
import pid.SimulatedInputSource;

public class Treta
{
	private static LegoSumo _legoSumo;
	private static PIDController _PidController;

	private static PIDTuner _PidAutoTuner;
	private static PIDTuner.Mode _PidTunerMode = Mode.ON_SIMULATED;
	private static InputSource _inputSource;
	private static OutputDestination _outputDestination;

	public static void main(String[] args)
	{
		setup();

		if (_PidTunerMode == PIDTuner.Mode.OFF)
		{
			bindButtons();
			selectStrategy();
		}
		else
		{
			_PidAutoTuner = new PIDTuner(_PidController, _inputSource, _outputDestination);
		}

		while (true)
		{
			if (_PidTunerMode == PIDTuner.Mode.OFF)
			{
				_legoSumo.update();
			}
			else
			{
				_PidAutoTuner.update();
			}
		}
	}

	private static void bindButtons()
	{
		Button.ESCAPE.addButtonListener(new ButtonListener()
		{
			public void buttonPressed(Button b)
			{
				System.exit(0);
			}

			public void buttonReleased(Button b)
			{

			}
		});
	}

	private static void selectStrategy()
	{
		System.out.println("Estratégia");
		LegoSumo.Direction initialDirection = LegoSumo.Direction.None;
		while (!Button.ENTER.isDown() || initialDirection == LegoSumo.Direction.None)
		{
			if (Button.LEFT.isDown())
			{
				initialDirection = LegoSumo.Direction.Left;
				System.out.println("Esquerda");
				break;
			}
			else if (Button.RIGHT.isDown())
			{
				initialDirection = LegoSumo.Direction.Right;
				System.out.println("Direita");
				break;
			}
		}
		Button.ENTER.waitForPressAndRelease();
		_legoSumo.start(initialDirection);
	}

	private static void setup()
	{
		_PidController = new PIDController().controllerDirection(PIDDirection.DIRECT).setPoint(0).sampleTime(10)
				.tunings(3f, 0.08f, 3.6f).outputLimits(-100, 100);

		if (_PidTunerMode == PIDTuner.Mode.ON_SIMULATED)
		{
			_inputSource = new SimulatedInputSource(2);
			_outputDestination = new SimulatedOutputDestination(4);
		}
		else
		{
			SensorArray sensorArray = new SensorArray(new SensorFilter(3, 2));
			sensorArray.addSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S1), 40, 1, -1f));
			sensorArray.addSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S2), 40, 3, 0f));
			sensorArray.addSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S4), 40, 1, 1f));

			MotorController motor = new TwoMotorsController(new NXTRegulatedMotor(MotorPort.B),
					new NXTRegulatedMotor(MotorPort.C), 1f, 1f, false, false);

			if (_PidTunerMode == PIDTuner.Mode.ON)
			{
				_inputSource = sensorArray;
				_outputDestination = motor;
			}
			_legoSumo = new LegoSumo(motor, sensorArray, _PidController, 5000);
		}
	}
}