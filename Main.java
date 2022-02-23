/*
 * Program Title: Super Auto Battler
 * Author: Hayden Mankin
 * Date: 2/21/2021
 * Purpose: Run a mock version of the super auto pets game in a console.
 */

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
  private static Scanner in;
  private static Team team1;
  private static Team team2;

  private enum State {
    CONTINUE, TIE, WIN_ONE, WIN_TWO
  }

  public static void main(String[] args) {
    in = new Scanner(System.in);

    boolean flag = true;

    do {
      setupTeams();
      startBattle();
      System.out.println();
      System.out.println("Would you like to play again? (yes to continue)");
      String input = getTokenFromConsole();
      flag = input.equalsIgnoreCase("yes");
    } while(flag);

    in.close();
  }

  private static void setupTeams() {
    System.out.println("Please create Team 1");
    team1 = createTeam();

    System.out.println("Please create Team 2");
    team2 = createTeam();
  }

  private static void startBattle() {
    
    
    State state = State.CONTINUE;

    System.out.println("\nSTARTING BATTLE\n");

    onBattleStart(team1, team2);
    onBattleStart(team2, team1);

    handleFainted();

    if (team1.isEmpty() && team2.isEmpty()) state = State.TIE;
    else if (team1.isEmpty())               state = State.WIN_TWO;
    else if (team2.isEmpty())               state = State.WIN_ONE;

    System.out.println("\nDISPLAYING TEAMS\n");
    displayTeams();
    System.out.print("Hit enter to continue...");
    in.nextLine();

    System.out.println("===========================");

    while (state == State.CONTINUE) {
      
      Animal front1 = team1.getFront();
      Animal front2 = team2.getFront();

      if (front1 instanceof BeforeAttack) {
        ((BeforeAttack) front1).beforeAttack(team1, team2); 
      }

      if (front2 instanceof BeforeAttack) {
        ((BeforeAttack) front2).beforeAttack(team2, team1); 
      }

      System.out.println("\nHANDLING ATTACKS\n");

      front1.attack(front2);
      front2.attack(front1);

      
      handleFainted();

      if (team1.isEmpty() && team2.isEmpty()) state = State.TIE;
      else if (team1.isEmpty())               state = State.WIN_TWO;
      else if (team2.isEmpty())               state = State.WIN_ONE;

      System.out.println("\nDISPLAYING TEAMS\n");
      displayTeams();

      System.out.print("Hit enter to continue...");
      in.nextLine();

      System.out.println("===========================");
    }

    if (state == State.WIN_ONE) {
      System.out.println("Team 1 wins!");
    } else if (state == State.WIN_TWO) {
      System.out.println("Team 2 wins!");
    } else {
      System.out.println("it is a tie!");
    }
  }

  private static void displayTeams() {
    System.out.println("TEAM 1: ");
    System.out.println(team1.toString());

    System.out.println("");

    System.out.println("TEAM 2: ");
    System.out.println(team2.toString());
  }

  private static void handleFainted() {
    boolean flag;

    do {
      flag = false;
      flag |= checkForFaintedOnTeam(team1, team2);
      flag |= checkForFaintedOnTeam(team2, team1);
    } while (flag);

  }

  private static boolean checkForFaintedOnTeam(Team friends, Team rivals) {
    boolean flag = false;
    for (Animal animal : friends.getAnimals()) {
      if (animal.getHealth() <= 0) {
        int position = friends.removeAnimal(animal);
        if (position >= 0) {
          System.out.println(animal.getSymbol() + " fainted");
          if (animal instanceof OnFaint ) {
            flag = true;
            ((OnFaint)animal).onFaint(friends, rivals, position);
          }
        }
      }
    }

    return flag;
  }

  private static void onBattleStart(Team friends, Team rivals) {
    for (Animal animal : friends.getAnimals()) {
      if (animal instanceof OnBattleStart) {
        ((OnBattleStart)animal).onBattleStart(friends, rivals);
      }
    }
  }

  private static Team createTeam() {
    System.out.println();
    System.out.println("\t1. From a file");
    System.out.println("\t2. From console");
    System.out.println();
    System.out.print("How would you like to create the team? ");

    Team team = null;

    boolean flag = true;
    do {
      int choice = getIntFromConsole();
      if (choice == 1) {
        try {
          team = readTeamFromFile();
          flag = false;
        } catch (Exception e) {
          System.out.println("I could not read that file, sorry. ");
          System.out.print("How would you like to create the team? ");
        }
      } else if (choice == 2) {
        team = readTeamFromConsole();
        flag = false;
      }
    } while (flag);

    return team;
  }

  private static Team readTeamFromFile() throws Exception {
    Team team = new Team();

    System.out.print("Enter a file name: ");

    String filename = getTokenFromConsole();

    try (Scanner fileIn = new Scanner(new File(filename))) {
      while(fileIn.hasNext()) {
        String token = fileIn.next();
        int health = fileIn.nextInt();
        int attack = fileIn.nextInt();

        Class clazz = Class.forName(token);
        Constructor constr = clazz.getConstructor(int.class, int.class);
        Animal newAnimal = (Animal) constr.newInstance(health, attack);
        team.addAnimal(newAnimal);
      }
    }

    return team;
  }

  private static Team readTeamFromConsole() {
    Team team = new Team();

    while (true) {
      System.out.print("What animal would you like to add (type \'end\' to exit)? ");
      String token = getTokenFromConsole();

      if (token.equalsIgnoreCase("end")) break;
      Class clazz;
      try {
        clazz = Class.forName(token);
      } catch (ClassNotFoundException e) {
        System.out.println("That is not a valid className");
        continue;
      }

      Constructor constr;

      try {
        constr = clazz.getConstructor(int.class, int.class);
      } catch (Exception e) {
        System.out.println("Something went wrong!");
        e.printStackTrace();
        continue;
      } 
      
      System.out.print("Enter it\'s health: ");
      int health = getIntFromConsole();

      System.out.print("Enter it\'s attack: ");
      int attack = getIntFromConsole();

      try {
        Animal newAnimal = (Animal) constr.newInstance(health, attack);
        team.addAnimal(newAnimal);
      } catch (Exception e) {
        System.out.println("Something went wrong!");
        e.printStackTrace();
      } 
    }

    return team;
  }

  public static int getIntFromConsole() {
    int ret = 0;
    boolean flag = true;

    do {
      try {
        ret = in.nextInt();
        flag = false;
      } catch (NoSuchElementException e) {
        System.out.print("That is not a valid input, please try again: ");
      } finally {
        in.nextLine();
      }
    } while(flag);

    return ret;
  }

  public static String getTokenFromConsole() {
    String ret = null;
    boolean flag = true;

    do {
      try {
        ret = in.next();
        flag = false;
      } catch (NoSuchElementException e) {
        System.out.print("That is not a valid input, please try again: ");
      } finally {
        in.nextLine();
      }
    } while(flag);

    return ret;
  }
}
