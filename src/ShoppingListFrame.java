import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;
import java.io.File;

public class ShoppingListFrame {
    public static final String APP_VERSION = "v0.0.4";
    public static final String DEFAULT_LOADING_FILE = "shopping_list.txt";
    public static final String ADDING_NEW_PRODUCT_VIEW_NAME = "adding_new_product_view";
    public static final String VIEWING_PRODUCTS_VIEW_NAME = "viewing_products_view";
    public static final String ADDING_NEW_CATEGORY_VIEW_NAME = "adding_new_category_view";
    public static final String ADDING_NEW_CATEGORY_BUTTON_VIEW_NAME = "adding_new_category_button_view";
    private static final String[] ACCEPTABLE_QUANTITY_TYPES = {"x", "g", "dag", "kg", "mm", "cm", "m", "ml", "l"};

    private final Vector<Category> categoriesAndProducts = new Vector<>();
    private final Vector<String> categoriesNames = new Vector<>();
    private JPanel shoppingListPanel;
    private JPanel newProductFormPanel;





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

    //todo dodac zapis do listy do pliku

    public void clearShoppingList(){
        Component[] components = shoppingListPanel.getComponents();
        for(Component component : components){
            shoppingListPanel.remove(component);
        }
        shoppingListPanel.revalidate();
        shoppingListPanel.repaint();
        categoriesAndProducts.clear();
        categoriesNames.clear();
        updateCategoryComboBox();
    }

    public void deleteCategory(JPanel categoryPanel, Category category){
        categoriesNames.remove(category.getName());
        JPanel parent = (JPanel) categoryPanel.getParent();
        parent.remove(categoryPanel);
        parent.revalidate();
        parent.repaint();
        updateCategoryComboBox();
    }

