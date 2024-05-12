import javax.swing.*;
import java.util.Vector;

public class Category {
    private final Vector<Product> allProducts = new Vector<>();
    private String name;
    private JPanel parentPanel = null;

    Category(String name){
        this.name = name;
    }

    public void addNewProduct(Product newProduct){
        for(Product product : allProducts){
            if(product.getName().equals(newProduct.getName())){
                product.changeQuantityTo(product.getQuantity() + newProduct.getQuantity());
                return;
            }
        }
        allProducts.add(newProduct);
    }

    public String getName(){
        return this.name;
    }

    public void setParentPanel(JPanel panel){
        this.parentPanel = panel;
    }

    public JPanel getParentPanel(){
        return this.parentPanel;
    }

    public int getProductsSize(){
        return this.allProducts.size();
    }

    public Vector<Product> returnAllCategoryProducts(){
        return allProducts;
    }

}
