import processing.core.*;

import java.util.ArrayList;
import java.util.List;
import ch.domizai.keyed.*;
import ch.domizai.keyed.effect.*;
import ch.domizai.keyed.lerp.*;
import ch.domizai.keyed.tween.*;

public class Main extends PApplet {

	Timeline tm;
	KVector pos;
	Keyed<Float> rot;
	PVector vec = new PVector(10, 20);
	Keyed<Float> vecK;
	Keyed<String> caption;
	List<PVector> obstacles = new ArrayList<>();
	float obstaclesRadius = 80;
	int obstaclesCount = 4;

	float fps = 30f; 
	ArrayList<Pin> pins = new ArrayList<>();
	CubicBezier path;

	float RED = 0;
	float GREEN = 1f/3f;
	float BLUE = 2f/3f;

	List<PVector> points = new ArrayList<>();
	Spline spline;

	List<Component> components = new ArrayList<>();
	List<MyComposition> compositions = new ArrayList<>();

	float off = 30f;

	public void settings() {
		size(400, 400);
	}

	public void setup() {
		pixelDensity(displayDensity());
		colorMode(HSB, 1, 1, 1, 1); // hue, saturation, brightness, alpha
		background(1);
		rectMode(CENTER);
		ellipseMode(CENTER);
		noStroke();
		fill(0, 75);
		smooth();
		
		frameRate(fps);
		Keyed.init(this);
		Keyed.setFrameRate(fps);
		Keyed.sync(false);
		Keyed.setUnit(Keyed.SECOND);
		Keyed.autoplay(false);
		// order matters, always call settings before initializing timelines or keyed values.

		// Timeline (seconds)
		tm = new Timeline();
		int duration = 10; 
		tm.setDuration(duration);
		for (int f = 0; f < duration; f++) pins.add(Pin.at(f));

		// Keyed values
		rot = new Keyed<>(new FloatLerp(), 0f);
		rot.setTimeline(tm);

		rot.key(pins.get(0), 0f);
		rot.key(pins.get(duration-1), TWO_PI);

		pos = new KVector(new PVector(width/2, height/2));
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
		// pos.key(Key.at(pins.get(duration / 2)), new PVector(width/2, height/2));
		pos.key(Key.at(pins.get((int)duration-1)).setEasing(1/3f), spline.at(1.0f));

		Tween<Float> splineX = spline.x();
		vecK = Keyed.bind(vec, "x")
			.setTimeline(tm)
			.key(Key.at(pins.get(1)).setEasing(1/3f), splineX.at(0.0f))
			.key(Key.at(pins.get(3)).setEasing(1/3f), splineX.at(1.0f));

		caption = new Keyed<>(new Typewriter(), "")
			.setTimeline(tm)
			.key(pins.get(0), "")
			.key(pins.get(2), "Hello Keyed")
			.key(pins.get(4), "Hello Keyed")
			.key(pins.get(6), "Hello World")
			.key(pins.get(8), "Hello World")
			.key(pins.get(9), "");

		// pos.addEffect(Effect.WIGGLE(100f, 3f));
		// pos.addEffect(new Orbit(20f, 0.3f));
		// pos.addEffect(new PixelSnap(20));
		// pos.addEffect(new StopMotion<>(4f / fps));
		// pos.addEffect(new Spring<>(new PVectorLerp(), 1.5f, 0.1f));
		// pos.addEffect(Effect.SPRING(1.5f, 0.05f));  
		// pos.addEffect(new Lag<>(new PVectorLerp(), 0.5f, 10));
		// pos.addEffect(Effect.LAG(0.5f, 10));

		// Custom effects
		// Effect: only sees the value at the current time t.
		// pos.addEffect((PVector v, float t) -> new PVector(v.x, v.y + 10 * sin(t * TWO_PI)));

		// TimeEffect: can sample the animation at any time; 
		// Example: averaging the last 0.3s gives a lagging, smoothed motion.
		// pos.addEffect((Tween<PVector> source, float t) -> {
		// 	int n = 10;
		// 	PVector sum = new PVector();
		// 	for (int i = 0; i < n; i++) sum.add(source.value(t - 0.5f * i / n));
		// 	return sum.div(n);
		// });

		initObstacles();

		pos.addEffect((PVector v, float t) -> {
			PVector p = v.copy();
			// Repeated so pushing out of one obstacle into an overlapping one still ends outside both.
			for (int iter = 0; iter < obstacles.size(); iter++) {
				boolean pushed = false;
				for (PVector obstacle : obstacles) {
					PVector away = PVector.sub(p, obstacle);
					float d = away.mag();
					if (d < obstaclesRadius - 0.001f) {
						if (d == 0) away.set(1, 0);
						p = PVector.add(obstacle, away.setMag(obstaclesRadius));
						pushed = true;
					}
				}
				if (!pushed) break;
			}
			return p;
		});

		components.add(new CircleComponent(5));
		components.add(new SquareComponent(7));
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
		List<PVector> trail = echo(pos, 100, 0.3f / fps);
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
		float delta = map(constrain(posPrev.dist(p), 0, 30), 0, 30, 31, 11);
		posPrev = p.copy();
		translate(p.x, p.y);
		rotate(rot.value());
		pushStyle();
		fill(0);
		rect(0, 0, delta, delta);
		popStyle();
		popMatrix();

		ellipse(vec.x, vec.y, 20, 20);

		pushStyle();
		fill(0);
		textSize(16);
		boolean cursorOn = frameCount / (int) (fps / 2) % 2 == 0;
		text(caption.value() + (cursorOn ? "_" : ""), 20, height - 20);
		popStyle();

		for (int i = compositions.size() - 1; i >= 0; i--) {
			MyComposition c = compositions.get(i);
			c.draw();
			if (c.isFinished()) {
				c.dispose();
				compositions.remove(i);
			}
		}

		pushStyle();
		noFill();
		stroke(255, 0, 0, 0.5f);
		for (PVector obstacle : obstacles)
			ellipse(obstacle.x, obstacle.y, obstaclesRadius * 2, obstaclesRadius * 2);
		popStyle();
	}

