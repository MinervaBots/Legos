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
		
	}
	
	private static void setup()
	{
		MotorController motor = new TwoMotorsController(new NXTRegulatedMotor(MotorPort.A), new NXTRegulatedMotor(MotorPort.B), 0f, 0f, false, false);
		PIDController pidController = new PIDController(0.1f, 0.5f, 1f, 5);
		
		Sensoring sensoring = new Sensoring();
		
		sensoring.AddSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S4), 40, -20f));
		sensoring.AddSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S1), 40, 0f));
		sensoring.AddSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S1), 40, 20f));
		
		_legoSumo = new LegoSumo(motor, sensoring, pidController);
	}
}