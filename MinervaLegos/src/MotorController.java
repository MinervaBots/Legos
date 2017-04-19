import pid.OutputDestination;

public interface MotorController extends OutputDestination
{
	void move(float power);
}
