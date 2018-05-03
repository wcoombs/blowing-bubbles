
public class PoppedBubble
{
	private Bubble bubble;
	private long lifeTime;
	private Particle[] particles;
	
	public PoppedBubble(Bubble theBubble, long deltaTime)
	{
		this.bubble = theBubble;
		this.lifeTime = deltaTime * (long) ((float) Math.random() * 50);
		this.particles = new Particle[(int) (this.bubble.getIncrement() * 100)];

		// ensure that the life time is not 0 (from Math.random() generating 0) or too short in general
		if (this.lifeTime < 50000000)
		{
			this.lifeTime += 50000000;
		}
		
		createParticles();
	}
	
	private void createParticles()
	{
		float x1, y1, x2, y2, vx, vy;
		int particleCounter = 0;
		
		for (float t = 0; t <= 1; t += this.bubble.getIncrement())
		{
			x1 = (float) this.bubble.getRadius() * (float) Math.cos(2 * Math.PI * t);
			y1 = (float) this.bubble.getRadius() * (float) Math.sin(2 * Math.PI * t);
			x2 = (float) this.bubble.getRadius() * (float) Math.cos(2 * Math.PI * ((t + this.bubble.getIncrement()) % 1));
			y2 = (float) this.bubble.getRadius() * (float) Math.sin(2 * Math.PI * ((t + this.bubble.getIncrement()) % 1));
			vx = (float) ((Math.random() * -1) + Math.random());
			vy = (float) ((Math.random() * -1) + Math.random());
			float[] color = new float[3];
			
			if (t < 0.7)
			{
				color[0] = this.bubble.getRedColor1();
				color[1] = this.bubble.getGreenColor1();
				color[2] = this.bubble.getBlueColor1();
			}
			else
			{
				color[0] = this.bubble.getRedColor2();
				color[1] = this.bubble.getGreenColor2();
				color[2] = this.bubble.getBlueColor2();
			}
			
			this.particles[particleCounter] = new Particle(x1, y1, x2, y2, vx, vy, color);
			particleCounter++;
		}
	}
	
	public Particle[] getParticles()
	{
		return this.particles;
	}
	
	public Bubble getBubble()
	{
		return this.bubble;
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
