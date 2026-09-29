import processing.core.*;
import java.util.ArrayList;
import java.util.List;
import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.lerp.*;
import ch.domizai.keyed.tween.*;

public class Main extends PApplet {

	Timeline tm;
	Keyed<PVector> pos;
	Keyed<Float> rot;

	float fps = 30f; 
	ArrayList<Pin> frames = new ArrayList<>();
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

		Keyed.init(this);

		// Timeline (seconds)
		tm = new Timeline();
		tm.setDuration(5);
		for (int f = 0; f < 6; f++) frames.add(Pin.at(f));

		// Keyed values
		rot = new Keyed<>(new FloatLerp(), 0f);
		rot.setTimeline(tm);

		rot.key(frames.get(0), 0f);
		rot.key(frames.get(5), TWO_PI);

		pos = new Keyed<PVector>(new PVectorLerp(), new PVector(width/2, height/2));
		pos.setTimeline(tm);

		float off = 30f;
		path = new BezierTween(
			new PVector(off, height-off),
			new PVector(width-off, height-off),
			new PVector(off, off),
			new PVector(width-off, off)
		);

		// TODO: Add setEasingIn and setEasingOut
		pos.key(Key.at(frames.get(1)).setEasing(Easing.QUAD_IN_OUT), path.at(0.0f));
		// pos.key(frames.get(2), new PVector(width/2, height/2));
		pos.key(Key.at(frames.get(3)).setEasing(Easing.QUAD_IN_OUT), path.at(1.0f));

		// pos.addEffect(Effect.WIGGLE(100f, 3f));
		// pos.addEffect(new Orbit(20f, 0.3f));
		// pos.addEffect(new PixelSnap(10));
		// pos.addEffect(new StopMotion<>(4f / fps));
		// pos.addEffect(new Spring<>(new PVectorLerp(), 1.5f, 0.1f));
		// pos.addEffect(Effect.SPRING(1.5f, 0.1f));  
	}


	public void draw() {
		background(255);

		pushStyle();
		fill(255, 0, 0, 50);
		List<PVector> trail = echo(pos, 50, 0.3f / fps);
		for (int i = 0; i < trail.size(); i++) {
			PVector q = trail.get(i);
			pushMatrix();
			translate(q.x, q.y);
			scale(map(i, 0, trail.size(), 1f, 0.1f));
			rotate(rot.value());
			rect(0, 0, 13, 13);
			popMatrix();
		}
		popStyle();

		pushMatrix();
		PVector p = pos.value();
		translate(p.x, p.y);
		rotate(rot.value());
		pushStyle();
		fill(0);
		rect(0, 0, 15, 15);
		popStyle();
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

	// Negative delay sample past or future.
	public <T> List<T> echo(Keyed<T> keyed, int samples, float delay) {
		Timeline timeline = keyed.timeline();
		List<T> values = new ArrayList<>();
		for (int i = 0; i < samples; i++)
			values.add(keyed.value(timeline.t(-delay * i)));
		return values;
	}
}
