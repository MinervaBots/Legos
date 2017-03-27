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
	
	public void Update()
	{
		float error = _sensoring.Update();
		System.out.println("Error: " + error);
		float power = _pidController.Run(error);
		System.out.println("Power: " + power);
		_motorController.Move(error, power);
	}
	
	public void Move(float left, float right)
	{
		_motorController.Move(0, 100);
	}
}