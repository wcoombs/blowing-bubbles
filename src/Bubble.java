class Bubble
{
	private float x, y, vx, vy;
	private float radius, rotation, rotationAmount;
	private float[] color1, color2;
	private float increment;
	private long lifeTime;

	public Bubble(float x, float y, float vx, float vy, float radius, float rotation, float rotationAmount,
			float[] color1, float[] color2)
	{
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.radius = radius;
		this.rotation = rotation;
		this.rotationAmount = rotationAmount;
		this.color1 = color1;
		this.color2 = color2;
		this.lifeTime = 50000000 * (long) ((float) Math.random() * 200);
		this.increment = (float) 0.1;		
	}
	
	public long getLifeTime()
	{
		return this.lifeTime;
	}
	
	public void adjustLifeTime(long newLifeTime)
	{
		this.lifeTime -= newLifeTime;
	}
	
	public float getIncrement()
	{
		return this.increment;
	}

	public float getRotation()
	{
		return this.rotation;
	}

	public void setRotation(float newRotation)
	{
		this.rotation = newRotation;
	}

	public float getRotationAmount()
	{
		return this.rotationAmount;
	}

	public float getRedColor1()
	{
		return this.color1[0];
	}

	public float getGreenColor1()
	{
		return this.color1[1];
	}

	public float getBlueColor1()
	{
		return this.color1[2];
	}

	public float getRedColor2()
	{
		return this.color2[0];
	}

	public float getGreenColor2()
	{
		return this.color2[1];
	}

	public float getBlueColor2()
	{
		return this.color2[2];
	}

	public float getX()
	{
		return this.x;
	}

	public float getY()
	{
		return this.y;
	}

	public float getVX()
	{
		return this.vx;
	}

	public float getVY()
	{
		return this.vy;
	}

	public float getRadius()
	{
		return this.radius;
	}

	public void setX(float newX)
	{
		this.x = newX;
	}

	public void setY(float newY)
	{
		this.y = newY;
	}

	public void setVX(float newVX)
	{
		this.vx = newVX;
	}

	public void setVY(float newVY)
	{
		this.vy = newVY;
	}

	public void setRadius(float newRadius)
	{
		this.radius = newRadius;
	}
}
