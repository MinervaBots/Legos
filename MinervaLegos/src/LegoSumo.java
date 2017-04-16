import java.util.ArrayList;
import java.util.List;

import pid.PIDController;
import pid.PIDTuner;

public class LegoSumo
{
	private int _initDelay;

	private SensorArray _sensorArray;
	private PIDController _PidController;
	private MotorController _motorController;

	private float _lastValidPower;

	private List<ActiveWeapon> _activeWeapons;

	public LegoSumo(MotorController motorController, SensorArray sensorArray, PIDController pidController, int delay)
	{
		_motorController = motorController;
		_sensorArray = sensorArray;
		_PidController = pidController;
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
		float direction = _sensorArray.update();
		System.out.println("Error: " + direction);
		if (_sensorArray.detectedCount != 0)
		{
			float power = _PidController.input(direction).run();
			System.out.println("Power: " + power);

			_motorController.move(power);
			if (power != 0)
			{
				// Se temos uma direção pra seguir, deixa ela guardada.
				// 0 é invalido pois o robô iria pra frente e provavelmente
				// sairia da arena.
				_lastValidPower = power;
			}
			return;
		}
		// Segue na ultima direção que o alvo foi localizado
		_motorController.move(_lastValidPower);
	}

	public void start(Direction initialDirection)
	{
		System.out.println("Iniciado");
		sleep(_initDelay);

		_sensorArray.init();
		for (ActiveWeapon weapon : _activeWeapons)
		{
			weapon.start();
		}
		if (initialDirection == Direction.Left)
		{
			_motorController.move(-100);
		}
		else
		{
			_motorController.move(100);
		}
		while (_sensorArray.update() == 0)
			;
	}

	private static void sleep(int delay)
	{
		try
		{
			Thread.sleep(delay);
		}
		catch (InterruptedException e)
		{
			// e.printStackTrace();
		}
	}

	public enum Direction
	{
		None, Left, Right
	}

	// ------------------------------ Tuning ------------------------------
	private PIDTuner _PidTuner;

}