import lejos.robotics.RegulatedMotor;

public class Weapon implements ActiveWeapon
{
	private RegulatedMotor _motor;
	private boolean _revert;
	
	public <T extends RegulatedMotor> Weapon(T motor, boolean revert)
	{
		_motor = motor;
		_revert = revert;
	}

	@Override
	public void start()
	{
		if(_revert)
		{
			_motor.backward();
		}
		else
		{
			_motor.forward();
		}
	}
	
	@Override
	public void setPower(int power)
	{
		power *= _motor.getMaxSpeed()/100;
		_motor.setSpeed(power);
	}
}
