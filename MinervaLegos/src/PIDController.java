
public class PIDController
{
	private int _sampleTime;
	private float _setPoint;
	
	private float _proportionalConstant;
	private float _integralConstant;
	private float _derivativeConstant;


	private long _lastTime;
	private float _lastInput;
	private float _lastOutput;

	private float _integrativeTermSum;

	public PIDController()
	{
		
	}
	
	public PIDController(int sampleTime, float setPoint, float proportionalConstant, float integralConstant, float derivativeConstant)
	{
		sampleTime(sampleTime);
		tunings(proportionalConstant, integralConstant, derivativeConstant);
		setPoint(setPoint);
	}
	
	public float run(float input)
	{
		long now = System.currentTimeMillis();
		long deltaTime = (now - _lastTime);
		if(deltaTime < _sampleTime)
		{
			return _lastOutput;
		}
		
		float error = _setPoint - input;
		
		// Salva o valor acumulado do fator integrativo
		// Isso torna possivel mudar a constante integrativa sem gerar uma mudança abruta na saída
		// já que o acumulo dos erros não é mais multiplicado pelo mesmo valor que antes
		_integrativeTermSum += error * _integralConstant;
		
		// Faz a derivada das entradas para evitar o "derivative kick", que ocorre mudando o setPoint
		// Não acontece em nenhum dos nossos projetos, mas é uma implementação melhor,
		// e o custo computacional é identico
		float dInput = (input - _lastInput);
		
		
		float output = _proportionalConstant * error;	// Proporcional
		output += _integrativeTermSum;		// Integrativo
		output -= _derivativeConstant * dInput;			// Derivativo
		
		_lastInput = input;
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
		// Essa converção não é necessária, mas permite que a gente entre com valores
		// de KI e KD em termos de 1/segundo
		float sampleTimeInSec = ((float)_sampleTime)/1000;
		_integralConstant = integralConstant * sampleTimeInSec;
		_derivativeConstant = derivativeConstant / sampleTimeInSec;
		// A aplicação direta dos valores aqui nas constantes só é possivel porque o
		// tempo de avalição do PID é fixado. Isso evita também que a multiplicação
		// E principalmente a divisão tenham que ser feitas cada vez que o PID é calculado
		// tl;dr: deixa o código mais rápido e mais eficiente.
		
		return this;
	}

	public PIDController sampleTime(int newSampleTime)
	{
	   if (newSampleTime > 0)
	   {
	      float ratio = newSampleTime / _sampleTime;
	      _integralConstant *= ratio;
	      _derivativeConstant /= ratio;
	      _sampleTime = newSampleTime;
	   }
	   else
	   {
		   throw new IllegalArgumentException("newSampleTime não pode ser menor ou igual a zero");
	   }
	   return this;
	}
}
