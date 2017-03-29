
public class LegoSumo
{
	private int _delay;
	
	private Sensoring _sensoring;
	private PIDController _pidController;
	private MotorController _motorController;
	
	private float _lastValidError;
	
	public LegoSumo(MotorController motorController, Sensoring sensoring, PIDController pidController, int delay)
	{
		_motorController = motorController;
		_sensoring = sensoring;
		_pidController = pidController;
		_delay = delay;
	}
	
	public void Update()
	{
		float error = _sensoring.Update();
		if(_sensoring.detectedCount != 0)
		{
			System.out.println("Error: " + error);
			float power = _pidController.Run(error);
			//System.out.println("Power: " + power);
			_motorController.Move(error, power);
			if(error != 0) _lastValidError = error;
			return;
		}
		_motorController.Move(_lastValidError, 200);
	}
	
	public void init(Direction initialDirection)
	{
		System.out.println("Iniciado");
	
		sleep(_delay);
		_sensoring.init();
		if(initialDirection == Direction.Left)
		{
			_motorController.Move(-1, 200);
		}
		else if(initialDirection == Direction.Right)
		{
			_motorController.Move(1, 200);
		}
		else
		{
			_motorController.Move(0, 0);
		}
		while(_sensoring.Update() == 0);
	}
	
	private static void sleep(int delay)
	{
		try
		{
			Thread.sleep(delay);
		}
		catch (InterruptedException e)
		{
			//e.printStackTrace();
		}
	}
	
	public enum Direction
	{
		None,
		Left,
		Right
	}
}