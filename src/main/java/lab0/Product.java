package lab0;

/**
 * The Product class represents a product in a store.
 * It contains information such as the product ID, name, description, price, quantity, and measure.
 *
 * The class provides methods for accessing and modifying the product's attributes,
 *      methods for reading and writing json files with product descriptions.
 */


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Product {

    /**
      * The unique identifier of the product.
    */
    private int id;

    /**
     * The name of the product.
     */
    private String name;

    /**
     * The description of the product.
     */
    private String description;

    /**
     * The price of the product.
     */
    private float price;

    /**
     * The quantity of the product available in stock.
     */
    private int amount;

    /**
     * The measure of the product
     */
    private Measure measure;

    /**
     * Constructs an empty Product object.
     */
    public Product(){}

    /**
     * Constructs a Product object with the specified attributes.
     * @param id the unique identifier of the product
     * @param name the name of the product
     * @param description the description of the product
     * @param price the price of the product
     * @param amount the quantity of the product available in stock
     * @param measure the measure of the product
     */
    public Product(int id, String name, String description, float price, int amount, Measure measure){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.amount = amount;
        this.measure = measure;
    }

    /**
     * Constructs a Product object by copying another Product object.
     * @param product the Product object to be copied
     */
    private Product(Product product){
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.amount = product.getAmount();
        this.measure = product.getMeasure();
    }


    /**
     * Function that returns copy of Product as new object.
     *
     * @param product  Product object. Each field of given object is copied
     *              to a new object
     * @return new Product
     */
    public Product clone(Product product){
        return new Product(product);
    }



    /**
     * Compares this object to the specified object. The result is true
     * if and only if the argument is not null, is an instance of Product
     * and is an object with the same fields as this object.
     *
     * @param obj  A Product object to compare this Product against
     * @return true if the given object represents a Product equivalent to this Product, false otherwise
     */
    @Override
    public boolean equals(Object obj){

        if (obj == null) return false;

        if (this == obj) return true;

        if (!(obj instanceof Product)) return false;

        Product prod = (Product) obj;

        return prod.id==this.id
                && prod.name.compareToIgnoreCase(this.name)==0
                && prod.description.compareToIgnoreCase(this.description)==0
                && Float.compare(prod.price, this.price)==0
                && prod.amount == this.amount
                && prod.measure == this.measure;
    }



    /**
     * Prints values of every field to console.
     */
    public void printProduct() {
        System.out.println(this.getId() + " | " + this.getName() + " | " +
                this.getDescription() + " | " + this.price + " | "
                + this.getAmount() + " | " + this.getMeasure()
                );
    }



    /**
     * Reads data from json file. If file exists, it creates JsonParser
     * and tries to parse every json-object to Product object till the end of file.
     * Every persed json-object is saved to ArrayList.
     *
     * @param file the file to read the products from
     * @return a list of Product objects read from the file
     * @throws IOException if an error occurs while reading the file
     */
    public static List<Product> readFromFile(String file) throws IOException {

        List<Product> productList = new ArrayList<>();

        // Create a factory for creating a JsonParser instance
        JsonFactory jsonFactory = new JsonFactory();
        // Create a JsonParser instance
        try (JsonParser jsonParser = jsonFactory.createParser(new FileInputStream(file))) {

            // Check the first token
            if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Expected content to be an array");
            }

            // Iterate over the tokens until the end of the array
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                // Read a contact and do something with it
                Product prod = readProduct(jsonParser);
                productList.add(prod);
            }
        }
        return productList;
    }


    /**
     * Parses json-object to a Product object.
     *
     * @param 'jsonParser'  String with path to file
     * @return List<Product>
     * @throws IOException  If file contains non-json objects
     *
     */
    private static Product readProduct(JsonParser jsonParser) throws IOException {
        // Check the first token
        if (jsonParser.currentToken() != JsonToken.START_OBJECT) {
            throw new IllegalStateException("Expected content to be an object");
        }

        Product prod = new Product();

        // Iterate over the properties of the object
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            // Get the current property name
            String property = jsonParser.getCurrentName();

            // Move to the corresponding value
            jsonParser.nextToken();

            // Evaluate each property name and extract the value
            switch (property) {
                case "id":
                    prod.setId(jsonParser.getIntValue());
                    break;
                case "name":
                    prod.setName(jsonParser.getText());
                    break;
                case "description":
                    prod.setDescription(jsonParser.getText());
                    break;
                case "price":
                    String temp = jsonParser.getText();
                    prod.setPrice(Float.parseFloat(temp.substring(1, temp.length())));
                    break;
                case "amount":
                    prod.setAmount(Integer.parseInt(jsonParser.getText()));
                    break;
                case "measure":
                    String measure = jsonParser.getText();
                    if (!(measure.compareToIgnoreCase("null") ==0)){
                        prod.setMeasure(Measure.valueOf(jsonParser.getText()));
                    } else {
                        prod.setMeasure(null);
                    }
                    break;
                // Unknown properties are ignored
            }
        }
        return prod;
    }

    /**
    *
    * Writes the current Product object to a file in JSON format.
    *
    * @param file the path to the file where the object will be written
    * @throws IOException if an I/O error occurs while writing to the file
    */
    public void writeToFile(String file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);



        try {
            FileWriter out = new FileWriter(file, true);
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            long len = randomAccessFile.length();
            randomAccessFile.seek(len-1); //to set pointer in the file (из-за запятых и ']')
            randomAccessFile.write(',');
            randomAccessFile.write('\n');

            objectMapper.writeValue(randomAccessFile, this);
            randomAccessFile.write(']');
            out.close();
            randomAccessFile.close();
            System.out.println("Object [ " + this + " ] was added to file [ " + file + " ]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Shuffles the order of elements in the given list of Product objects.
     *
     * @param productList the list of Product objects to be shuffled
     */
    public static void shuffle(List<Product> productList){
        for (int i= productList.size()-1; i > 0; i--){
            int j = (int) Math.floor(Math.random() * (i+1));
            Product temp = productList.get(j);
            productList.set(j, productList.get(i));
            productList.set(i, temp);
        }
    }

    /**
     *
     * Writes the given list of Product objects to a new file in JSON format.
     * @param filepath the path to the file where the list of products will be written
     * @param productList the list of Product objects to be written to the file
     */
    public static void writeProductsToNewFile(String filepath, List<Product> productList){

        try (PrintWriter out = new PrintWriter(new FileWriter(filepath))) {
            File myObj = new File("filename.txt");
            myObj.createNewFile();
            Gson gson = new Gson();
            String jsonString = gson.toJson(productList);
            out.write(jsonString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
