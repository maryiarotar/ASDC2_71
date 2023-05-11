package lab0;

import java.io.IOException;
import java.util.List;

//The Lab0 class is the main class for executing the Lab 0 task.
public class Lab0 {

    public static void main(String[] args) throws IOException {

        //LAB 0
        // прочитайте файл в массив объектов и выведите данный массив на экран.

        String file = "MOCK_DATA.json";
        List<Product> products = Product.readFromFile(file);

        System.out.println("№ |         name         |       description         |" +
                " price   | amount  | measure");

        for (Product prod : products) {
            prod.printProduct();
        }

/*        Product newProd = new Product();
        newProd.setId(products.size()+1);
        newProd.setName("Bbb Ccccccc");
        newProd.writeToFile(file);*/

    }

}