    public void generateProductPanel(Product product, JPanel categoryPanel, Category category, GridBagConstraints gbc){
        JPanel productPanel = new JPanel(new GridLayout(1, 2));
        JLabel productDataLabel = new JLabel(product.toString());
        JButton deleteProductBtn = new JButton("X");
        deleteProductBtn.addActionListener(e -> {
            category.removeProduct(product);
            categoryPanel.removeAll();
            generateProductsInCategoryPanel(categoryPanel, category);
            categoryPanel.revalidate();
            categoryPanel.repaint();
            if (category.returnAllCategoryProducts().isEmpty()) {
                categoriesAndProducts.remove(category);
                deleteCategory(categoryPanel, category);
            }
        });
        productPanel.add(productDataLabel);
        productPanel.add(deleteProductBtn);
        deleteProductBtn.setPreferredSize(new Dimension(100, 40));
        categoryPanel.add(productPanel, gbc);
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
            gbc.gridy = gy;
            generateProductPanel(product, categoryPanel, category, gbc);
            gy++;
        }
    }


    public void generateShoppingList(){
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

    public void updateProductList(String categoryName, Product newProduct){
        Component[] categoriesPanels = shoppingListPanel.getComponents();
        for(Component component : categoriesPanels){
            JPanel categoryPanel = (JPanel) component;
            String categoryPanelName = ((JLabel)categoryPanel.getComponents()[0]).getText();
            if(categoryPanelName.substring(0,categoryPanelName.length()-1).equals(categoryName)){
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = 2;
                if(categoryPanel.getComponentCount() <= 1){
                    gbc.gridy = 2;
                }
                else{
                    JPanel lastProductPanel = (JPanel) categoryPanel.getComponent(categoryPanel.getComponentCount()-1);
                    GridBagConstraints lastProductGBC = ((GridBagLayout) categoryPanel.getLayout()).getConstraints(lastProductPanel);
                    gbc.gridy = lastProductGBC.gridy + 1;
                }
                Category newProductCategory = categoriesAndProducts.getFirst();
                for(Category category : categoriesAndProducts){
                    if(category.getName().equals(categoryName)){
                        newProductCategory = category;
                        break;
                    }
                }
                if(newProductCategory.contains(newProduct)){
                    categoryPanel.removeAll();
                    generateProductsInCategoryPanel(categoryPanel, newProductCategory);
                }
                else{
                    generateProductPanel(newProduct, categoryPanel, newProductCategory, gbc);
                }
                categoryPanel.revalidate();
                categoryPanel.repaint();
                break;
            }
        }
    }

    public void addNewProductToCategory(String categoryName, Product newProduct){
        for(Category category : categoriesAndProducts){
            if(category.getName().equals(categoryName)){
                category.addNewProduct(newProduct);
            }
        }
        updateProductList(categoryName, newProduct);
    }

    public JComboBox<?> getCategoryComboBox(){
        JPanel categoriesPanel = (JPanel) newProductFormPanel.getComponent(2); //index of choosing category
        JComboBox<?> categoriesComboBox = null;
        for(Component component : categoriesPanel.getComponents()){
            if(component instanceof JComboBox<?>){
                categoriesComboBox = (JComboBox<?>) component;
            }
        }
        return categoriesComboBox;
    }

    public void updateCategoryComboBox(){
        JComboBox<?> categoriesComboBox = getCategoryComboBox();
        if(categoriesComboBox == null) return;
        categoriesComboBox.setSelectedItem(null);
        categoriesComboBox.getParent().revalidate();
        categoriesComboBox.getParent().repaint();
    }

    public void showInputTextForNewCategoryView(JPanel addingNewCategoryPanel){
        CardLayout layout = (CardLayout) addingNewCategoryPanel.getLayout();
        layout.show(addingNewCategoryPanel, ADDING_NEW_CATEGORY_VIEW_NAME);
    }

    public void showAddingNewCategoryButtonView(JPanel addingNewCategoryPanel){
        CardLayout layout = (CardLayout) addingNewCategoryPanel.getLayout();
        layout.show(addingNewCategoryPanel, ADDING_NEW_CATEGORY_BUTTON_VIEW_NAME);
    }

    public JPanel generateCategoryPanel(Category category){
        JPanel newCategoryPanel = new JPanel(new GridBagLayout());
        JLabel categoryNameLabel = new JLabel(category.getName() + ":");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;      gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        newCategoryPanel.add(categoryNameLabel, gbc);
        return newCategoryPanel;
    }

    public void addNewCategoryToShoppingListPanel(Category newCategory){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 1;
        JPanel lastCategoryPanel = (JPanel) shoppingListPanel.getComponent(shoppingListPanel.getComponentCount()-1);
        GridBagConstraints lastCategoryGBC = ((GridBagLayout)shoppingListPanel.getLayout()).getConstraints(lastCategoryPanel);
        gbc.gridy = lastCategoryGBC.gridy + 1;
        JPanel newCategoryPanel = generateCategoryPanel(newCategory);
        shoppingListPanel.add(newCategoryPanel, gbc);
        shoppingListPanel.revalidate();
        shoppingListPanel.repaint();
    }

    public JTextField getNewCategoryTextField(){
        JPanel addingNewCategoriesPanel = (JPanel) newProductFormPanel.getComponent(3); //index of adding new category
        JPanel newCategoryInputPanel = null;
        for(Component component : addingNewCategoriesPanel.getComponents()){
            if(component instanceof JPanel){
                newCategoryInputPanel = (JPanel) component;
                break;
            }
        }
        if(newCategoryInputPanel == null) return null;
        JTextField textField = null;
        for(Component component : newCategoryInputPanel.getComponents()){
            if(component instanceof JTextField){
                textField = (JTextField) component;
                break;
            }
        }
        return textField;
    }

    public void addNewCategory(){
        JTextField newCategoryTextField = getNewCategoryTextField();
        if(newCategoryTextField == null) return;
        Category newCategory = new Category(newCategoryTextField.getText());
        categoriesAndProducts.add(newCategory);
        categoriesNames.add(newCategory.getName());
        updateCategoryComboBox();
        addNewCategoryToShoppingListPanel(newCategory);
    }

    public JPanel generateAddingNewCategoryPanel(){
        JPanel addingNewCategoryPanel = new JPanel(new CardLayout());

        JButton addNewCategoryButton = new JButton("Add new category");
        addNewCategoryButton.addActionListener(e -> {
            showInputTextForNewCategoryView(addingNewCategoryPanel);
        });

        JPanel addingNewCategoryInputTextPanel = new JPanel(new GridLayout(1,2));
        JTextField newCategoryInput = new JTextField(15);
        JButton newCategoryAddButton = new JButton("ACCEPT");
        newCategoryAddButton.addActionListener(e -> {
            addNewCategory();
            showAddingNewCategoryButtonView(addingNewCategoryPanel);
        });
        addingNewCategoryInputTextPanel.add(newCategoryInput);
        addingNewCategoryInputTextPanel.add(newCategoryAddButton);

        addingNewCategoryPanel.add(addNewCategoryButton, ADDING_NEW_CATEGORY_BUTTON_VIEW_NAME);
        addingNewCategoryPanel.add(addingNewCategoryInputTextPanel, ADDING_NEW_CATEGORY_VIEW_NAME);
        showAddingNewCategoryButtonView(addingNewCategoryPanel);

        return addingNewCategoryPanel;
    }

    public JPanel generateAddingNewProductPanel(JPanel mainPanel){
        JPanel addingNewProductPanel = new JPanel();
        addingNewProductPanel.setLayout(new GridLayout(3,1));

        JLabel addingNewProductHeader = new JLabel("<html><u>Adding New Product</u></html>");

        newProductFormPanel = new JPanel();
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
        for(Category category : categoriesAndProducts){
            categoriesNames.add(category.getName());
        }
        JComboBox<String> newProductCategoryComboBox = new JComboBox<>(categoriesNames);
        newProductCategoryPanel.add(newProductCategoryLabel);
        newProductCategoryPanel.add(newProductCategoryComboBox);

        JPanel newProductQuantityPanel = new JPanel();
        newProductQuantityPanel.setLayout(new GridBagLayout());
        JLabel newProductQuantityLabel = new JLabel("Quantity:");
        JTextField newProductQuantityTextField = new JTextField(10);
        ((AbstractDocument) newProductQuantityTextField.getDocument()).setDocumentFilter(new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.isEmpty()
                        || isDigit(string)
                        || string.equals(".")
                        && !newProductQuantityTextField.getText().contains(".")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.isEmpty() || isDigit(text) || text.equals(".")) {
                    if ((text.equals(".")) && !newProductQuantityTextField.getText().contains(".")) {
                        super.replace(fb, offset, length, text, attrs);
                    } else if (!text.equals(".")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            }
            private boolean isDigit(String text) {
                return text != null && text.matches("\\d");
            }
        });
        JComboBox<String> newProductQuantityTypeComboBox = new JComboBox<>(ACCEPTABLE_QUANTITY_TYPES);
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
        newProductAddButton.addActionListener(e -> {
            String productName = newProductNameTextField.getText();
            Float productQuantity = Float.parseFloat(newProductQuantityTextField.getText());
            String productQuantityType = (String) newProductQuantityTypeComboBox.getSelectedItem();
            String productCategory = (String) newProductCategoryComboBox.getSelectedItem();
            Product newProduct = new Product(productName, productQuantity, productQuantityType);
            addNewProductToCategory(productCategory, newProduct);
            newProductNameTextField.setText("");
            newProductQuantityTextField.setText("");
            showViewingProductsView(mainPanel);
        });
        newProductCancelButton.addActionListener(e -> {
            showViewingProductsView(mainPanel);
        });

        JPanel addingNewCategoryPanel = generateAddingNewCategoryPanel();

        newProductFormPanel.add(newProductNamePanel);
        newProductFormPanel.add(newProductQuantityPanel);
        newProductFormPanel.add(newProductCategoryPanel);
        newProductFormPanel.add(addingNewCategoryPanel);


        addingNewProductPanel.add(addingNewProductHeader);
        addingNewProductPanel.add(newProductFormPanel);
        addingNewProductPanel.add(newProductAddAndCancelButtonsPanel);

        return addingNewProductPanel;
    }


    public JPanel generateViewingProductsPanel(JPanel mainPanel){
        // creating main panel - viewing products         [DONE]
        JPanel viewingProductsMainPanel = new JPanel(); // viewing products on shopping list main panel contains 2 smaller panels
        viewingProductsMainPanel.setLayout(new GridLayout(1,2));

        shoppingListPanel = new JPanel(new GridBagLayout()); // subpanel of viewingProductsPanel, constains shopping list

        JPanel optionsPanel = new JPanel(); // subpanel of viewingProductsPanel, constains options to manage SL
        optionsPanel.setLayout(new GridLayout(3, 1));

        // creating optionsPanel         [DONE]
        JButton clearShoppingListButton = new JButton("Clear Shopping List");
        JButton addNewProductButton = new JButton("Add New Product");
        JLabel appVersion = new JLabel(APP_VERSION);

        optionsPanel.add(clearShoppingListButton);
        optionsPanel.add(addNewProductButton);
        optionsPanel.add(appVersion);


        // creating main panel - adding products



        generateShoppingList();

        //creating scrollPanel which contains product list
        JScrollPane shoppingListScrollPane = new JScrollPane(shoppingListPanel);
        shoppingListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        viewingProductsMainPanel.add(shoppingListScrollPane);
        viewingProductsMainPanel.add(optionsPanel);

        // adding actionListeners to buttons
        clearShoppingListButton.addActionListener(e -> {
            clearShoppingList();
        });
        addNewProductButton.addActionListener(e -> {
            showAddingNewProductView(mainPanel);
        });


        return viewingProductsMainPanel;
    }

    public JPanel generateMainPanel(){
        JPanel mainPanel = new JPanel(new CardLayout());
        JPanel addingNewProductPanel = generateAddingNewProductPanel(mainPanel);
        JPanel viewingProductsPanel = generateViewingProductsPanel(mainPanel);
        mainPanel.add(addingNewProductPanel, ADDING_NEW_PRODUCT_VIEW_NAME);
        mainPanel.add(viewingProductsPanel, VIEWING_PRODUCTS_VIEW_NAME);
        return mainPanel;
    }

    public void showViewingProductsView(JPanel mainPanel){
        CardLayout mainPanelCardLayout = (CardLayout) mainPanel.getLayout();
        mainPanelCardLayout.show(mainPanel, VIEWING_PRODUCTS_VIEW_NAME);
    }

    public void showAddingNewProductView(JPanel mainPanel){
        CardLayout mainPanelCardLayout = (CardLayout) mainPanel.getLayout();
        mainPanelCardLayout.show(mainPanel, ADDING_NEW_PRODUCT_VIEW_NAME);
    }

    public void start() {
        JFrame frame = new JFrame("Shopping List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.out.println("Loading list from file...");
        loadShoppingListFromFile(DEFAULT_LOADING_FILE);
        System.out.println("Finished loading list from file.");

        JPanel mainPanel = generateMainPanel();
        showViewingProductsView(mainPanel);

        frame.getContentPane().add(mainPanel);
        frame.setMinimumSize(new Dimension(500,500));
        frame.setMaximumSize(new Dimension(1500,1500));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
