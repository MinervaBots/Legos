
public class PIDController
{
	private int _sampleTime;
	private float _setPoint;
	private float _input;
	
	private float _proportionalConstant;
	private float _integralConstant;
	private float _derivativeConstant;


	private long _lastTime;
	private float _lastInput;
	private float _lastOutput;

	private float _integrativeTermSum;

	private float _minOutput;
	private float _maxOutput;
	
	private boolean _inAuto;
	
	public PIDController()
	{
		// Inicializa em modo autom�tico
		_inAuto = true;
	}
	
	public PIDController(int sampleTime, float setPoint, float proportionalConstant, float integralConstant, float derivativeConstant, float minOutput, float maxOutput)
	{
		this();
		sampleTime(sampleTime);
		tunings(proportionalConstant, integralConstant, derivativeConstant);
		setPoint(setPoint);
		outputLimits(minOutput, maxOutput);
	}
	
	public float run()
	{
		// Se n�o estiver em modo autom�tico retorna antes de calcular qualquer coisa
		if(!_inAuto)
		{
			return _lastOutput;
		}
		
		long now = System.currentTimeMillis();
		long deltaTime = (now - _lastTime);
		if(deltaTime < _sampleTime)
		{
			return _lastOutput;
		}
		
		float error = _setPoint - _input;
		
		// Salva o valor acumulado do fator integrativo
		// Isso torna possivel mudar a constante integrativa sem gerar uma mudan�a abruta na sa�da
		// j� que o acumulo dos erros n�o � mais multiplicado pelo mesmo valor que antes
		_integrativeTermSum += error * _integralConstant;
		// Faz o clamp disso pra evitar que o erro se acumule indefinidamente]
		// e extrapole os limites que o nosso sistema usa.
		// Apesar da sa�da do sistema tamb�m ser limitado, precisa fazer do acumulo dos erros
		// pra que o sistema responda imediatamente a uma mudan�a na entrada e n�o tente compensar o integrativo desnecessariamente
		_integrativeTermSum = clamp(_integrativeTermSum, _minOutput, _maxOutput);
		   
		// Faz a derivada das entradas para evitar o "derivative kick", que ocorre mudando o setPoint
		// N�o acontece em nenhum dos nossos projetos, mas � uma implementa��o melhor,
		// e o custo computacional � identico
		float dInput = (_input - _lastInput);
		
		
		float output = _proportionalConstant * error;	// Proporcional
		output += _integrativeTermSum;					// Integrativo
		output -= _derivativeConstant * dInput;			// Derivativo
		
		// Faz clamp da sa�da do PID tamb�m, pois os fatores proporcional e derivativo tamb�m 
		// podem fazer com que a sa�da extrapole o intervalo de trabalho do sistema
		output = clamp(output, _minOutput, _maxOutput);
		
		_lastInput = _input;
		_lastTime = now;
		_lastOutput = output;
		
		return output;
	}
	
	public PIDController setPoint(float newSetPoint)
	{
		_setPoint = newSetPoint;
		return this;
	}
	
	public PIDController tunings(float proportionalConstant, float integralConstant, float derivativeConstant)
	{
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
		
		return this;
	}

	public PIDController sampleTime(int newSampleTime)
	{
		if(_sampleTime == 0)
		{
			_sampleTime = newSampleTime;
		}
		else if (newSampleTime > 0)
	   {
	      float ratio = newSampleTime / _sampleTime;
	      _integralConstant *= ratio;
	      _derivativeConstant /= ratio;
	      _sampleTime = newSampleTime;
	   }
	   else
	   {
		   throw new IllegalArgumentException("newSampleTime n�o pode ser menor ou igual a zero");
	   }
	   return this;
	}
	
	
	public PIDController outputLimits(float min, float max)
	{
	   if(min > max)
	   {
		   throw new IllegalArgumentException("min n�o pode ser maior que max");
	   }
	   _minOutput = min;
	   _maxOutput = max;
	   
	   _integrativeTermSum = clamp(_integrativeTermSum, _minOutput, _maxOutput);
	   return this;
	}
	
	private static float clamp(float value, float min, float max)
	{
		if(value < min) return min;
		else if(value > max) return max;
		return value;
	}
	
	public void toggle(boolean newAuto)
	{
		// Se for de manual para autom�tico,
		// ajusta alguns valores pra garantir uma transi��o suave entre os modos
		if(newAuto && !_inAuto)
	    {
			_lastInput = _input;
			_integrativeTermSum = clamp(_lastOutput, _minOutput, _maxOutput);
	    }
		_inAuto = newAuto;
	}
	
	public void toggle()
	{
		toggle(!_inAuto);
	}
	
	public PIDController input(float newInput)
	{
		_input = newInput;
		return this;
	}
}
