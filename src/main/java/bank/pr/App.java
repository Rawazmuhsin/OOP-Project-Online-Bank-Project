package bank.pr;


 
public class App 
{
    public static void main( String[] args )
    {



        try {
            // Use your existing DatabaseConnection class
            if (DatabaseConnection.getConnection() != null) {
                System.out.println(" Database connected successfully!");
            }
        } catch (Exception e) {
            System.out.println(" Failed to connect to database: " + e.getMessage());
        }

    }
}
