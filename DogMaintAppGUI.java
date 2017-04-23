// Written by: Shunsuke Haga
// Date: 2017-04-20
//
// Implemented the code from ProductMaintAppFinal.
// GUI application to manage famous dogs.

/*
 *
 * This class implements a fairly pimped-out GUI for the Dog Maintenance
 * application. It can be controlled entirely from the keyboard (see 'Help'
 * for keyboard shortcuts) and allows quick navigation via next/previous
 * buttons.
 *
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// NOTE: Remember to recompile Dog.java!
public class DogMaintAppGUI extends JFrame {
    private DogDAO dogDAO = null;

    private JTextField codeField;
    private JTextField idField;
    private JTextField nameField;
	private JTextField breedField;
    private JTextField sizeField;
    private JTextField weightField;
    private JTextField sexField;

    private JList dogList;
    private DefaultListModel dogListModel;

    private JButton saveButton;

    private ArrayList<JTextField> editableTextFields;
    private ArrayList<JTextField> allTextFields;

    public DogMaintAppGUI() {
        super("Dog Maintenance Application");

        try {
            dogDAO = DAOFactory.getDogDAO();
        } catch (Exception e) {
            this.handleFatalException(e);
        }

        editableTextFields = new ArrayList<JTextField>();
        allTextFields = new ArrayList<JTextField>();

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(6, 2));

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(590, 390);
		//setSize(890, 390);

        //codeField = addLabelAndTextField("Code", 100, true, centerPanel);
		idField = addLabelAndTextField("ID", 100, true, centerPanel);
        //idField = new JTextField("-1");  // not shown
		codeField = new JTextField("-1");  // not shown
        nameField = addLabelAndTextField("Name", 100, true, centerPanel);
		breedField = addLabelAndTextField("Breed", 100, true, centerPanel);
        sizeField = addLabelAndTextField("Size", 10, true, centerPanel);
        weightField = addLabelAndTextField("Weight", 10, true, centerPanel);
        sexField = addLabelAndTextField("Sex", 10, true, centerPanel);

        add(centerPanel, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new GridLayout(2, 1));

        // NOTE: The list will contain dog names only, due to my
        // workaround with the Dog class's toString() method.
        dogListModel = new DefaultListModel();
        dogList = new JList(dogListModel);
        dogList.setLayoutOrientation(JList.VERTICAL);
        dogList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        try {
            refreshDogList();
        } catch (Exception e) {
            handleFatalException(e);
        }
        dogList.setSelectedIndex(0);

        JScrollPane scrollPane = new JScrollPane(dogList);
        eastPanel.add(scrollPane);

        JPanel eastPanelBottom = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridy = 0;
        JPanel navButtonPanel = new JPanel(new GridLayout(1, 2));
        JButton prevButton = new JButton("<<   Prev");
        prevButton.setMnemonic('[');
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int index = dogList.getSelectedIndex();
                if (index != 0) {
                    index--;
                } else {
                    index = dogList.getModel().getSize() - 1;
                }
                dogList.setSelectedIndex(index);
            }
        });
        navButtonPanel.add(prevButton);

        JButton nextButton = new JButton("Next   >>");
        nextButton.setMnemonic(']');
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int index = dogList.getSelectedIndex();
                if (index != dogList.getModel().getSize() - 1) {
                    index++;
                } else {
                    index = 0;
                }
                dogList.setSelectedIndex(index);
            }
        });
        navButtonPanel.add(nextButton);
        eastPanelBottom.add(navButtonPanel, constraints);

        constraints.gridy++;
        JButton viewButton = new JButton("View Selected");
        viewButton.setMnemonic('v');
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                viewDog();
            }
        });
        eastPanelBottom.add(viewButton, constraints);

        constraints.gridy++;
        JButton viewAllButton = new JButton("View All");
        viewAllButton.setMnemonic('a');
        viewAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                viewAllDogs();
            }
        });
        eastPanelBottom.add(viewAllButton, constraints);

        constraints.gridy++;
        JButton editButton = new JButton("Edit");
        editButton.setMnemonic('e');
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                editDog();
            }
        });
        eastPanelBottom.add(editButton, constraints);

        constraints.gridy++;
        JButton deleteButton = new JButton("Delete");
        deleteButton.setMnemonic('d');
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                deleteDog();
            }
        });
        eastPanelBottom.add(deleteButton, constraints);

        constraints.gridy++;
        JButton helpButton = new JButton("Help");
        helpButton.setMnemonic('h');
        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                help();
            }
        });
        eastPanelBottom.add(helpButton, constraints);

        eastPanel.add(eastPanelBottom);
        add(eastPanel, BorderLayout.EAST);

        // south panel
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1, 2));

        JButton newButton = new JButton("New");
        newButton.setMnemonic('n');
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                clearTextFields();
                setFieldsEditable(true);
                saveButton.setEnabled(true);
                codeField.requestFocus();
            }
        });
        southPanel.add(newButton);

        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.setMnemonic('s');
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    saveDog();
                } catch (Exception e) {
                    handleFatalException(e);
                }
            }
        });
        southPanel.add(saveButton);
        add(southPanel, BorderLayout.SOUTH);

        // center the frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);
        setVisible(true);
    }

    private void refreshDogList() throws Exception {
        List<Dog> dogs = dogDAO.getDogs();

        dogListModel.clear();
        for (Dog dog : dogs) {
            dogListModel.addElement(dog);
        }
    }

    private int findDogbyCode(String code){
		int index = 0;
        List<Dog> dogs = dogDAO.getDogs();

        for (Dog dog : dogs) {
			if (dog.getCode().equals(code))
					   return index;
			index++;
        }
		return -1;
    }

    private void viewDog() {
        Dog dog = getSelectedDog();
        if (dog != null) {
            clearTextFields();
            setFieldsEditable(false);
            populateFieldsFromDog(dog);
            saveButton.setEnabled(false);
        }
    }

    private void viewAllDogs() {
        List<Dog> dogs = dogDAO.getDogs();
        StringBuilder dogsTextBuilder =
            new StringBuilder(dogs.size() * 100);

        for (Dog p : dogs) {
            dogsTextBuilder.append(p.toStringAllFields()).append("\n");
        }

        JTextArea textArea = new JTextArea(20, 25);
        String dogsText = dogsTextBuilder.toString();
        dogsText = dogsText.substring(0, dogsText.length() - 2);
        textArea.setText(dogsText);
        textArea.setCaretPosition(0);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane,
                "All Dogs", JOptionPane.INFORMATION_MESSAGE);
    }

    // NOTE: Editing the dog's code field is not allowed, since the DAO uses
    // that to look them up in its internal data structure.
    private void editDog() {
        Dog dog = getSelectedDog();
        if (dog != null) {
            clearTextFields();
            setFieldsEditable(true);
            codeField.setEditable(false);
            nameField.requestFocus();
            populateFieldsFromDog(dog);
            saveButton.setEnabled(true);
        }
    }

    private void deleteDog() {
        Dog dog = getSelectedDog();
        if (dog != null) {
            // ENTER should always press the focused button
            UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);

            int result = JOptionPane.showOptionDialog(this,
                    "Are you sure you want to delete this dog?\n\n" +
                    dog + "\n\n", "Confirm Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);

            if (result == JOptionPane.YES_OPTION) {
                int index = dogList.getSelectedIndex();
                try {
                    dogDAO.deleteDog(dog);
                    refreshDogList();
                    index--;
                    JOptionPane.showMessageDialog(this,
                            "Dog deleted successfully.\n", "Success",
                            JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    handleFatalException(e);
                }

                if (codeField.getText().equals(dog.getId())) {
                    clearTextFields();
                    setFieldsEditable(false);
                    saveButton.setEnabled(false);
                }
                dogList.setSelectedIndex(index);
            } else {
                return;
            }
        }
    }

    private void help() {
        String helpMessage = "New                     -  alt+n\n"
            + "Save                    -  alt+s\n\n"
            + "Prev                     -  alt+[\n"
            + "Next                     -  alt+]\n\n"
            + "View Selected    -  alt+v\n"
            + "View All               -  alt+a\n"
            + "Edit                      -  alt+e\n"
            + "Delete                 -  alt+d\n"
            + "Help                     -  alt+h\n";

        JOptionPane.showMessageDialog(this, helpMessage,
                "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveDog() throws Exception {
        Dog dog = populateDogFromFields(); // remember the selection
        int index = dogList.getSelectedIndex();
        if (dog != null) {
			int code = Integer.parseInt(dog.getCode());
			/*
			if (dog.getCode().equals("") || dog.getCode().isEmpty())
				code = -1;
			else
				code = Integer.parseInt(dog.getCode());
			*/
			if (code < 0) {
				dogDAO.addDog(dog);
				index++;
			} else {
				dogDAO.updateDog(dog);
			}
			/***
            // if the hidden id is -1, this is a new dog; otherwise the id
            // should be the empty string. id string length is checked first
            // since calling parseInt() on a null string throws an exception.
            if (dog.getId().length() == 0 ||
                    Integer.parseInt(dog.getId()) != -1) {
                dogDAO.updateDog(dog);
            } else {
                dogDAO.addDog(dog);
                index++;
            }
			***/

            JOptionPane.showMessageDialog(this,
                    "Dog saved successfully:\n\n" + dog + "\n\n",
                    "Success", JOptionPane.WARNING_MESSAGE);
        }
        clearTextFields();
        setFieldsEditable(false);
        refreshDogList();
        dogList.setSelectedIndex(index);
        saveButton.setEnabled(false);
    }

    private Dog getSelectedDog() {
        int selectedIndex = dogList.getSelectedIndex();

        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a dog from the list.\n",
                    "Error", JOptionPane.PLAIN_MESSAGE);
            return null;
        }
        return (Dog) dogListModel.getElementAt(selectedIndex);
    }

    private void setFieldsEditable(boolean b) {
        for (JTextField textField : editableTextFields) {
            textField.setEditable(b);
        }
    }

    protected void clearTextFields() {
        for (JTextField textField : allTextFields) {
            textField.setText("");
        }
        codeField.setText("-1");
    }

    private void handleFatalException(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage() + "\n",
                "Fatal Error", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        System.exit(1);
    }

    private Dog populateDogFromFields() {
        try {
            Dog dog = new Dog();

            dog.setCode(codeField.getText());
			/*
			int code = (int)Integer.parseInt(codeField.getText());
			if ( code > 999 || code < 1 )
				throw new Exception ("Invalid Code.");
			String code_str = String.format("%03d", code);
			dog.setCode(code_str);
			*/
			
			String id = idField.getText();
			Pattern regex = Pattern.compile("^[A-Z]{3}$");
			Matcher m = regex.matcher(id);
			if (! m.find())
				throw new Exception ("Invalid ID. Expectiong AAA ~ ZZZ");
            dog.setId(id);

            dog.setName(nameField.getText());
			dog.setBreed(breedField.getText());
            dog.setSize((int)Integer.parseInt(sizeField.getText()));
            dog.setWeight(getDoubleValue(weightField.getText(), "Weight"));

			String sex = sexField.getText();
			regex = Pattern.compile("(^[mMfF]$|^[mM]ale$|^[fF]emale$)");
			m = regex.matcher(sex);
			if (! m.find() )
				throw new Exception ("Invalid gender!. Expectiong M or F");
            dog.setSex(sex.toUpperCase().charAt(0));
            return dog;
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, nfe.getMessage() + "\n", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return null;
        } catch (Exception err) {
            JOptionPane.showMessageDialog(this, err.getMessage() + "\n", "Invalid Input!",
                    JOptionPane.ERROR_MESSAGE);
            return null;
		}
    }

    private void populateFieldsFromDog(Dog dog) {
        codeField.setText(dog.getCode());
        idField.setText(dog.getId());
        nameField.setText(dog.getName());
		breedField.setText(dog.getBreed());
        sizeField.setText(String.valueOf(dog.getSize()));
        weightField.setText(String.valueOf(dog.getWeight()));
        sexField.setText(String.valueOf(dog.getSex()));
    }

    private double getDoubleValue(String input, String fieldName) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(fieldName + " must contain a numeric value!");
        }
    }

    private JTextField addLabelAndTextField(String label, int fieldLength,
            boolean textFieldIsEditable, JPanel panel) {
        panel.add(new JLabel(label));

        JTextField textField = new JTextField(fieldLength);
        textField.setEditable(false);
        panel.add(textField);

        if (textFieldIsEditable) {
            editableTextFields.add(textField);
        }
        allTextFields.add(textField);

        return textField;
    }

    public static void main(String[] args) {
        new DogMaintAppGUI();
    }
}
