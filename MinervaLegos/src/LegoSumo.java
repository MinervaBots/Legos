import java.util.ArrayList;
import java.util.List;

public class LegoSumo
{
	private int _initDelay;
	
	private Sensoring _sensoring;
	private PIDController _pidController;
	private MotorController _motorController;
	
	private float _lastValidError;

	private List<ActiveWeapon> _activeWeapons;
	
	public LegoSumo(MotorController motorController, Sensoring sensoring, PIDController pidController, int delay)
	{
		_motorController = motorController;
		_sensoring = sensoring;
		_pidController = pidController;
		_initDelay = delay;
		_activeWeapons = new ArrayList<ActiveWeapon>();
	}
	
	public void addWeapon(ActiveWeapon weapon, int initPower)
	{
		weapon.setPower(initPower);
		_activeWeapons.add(weapon);
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
	
	public void start(Direction initialDirection)
	{
		System.out.println("Iniciado");
		sleep(_initDelay);
		
		_sensoring.init();
		for	(ActiveWeapon weapon : _activeWeapons)
		{
			weapon.start();
		}
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