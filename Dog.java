import java.text.NumberFormat;

public class Dog {
    private String code;
    private String id;
    private String name;
	private String breed;
    private int size;
    private double weight;
    private char sex;

    public Dog() {
        this("", "", "", "", 0, 0.0, ' ');
    }

    public Dog(String code, String id, String name, String breed,
            int size, double weight, char sex) {
        setCode(code);
        setId(id);
        setName(name);
		setBreed(breed);
        setSize(size);
        setWeight(weight);
        setSex(sex);
    }

	public void setCode(String code) { this.code = code; }
	public String getCode() { return code; }
	public void setId(String id) { this.id = id; }
	public String getId() { return id; }
	public void setName(String name) { this.name = name; }
	public String getName() { return name; }
	public void setBreed(String breed) { this.breed = breed; }
	public String getBreed() { return breed; }
	public void setSize(int size) { this.size = size; }
	public int getSize() { return size; }
    public String getSizeString() {
        String sizeString = Integer.toString(size);
        return sizeString;
    }
	public void setWeight(double weight) { this.weight = weight; }
	public double getWeight() { return weight; }
    public String getWeightString() {
        String weightString = Double.toString(weight);
        return weightString;
    }
	public void setSex(char sex) { this.sex = sex; }
	public char getSex() { return sex; }
    public boolean equals(Object object) {
        if (object instanceof Dog) {
            Dog dog2 = (Dog) object;

            if (
                code.equals(dog2.getCode()) &&
                id.equals(dog2.getId()) &&
                name.equals(dog2.getName()) &&
				breed.equals(dog2.getBreed()) &&
                size == dog2.getSize() &&
                weight == dog2.getWeight() &&
                sex == dog2.getSex()) {
                return true;
            }
        }
        return false;
    }

    // This is kind of an ugly way of circumventing the implicit call to this
    // class's toString() method by the DefaultListModel's addElement() method.
    public String toStringAllFields() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("code: ").append(code).append(" \n")
            .append("name: ").append(name).append(" \n")
			.append("breed: ").append(breed).append(" \n")
            .append("size: ").append(size).append(" \n")
            //.append("weight: ").append(getFormattedWeight()).append(" \n")
			.append("weight: ").append(weight).append(" \n")
            .append("sex: ").append(sex).append("\n");

        return sb.toString();
    }

    @Override
	public String toString() {
		//return "[ code: " + code + ", id: " + id + ", name: " + name + ",  breed: " + breed + ", size: " + size + ", weight: " + weight + ", sex: " + sex + " ]";
		return name;
	}
}
