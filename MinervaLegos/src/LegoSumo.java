import java.util.ArrayList;
import java.util.List;

public class LegoSumo
{
	private int _initDelay;
	
	private SensorArray _sensorArray;
	private PIDController _pidController;
	private MotorController _motorController;
	
	private float _lastValidError;

	private List<ActiveWeapon> _activeWeapons;
	
	public LegoSumo(MotorController motorController, SensorArray sensorArray, PIDController pidController, int delay)
	{
		_motorController = motorController;
		_sensorArray = sensorArray;
		_pidController = pidController;
		_initDelay = delay;
		_activeWeapons = new ArrayList<ActiveWeapon>();
	}
	
	public void addWeapon(ActiveWeapon weapon, int initPower)
	{
		weapon.setPower(initPower);
		_activeWeapons.add(weapon);
	}
	
	public void update()
	{
		float error = _sensorArray.update();
		if(_sensorArray.detectedCount != 0)
		{
			System.out.println("Error: " + error);
			float power = _pidController.run(error);
			System.out.println("Power: " + power);
			_motorController.move(error, power);
			if(error != 0) _lastValidError = error;
			return;
		}
		_motorController.move(_lastValidError, 200);
	}
	
	public void start(Direction initialDirection)
	{
		System.out.println("Iniciado");
		sleep(_initDelay);
		
		_sensorArray.init();
		for	(ActiveWeapon weapon : _activeWeapons)
		{
			weapon.start();
		}
		if(initialDirection == Direction.Left)
		{
			_motorController.move(-1, 200);
		}
		else
		{
			_motorController.move(1, 200);
		}
		while(_sensorArray.update() == 0);
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