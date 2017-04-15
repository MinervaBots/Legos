import lejos.robotics.RegulatedMotor;

public class TwoMotorsController implements MotorController
{
	private RegulatedMotor _leftMotor;
	private RegulatedMotor _rightMotor;
	private float _maxSpeedPercentage;
	
	private float _offsetLeft;
	private float _offsetRight;
	private char _invertionLeft;
	private char _invertionRight;
	
	public <T extends RegulatedMotor> TwoMotorsController(T leftMotor, T rightMotor, float offsetLeft, float offsetRight, boolean invertLeft, boolean invertRight)
	{
		_leftMotor = leftMotor;
		_rightMotor = rightMotor;
		_offsetLeft = offsetLeft;
		_offsetRight = offsetRight;
		_invertionLeft = (char) (invertLeft ? -1 : 1);
		_invertionRight = (char) (invertRight ? -1 : 1);
		_maxSpeedPercentage = rightMotor.getMaxSpeed()/100;
	}
	
	@Override
	public void move(float power)
	{
		float leftPower = 100;
		float rightPower = 100;
		if(power < 0)
		{
			leftPower = 100 - Math.abs(power);
		}
		else if(power > 0)
		{
			rightPower = 100 - Math.abs(power);
		}
		
		leftPower = Math.round(leftPower * _maxSpeedPercentage * _invertionLeft * _offsetLeft);
		rightPower = Math.round(rightPower * _maxSpeedPercentage * _invertionRight * _offsetRight);

		_leftMotor.setSpeed((int)Math.abs(leftPower));
		_rightMotor.setSpeed((int)Math.abs(rightPower));
	
		if(leftPower < 0)
		{
			_leftMotor.backward();
		}
		else if(leftPower > 0)
		{
			_leftMotor.forward();
		}

		if(rightPower < 0)
		{
			_rightMotor.backward();
		}
		else if(rightPower > 0)
		{
			_rightMotor.forward();
		}
	}

	@Override
	public void write(float value)
	{
		move(value);
	}
}