package shermanlo77.physicssim;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

public abstract class Simulation extends PApplet {

  //background colour
  int background;
  //default fill colour for boxes
  int foreground;
  //colour used for texts and borders for boxes
  int outline;
  //fill colour when the mouse is over a box
  int hover;
  //fill colour to give user attention
  int attention;

  //the font data
  PFont font;

  //button to start/stop the simulation
  Button pause;
  boolean isPause = false;

  //button to change the colour scheme
  Button changeColour;
  //colourPointer points to a colour scheme
  int colourPointer;

  @Override
  public void settings() {
    this.size(950, 490);
  }

  @Override
  public void setup() {
    this.frameRate(60);
    //Typography data
    this.font = this.loadFont("Calibri-24.vlw");
    this.textFont(this.font);
  }

  public void controlPanel() {
    this.stroke(this.outline);
    this.fill(this.background);
    this.strokeWeight(1);
    this.rectMode(PConstants.CORNER);
    this.rect(2*this.width/3, -1, this.width+1, this.height+1);
  }

  public void colourScheme(int colourPointer) {
    /**
     * @param colourPointer points to a colour scheme
     */
    if (colourPointer == 0) {
      //Black
      this.background = this.color(0);
      this.foreground = this.color(0);
      this.outline = this.color(255);
      this.hover = this.color(150);
      this.attention = this.color(0, 0, 255);
    } else if (colourPointer == 1) {
      //White
      this.background = this.color(255);
      this.foreground = this.color(255);
      this.outline = this.color(0);
      this.hover = this.color(150);
      this.attention = this.color(18, 64, 171);
    } else if (colourPointer == 2) {
      //Blue
      this.background = this.color(6, 38, 111);
      this.foreground = this.color(42, 68, 128);
      this.outline = this.color(255);
      this.hover = this.color(150);
      this.attention = this.color(255, 0, 0);
    }
  }
}
