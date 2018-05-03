
//------------------------------------------------------
//
// NAME           : William Coombs
// STUDENT NUMBER : 6852347
// COURSE         : COMP 3490
// INSTRUCTOR     : John Braico
// ASSIGNMENT     : #3
// QUESTION       : #1
//
// REMARKS: The purpose of this program is to create and
//          animate bubbles using a bubble wand. The
//          bubbles are generated based on the movement
//          speed of the wand, and pop when they either
//          hit the edge of the screen, are hit by the
//          bubble wand by clicking the screen, or when
//          their randomly generated lifespan ends.
//
//------------------------------------------------------
import javax.swing.*;
import java.util.ArrayList;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class A3Q1 implements GLEventListener, MouseListener, MouseMotionListener
{
	public static final boolean TRACE = false;

	public static final String WINDOW_TITLE = "A3Q1: William Coombs";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 480;

	public static void main(String[] args)
	{
		final JFrame frame = new JFrame(WINDOW_TITLE);

		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if (TRACE)
					System.out.println("closing window '" + ((JFrame) e.getWindow()).getTitle() + "'");
				System.exit(0);
			}
		});

		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		final GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setDoubleBuffered(true);
		final GLCanvas canvas = new GLCanvas(capabilities);
		try
		{
			Object self = self().getConstructor().newInstance();
			self.getClass().getMethod("setup", new Class[] { GLCanvas.class }).invoke(self, canvas);
			canvas.addGLEventListener((GLEventListener) self);
			canvas.addMouseListener((MouseListener) self);
			canvas.addMouseMotionListener((MouseMotionListener) self);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		canvas.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		canvas.setAutoSwapBufferMode(true);

		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);

		if (TRACE)
			System.out.println("-> end of main().");
	}

	private static Class<?> self()
	{
		// This ugly hack gives us the containing class of a static method
		return new Object()
		{
		}.getClass().getEnclosingClass();
	}

	/*** Instance variables and methods ***/

	int width, height;
	float left, top, right, bottom;
	long time = 0;

	// TODO: add what you need

	private BubbleWand wand = null;
	private int[] lastDragPosition;
	private ArrayList<Bubble> bubbleList = null;
	private ArrayList<PoppedBubble> poppedList = null;
	private boolean wandSwinging;
	private Smack smack;

	public boolean checkBoundingBox(float clickX, float clickY, Bubble givenBubble)
	{
		boolean result = false;

		float[][] boundingBoxV1 = new float[3][1];
		boundingBoxV1[0][0] = givenBubble.getX() - givenBubble.getRadius();
		boundingBoxV1[1][0] = givenBubble.getY() - givenBubble.getRadius();
		boundingBoxV1[2][0] = 1;

		float[][] boundingBoxV2 = new float[3][1];
		boundingBoxV2[0][0] = givenBubble.getX() - givenBubble.getRadius();
		boundingBoxV2[1][0] = givenBubble.getY() + givenBubble.getRadius();
		boundingBoxV2[2][0] = 1;

		float[][] boundingBoxV3 = new float[3][1];
		boundingBoxV3[0][0] = givenBubble.getX() + givenBubble.getRadius();
		boundingBoxV3[1][0] = givenBubble.getY() + givenBubble.getRadius();
		boundingBoxV3[2][0] = 1;

		float[][] boundingBoxV4 = new float[3][1];
		boundingBoxV4[0][0] = givenBubble.getX() + givenBubble.getRadius();
		boundingBoxV4[1][0] = givenBubble.getY() - givenBubble.getRadius();
		boundingBoxV4[2][0] = 1;

		float[][] transformedBoundingBox = new float[4][2];
		transformedBoundingBox[0][0] = boundingBoxV1[0][0];
		transformedBoundingBox[0][1] = boundingBoxV1[1][0];
		transformedBoundingBox[1][0] = boundingBoxV2[0][0];
		transformedBoundingBox[1][1] = boundingBoxV2[1][0];
		transformedBoundingBox[2][0] = boundingBoxV3[0][0];
		transformedBoundingBox[2][1] = boundingBoxV3[1][0];
		transformedBoundingBox[3][0] = boundingBoxV4[0][0];
		transformedBoundingBox[3][1] = boundingBoxV4[1][0];

		result = vertexInPolygon(clickX, clickY, transformedBoundingBox);

		return result;
	}

	// similar to A1's vertexInQuad method
	public boolean vertexInPolygon(float clickX, float clickY, float[][] polyVertices)
	{
		boolean result = false;
		int numberOfVertices = polyVertices.length;

		// for the formula for cross products, E1 X A1, edgeVector is E1 and A1 is pointVector
		float[][] edgeVector = new float[numberOfVertices][2];
		float[][] pointVector = new float[numberOfVertices][2];
		float[] crossProducts = new float[numberOfVertices];

		// calculate the vectors for edges and the point for each of the given vertices in the poly
		for (int i = 0; i < numberOfVertices; i++)
		{
			edgeVector[i][0] = polyVertices[(i + 1) % numberOfVertices][0] - polyVertices[i][0];
			edgeVector[i][1] = polyVertices[(i + 1) % numberOfVertices][1] - polyVertices[i][1];

			pointVector[i][0] = clickX - polyVertices[i][0];
			pointVector[i][1] = clickY - polyVertices[i][1];
		}

		// calculate the cross product of each En X An, where n is the length of the poly
		for (int i = 0; i < numberOfVertices; i++)
		{
			crossProducts[i] = (edgeVector[i][0] * pointVector[i][1]) - (edgeVector[i][1] * pointVector[i][0]);
		}

		// we are checking if the cross products are all the same, rather than if they are all just negative
		if ((crossProducts[0] < 0 && crossProducts[1] < 0 && crossProducts[2] < 0 && crossProducts[3] < 0)
				|| (crossProducts[0] > 0 && crossProducts[1] > 0 && crossProducts[2] > 0 && crossProducts[3] > 0))
		{
			result = true;
		}

		return result;
	}

	public boolean checkEdgeHit(Bubble bubble)
	{
		boolean result = false;

		if ((bubble.getX() + bubble.getRadius() > width) || (bubble.getX() - bubble.getRadius() < 0)
				|| (bubble.getY() + bubble.getRadius() > height) || (bubble.getY() - bubble.getRadius() < 0))
		{
			result = true;
		}

		return result;
	}

	public boolean generateBubble(int clickX, int clickY, int deltaX, int deltaY)
	{
		boolean result = false;

		if (deltaX != 0 && deltaY != 0)
		{
			float probability = ((float) Math.random() * Math.abs(deltaX) * Math.abs(deltaY)) / 5;
			
			if (probability >= 1)
				result = true;
		}

		return result;
	}

	public void setup(final GLCanvas canvas)
	{
		// Called for one-time setup
		if (TRACE)
			System.out.println("-> executing setup()");

		new Timer().scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				canvas.repaint();
			}
		}, 1000, 1000 / 60);

		// TODO: Add code here
		wand = new BubbleWand();
		lastDragPosition = null;
		bubbleList = new ArrayList<Bubble>();
		poppedList = new ArrayList<PoppedBubble>();
		wandSwinging = false;
		smack = null;
	}

	@Override
	public void init(GLAutoDrawable drawable)
	{
		// Called when the canvas is (re-)created - use it for initial GL setup
		if (TRACE)
			System.out.println("-> executing init()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void display(GLAutoDrawable drawable)
	{
		long delta = 0;
		long now = System.nanoTime();
		if (time != 0 && now - time < 100000000)
			delta = now - time;
		time = now;

		// Draws the display
		if (TRACE)
			System.out.println("-> executing display()");

		final GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		// TODO: Update the world, and draw it

		// draw the bubbles
		for (int i = 0; i < bubbleList.size(); i++)
		{
			Bubble currentBubble = bubbleList.get(i);
			boolean hitEdge = checkEdgeHit(currentBubble);

			if (hitEdge)
			{
				PoppedBubble newPoppedBubble = new PoppedBubble(currentBubble, delta);
				bubbleList.remove(currentBubble);

				poppedList.add(newPoppedBubble);
			}
			else
			{
				gl.glMatrixMode(GL2.GL_MODELVIEW);
				gl.glLoadIdentity();

				float newX = currentBubble.getX() + currentBubble.getVX();
				float newY = currentBubble.getY() + currentBubble.getVY();
				float newRotation = currentBubble.getRotation() + currentBubble.getRotationAmount();

				gl.glTranslatef(newX, newY, 0);
				gl.glRotatef(newRotation, 0, 0, 1);
				gl.glColor3f(currentBubble.getRedColor1(), currentBubble.getGreenColor1(),
						currentBubble.getBlueColor1());
				gl.glBegin(GL2.GL_LINE_LOOP);

				for (float t = 0; t <= 1; t += currentBubble.getIncrement())
				{
					if (t < 0.7)
						gl.glColor3f(currentBubble.getRedColor1(), currentBubble.getGreenColor1(),
								currentBubble.getBlueColor1());
					else
						gl.glColor3f(currentBubble.getRedColor2(), currentBubble.getGreenColor2(),
								currentBubble.getBlueColor2());

					float x = (float) currentBubble.getRadius() * (float) Math.cos(2 * Math.PI * t);
					float y = (float) currentBubble.getRadius() * (float) Math.sin(2 * Math.PI * t);

					gl.glVertex2f(x, y);
				}

				currentBubble.setX(newX);
				currentBubble.setY(newY);
				currentBubble.setRotation(newRotation);
				currentBubble.adjustLifeTime(5000000);

				if (currentBubble.getLifeTime() <= 0)
				{
					PoppedBubble newPopped = new PoppedBubble(currentBubble, delta);
					bubbleList.remove(currentBubble);
					poppedList.add(newPopped);
				}

				gl.glEnd();
				gl.glLoadIdentity();
			}
		}

		// draw the popped bubbles
		for (int i = 0; i < poppedList.size(); i++)
		{
			PoppedBubble currentPopped = poppedList.get(i);

			for (int j = 0; j < currentPopped.getParticles().length; j++)
			{
				gl.glMatrixMode(GL2.GL_MODELVIEW);
				gl.glLoadIdentity();

				Particle currentParticle = currentPopped.getParticles()[j];

				float newX1 = currentParticle.getX1() + currentParticle.getVX();
				float newY1 = currentParticle.getY1() + currentParticle.getVY();
				float newX2 = currentParticle.getX2() + currentParticle.getVX();
				float newY2 = currentParticle.getY2() + currentParticle.getVY();

				gl.glTranslatef(currentPopped.getBubble().getX(), currentPopped.getBubble().getY(), 0);
				gl.glBegin(GL2.GL_LINES);

				gl.glColor3f(currentParticle.getRedColor(), currentParticle.getGreenColor(),
						currentParticle.getBlueColor());
				gl.glVertex2f(newX1, newY1);
				gl.glVertex2f(newX2, newY2);

				gl.glEnd();
				gl.glLoadIdentity();

				currentParticle.setX1(newX1);
				currentParticle.setY1(newY1);
				currentParticle.setX2(newX2);
				currentParticle.setY2(newY2);
			}

			currentPopped.adjustLifeTime(5000000);

			if (currentPopped.getLifeTime() < 0)
			{
				poppedList.remove(currentPopped);
				i--;
			}
		}

		gl.glEnd();
		gl.glLoadIdentity();

		// draw the smack, if applicable
		if (smack != null)
		{
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(smack.getCentreX(), smack.getCentreY(), 0);
			gl.glColor3f(1f, 1f, 1f);
			for (int i = 0; i < 360; i += 45)
			{
				gl.glRotated(i, 0, 0, 1);
				gl.glTranslatef(0, 0, 0);
				gl.glBegin(GL2.GL_LINES);
				
				gl.glVertex2f(10, 10);
				gl.glVertex2f(20, 20);
				
				gl.glEnd();
			}
			
			gl.glLoadIdentity();
			
			smack.adjustLifeTime(5000000);

			if (smack.getLifeTime() < 0)
			{
				smack = null;
			}
		}
		
		// draw the bubble wand
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(wand.getX(), wand.getY(), 0);

		if (wandSwinging)
		{
			if (wand.getDownSwing())
			{
				gl.glRotated(wand.getCurrentRotation() - wand.getRotationAmount(), 0, 0, 1);
				gl.glPushMatrix();

				wand.setCurrentRotation(-wand.getRotationAmount());

				if (wand.getCurrentRotation() == wand.getMaxRotation())
				{
					smack = new Smack(wand.getX(), wand.getY());
					wand.setDownSwing(false);
					wand.setUpSwing(true);

					for (int i = 0; i < bubbleList.size(); i++)
					{
						Bubble currentBubble = bubbleList.get(i);

						// see the README provided with the assignment submission as to why we're simply
						// checking the bounding box of each bubble
						boolean hitBoundingBox = checkBoundingBox(wand.getX(), wand.getY(), currentBubble);

						if (hitBoundingBox)
						{
							PoppedBubble newPoppedBubble = new PoppedBubble(currentBubble, delta);
							bubbleList.remove(currentBubble);
							poppedList.add(newPoppedBubble);
						}
					}
				}
			}
			else
			{
				gl.glRotated(wand.getCurrentRotation() + wand.getRotationAmount(), 0, 0, 1);
				gl.glPushMatrix();

				wand.setCurrentRotation(wand.getRotationAmount());

				if (wand.getCurrentRotation() == wand.getMinRotation())
				{
					wand.setUpSwing(false);
					wandSwinging = false;
				}
			}
		}

		gl.glTranslatef(0, 0, 0);
		gl.glLineWidth(3f);
		gl.glColor3f(0.5f, 0.6f, 1f);
		gl.glBegin(GL2.GL_LINES);

		gl.glVertex2f(0, 0);
		gl.glVertex2f(0, 0 - wand.getStaffLength());
		gl.glEnd();

		// draw the top circle
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(0, wand.getTopCircleRadius() * (float) 1.5, 0);
		gl.glScalef(1, 1.5f, 0);

		gl.glBegin(GL.GL_LINE_LOOP);
		for (float t = 0; t <= 1; t = t + (float) 0.1)
		{
			float x = (float) wand.getTopCircleRadius() * (float) Math.cos(2 * Math.PI * t);
			float y = (float) wand.getTopCircleRadius() * (float) Math.sin(2 * Math.PI * t);
			gl.glVertex2f(x, y);
		}

		gl.glEnd();

		// draw the bottom circle
		gl.glPopMatrix();
		gl.glTranslatef(0, -wand.getStaffLength() - wand.getBottomCircleRadius(), 0);
		gl.glBegin(GL.GL_LINE_LOOP);
		for (float t = 0; t <= 1; t = t + (float) 0.1)
		{
			float x = (float) wand.getBottomCircleRadius() * (float) Math.cos(2 * Math.PI * t);
			float y = (float) wand.getBottomCircleRadius() * (float) Math.sin(2 * Math.PI * t);
			gl.glVertex2f(x, y);
		}

		gl.glEnd();
		gl.glLoadIdentity();
		gl.glLineWidth(1f);
	}

	public float lerp(float t, float a, float b)
	{
		return (1 - t) * a + t * b;
	}

	@Override
	public void dispose(GLAutoDrawable drawable)
	{
		// Called when the canvas is destroyed (reverse anything from init)
		if (TRACE)
			System.out.println("-> executing dispose()");
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height)
	{
		// Called when the canvas has been resized
		if (TRACE)
			System.out.println("-> executing reshape(" + x + ", " + y + ", " + width + ", " + height + ")");

		final GL2 gl = drawable.getGL().getGL2();

		this.width = width;
		this.height = height;
		// TODO: choose your coordinate system
		// final float ar = (float)width / (height == 0 ? 1 : height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		// left = ar < 1 ? -1.0f : -ar;
		// right = ar < 1 ? 1.0f : ar;
		// bottom = ar > 1 ? -1.0f : -1/ar;
		// top = ar > 1 ? 1.0f : 1/ar;
		// gl.glOrthof(left, right, bottom, top, -1.0f, 1.0f);
		gl.glOrthof(0, width, 0, height, 0.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	// TODO: use this class if you like, or your own

	// NOTE: This class is now in it's own file, rather than in this file

	// class Bubble {
	// public float x, y, vx, vy;
	// public float radius;
	//
	// public Bubble(float x, float y, float vx, float vy, float radius) {
	// this.x = x;
	// this.y = y;
	// this.vx = vx;
	// this.vy = vy;
	// this.radius = radius;
	// }
	//
	// }

	@Override
	public void mouseDragged(MouseEvent e)
	{
		// TODO Auto-generated method stub
		int clickX = e.getX();
		int clickY = height - e.getY();

		wand.setX(clickX);
		wand.setY(clickY);

		int deltaX = clickX - lastDragPosition[0];
		int deltaY = clickY - lastDragPosition[1];

		boolean result = generateBubble(clickX, clickY, deltaX, deltaY);

		if (result)
		{
			float x = clickX;

			// add the wand's top circle radius to the bubble's y coordinate, so that the bubbles generate starting
			// from the circle itself, not the designated centre of the wand where the staff and top circle meet
			float y = clickY + wand.getTopCircleRadius() * (float) 1.5;
			float vx = (float) Math.random() * deltaX;
			float vy = (float) Math.random() * deltaY;
			float radius = (float) Math.random() * 20;
			float theta = (float) Math.random() * 360;
			float thetaSpeed = (float) Math.random();
			float[] color1 = new float[3];
			float[] color2 = new float[3];
			color1[0] = (float) Math.random();
			color1[1] = (float) Math.random();
			color1[2] = (float) Math.random();

			color2[0] = (float) Math.random();
			color2[1] = (float) Math.random();
			color2[2] = (float) Math.random();

			// make sure the bubble doesn't move too slowly
			if (vx > -0.3 && vx < 0.3)
			{
				if (vx < 0)
					vx -= 0.3;
				else
					vx += 0.3;
			}
			if (vy > -0.3 && vy < 0.3)
			{
				if (vy < 0)
					vy -= 0.3;
				else
					vy += 0.3;
			}
			
//			// make sure the bubble doesn't move too fast
			if (vx < -1.5 || vx > 1.5)
			{
				if (vx < 0)
					vx = (vx / (vx - (float) 1.5)) * -1;
				else
					vx = vx / (vx + (float) 1.5);
			}
			if (vy < -1.5 || vy > 1.5)
			{
				if (vy < 0)
					vy = (vy / (vy - (float) 1.5)) * -1;
				else
					vy = vy / (vy + (float) 1.5);
			}

			// make sure the bubble isn't too small
			if (radius < 5)
				radius += 5;

			// make sure the bubble doesn't rotate too slowly
			if (thetaSpeed < 0.3)
				thetaSpeed += 0.3;

			// make sure the color of the bubble is not too dark
			if (color1[0] < 0.4)
				color1[0] += 0.4;
			if (color1[1] < 0.4)
				color1[1] += 0.4;
			if (color1[2] < 0.4)
				color1[2] += 0.4;

			if (color2[0] < 0.4)
				color2[0] += 0.4;
			if (color2[1] < 0.4)
				color2[1] += 0.4;
			if (color2[2] < 0.4)
				color2[2] += 0.4;

			Bubble newBubble = new Bubble(x, y, vx, vy, radius, theta, thetaSpeed, color1, color2);
			bubbleList.add(newBubble);
		}

		lastDragPosition[0] = clickX;
		lastDragPosition[1] = clickY;
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// TODO Auto-generated method stub
		int mouseX = e.getX();
		int mouseY = height - e.getY();

		wand.setX(mouseX);
		wand.setY(mouseY);
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		// TODO Auto-generated method stub
		int clickX = e.getX();
		int clickY = height - e.getY();

		if (!wandSwinging)
		{
			wand.setDownSwing(true);
			wandSwinging = true;
		}
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		// TODO Auto-generated method stub
		int clickX = e.getX();
		int clickY = height - e.getY();

		if (lastDragPosition == null)
		{
			lastDragPosition = new int[] { clickX, clickY };
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		// TODO Auto-generated method stub
		if (lastDragPosition != null)
		{
			lastDragPosition = null;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

}
