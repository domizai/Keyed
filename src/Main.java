import processing.core.*;
import java.util.ArrayList;
import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.lerp.*;
import ch.domizai.keyed.tween.*;

public class Main extends PApplet {

	Timeline tm;
	Keyed<PVector> pos;
	Keyed<Float> rot;

	float fps = 30f; 
	ArrayList<Frame> frames = new ArrayList<>();
	BezierTween path;

	public void settings() {
		size(400, 400);
	}

	public void setup() {
		background(255);
		pixelDensity(displayDensity());
		frameRate(fps);
		rectMode(CENTER);
		ellipseMode(CENTER);
		noStroke();
		fill(0, 75);
		smooth();

		// Timeline
		tm = new Timeline();
		tm.setDuration(5 * fps);
		for (int f = 0; f < 6; f++) frames.add(Frame.at(f * fps));

		// Keyed values
		rot = new Keyed<>(new FloatLerp(), 0f);
		rot.setTimeline(tm);

		rot.key(frames.get(0), 0f);
		rot.key(frames.get(5), TWO_PI);

		pos = new Keyed<>(new PVectorLerp(), new PVector(width/2, height/2));
		pos.setTimeline(tm);

		float off = 30f;
		path = new BezierTween(
			new PVector(off, height-off),
			new PVector(width-off, height-off),
			new PVector(off, off),
			new PVector(width-off, off)
		);

		pos.key(Key.at(frames.get(2)).setEasing(Easing.QUAD_IN_OUT), path.at(0.0f));
		// pos.key(frames.get(1), new PVector(width/2, height/2));
		pos.key(Key.at(frames.get(3)).setEasing(Easing.QUAD_IN_OUT), path.at(1.0f));

		// pos.addEffect(Effect.WIGGLE(100f, 0.1f));
		// pos.addEffect(new Orbit(20f, 0.01f));
		// pos.addEffect(new PixelSnap(10));
		// pos.addEffect(new StopMotion<>(4f));
		// pos.addEffect(new Spring<>(new PVectorLerp(), 2f / fps, 0.2f));  // 2 wobbles per second
	}

	public void draw() {
		background(255);
		tm.step();
		pushMatrix();
		PVector p = pos.value();
		translate(p.x, p.y);
		rotate(rot.value());
		rect(0, 0, 15, 15);
		popMatrix();
	}

    static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "Main" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} 
		else {
			PApplet.main(appletArgs);
		}
	}
}
