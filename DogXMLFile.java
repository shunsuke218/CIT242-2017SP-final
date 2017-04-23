import java.util.*;
import java.io.*;
import javax.xml.stream.*;  // StAX API

public class DogXMLFile implements DogDAO {
	
    private String dogsFilename = "Dogs.xml";
    private File dogsFile = null;

	// For testing purpose.
	public static void main(String[] args){
		DogDAO dogDAO = null;
		dogDAO = DAOFactory.getDogDAO();
		ArrayList<Dog> dogs = dogDAO.getDogs();
		for (Dog dog : dogs){
			System.out.println(dog);
		}
	};

    public DogXMLFile() {
        dogsFile = new File(dogsFilename);
    }

    private void checkFile() throws IOException {
        if (!dogsFile.exists()) {
            dogsFile.createNewFile();
        }
    }

    private boolean saveDogs(ArrayList<Dog> dogs) {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

        try {
            this.checkFile();

            FileWriter fileWriter = new FileWriter(dogsFilename);
            XMLStreamWriter writer = outputFactory.createXMLStreamWriter(fileWriter);

            writer.writeStartDocument("1.0");
            writer.writeStartElement("Dogs");

            for (Dog p : dogs) {
                writer.writeStartElement("Dog");
                writer.writeAttribute("Code", p.getCode());

                writer.writeStartElement("ID");
                writer.writeCharacters(p.getId());
                writer.writeEndElement();

                writer.writeStartElement("Name");
                writer.writeCharacters(p.getName());
                writer.writeEndElement();

                writer.writeStartElement("Breed");
                writer.writeCharacters(p.getBreed());
                writer.writeEndElement();

                writer.writeStartElement("Size");
                int size = (int) p.getSize();
                writer.writeCharacters(Integer.toString(size));
                writer.writeEndElement();

                writer.writeStartElement("Weight");
                double weight = p.getWeight();
                writer.writeCharacters(Double.toString(weight));
                writer.writeEndElement();

                writer.writeStartElement("Sex");
                char sex = p.getSex();
                writer.writeCharacters(Character.toString(sex));
                writer.writeEndElement();

                writer.writeEndElement();
            }
            writer.writeEndElement();
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<Dog> getDogs() {
        ArrayList<Dog> dogs = new ArrayList<Dog>();
        Dog p = null;
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            this.checkFile();

            FileReader fileReader = new FileReader(dogsFilename);
            XMLStreamReader reader = inputFactory.createXMLStreamReader(fileReader);

            while (reader.hasNext()) {
                int eventType = reader.getEventType();

                switch (eventType) {
                case XMLStreamConstants.START_ELEMENT:
                    String elementName = reader.getLocalName();
                    if (elementName.equals("Dog")) {
                        p = new Dog();
                        String code = reader.getAttributeValue(0);
                        p.setCode(code);
                    }
                    if (elementName.equals("ID")) {
                        String id = reader.getElementText();
                        p.setId(id);
                    }
                    if (elementName.equals("Name")) {
                        String name = reader.getElementText();
                        p.setName(name);
                    }
                    if (elementName.equals("Breed")) {
                        String breed = reader.getElementText();
                        p.setBreed(breed);
                    }
                    if (elementName.equals("Size")) {
                        String sizeString = reader.getElementText();
                        int size = Integer.parseInt(sizeString);
                        p.setSize(size);
                    }
                    if (elementName.equals("Weight")) {
                        String weightString = reader.getElementText();
                        double weight = Double.parseDouble(weightString);
                        p.setWeight(weight);
                    }
                    if (elementName.equals("Sex")) {
                        String sexString = reader.getElementText();
                        char sex = sexString.charAt(0);
                        p.setSex(sex);
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    elementName = reader.getLocalName();
                    if (elementName.equals("Dog")) {
                        dogs.add(p);
                    }
                    break;
                default:
                    break;
                }
                reader.next();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return null;
        }
        return dogs;
    }

    public Dog getDog(String code) {
        ArrayList<Dog> dogs = this.getDogs();

        for (Dog p : dogs) {
            if (p.getCode().equals(code)) {
                return p;
            }
        }
        return null;
    }

    public boolean addDog(Dog d) {
        ArrayList<Dog> dogs = this.getDogs();

		for (Dog p : dogs) 
			if (p.getCode().equals(d.getCode()))
				return false;
		if (d.getCode().equals("-1"))
			assignCode(d);
        dogs.add(d);

        return this.saveDogs(dogs);
    }

	public void assignCode(Dog d) {
		int value = 0;
		ArrayList<Dog> dogs = this.getDogs();
		ArrayList<Integer> dogs_code = new ArrayList<Integer>();

		for (Dog p : dogs)
			dogs_code.add((int)Integer.parseInt(p.getCode()));
		Collections.sort(dogs_code);
		for (Integer i : dogs_code) {
			value = i.intValue() + 1;
			if (! dogs_code.contains(new Integer(value))) {
				break;
			}
		}
		if (value == 0) {
			System.out.println("Too many dogs here!! Can't add!!");
			return;
		} else {
			String code_str = String.format("%03d", value);
			d.setCode(code_str);
			return;
		}
	}

    public boolean deleteDog(Dog p) {
        ArrayList<Dog> dogs = this.getDogs();
        dogs.remove(p);

        return this.saveDogs(dogs);
    }

    public boolean updateDog(Dog newDog) {
        ArrayList<Dog> dogs = this.getDogs();

        // get the old dog and remove it
        Dog oldDog = this.getDog(newDog.getCode());
        int i = dogs.indexOf(oldDog);
        dogs.remove(i);

        // add the updated dog
        dogs.add(i, newDog);

        return this.saveDogs(dogs);
    }

	// Does the dog already exists?
    public boolean existsDog(String code) {
        ArrayList<Dog> dogs = this.getDogs();

        for (Dog p : dogs) {
            if (p.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

}
