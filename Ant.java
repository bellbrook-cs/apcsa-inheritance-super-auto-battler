import java.util.ArrayList;

public class Ant extends Animal implements OnFaint {

    public Ant(int health, int attack) {
        super(health, attack);
    }

    public void onFaint(Team friends, Team rivals, int position) {
        System.out.println("Giving random teammate a buff!");
       
        if (friends.isEmpty()) {
            System.out.println("No friends left to buff :(");
        } else {
            ArrayList<Animal> options = friends.getAnimals();
            Animal selected = options.get((int)(Math.random() * options.size()));
            System.out.println("Giving " + selected.getSymbol() + " (+1, +2)");
            selected.setHealth(selected.getHealth() + 1);
            selected.setAttack(selected.getAttack() + 2);
        }
    }

    public String getSymbol() {
        return "Ant";
    }

    public String toString() {
        return "Ant(" + getHealth() + ", " + getAttack() + ")\nOn faint, give a random friend (+1, +2)";
    }
    
}
