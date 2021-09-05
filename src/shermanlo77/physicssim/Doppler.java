package shermanlo77.physicssim;

import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PConstants;

public class Doppler extends Simulation {

  //Initialize variables

  //SIMULATION VARIABLES
  //x=source position
  private float x;
  //gamma=relativistic factor
  private float gamma;
  //v/c OR speed of source
  private float v;

  //frameCounter increments every frame
  private int frameCounter;
  //t is the time period to create a new wave front
  private int t;

  //array of wave fronts
  private ArrayList<Wave> waves;
  private boolean relativity, startSimulation;

  //GUI
  private NumberInput vInput; //user input for v/c
  private NumberInput lambdaInput; //user input for lambda
  private DropDown relativityInput; //user choice of relativity
  private Button start; //button to start/stop the simulation

  @Override
  public void setup() {
    super.setup();

    //Assign variables
    this.x = 0;
    this.frameCounter = 0;
    this.colourPointer = 0;
    this.waves = new ArrayList<Wave>();

    this.vInput = new NumberInput(this, 2*this.width/3+5, 10, "v/c = ", "x10¯²", 40, 0, 99);
    /* V_INPUT is a number input GUI for the speed of the source:-
      it is at coordinates (2*width/3+5,10)
      with heading "v/c = "
      with units "x10¯²"
      the initial value is 0.4
      the lowest value is 1
      the highest value is 9 */

    this.lambdaInput = new NumberInput(this, 2*this.width/3+5, 45, "λ = ", "m", 20, 5, 99);
    /* LAMBDA_INPUT is a number input GUI for the wavelength:-
      it is at coordinates (2*width/3+5,45)
      with heading "λ = "
      the initial value is 20
      the lowest value is 1
      the highest value is 99 */

    String [] data; //the options in the drop down menu
    data = new String [2];
    data [0] = "On";
    data [1] = "Off";
    this.relativity = true;
    this.relativityInput = new DropDown(this, 2*this.width/3+5, 80, 0, data,
        "Relativistic factor: ");
    /* RELATIVITY_INPUT is a drop down menu GUI:-
      it is at coordinates(2*width/3+5,80)
      the initial value is 1, ie on
      with these options: On, off
      the heading is "Relativity: "*/

    this.start = new Button(this, 2*this.width/3+5, 235, "Start/Stop",
        10 + this.textWidth("Start/Stop"));
    this.startSimulation = false;
    /* START is a button GUI:-
      it is at coordinates (2*width/3+5, 235)
      "Start/Stop" is written in the button
      it is as long as the string with 10 pixels padding
      initially the simulation is stopped */

    this.pause = new Button(this, 2*this.width/3+5, 270, "Pause/Play",
        10 + this.textWidth("Pause/Play"));
    this.isPause = false;
    /* START is a button GUI:-
      it is at coordinates (2*width/3+5, 270)
      "Pause/Play" is written in the button
      it is as long as the string with 10 pixels padding
      initially the simulation is stopped */

    this.changeColour = new Button(this, 2*this.width/3+5, 330, "Change colour",
        10 + this.textWidth("Change colour"));
    this.colourPointer = 2;
    /* CHANGE_COLOUR is a button GUI:-
      it is at coordinates (2*width/3+5,380)
      "Change colour" is written in the button
      it is as long as the string with 10 pixels padding
      initially the colourPointer is pointing at 0 */
  }

