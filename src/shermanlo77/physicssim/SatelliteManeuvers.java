package shermanlo77.physicssim;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;

public class SatelliteManeuvers extends Simulation {

  //Initialize variables

  //SCIENTIFIC CONSTANTS
  //G is the gravitational force constant in [N.m².kg¯²]
  private float G = 6.67f * PApplet.pow(10,-11);
  //M is the mass of the sun in [kg]
  private float mass;
  //the distance from the Centre of the Sun to the Mouse in [pixels]
  private int distanceSunToMouse;
  //dTdt is the rate of time in [simulation days per real seconds]
  private int dTdt;

  //Earth data
  //image of the Earth
  private PImage earth;
  //the angle of rotation of the Earth
  private float earthTheta;

 //time the simulation has been running for simulation hours
  private float hourCounter;

  //SIMULATION CONSTANTS
  //the position vector of the tip and tail of the dragged arrow
  private PVector head, tail;
  //the direction vector for the arrow
  private PVector arrow;
  //the direction vector pointing to the Earth from the tail of the arrow
  private PVector radius;
  //the angle between the arrow and a vertical line
  private float arrowAngle;
  //the angle between the radius and the arrow
  private int vRAngleBetween;
  //the magnitude of the velocity vector
  private int vMag;

  //POINTERS
  //linePointer points to the line to be drawn
  private int linePointer;

  //GUI
  //user input for the mass of the Earth
  private NumberInput massInput;
  //a drop down menu which allows users to choose the line to be displayed
  private DropDown lineSelection;
  //user input for the value of dT/dt
  private NumberInput timeScale;
  //a reset button which delete all planets and returns all values back to initial values
  private  Button reset;

  //OTHERS
  //true if the mouse is being dragged to make a satellite object
  private boolean dragging;
  //the array of satellite objects
  private ArrayList<Satellite> satellites;

  @Override
  public void setup() {
    super.setup();

    //Assign variables
    this.satellites = new ArrayList<Satellite>();

    this.massInput = new NumberInput(this, 2*this.width/3+5, 140, "Mass of Earth: ", "x10²³ kg",
        60, 1, 99);
    /* MASS_INPUT is a number input GUI for the mass of the Earth:-
      it is at coordinates (2*width/3+5,140)
      with heading "Mass of Earth: "
      with units "x10²³ kg"
      the initial value is 60 ie the real mass of the Earth
      the lowest value is 1
      the highest value is 99 */

    String [] line; //the options in the drop down menu
    line = new String [3];
    line [0] = "None";
    line [1] = "Force lines";
    line [2] = "Equipotentials";
    this.linePointer = 0;
    this.lineSelection = new DropDown(this, 2*this.width/3+5, 175, this.linePointer, line,
        "Display lines: ");
    /* LINE_SELECTION is a drop down menu GUI:-
      it is at coordinates(2*width/3+5,175)
      the initial value is 0, ie linePointer
      with these options: None, Force lines, Equipotentials
      the heading is "Display lines: "*/

    this.timeScale = new NumberInput(this, 2*this.width/3+5, 210 , "Time Scale : 1 sec = ", "hours",
        2, 1,9);
    /* TIME_SCALE is a number input GUI:-
      it is at coordinates(2*width/3+5,210)
      with heading "Time Scale : 1 sec = "
      with units "hours"
      the initial value is 2
      the lowest value is 1
      the highest value is 9 */

    this.pause = new Button(this, 2*this.width/3+5, 270, "Pause/Play",
        10+this.textWidth("Pause/Play"));
    this.isPause = false;
    /* PAUSE is a button GUI:-
      it is at coordinates (2*width/3+5,270)
      "Pause/Play" is written in the button
      it is as long as the string with 10 pixels padding
      initially the simulation is not paused*/

    this.reset = new Button(this, 2*this.width/3+5, 305, "Reset", 10 + this.textWidth("Reset"));
    /* RESET is a button GUI:-
      it is at coordinates(2*width/3+5,305)
      "Reset" is written in the button
      it is as long as the string with 10 pixels padding */

    this.changeColour = new Button(this, 2*this.width/3+5, 365, "Change colour",
        10 + this.textWidth("Change colour"));
    this.colourPointer = 0;
    /* CHANGE_COLOUR is a button GUI:-
      it is at coordinates (2*width/3+5,365)
      "Change colour" is written in the button
      it is as long as the string with 10 pixels padding
      initially the colourPointer is pointing at 0 */

    this.hourCounter = 0;

    this.earth = this.loadImage("earth.png");
    this.earthTheta = 0;
  }

