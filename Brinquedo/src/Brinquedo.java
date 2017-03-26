import lejos.hardware.Button;
import lejos.hardware.Key;
import lejos.hardware.KeyListener;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTUltrasonicSensor;

public class Brinquedo
{
	private static LegoSumo _legoSumo;
	
	public static void main(String[] args)
	{
		bindButtons();
		setup();
		assert(_legoSumo != null);
		selectStrategy();
		
		while(true)
		{
			_legoSumo.Update();
		}
	}
	
	private static void bindButtons()
	{
		Button.ESCAPE.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(Key k)
			{
				System.exit(0);
			}

			@Override
			public void keyReleased(Key k)
			{
			}
		});
	}
	
	private static void selectStrategy()
	{
		
	}
	
	private static void setup()
	{
		MotorController motor = new TwoMotorsController(new NXTRegulatedMotor(MotorPort.A), new NXTRegulatedMotor(MotorPort.B), 0f, 0f, false, false);
		PIDController pidController = new PIDController(0.1f, 0.5f, 1f, 5);
		
		Sensoring sensoring = new Sensoring();
		
		sensoring.AddSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S4), 40, -20f));
		//sensoring.AddSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S1), 40, 0f));
		sensoring.AddSensor(new UltraSensor(new NXTUltrasonicSensor(SensorPort.S1), 40, 20f));
		
		_legoSumo = new LegoSumo(motor, sensoring, pidController);
	}
}
