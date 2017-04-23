public class DAOFactory {
    // this method maps the DogDAO interface
    // to the appropriate data storage mechanism
    public static DogDAO getDogDAO() {
        DogDAO pDAO = new DogXMLFile();
        return pDAO;
    }
}