  @Override
  public void draw() {

    //Update colour scheme
    this.colourScheme(this.colourPointer);

    this.background(this.background);

    //Update mass of the sun
    if (!this.massInput.acceptKeyboard) {
      this.mass = this.massInput.returnValue(); //kg
      this.mass *= PApplet.pow(10, 23); //kg x10²³
    }
    //Update dT/dt
    if (!this.timeScale.acceptKeyboard) {
      this.dTdt = this.timeScale.returnValue();
    }

    this.pushMatrix();
    this.translate(this.width/3, this.height/2);

    //if line pointer points to gravitational lines, draw gravitational force lines
    if (this.linePointer == 1) {
      this.gravitationalLines();
    } else if (this.linePointer == 2) {
      //else if line pointer points to equipotentials, draw equipotentials
      this.equipotentialsLines();
    }

    //draw Earth
    this.pushMatrix();
    this.rotate(this.earthTheta);
    this.imageMode(PConstants.CENTER);
    this.image(this.earth, 0, 0);

    //rotate Earth and increase the hourCounter if the simulation is not paused
    if (!this.isPause) {
      this.earthTheta += PConstants.TWO_PI / (24/this.dTdt*this.frameRate);
      if (this.earthTheta > PConstants.TWO_PI) {
        this.earthTheta -= PConstants.TWO_PI;
      }
      this.hourCounter += this.dTdt/this.frameRate;
    }
    this.popMatrix();

    this.popMatrix();

    //if the mouse is being dragged, draw the initial velocity vector
    if (this.dragging) {
      this.stroke(this.outline);
      this.line(this.tail.x, this.tail.y, this.head.x, this.head.y);
      this.line(this.width/3, this.height/2, this.tail.x, this.tail.y);
      this.drawArrow(this.head.x, this.head.y, this.arrowAngle);
    }

    //for all satellites in the array...
    for (int i=this.satellites.size()-1; i>=0; i--) {
      Satellite satellite = this.satellites.get(i);

      //if the simulation is not paused, move the satellites
      if (!this.isPause) {
        satellite.move();

        //if satellites needs to be destroyed, remove it
        if (satellite.needsToDestroy()) {
          this.satellites.remove(i);
        }
        //draw a new trail dot every simulation minute
        if (this.hourCounter >= 1/60) {
          satellite.updateTrails();
          this.hourCounter -= 1/60;
        }
      }
      //display the satellite and its trails
      satellite.display();
      satellite.showTrails();
    }

    //Create a square for user control panel
    this.stroke(this.outline);
    this.fill(this.background);
    this.strokeWeight(1);
    this.rectMode(PConstants.CORNER);
    this.rect(2*this.width/3, -1, this.width+1, this.height+1);

    //Display radius of the mouse position from the sun
    this.fill(this.outline);
    if (!this.dragging) {
      this.distanceSunToMouse = PApplet.round(
          PApplet.dist(this.width/3, this.height/2, this.mouseX, this.mouseY));
      this.distanceSunToMouse *= 0.228;
      //if the mouse is not in the simulation area, radius is zero
      if (this.mouseX >= 2*this.width/3) {
        this.distanceSunToMouse = 0;
      }
    }
    //Display initial velocity vector information
    this.text("Radius: "+ this.distanceSunToMouse +"x10⁶ m", 2*this.width/3 + 5,
        10 + this.textDescent() + this.textAscent());
    this.text("Angle: "+ this.vRAngleBetween +"°", 2*this.width/3 + 5,
        45 + this.textDescent() + this.textAscent());
    this.text("Speed: "+ this.vMag +" m.s¯¹", 2*this.width/3 + 5,
        80 + this.textDescent() + this.textAscent());

    //Display all GUI
    this.massInput.display();
    this.timeScale.display();
    this.lineSelection.display();
    this.pause.display();
    this.reset.display();
    this.changeColour.display();
  }

  //draw an arrow head at position (x,y) and at an angle given by the bearing
  private void drawArrow(float x, float y, float bearing) {
    this.pushMatrix();
    this.translate(x, y);
    this.rotate(bearing);
    this.line(0, 0, 10, 10);
    this.line(0, 0, -10, 10);
    this.popMatrix();
  }

