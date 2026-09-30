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
	PVector vec = new PVector(10, 20);
	Keyed<Float> vecK;

	float fps = 30f; 
	ArrayList<Pin> pins = new ArrayList<>();
	CubicBezier path;

	float RED = 0;
	float GREEN = 1f/3f;
	float BLUE = 2f/3f;

	List<PVector> points = new ArrayList<>();
	Spline spline;

	float off = 30f;

	public void settings() {
		size(400, 400);
	}

	public void setup() {
		pixelDensity(displayDensity());
		colorMode(HSB, 1, 1, 1, 1); // hue, saturation, brightness, alpha
		background(1);
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
		for (int f = 0; f < 6; f++) pins.add(Pin.at(f));

		// Keyed values
		rot = new Keyed<>(new FloatLerp(), 0f);
		rot.setTimeline(tm);

		rot.key(pins.get(0), 0f);
		rot.key(pins.get(5), TWO_PI);

		pos = new Keyed<PVector>(new PVectorLerp(), new PVector(width/2, height/2));
		pos.setTimeline(tm);

		path = new CubicBezier(
			new PVector(off, height-off),
			new PVector(width-off, height-off),
			new PVector(off, off),
			new PVector(width-off, off)
		);

		shoufflePoints();
		spline = new Spline(points).setTightness(0);
		pos.key(Key.at(pins.get(1)).setEasing(1/3f), spline.at(0.0f));
		// pos.key(pins.get(2), new PVector(width/2, height/2));
		pos.key(Key.at(pins.get(3)).setEasing(1/3f), spline.at(1.0f));

		Tween<Float> splineX = spline.x();
		vecK = Keyed.bind(vec, "x")
			.setTimeline(tm)
			.key(Key.at(pins.get(1)).setEasing(1/3f), splineX.at(0.0f))
			.key(Key.at(pins.get(3)).setEasing(1/3f), splineX.at(1.0f));

		// pos.addEffect(Effect.WIGGLE(100f, 3f));
		// pos.addEffect(new Orbit(20f, 0.3f));
		// pos.addEffect(new PixelSnap(20));
		// pos.addEffect(new StopMotion<>(4f / fps));
		// pos.addEffect(new Spring<>(new PVectorLerp(), 1.5f, 0.1f));
		// pos.addEffect(Effect.SPRING(1.5f, 0.05f));  
		// pos.addEffect(new Lag<>(new PVectorLerp(), 0.5f, 10));
		// pos.addEffect(Effect.LAG(0.5f, 10));
		// Effect: only sees the value at the current time t.

		// Custom effects
		// pos.addEffect((PVector v, float t) -> new PVector(v.x, v.y + 10 * sin(t * TWO_PI)));

		// TimeEffect: can sample the animation at any time; 
		// Example: averaging the last 0.3s gives a lagging, smoothed motion.
		pos.addEffect((Tween<PVector> source, float t) -> {
			int n = 10;
			PVector sum = new PVector();
			for (int i = 0; i < n; i++) sum.add(source.value(t - 0.5f * i / n));
			return sum.div(n);
		});
	}

	PVector posPrev;

	public void draw() {
		background(1);

		pushStyle();
		int steps = 200;
		for (int i = 0; i < points.size() * steps; i++) {
			PVector pt = spline.value(i / (float)steps);
			fill(0, 0, 0, 0.3f);
			ellipse(pt.x, pt.y, 3, 3);
		}
		for (int i = 1; i < points.size() - 1; i++) {
			PVector pt = points.get(i);
			fill(0, 0, 0, 0.9f);
			ellipse(pt.x, pt.y, 7, 7);
		}
		popStyle();

		pushStyle();
		List<PVector> trail = echo(pos, 50, 0.3f / fps);
		for (int i = 0; i < trail.size(); i++) {
			PVector q = trail.get(i);
			pushMatrix();
			translate(q.x, q.y);
			float d = map(i, 0, trail.size(), 1f, 0.1f);
			fill(d, 1, 1, 0.6f);
			scale(d);
			rotate(rot.value());
			rect(0, 0, 23, 23);
			popMatrix();
		}
		popStyle();

		pushMatrix();
		PVector p = pos.value();
		if (posPrev == null) posPrev = p.copy();
		float delta = map(constrain(posPrev.dist(p), 0, 30), 0, 30, 51, 23);
		posPrev = p.copy();
		translate(p.x, p.y);
		rotate(rot.value());
		pushStyle();
		fill(0);
		rect(0, 0, delta, delta);
		popStyle();
		popMatrix();

		ellipse(vec.x, vec.y, 20, 20);
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

	private void shoufflePoints() {
		points.clear();
		for (int i = 0; i < 10; i++)
			points.add(new PVector(random(off, width-off), random(off, height-off)));
	}

	public void mouseClicked() {
		shoufflePoints();
		spline.setPoints(points);
	}
}
