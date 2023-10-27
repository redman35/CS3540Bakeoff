package cs3540;

import java.util.ArrayList;
import java.util.Collections;
import processing.core.PApplet;

public class BakeOff2 extends PApplet {

	// these are variables you should probably leave alone
	int index = 0; // starts at zero-ith trial
	float border = 0; // some padding from the sides of window, set later
	int trialCount = 12; // this will be set higher for the bakeoff
	int trialIndex = 0; // what trial are we on
	int errorCount = 0; // used to keep track of errors
	float errorPenalty = 0.5f; // for every error, add this value to mean time
	int startTime = 0; // time starts when the first click is captured
	int finishTime = 0; // records the time of the final click
	boolean userDone = false; // is the user done

	boolean wPressed = false;
	boolean aPressed = false;
	boolean sPressed = false;
	boolean dPressed = false;

	final int screenPPI = 72; // what is the DPI of the screen you are using
	// you can test this by drawing a 72x72 pixel rectangle in code, and then
	// confirming with a ruler it is 1x1 inch.

	// These variables are for my example design. Your input code should
	// modify/replace these!
	float logoX = 500;
	float logoY = 500;
	float logoZ = 50f;
	float logoRotation = 0;

	// Speed setting
	float speedSetting = 5f;
	boolean followMouse = false;

	// The argument passed to main must match the class name
	public static void main(String[] args) {
		// Tell processing what class we want to run.
		PApplet.main("cs3540.BakeOff2");
	}

	private class Destination {
		float x = 0;
		float y = 0;
		float rotation = 0;
		float z = 0;
	}

	ArrayList<Destination> destinations = new ArrayList<Destination>();

	public void settings() {
		size(800, 600);
	}

	public void setup() {
		rectMode(CENTER);
		textFont(createFont("Arial", inchToPix(.3f))); // sets the font to Arial that is 0.3" tall
		textAlign(CENTER);
		rectMode(CENTER); // draw rectangles not from upper left, but from the center outwards

		// don't change this!
		border = inchToPix(2f); // padding of 1.0 inches

		for (int i = 0; i < trialCount; i++) // don't change this!
		{
			Destination d = new Destination();
			d.x = random(border, width - border); // set a random x with some padding
			d.y = random(border, height - border); // set a random y with some padding
			d.rotation = random(0, 360); // random rotation between 0 and 360
			int j = (int) random(20);
			d.z = ((j % 12) + 1) * inchToPix(.25f); // increasing size from .25 up to 3.0"
			destinations.add(d);
			println("created target with " + d.x + "," + d.y + "," + d.rotation + "," + d.z);
		}

		Collections.shuffle(destinations); // randomize the order of the button; don't change this.
	}

