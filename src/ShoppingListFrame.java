import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;

public class ShoppingListFrame {
    public static final String APP_VERSION = "v0.0.2";
    public static final String DEFAULT_LOADING_FILE = "shopping_list.txt";
    private final Vector<Category> categoriesAndProducts = new Vector<>();





    public void loadShoppingListFromFile(String file){
        try{
            File fileIn = new File(file);
            Scanner fileScanner = new Scanner(fileIn);
            Category newCategory = new Category("dummyCategory");
            while(fileScanner.hasNextLine()){
                String fileLine = fileScanner.nextLine().trim();
                if(fileLine.startsWith(">")){
                    newCategory = new Category(fileLine.substring(1));
                    categoriesAndProducts.add(newCategory);
                }
                else{
                    String[] productParts = fileLine.split(" ");
                    Product newProduct = new Product(
                            productParts[0],
                            Float.parseFloat(productParts[1]),
                            productParts[2]
                    );
                    newCategory.addNewProduct(newProduct);
                }
            }
            fileScanner.close();
        }
        catch (FileNotFoundException e){
            System.out.println("Nie znaleziono pliku do zaladowania listy zakupow.");
        }
    }


    public void generateProductsInCategoryPanel(JPanel categoryPanel, Category category){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        int gx = 1, gy = 1;
        JLabel categoryNameLabel = new JLabel(category.getName() + ":");
        gbc.gridx = gx;     gbc.gridy = gy;
        categoryPanel.add(categoryNameLabel, gbc);

        gx = 2;
        gbc.gridx = gx;
        gy++;
        for(Product product : category.returnAllCategoryProducts()){
            JPanel productPanel = new JPanel(new GridLayout(1, 2));
            JLabel productDataLabel = new JLabel(product.toString());
            JButton deleteProductBtn = new JButton("X");
            // TODO add listener to button
            productPanel.add(productDataLabel);
            productPanel.add(deleteProductBtn);
            deleteProductBtn.setPreferredSize(new Dimension(100, 40));
            gbc.gridy = gy;
            categoryPanel.add(productPanel, gbc);
            gy++;
        }
    }

    public void generateShoppingList(JPanel shoppingListPanel){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        int gy = 1;
        for(Category category : categoriesAndProducts){
            JPanel nextCategoryPanel = new JPanel(new GridBagLayout());
            generateProductsInCategoryPanel(nextCategoryPanel, category);
            gbc.gridy = gy;
            shoppingListPanel.add(nextCategoryPanel, gbc);
            gy++;
        }
    }

    public int getCategoriesSize(){
        return this.categoriesAndProducts.size();
    }

