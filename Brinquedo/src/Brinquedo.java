import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.NXTUltrasonicSensor;

public class Brinquedo
{
	private static LegoSumo _legoSumo;
	
	public static void main(String[] args)
	{
		setup();
		bindButtons();
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
		MotorController motor = new TwoMotorsController(new NXTRegulatedMotor(MotorPort.B), new NXTRegulatedMotor(MotorPort.D), 1f, 1f, false, false);
		PIDController pidController = new PIDController(0, 3f, 0.08f, 3.6f);

		Sensoring sensoring = new Sensoring();
		
		sensoring.addSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S1), 40, -20f));
		sensoring.addSensor(new InfraRedSensor(new EV3IRSensor(SensorPort.S2), 40, 0f));
		sensoring.addSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S4), 40, 20f));
		
		_legoSumo = new LegoSumo(motor, sensoring, pidController, 5000);
		_legoSumo.addWeapon(new Weapon(new NXTRegulatedMotor(MotorPort.C), false), 100);
	}
}