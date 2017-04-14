
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

	public PIDController(int sampleTime, float setPoint, float proportionalConstant, float integralConstant, float derivativeConstant)
	{
		_sampleTime = sampleTime;
		_setPoint = setPoint;
		_lastTime = System.currentTimeMillis();

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
		// Isso torna possivel mudar a constante integrativa sem gerar uma mudan�a abruta na sa�da
		// j� que o acumulo dos erros n�o � mais multiplicado pelo mesmo valor que antes
		_integrativeTermSum += error * _integralConstant;
		
		// Faz a derivada das entradas para evitar o "derivative kick", que ocorre mudando o setPoint
		// N�o acontece em nenhum dos nossos projetos, mas � uma implementa��o melhor,
		// e o custo computacional � identico
		float dInput = (input - _lastInput);
		
		
		float output = _proportionalConstant * error;	// Proporcional
		output += _integrativeTermSum;		// Integrativo
		output -= _derivativeConstant * dInput;			// Derivativo
		
		_lastInput = input;
		_lastTime = now;
		_lastOutput = output;
		
		return output;
	}
}
