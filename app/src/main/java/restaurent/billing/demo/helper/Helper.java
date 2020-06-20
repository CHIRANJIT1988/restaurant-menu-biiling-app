package restaurent.billing.demo.helper;


/**
 * Created by dell on 19-06-2015.
 */

public class Helper
{

    // directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "gmc.manasa.photo";


    public static String toCamelCase(String inputString)
    {

        String result = "";

        if (inputString.length() == 0)
        {
            return result;
        }

        char firstChar = inputString.charAt(0);
        char firstCharToUpperCase = Character.toUpperCase(firstChar);

        result = result + firstCharToUpperCase;

        for (int i = 1; i < inputString.length(); i++)
        {

            char currentChar = inputString.charAt(i);
            char previousChar = inputString.charAt(i - 1);

            if (previousChar == ' ')
            {

                char currentCharToUpperCase = Character.toUpperCase(currentChar);
                result = result + currentCharToUpperCase;
            }

            else
            {
                char currentCharToLowerCase = Character.toLowerCase(currentChar);
                result = result + currentCharToLowerCase;
            }
        }

        return result;
    }



    public static Order orderObject(String message)
    {

        String []order = message.split("<BR>");

        String order_id = order[0];
        int item_code = Integer.valueOf(order[1]);
        int quantity = Integer.valueOf(order[2]);
        int table_no = Integer.valueOf(order[3]);
        String datetime = order[4];
        String waiter_name = order[5];

        Order obj = new Order(order_id, item_code, quantity, table_no, datetime, waiter_name, "received");

        return obj;
    }
}