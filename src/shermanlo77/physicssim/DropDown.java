package shermanlo77.physicssim;

class DropDown {

  //Initialize variables
  //the Simulation which owns the instance of this
  private Simulation simulation;
  //the coordinates of the top left corner of the drop down menu
  private int x, y;
  //the length and height of an option in the drop down menu
  private float menuLength, optionHeight;
  //heading is selected option
  private String heading;
  //label is the String describing the drop down menu
  private String label;
  //the array of options as a string
  private String [] optionsArray;
  //true if the drop down menu is opened
  boolean menuOpen;
  //an array of buttons GUI
  private Button [] buttonsArray;

  //Attributes
  public DropDown(Simulation simulation, int x, int y, int intialValue, String [] optionsArray,
        String label) {

    //Assign attributes
    this.simulation = simulation;
    this.x = x;
    this.y = y;
    this.optionsArray = optionsArray;
    this.label = label;
    this.optionHeight = simulation.textAscent() + simulation.textDescent() + 6;

    //find the longest string, the length of it = menuLength
    this.menuLength = 0;
    for (int i=0; i<optionsArray.length; i++) {
      if (simulation.textWidth(optionsArray[i]) > this.menuLength) {
        this.menuLength = simulation.textWidth(optionsArray[i]) + 10;
      }
    }

    //Translate the coordinates so that x and y are the coordinates for the top left corner of the
    //label of the drop down menu
    this.x += simulation.textWidth(label);

    //Setup
    this.heading = optionsArray[intialValue]; //set the initial value
    this.menuOpen = false;

    //Creates an array of buttons which will represent the drop down menu
    this.buttonsArray = new Button [optionsArray.length]; //make one button for each option

    //for each button...
    for (int i=0; i<optionsArray.length; i++) {
      buttonsArray[i] = new Button(simulation, this.x, this.y + ((i+1)*this.optionHeight),
          this.optionsArray[i], this.menuLength);
      //make a new button at coordinates (x,y+((i+1)*optionHeight)) with the string optionsArray[i]
      //in the button
    }
  }

  //Methods

  //display the menu
  void display() {

    //displays the menu if it is open
    if (this.menuOpen) {
      //display all the buttons in the array buttonsArray
      for (int i=0; i<optionsArray.length; i++) {
        this.buttonsArray[i].display();
      }
      this.simulation.fill(this.simulation.attention);
    }

    //highlight the box if the mouse is over it
    else if ((this.simulation.mouseX > this.x)
        && (this.simulation.mouseX < this.x + this.menuLength)
        && (this.simulation.mouseY > this.y)
        && (this.simulation.mouseY < this.y + this.optionHeight)) {
      this.simulation.fill(this.simulation.hover);
    }

    else {
      this.simulation.fill(this.simulation.foreground);
    }

    //draw the drop down heading
    this.simulation.rect(this.x, this.y, this.menuLength, this.optionHeight);

    //draw the string of the current option known as heading
    this.simulation.fill(this.simulation.outline);
    this.simulation.text(this.heading, this.x,
        this.y + this.simulation.textDescent() + this.simulation.textAscent());

    //draw the label string to the left of the drop down menu
    this.simulation.text(this.label, this.x - this.simulation.textWidth(this.label),
        this.y + this.simulation.textDescent() + this.simulation.textAscent());
  }

  //open or close the menu according to where the mouse is
  void activateMenu() {
    //if mouse is on the menu or any other option
    if ((this.simulation.mouseX > this.x)
        && (this.simulation.mouseX < this.x + this.menuLength)
        && (this.simulation.mouseY > this.y)
        && (this.simulation.mouseY < this.y + (this.buttonsArray.length+1)*this.optionHeight)) {
      //if mouse is on the heading...
      if ((this.simulation.mouseX > this.x)
          && (this.simulation.mouseX < this.x + this.menuLength)
          && (this.simulation.mouseY > this.y)
          && (this.simulation.mouseY < this.y + this.optionHeight)) {
        this.menuOpen= !this.menuOpen; //...reverse menuOpen boolean
      }
    }
    //if mouse is not on the menu
    else {
      this.closeMenu();
    }
  }

  //close the menu
  void closeMenu() {
    this.menuOpen = false;
  }

  //Returns a value
  int returnValue() {
    if (this.menuOpen) { //if the menu is opened
      //find the button in which is under the mouse and return its position in the array
      for (int i=0; i<this.buttonsArray.length; i++) {
        this.buttonsArray[i].clickedMouseX = this.simulation.mouseX;
        this.buttonsArray[i].clickedMouseY = this.simulation.mouseY;
        if (this.buttonsArray[i].returnValue()) {
          this.heading = this.optionsArray[i];
          return i;
        }
      }
      //if the mouse is out of the menu close it
      if (!((this.simulation.mouseX > this.x)
          && (this.simulation.mouseX < this.x + this.menuLength)
          && (this.simulation.mouseY > this.y)
          && (this.simulation.mouseY < this.y+ (this.buttonsArray.length+1) * this.optionHeight))) {
        this.closeMenu();
      }
    }
    //return a flag '-1' if the menu is not open and a return value is not valid
    return -1;
  }
}
