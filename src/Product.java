public class Product {
    private final String name;
    private float quantity;
    private final String quantityType;

    Product(String name, float quantity, String quantityType){
        this.name = name;
        this.quantity = quantity;
        this.quantityType = quantityType;
    }

    public void changeQuantityTo(float newQuantity){
        this.quantity = newQuantity;
    }

    public String getName(){
        return this.name;
    }

    public float getQuantity(){
        return this.quantity;
    }

    public String getQuantityType(){
        return this.quantityType;
    }

    @Override
    public String toString(){
        return getName() + " " + getQuantity() + getQuantityType();
    }
}
