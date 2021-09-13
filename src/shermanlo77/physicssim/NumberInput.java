package shermanlo77.physicssim;

import processing.core.PApplet;
import processing.core.PConstants;

class NumberInput {

  //Initialize variables
  //the Simulation which owns the instance of this
  private Simulation simulation;
  //the coordinates of the top left corner of the button
  private int x, y;
  //the length and height of the button
  private float boxLength, boxHeight;
  //number of digits currently and the maximum amount of digits the number input GUI can hold
  private int numberOfDigits, maxDigits;
  //the boundaries of the upper and lower values
  private String highestValue, lowestValue;
  //array of digits
  private int [] digitsArray;
  //true if the number-input GUI accept keyboard input
  boolean acceptKeyboard;
  //strings; heading goes on the left of the GUI and units goes on the right of the GUI
  private String units, heading;

  private boolean justClicked;

  //Attributes
  public NumberInput(Simulation simulation, int x, int y, String heading, String units,
      int initialValueTemp, int lowestValue, int highestValue) {

    //Assign attributes
    this.simulation = simulation;
    this.x = x;
    this.y = y;
    this.units = units;
    this.heading = heading;
    this.lowestValue = PApplet.str(lowestValue);
    this.highestValue = PApplet.str(highestValue);
    this.maxDigits = this.highestValue.length();

    this.boxLength = 10 + simulation.textWidth(this.highestValue);
    this.boxHeight = simulation.textAscent() + simulation.textDescent() + 6;

    //Assign other variables
    this.numberOfDigits = 0;
    this.acceptKeyboard = false;
    this.justClicked = false;
    this.digitsArray = new int [maxDigits];

    //append initial value
    String initialValue = PApplet.str(initialValueTemp);
    for (int i=0; i<initialValue.length(); i++) {
      this.digitsArray[i] = PApplet.parseInt(initialValue.substring(i, i+1));
    }
    this.numberOfDigits = initialValue.length();

    //Translate the coordinates so that x and y are the coordinates for the number input box
    this.x += simulation.textWidth(this.heading);
  }

  //Methods

  //Draws the boxes
  public void display() {

    //Colour the box...
    //when the box is clicked
    if (this.acceptKeyboard) {
      this.simulation.fill(this.simulation.attention);
    } else if ((this.simulation.mouseX > this.x) //when the mouse is over the box
        && (this.simulation.mouseX < this.x + this.boxLength)
        && (this.simulation.mouseY > this.y)
        && (this.simulation.mouseY < this.y + this.boxHeight)) {
      this.simulation.fill(this.simulation.hover);
    } else { //when the mouse is outside the box
      this.simulation.fill(this.simulation.foreground);
    }

    //Draw the box
    this.simulation.rect(this.x, this.y, this.boxLength, this.boxHeight);

    //Collect the digits
    String numberToPrint = "";
    for (int i=0; i<this.numberOfDigits; i++) {
      numberToPrint += this.digitsArray[i];
    }

    //Print the digits
    this.simulation.fill(this.simulation.outline);
    this.simulation.text(numberToPrint, this.x + 5,
        this.y + this.simulation.textDescent() + this.simulation.textAscent());

    //Prints the units of measurements
    this.simulation.text(this.units, this.x + this.boxLength + 5,
        this.y+this.simulation.textDescent() + this.simulation.textAscent());

    //Print the heading
    this.simulation.text(this.heading, this.x - this.simulation.textWidth(this.heading),
        this.y + this.simulation.textDescent() + this.simulation.textAscent());
  }

  //Set boxes to or not to accept keyboard inputs ONCE the mouse is pressed
  void activateKeyboardInput() {
    if ((this.simulation.mouseX > x)
        && (this.simulation.mouseX < this.x + this.boxLength)
        && (this.simulation.mouseY > this.y)
        && (this.simulation.mouseY < this.y + this.boxHeight)) {
      this.acceptKeyboard = true;
      this.justClicked = true;
    } else {
      this.acceptKeyboard = false;
    }
  }

  //Inserts or deletes a digit according to the key pressed pressed
  void keyboardInput() {
    if (this.acceptKeyboard) {
      char key = this.simulation.key;
      if (key == '1') {
        this.appendDigit("1");
      } else if (key == '2') {
        this.appendDigit("2");
      } else if (key == '3') {
        this.appendDigit("3");
      } else if (key == '4') {
        this.appendDigit("4");
      } else if (key == '5') {
        this.appendDigit("5");
      } else if (key == '6') {
        this.appendDigit("6");
      } else if (key == '7') {
        this.appendDigit("7");
      } else if (key == '8') {
        this.appendDigit("8");
      } else if (key == '9') {
        this.appendDigit("9");
      } else if (key == '0') {
        this.appendDigit("0");
      } else if (key == PConstants.BACKSPACE) {
        if (this.numberOfDigits != 0) {
          this.numberOfDigits--;
          this.justClicked = false;
        }
      } else if ((key == PConstants.RETURN) || (key == PConstants.ENTER)) {
        this.acceptKeyboard = false;
        this.justClicked = false;
      }
    }
  }

  //To add a digit to the array
  private void appendDigit(String digit) {
    if (this.justClicked) {
      this.numberOfDigits = 0;
      this.justClicked = false;
    }
    if (this.numberOfDigits != this.maxDigits) {
      this.digitsArray[numberOfDigits] = PApplet.parseInt(digit);
      this.numberOfDigits++;
    }
  }

  //To replace the array of digits completely
  private void replaceArray(String number) {
    this.numberOfDigits = 0;
    for (int i=0; i<number.length(); i++) {
      this.appendDigit(number.substring(i, i+1));
    }
  }

  //Returns a value
  public int returnValue() {

    //if there are no user input return the lowest possible value
    if (this.numberOfDigits == 0) {
      this.replaceArray(this.lowestValue);
      return PApplet.parseInt(this.lowestValue);
    }

    //convert the array of digits to a single string
    String numberToConvert = "";
    for (int i=0; i<this.numberOfDigits; i++) {
      numberToConvert += this.digitsArray[i];
    }

    //if the user input is smaller then the smallest possible value...
    //...change the input value to the smallest possible value and return it
    if (PApplet.parseInt(numberToConvert) < PApplet.parseInt(lowestValue)) {
      this.replaceArray(lowestValue);
      return PApplet.parseInt(lowestValue);
    } else if (PApplet.parseInt(numberToConvert) > PApplet.parseInt(highestValue)) {
      //if the user input is bigger then the highest possible value...
      //...change the input value to the highest possible value and return it
      this.replaceArray(this.highestValue);
      return PApplet.parseInt(this.highestValue);
    }

    return PApplet.parseInt(numberToConvert);
  }
}
