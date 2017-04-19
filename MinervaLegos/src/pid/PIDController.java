package pid;

public class PIDController
{
	private PIDDirection _controllerDirection;
	private PIDMode _controllerMode;
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
		_lastTime = System.currentTimeMillis();
		controllerMode(PIDMode.AUTOMATIC); // O padrão é em modo automático
		controllerDirection(PIDDirection.DIRECT);
	}

	public PIDController(PIDDirection direction, int sampleTime, float setPoint, float proportionalConstant,
			float integralConstant, float derivativeConstant, float minOutput, float maxOutput)
	{
		this();
		controllerDirection(direction);
		sampleTime(sampleTime);
		tunings(proportionalConstant, integralConstant, derivativeConstant);
		setPoint(setPoint);
		outputLimits(minOutput, maxOutput);
	}

	public float run()
	{
		// Se não estiver em modo automático retorna antes de calcular qualquer
		// coisa
		if (!_inAuto)
		{
			return _lastOutput;
		}

		long now = System.currentTimeMillis();
		long deltaTime = (now - _lastTime);
		if (deltaTime < _sampleTime)
		{
			return _lastOutput;
		}

		float error = _setPoint - _input;

		// Salva o valor acumulado do fator integrativo
		// Isso torna possivel mudar a constante integrativa sem gerar uma
		// mudança abruta na saída
		// já que o acumulo dos erros não é mais multiplicado pelo mesmo valor
		// que antes
		_integrativeTermSum += error * _integralConstant;
		// Faz o clamp disso pra evitar que o erro se acumule indefinidamente]
		// e extrapole os limites que o nosso sistema usa.
		// Apesar da saída do sistema também ser limitado, precisa fazer do
		// acumulo dos erros
		// pra que o sistema responda imediatamente a uma mudança na entrada e
		// não tente compensar o integrativo desnecessariamente
		_integrativeTermSum = clamp(_integrativeTermSum, _minOutput, _maxOutput);

		// Faz a derivada das entradas para evitar o "derivative kick", que
		// ocorre mudando o setPoint
		// Não acontece em nenhum dos nossos projetos, mas é uma implementação
		// melhor,
		// e o custo computacional é identico
		float dInput = (_input - _lastInput);

		float output = _proportionalConstant * error; // Proporcional
		output += _integrativeTermSum; // Integrativo
		output -= _derivativeConstant * dInput; // Derivativo

		// Faz clamp da saída do PID também, pois os fatores proporcional e
		// derivativo também
		// podem fazer com que a saída extrapole o intervalo de trabalho do
		// sistema
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
		if (proportionalConstant < 0 || integralConstant < 0 || derivativeConstant < 0)
		{
			throw new IllegalArgumentException(
					"As constantes devem ser valores apenas positivos\n" + "Use o modo de operação inverso");
		}

		_proportionalConstant = proportionalConstant;
		// Essa converção não é necessária, mas permite que a gente entre com
		// valores
		// de KI e KD em termos de 1/segundo
		float sampleTimeInSec = ((float) _sampleTime) / 1000;
		_integralConstant = integralConstant * sampleTimeInSec;
		_derivativeConstant = derivativeConstant / sampleTimeInSec;
		// A aplicação direta dos valores aqui nas constantes só é possivel
		// porque o
		// tempo de avalição do PID é fixado. Isso evita também que a
		// multiplicação
		// E principalmente a divisão tenham que ser feitas cada vez que o PID é
		// calculado
		// tl;dr: deixa o código mais rápido e mais eficiente.

		if (_controllerDirection == PIDDirection.INVERSE)
		{
			_proportionalConstant *= -1;
			_integralConstant *= -1;
			_derivativeConstant *= -1;
		}
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

	public PIDController outputLimits(float min, float max)
	{
		if (min > max)
		{
			throw new IllegalArgumentException("min não pode ser maior que max");
		}
		_minOutput = min;
		_maxOutput = max;

		_integrativeTermSum = clamp(_integrativeTermSum, _minOutput, _maxOutput);
		return this;
	}

	public PIDController input(float newInput)
	{
		_input = newInput;
		return this;
	}

	public PIDController controllerDirection(PIDDirection direction)
	{
		_controllerDirection = direction;
		return this;
	}

	public PIDController controllerMode(PIDMode mode)
	{
		// Se for de manual para automático,
		// ajusta alguns valores pra garantir uma transição suave entre os modos
		if (mode == PIDMode.AUTOMATIC && _controllerMode == PIDMode.MANUAL)
		{
			_lastInput = _input;
			_integrativeTermSum = clamp(_lastOutput, _minOutput, _maxOutput);
		}
		_controllerMode = mode;
		return this;
	}

	private static float clamp(float value, float min, float max)
	{
		if (value < min)
			return min;
		else if (value > max)
			return max;
		return value;
	}

	public PIDMode getControllerMode()
	{
		return _controllerMode;
	}

	public PIDDirection getControllerDirection()
	{
		return _controllerDirection;
	}

	@Override
	public String toString()
	{
		return "Kp = " + _proportionalConstant + "\n" + "Ki = " + _integralConstant + "\n" + "Kd = "
				+ _derivativeConstant;
	}
}