	public void mouseMoved() {
		tm.to(map(mouseX, 0, width, 0, tm.duration()));
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
	public <T> List<T> echo(KeyedBase<T, ?> keyed, int samples, float delay) {
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

	private void initObstacles() {
		obstacles.clear();
		for (int i = 0; i < obstaclesCount; i++)
			obstacles.add(new PVector(random(0, width), random(0, height)));
	}

	public void mouseClicked() {
		shoufflePoints();
		initObstacles();
		spline.setPoints(points);
		var comp = new MyComposition(components, 20, new PVector(mouseX, mouseY));
		comp.timeline().autoplay(true);
		compositions.add(comp);
	}

	abstract class Component {
		
		int c = color(0);
		
		abstract void draw(PVector position);

		public void setColor(int color) {
			this.c = color;
		}
	}

	class CircleComponent extends Component {
		float radius;

		public CircleComponent(float radius) {
			this.radius = radius;
		}

		@Override
		public void draw(PVector position) {
			pushStyle();
			fill(c);
			ellipse(position.x, position.y, radius, radius);
			popStyle();
		}
	}

	class SquareComponent extends Component {
		float size;

		public SquareComponent(float size) {
			this.size = size;
		}

		@Override
		public void draw(PVector position) {
			pushStyle();
			fill(c);
			rect(position.x, position.y, size, size);
			popStyle();
		}
	}

	class MyComposition extends Composition {
		List<Component> used = new ArrayList<>();
		List<Keyed<PVector>> pos = new ArrayList<>();
		PVector origin;
		final static float DURATION = 1;

		public MyComposition(List<Component> components, int usedSize, PVector origin) {
			super(DURATION);
			this.origin = origin;
			for (int i = 0; i < usedSize; i++) {
				used.add(components.get(i % components.size()));
				pos.add(keyed(new PVectorLerp(), new PVector())
					.key(0, new PVector())
					.key(Key.at(DURATION).setEasing(5/6f), new PVector(random(-50, 50), random(-50, 50))));
			}
		}

		public void draw() {
			float t = 1 - this.timeline().t() / DURATION; 
			for (int i = 0; i < pos.size(); i++) {
				Component c = used.get(i);
				c.setColor(color(RED, 1, 1, Easing.quadOut(t)));
				c.draw(pos.get(i).value().add(origin));
			}
		}
	}
}
