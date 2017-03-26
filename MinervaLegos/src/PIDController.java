
public class PIDController
{
	private float _proportionalConstant;
	private float _integralConstant;
	private float _derivativeConstant;
	
	private float _proportional;
	private float _integral;
	private float _derivative;
	private float _lastError;
	private float _lastRun;
	private int _delayMs;
	
	public PIDController(float proportionalConstant, float integralConstant, float derivativeConstant, int delayMs)
	{
		_proportionalConstant = proportionalConstant;
		_integralConstant = integralConstant;
		_derivativeConstant = derivativeConstant;
		_delayMs = delayMs;
	}
	
	public float Run(float error)
	{
		if ((System.currentTimeMillis() - _lastRun) > _delayMs)
		{
			_integral += _integralConstant * error;
			_derivative = _derivativeConstant * (error - _lastError);
			_proportional = _proportionalConstant * error;
			
			_lastError = error;
			_lastRun = System.currentTimeMillis();
		}
		return (_proportional + _integral + _derivative);
	}
}