  @Override
  public void draw() {

    //Update colour scheme
    this.colourScheme(this.colourPointer);
    this.background(this.background);

    //Update v/c
    if (!this.vInput.acceptKeyboard) {
      this.v = this.vInput.returnValue();
      this.v /= 100;
    }

    //Update t
    if (!this.lambdaInput.acceptKeyboard) {
      this.t = this.lambdaInput.returnValue();
    }

    //If the simulation is not stopped
    if (this.startSimulation) {

      //Draw source
      this.noStroke();
      this.fill(0, 255, 0);
      this.ellipse(x, height/2, 10, 10);

      //Work out relativistic factor
      if (this.relativity) {
        this.gamma = 1/(PApplet.sqrt(1-PApplet.sq(this.v)));
      }
      else {
        this.gamma = 1;
      }

      //Make new waves fronts if required and move the source
      if (!this.isPause) {
        if (this.frameCounter%(PApplet.round(this.gamma)*this.t) == 0) {
          //if the frameCounter has reached the time period to make a new wave front...
          this.waves.add(new Wave(x)); //make a new wave front at position x
        }
        this.x += this.v; //move the source
        this.frameCounter++; //increment the frameCounter
      }

      //Display all wave fronts
      for (int i=this.waves.size()-1; i>=0; i--) {
        Wave wave = waves.get(i);
        wave.display();
        if (wave.r > 2*this.width/3) {
          this.waves.remove(i);
        }
      }

      //Restart the simulation when the particle travels all the way
      if (this.x >= 2*this.width/3) {
        this.x = 0;
        this.frameCounter = 0;
        for (int i=this.waves.size()-1; i>=0; i--) {
          this.waves.remove(i);
        }
      }
    }

    //Create a square for user control panel
    this.stroke(this.outline);
    this.fill(this.background);
    this.strokeWeight(1);
    this.rectMode(PConstants.CORNER);
    this.rect(2*this.width/3, -1, this.width+1, this.height+1);

    //Display delta lambda + lambda
    this.fill(this.outline);
    this.text("λ+Δλ = " + this.t*this.gamma*(1+this.v) + " m", 2*this.width/3 + 5,
        140 + this.textDescent() + this.textAscent());

    //Display gamma
    this.text("γ = " + gamma, 2*this.width/3+5, 175 + this.textDescent() + this.textAscent());

    //Display GUI
    this.vInput.display();
    this.lambdaInput.display();
    this.relativityInput.display();
    this.start.display();
    this.pause.display();
    this.changeColour.display();
  }

  @Override
  public void mousePressed() {
    if (this.mouseButton == PConstants.LEFT) { //Event: left mouse click
      //prepare all GUI
      this.vInput.activateKeyboardInput();
      this.lambdaInput.activateKeyboardInput();
      this.relativityInput.activateMenu();
      this.start.position();
      this.pause.position();
      this.changeColour.position();
    }
  }

  @Override
  public void mouseReleased() {
    if (this.mouseButton == PConstants.LEFT) {

      //If mouse is released on the drop down menu, update relativity boolean
      if (this.relativityInput.returnValue() != -1) {
        if (this.relativityInput.returnValue() == 0) {
          this.relativity = true;
        }
        else {
          this.relativity = false;
        }
        this.relativityInput.closeMenu();
      }

      //If the mouse clicks on the start/stop button, inverse startSimulation boolean
      if (this.start.returnValue()) {
        this.startSimulation = !this.startSimulation;

        //If the simulation shouldn't start, reset all variables
        if (!this.startSimulation) {
          this.x = 0;
          this.t = 0;
          this.v = 0;
          for (int i=this.waves.size()-1; i>=0; i--) {
            this.waves.remove(i);
          }
          this.gamma = 0;
          this.isPause = false;
        }
      }
      this.start.returnPosition();

      //If mouse clicks on pause button, inverse pause boolean
      if (this.pause.returnValue()) {
        isPause = !this.isPause;
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
    this.vInput.keyboardInput();
    this.lambdaInput.keyboardInput();
  }

  private class Wave {

    //centre = centre of the wavefront
    private float centre;
    //r = radius of the wavefront
    private float r;

    public Wave(float centre) {
      this.centre = centre;
    }

    //Method display()
    void display() {

      //draw the wave front
      noFill();
      stroke(outline);
      ellipse(this.centre, height/2, 2*this.r, 2*this.r);

      //if the simulation is not paused, increase the radius
      if (!isPause) {
        this.r++;
      }
    }
  }

  public static void main(String[] args) {
    PApplet.main("shermanlo77.physicssim.Doppler");
  }
}
