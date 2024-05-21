import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.Scanner;
import java.util.Vector;

public class ShoppingListFrame {
    public static final String DEFAULT_LOADING_FILE = "shopping_list.txt";
    public static final String ADDING_NEW_PRODUCT_VIEW_NAME = "adding_new_product_view";
    public static final String VIEWING_PRODUCTS_VIEW_NAME = "viewing_products_view";
    public static final String ADDING_NEW_CATEGORY_VIEW_NAME = "adding_new_category_view";
    public static final String ADDING_NEW_CATEGORY_BUTTON_VIEW_NAME = "adding_new_category_button_view";
    public static final String CLEARING_LIST_CONFIRM_VIEW_NAME = "clearing_list_confirm_view";
    public static final String CLEARING_LIST_BUTTON_VIEW_NAME = "clearing_list_button_view";
    private static final String[] ACCEPTABLE_QUANTITY_TYPES = {"x", "g", "dag", "kg", "mm", "cm", "m", "ml", "l"};

    private final Vector<Category> categoriesAndProducts = new Vector<>();
    private final Vector<String> categoriesNames = new Vector<>();
    private JPanel clearingListPanel;
    private JPanel shoppingListPanel;
    private JPanel newProductFormPanel;
    private JTextField newProductNameTextField;
    private JTextField newProductQuantityTextField;
    private JComboBox<String> newProductQuantityTypeComboBox;
    private JComboBox<String> newProductCategoryComboBox;





