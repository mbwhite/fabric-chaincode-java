/*
 * SPDX-License-Identifier: Apache-2.0
 */

package fabcar;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.ledger.Collection;
import org.hyperledger.fabric.ledger.KeyQueryHandler;
import org.hyperledger.fabric.ledger.Collection.CollectionIterable;
import org.hyperledger.fabric.ledger.Ledger;
import org.hyperledger.fabric.ledger.State;
import org.hyperledger.fabric.shim.ChaincodeException;


/**
 * Java implementation of the Fabric Car Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "FabCar",
        info = @Info(
                title = "FabCar contract",
                description = "The hyperlegendary car contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "f.carr@example.com",
                        name = "F Carr",
                        url = "https://hyperledger.example.com")))
@Default
public final class FabCar implements ContractInterface {

    private enum FabCarErrors {
        CAR_NOT_FOUND,
        CAR_ALREADY_EXISTS
    }

    /**
     * Retrieves a car with the specified key from the ledger.
     *
     * @param ctx the transaction context
     * @param key the key
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public Car queryCar(final Context ctx, final String key) {

        // want to put this into the public collection, aka 'world state'
        Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);
        Car car = worldCollection.getObject(key, Car.class);
        if (car == null) {
            String errorMessage = String.format("Car %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.CAR_NOT_FOUND.toString());
        }
       
        return car;
    }

    /**
     * Creates some initial Cars on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction()
    public void initLedger(final Context ctx) {
        // want to put this into the public collection, aka 'world state'
        Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);


        String[] carData = {
                "{ \"make\": \"Toyota\", \"model\": \"Prius\", \"color\": \"blue\", \"owner\": \"Tomoko\" }",
                "{ \"make\": \"Ford\", \"model\": \"Mustang\", \"color\": \"red\", \"owner\": \"Brad\" }",
                "{ \"make\": \"Hyundai\", \"model\": \"Tucson\", \"color\": \"green\", \"owner\": \"Jin Soo\" }",
                "{ \"make\": \"Volkswagen\", \"model\": \"Passat\", \"color\": \"yellow\", \"owner\": \"Max\" }",
                "{ \"make\": \"Tesla\", \"model\": \"S\", \"color\": \"black\", \"owner\": \"Adrian\" }",
                "{ \"make\": \"Peugeot\", \"model\": \"205\", \"color\": \"purple\", \"owner\": \"Michel\" }",
                "{ \"make\": \"Chery\", \"model\": \"S22L\", \"color\": \"white\", \"owner\": \"Aarav\" }",
                "{ \"make\": \"Fiat\", \"model\": \"Punto\", \"color\": \"violet\", \"owner\": \"Pari\" }",
                "{ \"make\": \"Tata\", \"model\": \"nano\", \"color\": \"indigo\", \"owner\": \"Valeria\" }",
                "{ \"make\": \"Holden\", \"model\": \"Barina\", \"color\": \"brown\", \"owner\": \"Shotaro\" }"
        };

        for (int i = 0; i < carData.length; i++) {
            String key = String.format("CAR%03d", i);
            worldCollection.putState(key, carData[i]);
        }
    }

    /**
     * Creates a new car on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key for the new car
     * @param make the make of the new car
     * @param model the model of the new car
     * @param color the color of the new car
     * @param owner the owner of the new car
     * @return the created Car
     */
    @Transaction()
    public Car createCar(final Context ctx, final String key, final String make, final String model,
            final String color, final String owner) {
        
        // want to put this into the public collection, aka 'world state'
        Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);

        Car car = worldCollection.getObject(key,Car.class);
        if (car != null) {
            String errorMessage = String.format("Car %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.CAR_ALREADY_EXISTS.toString());
        }

        Car newcar = new Car(make, model, color, owner);        
        worldCollection.putState(key,newcar);

        return car;
    }

    /**
     * Retrieves every car between CAR0 and CAR999 from the ledger.
     *
     * @param ctx the transaction context
     * @return array of Cars found on the ledger
     */
    @Transaction()
    public Car[] queryAllCars(final Context ctx) {
        // want to get this into the public collection, aka 'world state'
        Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);

        final String startKey = "CAR0";
        final String endKey = "CAR999";
		
		KeyQueryHandler keyQuery = KeyQueryHandler.RANGE;
		keyQuery.setFromKey( startKey ).setToKey(endKey);
        
        List<Car> cars = new ArrayList<Car>();

        try (CollectionIterable<State> results = worldCollection.getStates(keyQuery)) {
            for (State state : results) {
                cars.add(state.getObject(Car.class));
            }
        }       

        Car[] response = cars.toArray(new Car[cars.size()]);

        return response;
    }

    /**
     * Changes the owner of a car on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key
     * @param newOwner the new owner
     * @return the updated Car
     */
    @Transaction()
    public Car changeCarOwner(final Context ctx, final String key, final String newOwner) {
        // want to put this into the public collection, aka 'world state'
        Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);

        Car car = worldCollection.getObject(key,Car.class);
        
        if (car==null) {
            String errorMessage = String.format("Car %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCarErrors.CAR_NOT_FOUND.toString());
        }

                
        Car newCar = new Car(car.getMake(), car.getModel(), car.getColor(), newOwner);
        
        worldCollection.putState(key, newCar);

        return newCar;
    }
}