  //draw gravitational lines
  private void gravitationalLines() {

    float lineLength;
    lineLength = (float) (this.mass * 3.33 * PApplet.pow(10, -23));

    this.stroke(122);

    //horizontal
    this.line(-lineLength, 0, lineLength, 0);
    this.drawArrow(-lineLength/2, 0, PConstants.HALF_PI);
    this.drawArrow(lineLength/2, 0, PConstants.HALF_PI + PConstants.PI);

    //vertical
    this.line(0, -lineLength, 0, lineLength);
    this.drawArrow(0, -lineLength/2, PConstants.PI);
    this.drawArrow(0, lineLength/2, PConstants.TWO_PI);

    //use circle geometry to make each force line have equal length

    //diagonal top left to bottom right
    this.line(-PApplet.sqrt(PApplet.sq(lineLength)/2),
        -PApplet.sqrt(PApplet.sq(lineLength)/2),
        PApplet.sqrt(PApplet.sq(lineLength)/2),
        PApplet.sqrt(PApplet.sq(lineLength)/2));
    this.drawArrow(-PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        -PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        PConstants.HALF_PI + PConstants.QUARTER_PI);
    this.drawArrow(PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        PConstants.TWO_PI - PConstants.QUARTER_PI);

    //diagonal bottom left to top right
    this.line(-PApplet.sqrt(PApplet.sq(lineLength)/2),
        PApplet.sqrt(PApplet.sq(lineLength)/2),
        PApplet.sqrt(PApplet.sq(lineLength)/2),
        -PApplet.sqrt(PApplet.sq(lineLength)/2));
    this.drawArrow(-PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        PConstants.QUARTER_PI);
    this.drawArrow(PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        -PApplet.sqrt(PApplet.sq(lineLength)/2)/2,
        PConstants.PI + PConstants.QUARTER_PI);
  }

  //draw equipotential lines
  private void equipotentialsLines() {
    float circleRadius;
    this.noFill();
    this.stroke(122);

    //draw a circle where the gravitational potential is in increments of i*pow(10,6)
    for (int i=1; i<50; i++) {
      circleRadius = (this.G*this.mass)/(i*PApplet.pow(10, 6));
      circleRadius /= 228*PApplet.pow(10, 3);
      this.ellipse(0, 0, circleRadius,circleRadius);
    }
  }

  @Override
  public void mousePressed() {
    if (this.mouseButton == PConstants.LEFT) { //Event: left mouse click

      //if the mouse is in the simulation area...
      if (this.mouseX < 2*this.width/3) {
        this.tail = new PVector(this.mouseX,this.mouseY); //save its position as a vector
        this.head = new PVector(this.mouseX, this.mouseY);
        //work out the direction vector of the acceleration due to gravity
        this.radius = new PVector(this.mouseX - this.width/3, this.mouseY - this.height/2);
        this.dragging = true;
      }

      //prepare all GUI
      this.massInput.activateKeyboardInput();
      this.lineSelection.activateMenu();
      this.reset.position();
      this.pause.position();
      this.changeColour.position();

      if (!this.lineSelection.menuOpen) {
        //prevents the time scale number input being selected when selecting an option from the line
        //selection drop down menu
        this.timeScale.activateKeyboardInput();
      }
    }
  }

  @Override
  public void mouseDragged() {
    if (this.mouseButton == PConstants.LEFT) {
      if (this.dragging) {
        //if the mouse is being dragged, updated all information of the initial velocity vector
        this.head = new PVector(this.mouseX, this.mouseY);
        this.arrow = PVector.sub(this.tail, this.head);
        this.vRAngleBetween = PApplet.round(
            PApplet.degrees(PVector.angleBetween(this.arrow, this.radius)));
        this.vMag = round(PVector.mult(this.arrow, 20).mag());
        this.arrowAngle = PApplet.atan2(this.head.x - this.tail.x, this.tail.y - this.head.y);
      }
    }
  }

