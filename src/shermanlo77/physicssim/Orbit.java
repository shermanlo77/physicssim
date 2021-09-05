package shermanlo77.physicssim;

import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;
import processing.core.PImage;

public class Orbit extends Simulation {

  //Initialize variables

  //SCIENTIFIC CONSTANTS
  //G is the gravitational force constant in [N.m².kg¯²]
  private float G = 6.67f * PApplet.pow(10,-11);
  //M is the mass of the sun in [kg]
  private float mass;
  //the distance from the Centre of the Sun to the Mouse in [pixels]
  private int distanceSunToMouse;
  //dTdt is the rate of time in [simulation days per real seconds]
  private int dTdt = 50;

  //points to the data to be shown for each planet
  private int dataSelectionPointer;

  //user input for the mass of the sun
  private NumberInput massInput;
  ////a reset button which delete all planets and returns all values back to initial values
  private Button reset;
  //a drop down menu which allows users to choose a data to display of each planet
  private DropDown dataSelection;
  //user input for the value of dT/dt
  private NumberInput timeScale;

  //dataFont is the font used to display planet data
  private PFont dataFont;
  //the array of planet objects
  private ArrayList<Planet> planets;
  //image of sun
  private PImage sun;

  @Override
  public void setup() {
    super.setup();

    //load image of sun
    this.sun = this.loadImage("sun.gif");
    this.imageMode(PConstants.CENTER);

    //Assign variables
    this.planets = new ArrayList<Planet>();

    //Typography data
    this.dataFont = loadFont("Calibri-12.vlw");

    //Set up GUI objects
    this.massInput = new NumberInput(this, 2*this.width/3+5, 45, "Mass of Sun: ", "x10²⁹ kg",
        20, 1, 99);
    /* MASS_INPUT is a number input GUI for the mass of the sun:-
      it is at coordinates (2*width/3+5,45)
      with heading "Mass of Sun: "
      with units "x10²⁹ kg"
      the initial value is 20 ie the real mass of the sun
      the lowest value is 1
      the highest value is 99 */

    String[] Data; //the options in the drop down menu
    Data = new String [4];
    Data[0] = "None";
    Data[1] = "Radius";
    Data[2] = "Speed";
    Data[3] = "Acceleration";
    this.dataSelectionPointer = 0;
    this.dataSelection = new DropDown(this, 2*this.width/3+5, 80, this.dataSelectionPointer, Data,
        "Display data: ");
    /* DATA_SELECTION is a drop down menu GUI:-
      it is at coordinates(2*width/3+5,80)
      the initial value is 0, ie dataSelectionPointer
      with these options: None, Radius, Speed, Acceleration
      the heading is "Display Data: "*/

    this.timeScale = new NumberInput(this, 2*this.width/3+5, 115 , "Time Scale : 1 sec = ", "days",
        50, 1, 99);
    /* TIME_SCALE is a number input GUI:-
      it is at coordinates(2*width/3+5,115)
      with heading "Time Scale : 1 sec = "
      with units "days"
      the initial value is 50
      the lowest value is 1
      the highest value is 99 */

    this.pause = new Button(this, 2*this.width/3+5, 175, "Pause/Play",
        10+this.textWidth("Pause/Play"));
    this.isPause = false;
    /* PAUSE is a button GUI:-
      it is at coordinates (2*width/3+5,175)
      "Pause/Play" is written in the button
      it is as long as the string with 10 pixels padding
      initially the simulation is not paused*/

    this.reset = new Button(this, 2*this.width/3+5, 210, "Reset", 10+this.textWidth("Reset"));
    /* RESET is a button GUI:-
      it is at coordinates(2*width/3+5,210)
      "Reset" is written in the button
      it is as long as the string with 10 pixels padding */

    this.changeColour = new Button(this, 2*this.width/3+5, 270, "Change colour",
        10+this.textWidth("Change colour"));
    this.colourPointer = 0;
    /* CHANGE_COLOUR is a button GUI:-
      it is at coordinates (2*width/3+5,270)
      "Change colour" is written in the button
      it is as long as the string with 10 pixels padding
      initially the colourPointer is pointing at 0 */
  }

  @Override
  public void draw() {

    //Update colour scheme
    this.colourScheme(this.colourPointer);
    this.background(this.background);

    //Update mass of the sun
    if (!this.massInput.acceptKeyboard) {
      this.mass = this.massInput.returnValue(); //kg
      this.mass *= (float) PApplet.pow(10, 29); //kg x10²⁹
    }
    //Update dT/dt
    if (!this.timeScale.acceptKeyboard) {
      this.dTdt = this.timeScale.returnValue();
    }

    this.pushMatrix();
    this.translate(this.width/3, this.height/2);
    //so that coordinates (0,0) is at the centre of the sun

    //Draw the sun
    this.image(this.sun, 0, 0);

    //Show all the planets in the array
    for (int i=this.planets.size()-1; i>=0; i--) {
      this.planets.get(i).display();
    }

    //Show all the planets data depending where the dataSelectionPointer is pointing at
    if (this.dataSelectionPointer != 0) {
      for (int i=this.planets.size()-1; i>=0; i--) {
        this.planets.get(i).showData(this.dataSelectionPointer);
      }
    }

    this.popMatrix();

    //Create a square for user control panel
    this.stroke(this.outline);
    this.fill(this.background);
    this.strokeWeight(1);
    this.rectMode(PConstants.CORNER);
    this.rect(2*this.width/3, -1, this.width+1, this.height+1);

    //Display radius of the mouse position from the sun
    this.fill(this.outline);
    this.distanceSunToMouse = PApplet.round(
        PApplet.dist(this.width/3, this.height/2, this.mouseX, this.mouseY));
    //if the mouse is not in the simulation area, radius is zero
    if (this.mouseX >= 2*this.width/3) {
      this.distanceSunToMouse = 0;
    }
    this.text("Radius: " + this.distanceSunToMouse + "x10⁹ m", 2*this.width/3 + 5,
        10 + this.textDescent() + this.textAscent());

    //Display all GUI

    this.massInput.display();
    this.reset.display();
    this.timeScale.display();
    this.dataSelection.display();
    this.pause.display();
    this.changeColour.display();
  }

