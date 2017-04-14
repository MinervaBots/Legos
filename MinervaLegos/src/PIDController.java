
public class PIDController
{
	private int _sampleTime;
	private float _setPoint;
	
	private float _proportionalConstant;
	private float _integralConstant;
	private float _derivativeConstant;


	private float _errorSum;
	private long _lastTime;
	private float _lastError;
	private float _lastOutput;

	private boolean _limitIntegralTerm;
	private float _integralMin;
	private float _integralMax;

	public PIDController(int sampleTime, float setPoint, float proportionalConstant, float integralConstant, float derivativeConstant)
	{
		_sampleTime = sampleTime;
		_setPoint = setPoint;
		_lastTime = System.currentTimeMillis();
		_lastError = 0;
		_errorSum = 0;
		_limitIntegralTerm = false;

		_proportionalConstant = proportionalConstant;
		// Essa converção não é necessária, mas permite que a gente entre com valores
		// de KI e KD em termos de 1/segundo
		float sampleTimeInSec = ((float)_sampleTime)/1000;
		_integralConstant = integralConstant * sampleTimeInSec;
		_derivativeConstant = derivativeConstant / sampleTimeInSec;
		// A aplicação direta dos valores aqui nas constantes só é possivel porque o
		// tempo de avalição do PID é fixado. Isso evita também que a multiplicação
		// E principalmente a divisão tenham que ser feitas cada vez que o PID é calculado
		// tl;dr: deixa o código mais rápido e mais eficiente.
	}
	
	public void setIntegralLimits(float min, float max)
	{
		_limitIntegralTerm = true;
		_integralMin = min;
		_integralMax = max;
	}
	
	public float run(float intput)
	{
		long now = System.currentTimeMillis();
		long deltaTime = (now - _lastTime);
		if(deltaTime < _sampleTime)
		{
			return _lastOutput;
		}
		
		float error = _setPoint - intput;
		
		_errorSum += error;
		if(_limitIntegralTerm)
		{
			if(_errorSum < _integralMin)
			{
				_errorSum = _integralMin;
			}
			else if(_errorSum > _integralMax)
			{
				_errorSum = _integralMax;
			}
		}
		
		float dErr = (error - _lastError);
		
		
		float output = _proportionalConstant * error;	// Proporcional
		output += _integralConstant * _errorSum;		// Integrativo
		output += _derivativeConstant * dErr;			// Derivativo
		
		_lastError = error;
		_lastTime = now;
		_lastOutput = output;
		
		return output;
	}
}
