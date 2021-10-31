import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

/**
 * This class checks if 2 persons are in contact so it can transmit the disease.
 * Only checks every given time (1000ms for now). This is because in a really big population
 * the processor lasts a relative long time to check all the collisions, this time is variable.
 * So for keeping it constant we choose a default value that will be enough in the worst scenario. This value has to be
 * chosen taking consideration the POPULATION and computer capacity.
 *
 * @author Oussama Rakye
 * @author Sumeet Saini
 */
public class CollisionChecker extends Thread {
    //Starts a new thread that runs parallel to the simulation
    @Override
    public synchronized void run() {
        while (true) {
            //Time is measured
            long start = System.currentTimeMillis();

            //LinkedHashMaps with people infected and people who can still get the disease respectively
            LinkedHashMap<Rectangle, Person> infectedPeople = new LinkedHashMap<>();
            LinkedHashMap<Rectangle, Person> canGetInfectedPeople = new LinkedHashMap<>();

            //New array that copies the contents to avoid concurrency error
            ArrayList<Person> personsWithIt = new ArrayList<>(Person.getPersonsWithDisease());
            //We add all the persons with its newly created bounds in the LinkedHashMap
                for (Person person : personsWithIt) {
                    //This if is for security and checking that the person is not null
                    if(person != null) {
                        //Bounds of the person in form of a rectangle
                        Rectangle bounds = new Rectangle(person.getLayoutX(), person.getLayoutY(), person.getRadius() * 2, person.getRadius() * 2);
                        infectedPeople.put(bounds, person);
                    }
                }

            //New array that copies the contents to avoid concurrency error
            ArrayList<Person> personsWithoutIt = new ArrayList<>(Person.getPersonsCanGetDisease());
            //We add all the persons with its newly created bounds in the LinkedHashMap
            for (Person person : personsWithoutIt) {
                //Bounds of the person in form of a rectangle
                Rectangle bounds = new Rectangle(person.getLayoutX(), person.getLayoutY(), person.getRadius() * 2, person.getRadius() * 2);
                canGetInfectedPeople.put(bounds, person);
            }

            //For each infected person we check if he can contagion someone
            while (infectedPeople.size()>=1) {
                //Bounds of the first person
                Rectangle firstBounds = infectedPeople.keySet().iterator().next();
                detectMeeting(canGetInfectedPeople, firstBounds);
                infectedPeople.remove(firstBounds);
            }
            MyWorld.updateValues();
            long waitedTime = System.currentTimeMillis()-start;
            System.out.println(waitedTime);

            if (waitedTime<800){
                try {
                    TimeUnit.MILLISECONDS.sleep(800-waitedTime);
                } catch (InterruptedException e) {

                }
            }
        }
    }


    /**
     * This class checks if has met someone during the movement.
     * If some of this two has the disease, this person will infect the other one
     */
    private void detectMeeting(LinkedHashMap<Rectangle, Person> linkedHashMapCan, Rectangle firstBounds) {
        //System.out.println(iterator.next());

        for (Rectangle secondBounds : linkedHashMapCan.keySet()) {
            if (secondBounds.intersects(firstBounds.getLayoutBounds())) {
                Person secondPerson = linkedHashMapCan.get(secondBounds);
                secondPerson.infect(false);
            }
        }
    }
}