    public void loadShoppingListFromFile(String file){
        try{
            File fileIn = new File(file);
            Scanner fileScanner = new Scanner(fileIn);
            Category newCategory = new Category("foo");
            while(fileScanner.hasNextLine()){
                String fileLine = fileScanner.nextLine().trim();
                if(fileLine.startsWith(">")){
                    newCategory = new Category(fileLine.substring(1));
                    categoriesAndProducts.add(newCategory);
                }
                else{
                    String[] productParts = fileLine.split("\\|");
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
            System.out.println("File to load shopping list wasn't found, it will be created when exiting app.");
        }
    }

    public void saveShoppingListToFile(String file) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (Category category : categoriesAndProducts) {
                writer.println(">" + category.getName());
                for (Product product : category.returnAllCategoryProducts()) {
                    writer.println(product.getName() + "|" + product.getQuantity() + "|" + product.getQuantityType());
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving the list - failed to create file.");
        }
    }

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
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.X_AXIS));
        JLabel productDataLabel = new JLabel(product.toString());
        productDataLabel.setFont(new Font("Serif", Font.PLAIN, 20));
        productDataLabel.setBorder(new EmptyBorder(0, 0, 5, 10));
        JButton deleteProductBtn = new JButton("X");
        deleteProductBtn.setFont(new Font("Serif", Font.BOLD, 14));
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
        deleteProductBtn.setPreferredSize(new Dimension(45, 45));
        categoryPanel.add(productPanel, gbc);
    }

    public void generateProductsInCategoryPanel(JPanel categoryPanel, Category category){
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        int gx = 1, gy = 1;
        JLabel categoryNameLabel = new JLabel(category.getName() + ":");
        categoryNameLabel.setFont(new Font("Serif", Font.BOLD, 20));
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

    public void updateCategoryComboBox(){
        JComboBox<?> categoriesComboBox = newProductCategoryComboBox;
        setNewProductCategoryComboBoxBorderBlack();
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
        if(shoppingListPanel.getComponentCount() > 0){
            JPanel lastCategoryPanel = (JPanel) shoppingListPanel.getComponent(shoppingListPanel.getComponentCount()-1);
            GridBagConstraints lastCategoryGBC = ((GridBagLayout)shoppingListPanel.getLayout()).getConstraints(lastCategoryPanel);
            gbc.gridy = lastCategoryGBC.gridy + 1;
        }
        else{
            gbc.gridy = 1;
        }
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
        if(newCategoryTextField.getText().isEmpty()){
            return;
        }
        Category newCategory = new Category(newCategoryTextField.getText());
        categoriesAndProducts.add(newCategory);
        categoriesNames.add(newCategory.getName());
        newCategoryTextField.setText("");
        updateCategoryComboBox();
        addNewCategoryToShoppingListPanel(newCategory);
    }

    public JPanel generateAddingNewCategoryInputTextPanel(JPanel addingNewCategoryPanel){
        JPanel addingNewCategoryInputTextPanel = new JPanel(new GridLayout(2,2));
        JLabel newCategoryLabel = new JLabel("New category name:");
        JTextField newCategoryInput = new JTextField(15);
        newCategoryInput.setFont(new Font("Serif", Font.PLAIN, 16));
        JButton newCategoryAddButton = new JButton("ACCEPT");
        newCategoryAddButton.addActionListener(e -> {
            addNewCategory();
            showAddingNewCategoryButtonView(addingNewCategoryPanel);
        });
        JButton newCategoryCancelButton = new JButton("CANCEL");
        newCategoryCancelButton.addActionListener(e -> {
            newCategoryInput.setText("");
            showAddingNewCategoryButtonView(addingNewCategoryPanel);
        });
        addingNewCategoryInputTextPanel.add(newCategoryLabel);
        addingNewCategoryInputTextPanel.add(newCategoryAddButton);
        addingNewCategoryInputTextPanel.add(newCategoryInput);
        addingNewCategoryInputTextPanel.add(newCategoryCancelButton);

        return addingNewCategoryInputTextPanel;
    }

    public JPanel generateAddingNewCategoryPanel(){
        JPanel addingNewCategoryPanel = new JPanel(new CardLayout());

        JButton addNewCategoryButton = new JButton("Add new category");
        addNewCategoryButton.setFont(new Font("Serif", Font.BOLD, 16));
        addNewCategoryButton.addActionListener(e -> {
            showInputTextForNewCategoryView(addingNewCategoryPanel);
        });

        addingNewCategoryPanel.add(addNewCategoryButton, ADDING_NEW_CATEGORY_BUTTON_VIEW_NAME);
        addingNewCategoryPanel.add(generateAddingNewCategoryInputTextPanel(addingNewCategoryPanel), ADDING_NEW_CATEGORY_VIEW_NAME);
        showAddingNewCategoryButtonView(addingNewCategoryPanel);

        return addingNewCategoryPanel;
    }

    public JLabel generateAddingNewProductHeader(){
        JLabel addingNewProductHeader = new JLabel("<html><u>Adding New Product</u></html>");
        addingNewProductHeader.setFont(new Font("Serif", Font.ITALIC, 36));
        addingNewProductHeader.setBorder(new EmptyBorder(0, 0, 30, 0));
        return addingNewProductHeader;
    }

    public JPanel generateNewProductNamePanel(){
        JPanel newProductNamePanel = new JPanel();
        newProductNamePanel.setLayout(new GridLayout(2,1));
        JLabel newProductNameLabel = new JLabel("Name:");
        newProductNameLabel.setFont(new Font("Serif", Font.BOLD, 16));
        newProductNameTextField = new JTextField(20);
        newProductNameTextField.setFont(new Font("Serif", Font.PLAIN, 16));
        setNewProductNameInputBorderBlack();
        newProductNamePanel.add(newProductNameLabel);
        newProductNamePanel.add(newProductNameTextField);
        return newProductNamePanel;
    }

    public JPanel generateNewProductCategoryPanel(){
        JPanel newProductCategoryPanel = new JPanel();
        newProductCategoryPanel.setLayout(new GridLayout(2,1));
        JLabel newProductCategoryLabel = new JLabel("Category:");
        newProductCategoryLabel.setFont(new Font("Serif", Font.BOLD, 16));
        for(Category category : categoriesAndProducts){
            categoriesNames.add(category.getName());
        }
        newProductCategoryComboBox = new JComboBox<>(categoriesNames);
        newProductCategoryComboBox.setFont(new Font("Serif", Font.PLAIN, 16));
        setNewProductCategoryComboBoxBorderBlack();
        newProductCategoryPanel.add(newProductCategoryLabel);
        newProductCategoryPanel.add(newProductCategoryComboBox);
        return newProductCategoryPanel;
    }

    public JPanel generateNewProductQuantityPanel(){
        JPanel newProductQuantityPanel = new JPanel();
        newProductQuantityPanel.setLayout(new GridBagLayout());
        JLabel newProductQuantityLabel = new JLabel("Quantity:");
        newProductQuantityLabel.setFont(new Font("Serif", Font.BOLD, 16));
        newProductQuantityTextField = new JTextField(10);
        newProductQuantityTextField.setFont(new Font("Serif", Font.PLAIN, 16));
        setNewProductQuantityInputBorderBlack();
        ((AbstractDocument) newProductQuantityTextField.getDocument()).setDocumentFilter(new QuantityFilter(newProductQuantityTextField));
        newProductQuantityTypeComboBox = new JComboBox<>(ACCEPTABLE_QUANTITY_TYPES);
        newProductQuantityTypeComboBox.setFont(new Font("Serif", Font.PLAIN, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;  gbc.gridy = 1;
        newProductQuantityPanel.add(newProductQuantityLabel, gbc);
        gbc.gridx = 1;  gbc.gridy = 2;
        newProductQuantityPanel.add(newProductQuantityTextField, gbc);
        gbc.gridx = 2;  gbc.gridy = 2;
        newProductQuantityPanel.add(newProductQuantityTypeComboBox, gbc);
        return newProductQuantityPanel;
    }

    private void setNewProductNameInputBorderBlack(){
        newProductNameTextField.setBorder(new LineBorder(Color.BLACK, 1));
    }

    private void setNewProductNameInputBorderRed(){
        newProductNameTextField.setBorder(new LineBorder(Color.RED, 2));
    }

    private void setNewProductQuantityInputBorderBlack(){
        newProductQuantityTextField.setBorder(new LineBorder(Color.BLACK, 1));
    }

    private void setNewProductQuantityInputBorderRed(){
        newProductQuantityTextField.setBorder(new LineBorder(Color.RED, 2));
    }

    private void setNewProductCategoryComboBoxBorderBlack(){
        newProductCategoryComboBox.setBorder(new LineBorder(Color.BLACK, 1));
    }

    private void setNewProductCategoryComboBoxBorderRed(){
        newProductCategoryComboBox.setBorder(new LineBorder(Color.RED, 2));
    }

    public boolean isNewProductDataValid(String productName,
                                         String productQuantity,
                                         Object category){
        boolean isValid = true;
        if(productName.isEmpty()){
            setNewProductNameInputBorderRed();
            isValid = false;
        }
        else{
            setNewProductNameInputBorderBlack();
        }
        if(productQuantity.isEmpty()){
            setNewProductQuantityInputBorderRed();
            isValid = false;
        }
        else{
            setNewProductQuantityInputBorderBlack();
        }
        if(category == null){
            setNewProductCategoryComboBoxBorderRed();
            isValid = false;
        }
        else{
            setNewProductCategoryComboBoxBorderBlack();
        }
        return isValid;
    }

    public boolean addNewProductToCategoryHandler(){
        if(!isNewProductDataValid(newProductNameTextField.getText(),
                                  newProductQuantityTextField.getText(),
                                  newProductCategoryComboBox.getSelectedItem())){
            return false;
        }
        String productName = newProductNameTextField.getText();
        float productQuantity = Float.parseFloat(newProductQuantityTextField.getText());
        String productQuantityType = (String) newProductQuantityTypeComboBox.getSelectedItem();
        String productCategory = (String) newProductCategoryComboBox.getSelectedItem();
        Product newProduct = new Product(productName, productQuantity, productQuantityType);
        addNewProductToCategory(productCategory, newProduct);

        newProductNameTextField.setText("");
        newProductQuantityTextField.setText("");
        setNewProductNameInputBorderBlack();
        setNewProductQuantityInputBorderBlack();

        return true;
    }

    public JPanel generateNewProductFormButtonsPanel(JPanel mainPanel){
        JPanel newProductAddAndCancelButtonsPanel = new JPanel(new FlowLayout());
        JButton newProductAddButton = new JButton("Add");
        newProductAddButton.setPreferredSize(new Dimension(100, 40));
        newProductAddButton.setFont(new Font("Serif", Font.BOLD, 16));
        JButton newProductCancelButton = new JButton("Cancel");
        newProductCancelButton.setPreferredSize(new Dimension(100, 40));
        newProductCancelButton.setFont(new Font("Serif", Font.BOLD, 16));
        newProductAddAndCancelButtonsPanel.add(newProductAddButton);
        newProductAddAndCancelButtonsPanel.add(newProductCancelButton);

        newProductAddButton.addActionListener(e -> {
            if(!addNewProductToCategoryHandler()){
                return;
            }
            showViewingProductsView(mainPanel);
        });
        newProductCancelButton.addActionListener(e -> {
            showViewingProductsView(mainPanel);
        });

        return newProductAddAndCancelButtonsPanel;
    }

    public JPanel generateNewProductFormPanel(){
        newProductFormPanel = new JPanel(new GridLayout(4,1));
        newProductFormPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        newProductFormPanel.add(generateNewProductNamePanel());
        newProductFormPanel.add(generateNewProductCategoryPanel());
        newProductFormPanel.add(generateNewProductQuantityPanel());
        newProductFormPanel.add(generateAddingNewCategoryPanel());

        return newProductFormPanel;
    }

    public JPanel generateAddingNewProductPanel(JPanel mainPanel){
        JPanel addingNewProductPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 1;      gbc.gridy = 1;
        addingNewProductPanel.add(generateAddingNewProductHeader(), gbc);
        gbc.gridy = 2;
        addingNewProductPanel.add(generateNewProductFormPanel(), gbc);
        gbc.gridy = 3;
        addingNewProductPanel.add(generateNewProductFormButtonsPanel(mainPanel), gbc);

        return addingNewProductPanel;
    }

    public JPanel generateShoppingListPanel(){
        shoppingListPanel = new JPanel(new GridBagLayout());
        generateShoppingList();
        return shoppingListPanel;
    }

    public JPanel generateConfirmationClearingListPanel(){
        JPanel confirmationClearingListPanel = new JPanel(new GridLayout(1,2));

        JButton confirmClearButton = new JButton("CONFIRM");
        confirmClearButton.setFont(new Font("Serif", Font.BOLD, 20));
        confirmClearButton.setForeground(Color.WHITE);
        confirmClearButton.setBorder(new LineBorder(new Color(0, 200, 0), 3));
        confirmClearButton.setBackground(new Color(0, 150, 0));
        confirmClearButton.addActionListener(e -> {
            clearShoppingList();
            showClearShoppingListButtonView();
        });
        JButton cancelClearButton = new JButton("CANCEL");
        cancelClearButton.setFont(new Font("Serif", Font.BOLD, 20));
        cancelClearButton.setBorder(new LineBorder(new Color(200, 0, 0), 3));
        cancelClearButton.setForeground(Color.WHITE);
        cancelClearButton.setBackground(new Color(150, 0, 0));
        cancelClearButton.addActionListener(e -> {
            showClearShoppingListButtonView();
        });

        confirmationClearingListPanel.add(cancelClearButton);
        confirmationClearingListPanel.add(confirmClearButton);

        return confirmationClearingListPanel;
    }

    public void showClearShoppingListButtonView(){
        CardLayout layout = (CardLayout) clearingListPanel.getLayout();
        layout.show(clearingListPanel, CLEARING_LIST_BUTTON_VIEW_NAME);
    }

    public void showConfirmationClearingListPanel(){
        CardLayout layout = (CardLayout) clearingListPanel.getLayout();
        layout.show(clearingListPanel, CLEARING_LIST_CONFIRM_VIEW_NAME);
    }

    public JPanel generateClearingListPanel(){
        clearingListPanel = new JPanel(new CardLayout());

        JButton clearShoppingListButton = new JButton("Clear Shopping List");
        clearShoppingListButton.setFont(new Font("Serif", Font.BOLD, 20));
        clearShoppingListButton.addActionListener(e -> {
            showConfirmationClearingListPanel();
        });

        clearingListPanel.add(generateConfirmationClearingListPanel(), CLEARING_LIST_CONFIRM_VIEW_NAME);
        clearingListPanel.add(clearShoppingListButton, CLEARING_LIST_BUTTON_VIEW_NAME);
        showClearShoppingListButtonView();

        return clearingListPanel;
    }

    public JPanel generateOptionsPanel(JPanel mainPanel){
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new GridLayout(3, 1));

        JButton addNewProductButton = new JButton("Add New Product");
        addNewProductButton.setFont(new Font("Serif", Font.BOLD, 20));
        addNewProductButton.addActionListener(e -> {
            showAddingNewProductView(mainPanel);
        });

        optionsPanel.add(generateClearingListPanel());
        optionsPanel.add(addNewProductButton);

        return optionsPanel;
    }

    public JPanel generateViewingProductsPanel(JPanel mainPanel){
        JPanel viewingProductsPanel = new JPanel();
        viewingProductsPanel.setLayout(new GridLayout(1,2));

        JScrollPane shoppingListScrollPane = new JScrollPane(generateShoppingListPanel()); //scroll for shopping list
        shoppingListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        shoppingListScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        viewingProductsPanel.add(shoppingListScrollPane);
        viewingProductsPanel.add(generateOptionsPanel(mainPanel));

        return viewingProductsPanel;
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

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveShoppingListToFile(DEFAULT_LOADING_FILE);
                System.exit(0);
            }
        });
        frame.getContentPane().add(mainPanel);
        frame.setMinimumSize(new Dimension(500,600));
        frame.setMaximumSize(new Dimension(1500,1500));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
