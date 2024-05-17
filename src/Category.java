import javax.swing.*;
import java.util.Vector;

public class Category {
    private final Vector<Product> allProducts = new Vector<>();
    private final String name;
    private JPanel parentPanel = null;

    Category(String name){
        this.name = name;
    }

    public void addNewProduct(Product newProduct){
        for(Product product : allProducts){
            if(product.getName().equals(newProduct.getName())){
                product.changeQuantityTo(product.getQuantity() + newProduct.getQuantity());
                //todo add second function product.checkQuantityType() ktora odpala product.changeQuantityType()
                return;
            }
        }
        allProducts.add(newProduct);
    }

    public void removeProduct(Product productToDelete){
        for(Product product : allProducts){
            if(product.getName().equals(productToDelete.getName())){
                allProducts.remove(product);
                break;
            }
        }
    }

    public boolean contains(Product product){
        for(Product prod : allProducts){
            if(prod.getName().equals(product.getName())){
                return true;
            }
        }
        return false;
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
