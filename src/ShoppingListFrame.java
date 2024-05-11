import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class ShoppingListFrame {
    public static final String APP_VERSION = "v0.0.1";

    public void start() {
        JFrame frame = new JFrame("Shopping List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // creating main panel - viewing products         [DONE]
        JPanel viewingProductsMainPanel = new JPanel(); // viewing products on shopping list main panel contains 2 smaller panels
        viewingProductsMainPanel.setLayout(new GridLayout(1,2));

        JPanel shoppingListPanel = new JPanel(); // subpanel of viewingProductsPanel, constains shopping list
        shoppingListPanel.setLayout(new GridLayout(3,1));
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





        // example of adding category and product
        JLabel category1 = new JLabel("Kategoria1:");
        JPanel cat1prod1 = new JPanel();
        cat1prod1.setLayout(new GridLayout(1, 2));
        JLabel product1 = new JLabel("Product 1\t2x");
        JButton prod1delete = new JButton("X");
        cat1prod1.add(product1);
        cat1prod1.add(prod1delete);
        JPanel cat1prod2 = new JPanel();
        cat1prod2.setLayout(new GridLayout(1, 2));
        JLabel product2 = new JLabel("Product 2\t500ml");
        JButton prod2delete = new JButton("X");
        cat1prod2.add(product2);
        cat1prod2.add(prod2delete);


        // example of adding 1 category to shoppingListPanel
        shoppingListPanel.add(category1);
        shoppingListPanel.add(cat1prod1);
        shoppingListPanel.add(cat1prod2);






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
        frame.pack();
        frame.setVisible(true);
    }
}