	public void draw() {

		background(40); // background is dark grey
		fill(200);
		noStroke();

		// shouldn't really modify this printout code unless there is a really good
		// reason to
		if (userDone) {
			text("User completed " + trialCount + " trials", width / 2, inchToPix(.4f));
			text("User had " + errorCount + " error(s)", width / 2, inchToPix(.4f) * 2);
			text("User took " + (finishTime - startTime) / 1000f / trialCount + " sec per destination", width / 2,
					inchToPix(.4f) * 3);
			text("User took " + ((finishTime - startTime) / 1000f / trialCount + (errorCount * errorPenalty))
					+ " sec per destination inc. penalty", width / 2, inchToPix(.4f) * 4);
			return;
		}

		// ===========DRAW DESTINATION SQUARES=================
		for (int i = trialIndex; i < trialCount; i++) // reduces over time
		{
			pushMatrix();
			Destination d = destinations.get(i); // get destination trial
			translate(d.x, d.y); // center the drawing coordinates to the center of the destination trial
			rotate(radians(d.rotation)); // rotate around the origin of the destination trial
			noFill();
			strokeWeight(3f);
			if (trialIndex == i)
				stroke(255, 0, 0, 192); // set color to semi translucent
			else
				stroke(128, 128, 128, 128); // set color to semi translucent
			rect(0, 0, d.z, d.z);
			popMatrix();
		}

		// ===========DRAW LOGO SQUARE=================
		pushMatrix();
		translate(logoX, logoY); // translate draw center to the center oft he logo square
		rotate(radians(logoRotation)); // rotate using the logo square as the origin
		noStroke();
		fill(60, 60, 192, 192);
		rect(0, 0, logoZ, logoZ);
		popMatrix();

		// ===========DRAW EXAMPLE CONTROLS=================
		fill(255);
		scaffoldControlLogic(); // you are going to want to replace this!
		text("Trial " + (trialIndex + 1) + " of " + trialCount, width / 2, inchToPix(.8f));


		float stepSize = inchToPix(0.02f);

		if (wPressed && aPressed) {
			logoY -= (stepSize + speedSetting);
			logoX -= (stepSize + speedSetting);
		} else if (wPressed && dPressed) {
			logoY -= (stepSize + speedSetting);
			logoX += (stepSize + speedSetting);
		} else if (sPressed && aPressed) {
			logoY += (stepSize + speedSetting);
			logoX -= (stepSize + speedSetting);
		} else if (sPressed && dPressed) {
			logoY += (stepSize + speedSetting);
			logoX += (stepSize + speedSetting);
		} else {
			// Handle individual key presses (w, a, s, d) for cardinal directions
			if (wPressed) {
				logoY -= (stepSize + speedSetting);
			}
			if (sPressed) {
				logoY += (stepSize + speedSetting);
			}
			if (aPressed) {
				logoX -= (stepSize + speedSetting);
			}
			if (dPressed) {
				logoX += (stepSize + speedSetting);
			}
		}
	}

	// my example design for control, which is terrible
	void scaffoldControlLogic() {
		float x = width - inchToPix(1.5f);
		float y = height - inchToPix(1f);
		float buttonPaddingX = inchToPix(1f);
		float buttonPaddingY = inchToPix(0.5f);
		float stepSize = inchToPix(0.02f);

		Button[] buttons = {
				new Button(x - buttonPaddingX, y - buttonPaddingY, "CCW\n(<)", () -> logoRotation -= speedSetting),
				new Button(x + buttonPaddingX, y - buttonPaddingY, "CW\n(>)", () -> logoRotation += speedSetting),
				new Button(x - 2 * buttonPaddingX, y + buttonPaddingY, "-\n(˅)",
						() -> logoZ = constrain(logoZ - (stepSize + speedSetting / 2), (float) 0.01, inchToPix(4f))),
				new Button(x - 2 * buttonPaddingX, y - buttonPaddingY, "+\n(˄)",
						() -> logoZ = constrain(logoZ + (stepSize + speedSetting / 2), (float) 0.01, inchToPix(4f))),
				new Button(x - buttonPaddingX, y + buttonPaddingY, "-X\n(A)", () -> logoX -= (stepSize + speedSetting)),
				new Button(x + buttonPaddingX, y + buttonPaddingY, "+X\n(D)", () -> logoX += (stepSize + speedSetting)),
				new Button(x, y - buttonPaddingY, "+Y\n(W)", () -> logoY -= (stepSize + speedSetting)),
				new Button(x, y + buttonPaddingY, "-Y\n(S)", () -> logoY += (stepSize + speedSetting)),
				new Button(x - 3 * buttonPaddingX, y - buttonPaddingY, "Sp+\n(E)",
						() -> speedSetting = constrain(speedSetting + 0.1f, (float) 0.01, 7f)),
				new Button(x - 3 * buttonPaddingX, y + buttonPaddingY, "Sp-\n(Q)",
						() -> speedSetting = constrain(speedSetting - 0.1f, (float) 0.01, 7f)) };

		for (Button button : buttons) {
			button.display();
		}
	}

	class Button {
		float x, y;
		String label;
		Runnable action;

		Button(float x, float y, String label, Runnable action) {
			this.x = x;
			this.y = y;
			this.label = label;
			this.action = action;
		}