    public void start() {
        JFrame frame = new JFrame("Shopping List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // creating main panel - viewing products         [DONE]
        JPanel viewingProductsMainPanel = new JPanel(); // viewing products on shopping list main panel contains 2 smaller panels
        viewingProductsMainPanel.setLayout(new GridLayout(1,2));

        JPanel shoppingListPanel = new JPanel(new GridBagLayout()); // subpanel of viewingProductsPanel, constains shopping list

        JPanel optionsPanel = new JPanel(); // subpanel of viewingProductsPanel, constains options to manage SL
        optionsPanel.setLayout(new GridLayout(3, 1));

        // creating optionsPanel         [DONE]
        JButton clearShoppingListButton = new JButton("Clear");
        JButton addNewProductButton = new JButton("Add New Product");
        JLabel appVersion = new JLabel(APP_VERSION);

        optionsPanel.add(clearShoppingListButton);
        optionsPanel.add(addNewProductButton);
        optionsPanel.add(appVersion);



        // creating main panel - adding products
        JPanel addingNewProductsMainPanel = new JPanel();
        addingNewProductsMainPanel.setLayout(new GridLayout(3,1));

        JLabel addingNewProductHeader = new JLabel("<html><u>Adding New Product</u></html>");

        JPanel newProductFormPanel = new JPanel();
        newProductFormPanel.setLayout(new FlowLayout());

        JPanel newProductNamePanel = new JPanel();
        newProductNamePanel.setLayout(new GridLayout(2,1));
        JLabel newProductNameLabel = new JLabel("Name:");
        JTextField newProductNameTextField = new JTextField(30);
        newProductNamePanel.add(newProductNameLabel);
        newProductNamePanel.add(newProductNameTextField);

        JPanel newProductCategoryPanel = new JPanel();
        newProductCategoryPanel.setLayout(new GridLayout(2,1));
        JLabel newProductCategoryLabel = new JLabel("Category:");
        String[] tempCategories = {"Kategoria 1", "Kategoria2", "Kategoria3"};
        // TODO   Vector<String> Categories     ktore bedzie aktualizowane przy robieniu komponentu
        JComboBox<String> newProductCategoryComboBox = new JComboBox<>(tempCategories);
        newProductCategoryPanel.add(newProductCategoryLabel);
        newProductCategoryPanel.add(newProductCategoryComboBox);

        JPanel newProductQuantityPanel = new JPanel();
        newProductQuantityPanel.setLayout(new GridBagLayout());
        JLabel newProductQuantityLabel = new JLabel("Quantity:");
        JTextField newProductQuantityTextField = new JTextField(10);
        String[] tempType = {"gram", "ml", "x"};
        JComboBox<String> newProductQuantityTypeComboBox = new JComboBox<>(tempType);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;  gbc.gridy = 1;
        newProductQuantityPanel.add(newProductQuantityLabel, gbc);
        gbc.gridx = 1;  gbc.gridy = 2;
        newProductQuantityPanel.add(newProductQuantityTextField, gbc);
        gbc.gridx = 2;  gbc.gridy = 2;
        newProductQuantityPanel.add(newProductQuantityTypeComboBox, gbc);
        JPanel newProductAddAndCancelButtonsPanel = new JPanel(new FlowLayout());
        JButton newProductAddButton = new JButton("Add");
        JButton newProductCancelButton = new JButton("Cancel");
        newProductAddAndCancelButtonsPanel.add(newProductAddButton);
        newProductAddAndCancelButtonsPanel.add(newProductCancelButton);
        // adding actionListeners to buttons
        newProductAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().remove(addingNewProductsMainPanel);
                frame.getContentPane().add(viewingProductsMainPanel);
                frame.revalidate();
                frame.repaint();
            }
        });
        newProductCancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().remove(addingNewProductsMainPanel);
                frame.getContentPane().add(viewingProductsMainPanel);
                frame.revalidate();
                frame.repaint();
            }
        });


        newProductFormPanel.add(newProductNamePanel);
        newProductFormPanel.add(newProductQuantityPanel);
        newProductFormPanel.add(newProductCategoryPanel);


        addingNewProductsMainPanel.add(addingNewProductHeader);
        addingNewProductsMainPanel.add(newProductFormPanel);
        addingNewProductsMainPanel.add(newProductAddAndCancelButtonsPanel);





        loadShoppingListFromFile(DEFAULT_LOADING_FILE);

        generateShoppingList(shoppingListPanel);






        // adding both subpanels to main viewing products panel          [DONE]
        JScrollPane shoppingListPanelScrollPane = new JScrollPane(shoppingListPanel);
        shoppingListPanelScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        viewingProductsMainPanel.add(shoppingListPanelScrollPane);
        viewingProductsMainPanel.add(optionsPanel);





        // adding actionListeners to buttons (add new product button)
        addNewProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.getContentPane().remove(viewingProductsMainPanel);
                frame.getContentPane().add(addingNewProductsMainPanel);
                frame.revalidate();
                frame.repaint();
            }
        });


        // starting frame        [DONE]
        frame.getContentPane().add(viewingProductsMainPanel);
        frame.setMinimumSize(new Dimension(500,500));
        frame.setMaximumSize(new Dimension(1500,1500));
        frame.pack();
        frame.setVisible(true);
    }
}
