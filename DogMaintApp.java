// Written by: Shunsuke Haga
// Date: 2017-04-20
//
// Implemented the code from ProductMaintAppFinal.
// CUI application to manage famous dogs.

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Formatter;

public class DogMaintApp implements DogConstants {
    private static DogDAO dogDAO = null;
    private static Scanner sc = null;

    public static void main(String args[]) {
        System.out.println("Welcome to the Dog Maintenance application\n");

        dogDAO = DAOFactory.getDogDAO();
        sc = new Scanner(System.in);

        displayMenu();

        String action = "";
        while (!action.equalsIgnoreCase("exit")) {
            action = Validator.getString(sc, "Enter a command: ");
            System.out.println();

            if (action.equalsIgnoreCase("list")) {
                displayAllDogs();
            } else if (action.equalsIgnoreCase("add")) {
                addDog();
            } else if (action.equalsIgnoreCase("del") || action.equalsIgnoreCase("delete")) {
                deleteDog();
            } else if (action.equalsIgnoreCase("update")) {
                updateDog();
            } else if (action.equalsIgnoreCase("help") || action.equalsIgnoreCase("menu")) {
                displayMenu();
            } else if (action.equalsIgnoreCase("exit")) {
                System.out.println("Bye.\n");
            } else {
                System.out.println("Error! Not a valid command.\n");
            }
        }
    }

    public static void displayMenu() {
        System.out.println("COMMAND MENU");
        System.out.println("list    - List all dogs");
        System.out.println("add     - Add a dog");
        System.out.println("del     - Delete a dog");
        System.out.println("update  - Update a dog");
        System.out.println("help    - Show this menu");
        System.out.println("exit    - Exit this application\n");
    }

    public static void displayAllDogs() {
        System.out.println("LIST OF DOGS");
        ArrayList<Dog> dogs = dogDAO.getDogs();
		System.out.println("Returned from  getDogs...");
        if (dogs == null) {
            System.out.println("Error! Unable to get dogs.\n");
        } else {
            Dog p = null;
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < dogs.size(); i++) {
                p = dogs.get(i);

                sb.append(
						  "code: " + StringUtils.padWithSpaces(p.getCode(), CODE_SIZE + 1) +
						  "id: " + StringUtils.padWithSpaces(p.getId(), CODE_SIZE + 1) +
						  "name: " + StringUtils.padWithSpaces(p.getName(), NAME_SIZE + 1) +
						  "breed: " + StringUtils.padWithSpaces(p.getBreed(), BREED_SIZE + 5) +
						  "size: " + StringUtils.padWithSpaces(p.getSizeString(), CODE_SIZE + 2) +
						  "weight: " + StringUtils.padWithSpaces(p.getWeightString(), CODE_SIZE + 2) +
						  "gender: " + p.getSex() + "\n");
            }

            System.out.println(sb.toString());
        }
    }

    public static void addDog() {
        int code = Validator.getInt(sc, "Enter dog code: ", 0, 1000);
		String code_str = String.format("%03d", code);
		String ID = Validator.getString(sc, "Enter dog ID: ", true);
        String name = Validator.getLine(sc, "Enter dog name: ");
		String breed = Validator.getLine(sc, "Enter dog breed: ");
        int size = Validator.getInt(sc, "Enter dog size: ");
        double weight = Validator.getDouble(sc, "Enter weight: ");
        char sex = Validator.getChar(sc, "Enter the sex of dog: ");

		
        Dog dog = new Dog();

        dog.setCode(code_str);
        dog.setId(ID);
        dog.setName(name);
		dog.setBreed(breed);
        dog.setSize(size);
        dog.setWeight(weight);
        dog.setSex(sex);
        boolean success = dogDAO.addDog(dog);

        System.out.println();
        if (success) {
            System.out.println(name + " was added to the database.\n");
        } else {
            System.out.println("Error! Unable to add dog\n");
        }
    }

    public static void deleteDog() {
        int code = Validator.getInt(sc, "Enter dog code to delete: ", 0, 1000);
		String code_str = String.format("%03d", code);
        Dog p = dogDAO.getDog(code_str);

        System.out.println();
        if (p != null) {
            boolean success = dogDAO.deleteDog(p);
            if (success) {
                System.out.println(p.getName()
								   + " was deleted from the database.\n");
            } else {
                System.out.println("Error! Unable to add dog\n");
            }
        } else {
            System.out.println("No dog matches that code.\n");
        }
    }

    public static void updateDog() {
        int code = Validator.getInt(sc, "Enter dog code: ", 0, 1000);
		String code_str = String.format("%03d", code);
		String ID = Validator.getString(sc, "Enter dog ID: ", true);
        String name = Validator.getLine(sc, "Enter dog name: ");
		String breed = Validator.getLine(sc, "Enter breed name: ");
        int size = Validator.getInt(sc, "Enter dog size: ");
        double weight = Validator.getDouble(sc, "Enter weight: ");
        char sex = Validator.getChar(sc, "Enter the sex of dog: ");

        Dog dog = new Dog();

        dog.setCode(code_str);
        dog.setId(ID);
        dog.setName(name);
        dog.setBreed(breed);
        dog.setSize(size);
        dog.setWeight(weight);
        dog.setSex(sex);
        boolean success = dogDAO.updateDog(dog);

        System.out.println();
        if (success) {
            System.out.println(name
							   + " was added to the database.\n");
        } else {
            System.out.println("Error! Unable to add dog\n");
        }
    }
}