		void display() {
			float buttonSize = inchToPix(1f);
			float distance = dist(x, y, mouseX, mouseY);
			fill(25, 25, 25, 50);
			rect(x, y, buttonSize, buttonSize, buttonSize / 5);
			fill(255, 255, 255);
			text(label, x, y);
			if (mousePressed && distance < buttonSize / 2) {
				action.run();
			}
		}
	}

	public void keyPressed() {
		float stepSize = inchToPix(0.02f);

		if (key == 'w') {
			wPressed = true;
		}
		if (key == 's') {
			sPressed = true;
		}
		if (key == 'a') {
			aPressed = true;
		}
		if (key == 'd') {
			dPressed = true;
		}


		if (key == CODED) {
			if (keyCode == UP) {
				logoZ = constrain(logoZ + (stepSize + speedSetting / 2), (float) 0.01, inchToPix(4f));
			}
			if (keyCode == DOWN) {
				logoZ = constrain(logoZ - (stepSize + speedSetting / 2), (float) 0.01, inchToPix(4f));
			}
			if (keyCode == LEFT) {
				logoRotation -= speedSetting;
			}
			if (keyCode == RIGHT) {
				logoRotation += speedSetting;
			}
		}


		if (key == ' ') {
			followMouse = true;
		}
		if (key == 'r') {
			logoRotation += speedSetting;
		}
		if (key == 'e') {
			speedSetting += 0.5;
			println("Speed: " + speedSetting);

		}
		if (key == 'q') {
			speedSetting -= 0.5;
			if (speedSetting <= 0) {
				speedSetting = 0;
			}
			println("Speed: " + speedSetting);
		}

	}

	public void keyReleased() {
		if (key == 'w') {
			wPressed = false;
		}
		if (key == 's') {
			sPressed = false;
		}
		if (key == 'a') {
			aPressed = false;
		}
		if (key == 'd') {
			dPressed = false;
		}
		if (key == ' ') {
			followMouse = false;
		}
	}

	public void mouseMoved() {
		if (followMouse) {
			logoX = mouseX;
			logoY = mouseY;
		}
	}

	public void mousePressed() {
		if (startTime == 0) // start time on the instant of the first user click
		{
			startTime = millis();
			println("time started!");
		}
	}

	public void mouseReleased() {
		// check to see if user clicked middle of screen within 3 inches, which this
		// code uses as a submit button
		if (dist(width / 2, height / 2, mouseX, mouseY) < inchToPix(2f)) {
			if (userDone == false && !checkForSuccess())
				errorCount++;

			trialIndex++; // and move on to next trial

			if (trialIndex == trialCount && userDone == false) {
				userDone = true;
				finishTime = millis();
			}
		}
	}

	// probably shouldn't modify this, but email me if you want to for some good
	// reason.
	public boolean checkForSuccess() {
		Destination d = destinations.get(trialIndex);
		boolean closeDist = dist(d.x, d.y, logoX, logoY) < inchToPix(.05f); // has to be within +-0.05"
		boolean closeRotation = calculateDifferenceBetweenAngles(d.rotation, logoRotation) <= 5;
		boolean closeZ = abs(d.z - logoZ) < inchToPix(.1f); // has to be within +-0.1"

		println("Close Enough Distance: " + closeDist + " (logo X/Y = " + d.x + "/" + d.y + ", destination X/Y = "
				+ logoX + "/" + logoY + ")");
		println("Close Enough Rotation: " + closeRotation + " (rot dist="
				+ calculateDifferenceBetweenAngles(d.rotation, logoRotation) + ")");
		println("Close Enough Z: " + closeZ + " (logo Z = " + d.z + ", destination Z = " + logoZ + ")");
		println("Close enough all: " + (closeDist && closeRotation && closeZ));

		return closeDist && closeRotation && closeZ;
	}

	// utility function I include to calc diference between two angles
	double calculateDifferenceBetweenAngles(float a1, float a2) {
		double diff = abs(a1 - a2);
		diff %= 90;
		if (diff > 45)
			return 90 - diff;
		else
			return diff;
	}

	// utility function to convert inches into pixels based on screen PPI
	float inchToPix(float inch) {
		return inch * screenPPI;
	}
}