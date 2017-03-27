
public class PIDController
{
	private float _setPoint;
	private float _proportionalConstant;
	private float _integralConstant;
	private float _derivativeConstant;


	private float _errorSum;
	private long _lastTime;
	private float _lastError;
	
	public PIDController(float setPoint, float proportionalConstant, float integralConstant, float derivativeConstant)
	{
		_setPoint = setPoint;
		_proportionalConstant = proportionalConstant;
		_integralConstant = integralConstant;
		_derivativeConstant = derivativeConstant;
		_lastTime = System.currentTimeMillis();
	}
	
	public float Run(float intput)
	{
		long now = System.currentTimeMillis();
		long timeChange = (now - _lastTime);

		float error = _setPoint - intput;
		_errorSum += (error * timeChange);
		float dErr = (error - _lastError) / timeChange;
		   
		
		float output = _proportionalConstant * error;
		output += _integralConstant * _errorSum;
		output += _derivativeConstant * dErr;
		
		_lastError = error;
		_lastTime = now;
		
		return output;
	}
}