  @Override
  public void mouseReleased() {
    if (this.mouseButton == PConstants.LEFT) {

      //if the mouse is released after dragging, make a new satellite object
      if (this.dragging) {
        this.dragging = false;
        this.satellites.add(new Satellite(this.tail, this.head));
        this.vRAngleBetween = 0;
        this.vMag = 0;
      }

      //If mouse is released on the drop down menu, update the dataSelectionPointer
      if (this.lineSelection.returnValue() != -1) {
          this.linePointer = this.lineSelection.returnValue();
          this.lineSelection.closeMenu();
      }

      //If the mouse is released on reset button delete all planets
      if (this.reset.returnValue()) {
        for (int i = this.satellites.size()-1; i>=0; i--) {
          this.satellites.remove(i);
        }
      }
      this.reset.returnPosition();

      //If mouse is released on pause button, inverse pause boolean
      if (this.pause.returnValue()) {
        this.isPause = !this.isPause;
      }
      this.pause.returnPosition();

      //If mouse is released on change colour button, increment the pointer
      if (this.changeColour.returnValue()) {
        this.colourPointer++;
        if (this.colourPointer == 3) {
          this.colourPointer = 0;
        }
      }
      this.changeColour.returnPosition();
    }
  }

  @Override
  public void keyPressed() {
    //if a key is pressed update all number input GUI
    this.massInput.keyboardInput();
    this.timeScale.keyboardInput();
  }

  private class Satellite {

    //position vector of the satellite
    private PVector pos;
    //in pixels per second
    private PVector vVect, aVect;
    //array of previous position vectors
    private PVector [] trails;
    //the direction of the acceleration due to gravity
    private float theta;
    //the acceleration due to gravity
    private float a;
    //points to the most oldest position vector in the array trails
    private int trailsPointer;

    public Satellite(PVector tail, PVector head) {

      this.pos = tail; //position vector = point A ie where the mouse first clicked

      head.sub(tail); //work out the direction vector of initial velocity vector
      head.mult(20/(228*PApplet.pow(10, 3))); //convert to pixels per second
      this.vVect = head; //the velocity has been worked out and assigned

      //create the array of previous position vectors
      this.trails = new PVector [127];
      for (int i=0; i<this.trails.length; i++) {
        this.trails[i] = new PVector(this.pos.x, this.pos.y);
      }
      this.trailsPointer = 0;
    }

    //move the satellite
    void move() {

      //work out the acceleration due to gravity
      this.a = G * mass / PApplet.sq(228*PApplet.pow(10,3) *
          PApplet.dist(this.pos.x, this.pos.y, width/3, height/2)); //meters per second squared
      this.a /= 228*PApplet.pow(10, 3); //pixels per second squared

      //work out the direction of the acceleration due to gravity
      this.theta = PApplet.atan2(this.pos.y - height/2, this.pos.x - width/3);

      //resolve the acceleration due to gravity into component vectors
      //pixels per second squared
      this.aVect = new PVector(-this.a*PApplet.cos(this.theta), -this.a*PApplet.sin(this.theta));

      //work out the new velocity and position of the satellite
      this.vVect.add(PVector.mult(this.aVect, 3600*dTdt/frameRate));
      this.pos.add(PVector.mult(this.vVect, 3600*dTdt/frameRate));

    }

    //returns true or false if the satellite is outside the boundaries
    boolean needsToDestroy() {

      //if it is approaching earth, 33 pixels from the centre
      if (PApplet.dist(this.pos.x, this.pos.y, width/3, height/2)<33) {
        return true;
      } else if (PApplet.dist(this.pos.x, this.pos.y, width/3, height/2) > 1000) {
        //if it is 1000 pixels away from the Earth
        return true;
      } else { //else the satellite is within boundaries
        return false;
      }
    }

    //append a new position vector to the array
    void updateTrails() {
      this.trails[this.trailsPointer] = new PVector(this.pos.x, this.pos.y);
      this.trailsPointer++;
      if (this.trailsPointer == this.trails.length) {
        this.trailsPointer = 0;
      }
    }

    //display the satellite
    void display() {
      noStroke();
      fill(0, 255, 0);
      ellipse(this.pos.x, this.pos.y, 10, 10);
    }

    //display the satellite's trails
    void showTrails() {
      for (int i=0; i<this.trails.length; i++) {
        stroke(outline);
        point(this.trails[i].x, this.trails[i].y);
      }
    }
  }

  public static void main(String[] args) {
    PApplet.main("shermanlo77.physicssim.SatelliteManeuvers");
  }

}
