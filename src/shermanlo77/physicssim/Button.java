package shermanlo77.physicssim;

class Button {

  //Initialize variables
  //the Simulation which owns the instance of this
  private Simulation simulation;
  //position of the top left corner of the button at coordinates (x,y) during animations
  private int x;
  private float y;
  //position of the top left corner of the button at coordinates (xOrginal, yOrginal)
  private int xOrginal;
  private float yOrginal;
  //height and length of the button
  private float buttonHeight, buttonLength;
  //the coordinates of the mouse when clicked
  int clickedMouseX, clickedMouseY;
  //the string inside the button
  private String string;
  //true if the button is being animated
  private boolean buttonMoved;

  //Attributes
  public Button(Simulation simulation, int x, float y, String string, float buttonLength) {

    //Assign attributes
    this.simulation = simulation;
    this.xOrginal = x;
    this.yOrginal = y;
    this.string = string;

    this.buttonLength = buttonLength;
    this.buttonHeight = this.simulation.textAscent() + this.simulation.textDescent() + 6;

    //Assign other variables
    this.x = xOrginal; //save the x coordinates of the top left corner of the button
    this.y = yOrginal; //save the y coordinates of the top left corner of the button
    this.buttonMoved = false;
  }

  //Methods

  //Position the boxes
  public void position() {

    //save the coordinates of the clicked mouse
    this.clickedMouseX = this.simulation.mouseX;
    this.clickedMouseY = this.simulation.mouseY;

    //if the mouse is in the box, animate the box
    if ((this.simulation.mouseX > this.x)
        && (this.simulation.mouseX < this.x + this.buttonLength)
        && (this.simulation.mouseY >= this.y)
        && (this.simulation.mouseY < this.y + this.buttonHeight)) {
      if (!this.buttonMoved) { //only animate the box if it hasn't been animated before
        this.x += 3; //shift the box 3 pixels to the right
        this.y += 3; //shift the box 3 pixels down
        this.buttonMoved = true; //the button has been animated
      }
    } else {
      //else the button is returned to its original position
      this.returnPosition();
    }
  }

  //Move box to neutral position
  void returnPosition() {
    this.x = this.xOrginal;
    this.y = this.yOrginal;
    this.buttonMoved = false;
  }

  //Return a boolean value
  public boolean returnValue() {
   //if the mouse is BOTH pressed and released on the button, return true
   return ((this.clickedMouseX > this.xOrginal)
       && (this.clickedMouseX < this.xOrginal + this.buttonLength)
       && (this.clickedMouseY >= this.yOrginal)
       && (this.clickedMouseY < this.yOrginal + this.buttonHeight)
       && (this.simulation.mouseX > this.xOrginal)
       && (this.simulation.mouseX < this.xOrginal + this.buttonLength)
       && (this.simulation.mouseY >= this.yOrginal)
       && (this.simulation.mouseY < this.yOrginal + this.buttonHeight));
  }

  //Display the box
  public void display() {
    //the box is a certain colour according if the mouse is over the mouse or not

    //when the mouse is over the button
    if ((this.simulation.mouseX > this.xOrginal)
        && (this.simulation.mouseX < this.xOrginal + this.buttonLength)
        && (this.simulation.mouseY >= this.yOrginal)
        && (this.simulation.mouseY < this.yOrginal + this.buttonHeight)) {
      this.simulation.fill(this.simulation.attention);
    } else {
      //when the mouse is not over the button
      this.simulation.fill(this.simulation.foreground);
    }
    this.simulation.stroke(this.simulation.outline);
    this.simulation.rect(this.x, this.y, this.buttonLength, this.buttonHeight); //draw the button

    this.simulation.fill(this.simulation.outline);
    //draw the string in the button
    this.simulation.text(this.string, this.x,
        this.y + this.simulation.textDescent() + this.simulation.textAscent());
  }
}
