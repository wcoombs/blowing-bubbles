
public class BubbleWand
{
	private float topCircleRadius, bottomCircleRadius, staffLength, x, y;
	private int minRotation, maxRotation, currentRotation, rotationAmount;
	private boolean downSwing, upSwing;
	
	public BubbleWand()
	{
		this.topCircleRadius = 10;
		this.bottomCircleRadius = 5;
		this.staffLength = 30;
		this.maxRotation = -50;
		this.minRotation = 0;
		this.currentRotation = 0;
		this.rotationAmount = 10;
		this.downSwing = false;
		this.upSwing = false;
	}
	
	public int getRotationAmount()
	{
		return this.rotationAmount;
	}
	
	public int getMinRotation()
	{
		return this.minRotation;
	}
	
	public int getMaxRotation()
	{
		return this.maxRotation;
	}
	
	public void setCurrentRotation(int value)
	{
		this.currentRotation += value;
	}
	
	public int getCurrentRotation()
	{
		return this.currentRotation;
	}
	
	public boolean getDownSwing()
	{
		return this.downSwing;
	}
	
	public void setDownSwing(boolean result)
	{
		this.downSwing = result;
	}
	
	public void setUpSwing(boolean result)
	{
		this.upSwing = result;
	}
	
	public float getTopCircleRadius()
	{
		return this.topCircleRadius;
	}

	public float getBottomCircleRadius()
	{
		return this.bottomCircleRadius;
	}

	public float getStaffLength()
	{
		return this.staffLength;
	}
	
	public float getX()
	{
		return this.x;
	}
	
	public float getY()
	{
		return this.y;
	}
	
	public void setX(float newX)
	{
		this.x = newX;
	}
	
	public void setY(float newY)
	{
		this.y = newY;
	}
}
