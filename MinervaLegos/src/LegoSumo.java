public class LegoSumo
{
	private Sensoring _sensoring;
	private PIDController _pidController;
	private MotorController _motorController;
	
	public LegoSumo(MotorController motorController, Sensoring sensoring, PIDController pidController)
	{
		_motorController = motorController;
		_sensoring = sensoring;
		_pidController = pidController;
	}
	
	void Update()
	{
		float error = _sensoring.Update();
		float power = _pidController.Run(error);
		_motorController.Move(error, power);
	}
}