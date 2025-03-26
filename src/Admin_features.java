import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Admin_features {
    public static void handleAdminFeatures(DataInputStream input, DataOutputStream output) throws IOException, IOException {
        while (true) {
            String message = input.readUTF();

            if (message.equalsIgnoreCase("viewStats")) {
                viewStatistics(output);
            }
            else if (message.equalsIgnoreCase("storeData")) {
                for(ClientInfo customer:UberServer.customers)
                {
                    Database_features.saveCustomer(customer);
                }
                for(ClientInfo driver:UberServer.drivers)
                {
                    Database_features.saveDriver(driver);
                }
                for(Ride ride:UberServer.rides)
                {
                    Database_features.saveRide(ride);
                }
                try {
                    output.writeUTF("Data stored successfully");
                } catch (IOException e) {
                    System.out.println("Failed to send confirmation to client: " + e.getMessage());
                }

            }
            else if (message.equalsIgnoreCase("exit")) {
                break;
            }

    }
    }
        public static void viewStatistics(DataOutputStream output) throws IOException {
            StringBuilder stats = new StringBuilder();
            output.writeUTF("Admin Statistics Report");
            output.writeUTF("---------------------------------------\n");
            output.writeUTF("Total Customers: "+UberServer.customers.size());
            output.writeUTF("Total Drivers: "+UberServer.drivers.size());
            output.writeUTF("---------------------------------------\n");
            output.writeUTF("All Rides:\n");


            for (Ride r : UberServer.rides) {
                output.writeUTF("---------------------------------------\n");
                output.writeUTF("Ride ID: "+r.getRideId()+"\n"+"Pick Up Location:"+r.getPickup()+"\n"+"Destination Location:"+r.getDestination()+"\n"+"Status:"+r.getStatus()+"\n"+"Customer name: "+r.getCustomerUsername()+"\n"+"Assigned Driver: "+r.getAssignedDriver());
                output.writeUTF("---------------------------------------\n");

            }
            output.writeUTF("end");

        }



}
