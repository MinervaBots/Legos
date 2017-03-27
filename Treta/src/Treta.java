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
		//selectStrategy();
		while(true)
		{
			//_legoSumo.Move(100, 100);
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
		Button.ENTER.waitForPressAndRelease();
	}
	
	private static void setup()
	{
		MotorController motor = new TwoMotorsController(new NXTRegulatedMotor(MotorPort.B), new NXTRegulatedMotor(MotorPort.C), 1f, 1f, false, false);
		PIDController pidController = new PIDController(0, 0.4f, 0.01f, 10f);
		
		Sensoring sensoring = new Sensoring();
		
		sensoring.AddSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S1), 40, -20f));
		sensoring.AddSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S2), 40, 0f));
		sensoring.AddSensor(new UltraSensor(new UltrasonicSensor(SensorPort.S4), 40, 20f));
		
		_legoSumo = new LegoSumo(motor, sensoring, pidController);
	}
}