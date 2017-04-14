import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;

public class Treta
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
		while(!Button.ENTER.isDown() || initialDirection == LegoSumo.Direction.None)
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
		MotorController motor = new TwoMotorsController(new NXTRegulatedMotor(MotorPort.B), new NXTRegulatedMotor(MotorPort.C), 1f, 1f, false, false);
		PIDController pidController = new PIDController(
				PIDController.Direction.DIRECT, 10, 0, 90f, 0.1f, 10f, -100, 100);
		
		SensorArray sensorArray = new SensorArray(new SensorFilter(3, 2));
		
		sensorArray.addSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S1), 40, 1, -1f));
		sensorArray.addSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S2), 40, 3, 0f));
		sensorArray.addSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S4), 40, 1, 1f));
		
		_legoSumo = new LegoSumo(motor, sensorArray, pidController, 5000);
	}
}