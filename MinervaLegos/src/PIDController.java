
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
		_lastError = 0;
		_errorSum = 0;
	}
	
	public float run(float intput)
	{
		long now = System.currentTimeMillis();
		long deltaTime = (now - _lastTime);

		float error = _setPoint - intput;
		_errorSum += (error * deltaTime);
		float dErr = (error - _lastError);// / deltaTime;
		   
		
		float output = _proportionalConstant * error;	// Proporcional
		output += _integralConstant * _errorSum;		// Integrativo
		output += _derivativeConstant * dErr;			// Derivativo
		
		_lastError = error;
		_lastTime = now;
		
		return output;
	}
}
