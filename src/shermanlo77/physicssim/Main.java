package shermanlo77.physicssim;

import java.io.InputStream;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {

    //LICENSE printing
    InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("LICENSE");
    Scanner scanner = new Scanner(inputStream);
    scanner.useDelimiter("\n");
    while (scanner.hasNext()) {
      System.out.println(scanner.next());
    }
    scanner.close();

    System.out.println();
    System.out.println("Please also visit https://github.com/shermanlo77/physicssim");

    //parse args
    if (args.length > 0) {
      String userArg = args[0];
      if (userArg.equals("-orbit")) {
        shermanlo77.physicssim.Orbit.main(args);
      } else if (userArg.equals("-satellite")) {
        shermanlo77.physicssim.SatelliteManeuvers.main(args);
      } else if (userArg.equals("-doppler")) {
        shermanlo77.physicssim.Doppler.main(args);
      }
    }
  }

}
