import processing.core.*;
import java.util.ArrayList;
import ch.domizai.keyed.*;
import ch.domizai.keyed.tween.PVectorTween;
import ch.domizai.keyed.easing.Easing;
import ch.domizai.keyed.path.BezierCurve;

public class Main extends PApplet {

	Timeline tm;
	Keyed<PVector> pos;
	float fps = 30f; 
	ArrayList<Key> keys = new ArrayList<>();
	BezierCurve path;

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
		tm.setDuration(4 * fps);

		// Keys
		keys.add(Key.at(1 * fps));
		keys.add(Key.at(2 * fps));
		keys.add(Key.at(3 * fps));
		keys.get(0).setEaseInOut(Easing.CUBIC_IN_OUT);
		keys.get(1).setEaseInOut(Easing.CUBIC_IN_OUT);
		keys.get(2).setEaseInOut(Easing.CUBIC_IN_OUT);

		// Keyed values
		pos = new Keyed<>(PVectorTween::tween, new PVector());
		pos.setTimeline(tm);

		float off = 30f;
		path = new BezierCurve(
			new PVector(off, height-off),
			new PVector(off, off),
			new PVector(width-off, height-off),
			new PVector(width-off, height-off));

		pos.addKey(keys.get(0), path);
		pos.addKey(keys.get(2), path);
	}

	public void draw() {
		background(255);
		tm.step();
		PVector p = pos.value();
		rect(p.x, p.y, 5, 5);
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
