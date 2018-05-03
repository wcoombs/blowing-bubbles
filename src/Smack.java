
public class Smack
{
	private float centreX;
	private float centreY;
	private long lifeTime;
	
	public Smack(float x, float y)
	{
		this.centreX = x;
		this.centreY = y;
		this.lifeTime = 75000000;
	}
		
	public float getCentreX()
	{
		return this.centreX;
	}
	
	public float getCentreY()
	{
		return this.centreY;
	}
	
	public long getLifeTime()
	{
		return this.lifeTime;
	}
	
	public void adjustLifeTime(long newLifeTime)
	{
		this.lifeTime -= newLifeTime;
	}

}
