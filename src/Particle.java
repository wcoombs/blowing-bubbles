
public class Particle
{
	private float x1, y1, x2, y2, vx, vy;
	private float[] color;
	
	public Particle(float x1, float y1, float x2, float y2, float vx, float vy, float[] color)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.vx = vx;
		this.vy = vy;
		this.color = color;
	}
	
	public float getRedColor()
	{
		return this.color[0];
	}
	
	public float getGreenColor()
	{
		return this.color[1];
	}

	public float getBlueColor()
	{
		return this.color[2];
	}
	
	public void setX1(float newX1)
	{
		this.x1 = newX1;
	}
	
	public void setY1(float newY1)
	{
		this.y1 = newY1;
	}

	public void setX2(float newX2)
	{
		this.x2 = newX2;
	}

	public void setY2(float newY2)
	{
		this.y2 = newY2;
	}
	
	public float getX1()
	{
		return this.x1;
	}
	
	public float getY1()
	{
		return this.y1;
	}

	public float getX2()
	{
		return this.x2;
	}

	public float getY2()
	{
		return this.y2;
	}

	public float getVX()
	{
		return this.vx;
	}

	public float getVY()
	{
		return this.vy;
	}
}
