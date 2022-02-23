import java.util.ArrayList;

public class Team {
    private ArrayList<Animal> animals;

    public Team() {
        animals = new ArrayList<Animal>();
    }

    public Animal getFront() {
        return getAnimalByIndex(0);
    }

    public Animal getAnimalByIndex(int index) {
        return animals.get(index);
    }

    public ArrayList<Animal> getAnimals() {
        return (ArrayList<Animal>) animals.clone();
    }

    public boolean isEmpty() {
        return animals.isEmpty();
    }

    public void addAnimal(Animal animal) {
        this.addAnimal(animals.size(), animal);
    }

    public void addAnimal(int index, Animal animal) {
        animals.add(index, animal);
    }

    public int removeAnimal(Animal animal) {
        int index = animals.indexOf(animal);
        animals.remove(animal);

        return index;
    }

    public String toString() {
        String s = "";

        for (int i = 1; i <= animals.size(); i++) {
            s += i + ". " + animals.get(i - 1).toString() + "\n";
        }

        if (s.equals("")) {
            s = "<empty>";
        }

        return s;
    }
}
