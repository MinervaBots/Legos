
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
		// Essa conver��o n�o � necess�ria, mas permite que a gente entre com valores
		// de KI e KD em termos de 1/segundo
		float sampleTimeInSec = ((float)_sampleTime)/1000;
		_integralConstant = integralConstant * sampleTimeInSec;
		_derivativeConstant = derivativeConstant / sampleTimeInSec;
		// A aplica��o direta dos valores aqui nas constantes s� � possivel porque o
		// tempo de avali��o do PID � fixado. Isso evita tamb�m que a multiplica��o
		// E principalmente a divis�o tenham que ser feitas cada vez que o PID � calculado
		// tl;dr: deixa o c�digo mais r�pido e mais eficiente.
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