  @Override
  public void mousePressed() {
    if (this.mouseButton == PConstants.LEFT) { //Event: left mouse click

      //prepare all GUI
      this.massInput.activateKeyboardInput();
      this.reset.position();
      this.dataSelection.activateMenu();

      if (!this.dataSelection.menuOpen) {
        //prevents the time scale number input being selected when selecting an option from the
        //data selection drop down menu
        this.timeScale.activateKeyboardInput();
      }

      this.pause.position();
      this.changeColour.position();
    }

    //If the mouse is in the interactive area and 15 pixels away from the sun...
    //...create a planet
    if (this.mouseX < 2*this.width/3) {
      if (PApplet.dist(this.width/3, this.height/2, this.mouseX, this.mouseY) > 15) {
        if (this.mouseButton == PConstants.LEFT) {
          this.planets.add(new Planet(false));
        }
        else if (this.mouseButton == PConstants.RIGHT) {
          this.planets.add(new Planet(true));
        }
      }
    }
  }

  @Override
  public void mouseReleased() {
    if (this.mouseButton == PConstants.LEFT) {

      //If the mouse is released on reset button delete all planets
      if (this.reset.returnValue()) {
        for (int i=this.planets.size()-1; i>=0; i--) {
          this.planets.remove(i);
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

      //If mouse is released on the drop down menu, update the dataSelectionPointer
      if (this.dataSelection.returnValue() != -1) {
        this.dataSelectionPointer = this.dataSelection.returnValue();
        this.dataSelection.closeMenu();
      }
    }
  }

  @Override
  public void keyPressed() {
    //if a key is pressed update all number input GUI
    this.massInput.keyboardInput();
    this.timeScale.keyboardInput();
  }

  private class Planet{

    //r is the radius of the orbit in meters
    private float r;
    //scaleR is the radius of the orbit in pixels
    private float scaleR;
    //theta is the angular displacement
    private float theta;
    //omega is the angular velocity
    private float omega;
    //v is velocity of the orbit
    private float v;

    //string to show the planet's data
    private String data;
    //true if the orbit is clockwise
    private boolean rightSpin;

    //Attributes
    public Planet(boolean rightSpin) {

      this.rightSpin = rightSpin;

      //works out the coordinates of the mouse in polar form
      this.scaleR = PApplet.dist(width/3, height/2, mouseX, mouseY); //pixels
      this.r = this.scaleR * PApplet.pow(10, 9); //scale is 1 pixel = 1E9 meters
      this.theta = PApplet.atan2(mouseY-height/2, mouseX-width/3);
      //so that the polar coordinates are (r,theta)

    }

    //Methods

    void display() {

      //find Omega, the angular velocity
      this.v = (float) PApplet.sqrt((G*mass)/this.r);
      this.omega = this.v / this.r; //radians per seconds

      //define the direction of the orbit
      if (!this.rightSpin) {
        this.omega *= -1;
      }

      //Update theta
      if (!isPause) {
        this.omega *= 8.64*PApplet.pow(10,4); //radians per simulation days
        this.omega *= dTdt / frameRate; //radians per frame
        this.theta += this.omega;
      }

      //Draw the planet
      strokeWeight(1);
      stroke(0);
      fill(0, 255, 0);
      ellipse(this.scaleR*PApplet.cos(this.theta), this.scaleR*PApplet.sin(this.theta), 10, 10);

      //Decrease the size of theta if it gets too big
      if (this.theta > PConstants.TWO_PI) {
        this.theta -= PConstants.TWO_PI;
      }
      else if (this.theta < -PConstants.TWO_PI) {
        this.theta += PConstants.TWO_PI;
      }
    }

    void showData(int pointer) {

      //Show distance from sun
      if (pointer == 1) {
        this.data = PApplet.str(PApplet.round(this.scaleR)) + "x10⁹ m";
      }
      //Show speed
      else if (pointer == 2) {
        this.data = PApplet.str(PApplet.round(v)) + "m.s¯¹";
      }
      //Show acceleration towards the sun
      else if (pointer == 3) {
        float acceleration = PApplet.pow(v,2)*PApplet.pow(10,5)/r;
        this.data = PApplet.str(PApplet.round(acceleration)) + "x10¯⁵ m.s¯²";
      }

      pushMatrix();
      translate(this.scaleR*PApplet.cos(this.theta), scaleR*PApplet.sin(this.theta));
      //so that the coordinates (0,0) is at the centre of the planet

      fill(outline);
      stroke(outline);
      strokeWeight(0);
      textFont(dataFont,12);

      //draw the lines and data
      line(0, 0, 6, -6);
      line(6, -6, 6 + textWidth(data), -6);
      text(data, 6, -6);

      //return text back to default
      textFont(font, 24);
      popMatrix();
    }
  }

  public static void main(String[] args) {
    PApplet.main("shermanlo77.physicssim.Orbit");
  }
}
