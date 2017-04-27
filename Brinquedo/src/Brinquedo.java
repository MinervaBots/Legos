import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;
import lejos.utility.Delay;

public class Brinquedo
{
	private static LegoSumo _legoSumo;
	
	public static void main(String[] args)
	{
		bindButtons();
		setup();
		selectStrategy();
		
		while(true)
		{
			_legoSumo.update();
		}
	}
	
	private static void bindButtons()
	{
		Button.ESCAPE.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(Key b)
			{
				System.exit(0);
			}

			@Override
			public void keyReleased(Key b)
			{
			  
			}
		});
	}

	private static void selectStrategy()
	{
		System.out.println("Estratégia");
		LegoSumo.Direction initialDirection = LegoSumo.Direction.None;
		while(!Button.ENTER.isDown())
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
		MotorController motor = new TwoMotorsController(new NXTRegulatedMotor(MotorPort.B), new NXTRegulatedMotor(MotorPort.D), 1f, 1f, true, true);

		PIDController pidController = new PIDController()
				.setPoint(0)
				.sampleTime(20)
				.tunings(95f, 1.5f, 3.6f)
				.outputLimits(-100, 100);
		
		SensorArray sensorArray = new SensorArray();
		
		sensorArray.addSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S1), 40, 1, -1f));
		//sensorArray.addSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S2), 40, 2, 0));
		sensorArray.addSensor(new InfraRedSensor(new EV3IRSensor(SensorPort.S2), 40, 3, 0f));
		sensorArray.addSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S4), 40, 1, 1f));
		
		_legoSumo = new LegoSumo(motor, sensorArray, pidController, 5000);
		_legoSumo.addWeapon(new Weapon(new NXTRegulatedMotor(MotorPort.C), false), 100);

	}
}