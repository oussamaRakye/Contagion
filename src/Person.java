import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

/**
 * A person in a virus simulation represented by a Circle
 *
 * @author Oussama Rakye
 * @author Sumeet Saini
 * @version 2.1
 */

class Person extends Circle {
    //Array that contains all persons
    private static ArrayList<Person> persons = new ArrayList<>();
    //Array that contains all persons with the disease
    private static ArrayList<Person> personsWithDisease = new ArrayList<>();
    //Array that contains all persons with the disease
    private static ArrayList<Person> personsCanGetDisease = new ArrayList<>();
    //Boolean that's true while simulating
    private static boolean simulating = true;
    //Counters
    private static int counterInfectedPeople;
    private static int counterRecoveredPeople;
    private static int counterDeadPeople;
    private static int counterHealthyPeople;

    //Pane where it will be added to
    private Pane background;

    private Random random = new Random();
    //Speed of the person in the map in both axis
    private double dx;
    private double dy;

    //The disease, will be null if the person is healthy/recovered
    private Disease disease;
    //Once has passes the disease won't b able to get it again
    private boolean canGetInfected = true;
    //Boolean to mark if this person is alive
    private boolean alive;
    //Animation, it adds the movement to the persons
    private Timeline timeline;
    //The person doesn't move if quarantined
    private boolean quarantined;

    /**
     * Creates the person, gets colocated in a random position of the background
     *
     * @param background where the person will be added to
     */
    Person(Pane background, double radius) {
        super(radius, Color.CADETBLUE);
        this.relocate(random.nextInt(MyWorld.PANE_WIDTH + (int) radius * 3) - radius * 1.5, random.nextInt(MyWorld.PANE_HEIGHT + (int) radius * 3) - radius * 1.5);
        this.background = background;
        persons.add(this);
        personsCanGetDisease.add(this);

        alive = true;

        quarantined = random.nextDouble() < Constants.QUARANTINE;

        counterHealthyPeople++;

        //Sets the speed. The module will be equal to 6, with a random it can be positive or negative
        dx = Constants.SPEED * random.nextDouble(); //Step on x or velocity
        if (random.nextBoolean()) dx *= -1;
        dy = Math.sqrt(Math.pow(Constants.SPEED, 2) - Math.pow(dx, 2)); //Step on y
        if (random.nextBoolean()) dy *= -1;

        //Starts the movement
        addMovement();
    }

    /**
     * Pause moving all the persons
     */
    public static void pauseAll() {
        simulating = false;
        for(Person each : persons){
            each.timeline.pause();
        }
    }

    public static void resetValues() {
        persons.clear();
        personsCanGetDisease.clear();
        personsWithDisease.clear();
    }

    /**
     * Makes the person move/act
     */
    private void addMovement() {
        //Limits of the background
        Bounds bounds = background.getBoundsInLocal();

        //Start of the animation with the predefined time
        timeline = new Timeline(new KeyFrame(Duration.millis(Constants.TIME_STEP_MILLISECONDS),
                t -> {
                    if(!quarantined) {
                        //move the person
                        setLayoutX(getLayoutX() + dx);
                        setLayoutY(getLayoutY() + dy);

                        //If the person reaches the left or right border make the step negative changing the speed
                        if (getLayoutX() <= bounds.getMinX()) {

                            dx = Constants.SPEED * random.nextDouble(); //Step on x or velocity
                            dy = Math.sqrt(Math.pow(Constants.SPEED, 2) - Math.pow(dx, 2)); //Step on y
                            if (random.nextBoolean()) dy *= -1;

                        } else if (getLayoutX() >= bounds.getMaxX()) {
                            dx = -Constants.SPEED * random.nextDouble(); //Step on x or velocity
                            dy = Math.sqrt(Math.pow(Constants.SPEED, 2) - Math.pow(dx, 2)); //Step on y
                            if (random.nextBoolean()) dy *= -1;

                        }

                        //If the ball reaches the bottom or top border make the step negative changing the speed
                        if (getLayoutY() >= bounds.getMaxY()) {

                            dy = -Constants.SPEED * random.nextDouble(); //Step on y or velocity
                            dx = Math.sqrt(Math.pow(Constants.SPEED, 2) - Math.pow(dy, 2)); //Step on x
                            if (random.nextBoolean()) dx *= -1;

                        } else if (getLayoutY() <= bounds.getMinY()) {

                            dy = Constants.SPEED * random.nextDouble(); //Step on y or velocity
                            dx = Math.sqrt(Math.pow(Constants.SPEED, 2) - Math.pow(dy, 2)); //Step on x
                            if (random.nextBoolean()) dx *= -1;
                        }
                    }

                    //If this person has a disease, increase by one the longitude of it
                    if (disease != null) {
                        //If it has finished, call method finalDisease();
                        finalDisease();
                    }
                }));
        timeline.setCycleCount(Timeline.INDEFINITE);        //Make the animation run without a limit number of times
        timeline.play();
    }

    /**
     * Decides what will happen once the disease has arrived to its last day
     */
    private void finalDisease() {
        if(disease.getInfectionRemaining()<=0) {
            personsWithDisease.remove(this);
            setFill(Color.rgb(20, 240, 0));
            counterRecoveredPeople++;
            disease = null;
            if (--counterInfectedPeople <= 0) {
                stopAll();
            }
        }
        else {
            //Checks if this person survives according to the disease class
            double multiplier = 1;
            multiplier += 2*Math.pow((double)counterInfectedPeople/Constants.POPULATION, 2);
            if (disease.getIsDead(multiplier)) {
                alive = false;
                //Stop moving
                timeline.stop();
                //Set the color of the person to black
                setFill(Color.BLACK);
                counterDeadPeople++;
                disease = null;
                //Check if there is people still infected, otherwise stop everything.
                if (--counterInfectedPeople <= 0) {
                    stopAll();
                }
            }
            //No longer has a disease
        }
    }

    /**
     * Stop moving all the persons
     */
    private void stopAll() {
        simulating = false;
        for(Person each : persons){
            each.timeline.stop();
        }
    }

    /**
     * Stop moving all the persons
     */
    public static void resumeAll() {
        simulating = false;
        for(Person each : persons){
            each.timeline.play();
        }
    }



    /**
     * @return true if it is still simulating, false otherwise.
     */
    static boolean isSimulating(){
        return simulating;
    }

    /**
     * Creates a new disease for this person and increases the counter of infected people
     */
    void infect(boolean getsInfected) {
        if(canGetInfected) {
            if(random.nextDouble()<Constants.INFECTION_RATE || getsInfected) {
                //This person won't be able to get infected gain
                setCantGetInfected();
                counterHealthyPeople--;
                personsWithDisease.add(this);
                personsCanGetDisease.remove(this);
                counterInfectedPeople++;
                disease = new Disease();
                //Set the person color to red
                this.setFill(javafx.scene.paint.Color.rgb(245, 6, 0));
            }
        }
    }

//    /**
//     * @return the disease of this person
//     */
//    public Disease getDisease() {
//        return disease;
//    }

//    /**
//     * @return true if this person can get infected, false otherwise
//     */
//    public boolean isCanGetInfected() {
//        return canGetInfected;
//    }

    /**
     * Set false if this person can't get infected again
     */
    private void setCantGetInfected() {
        this.canGetInfected = false;
    }

//    /**
//     * Checks the state of this person
//     * @return state state
//     */
//    public String getState(){
//        if(alive && !canGetInfected) return "recovered";
//        else if(alive && disease != null) return "infected";
//        else if(!alive) return "dead";
//        //Returns an empty string if this person is healthy and has never got the disease
//        else return "";
//    }

    static int getInfected()
        {
            return counterInfectedPeople;
        }

    static int getRecovered() {
        return counterRecoveredPeople;
    }

    static int getDead() {
        return counterDeadPeople;
    }

//    public static ArrayList<Person> getPersons() {
//        return persons;
//    }

    static ArrayList<Person> getPersonsWithDisease() {
        return personsWithDisease;
    }

    static ArrayList<Person> getPersonsCanGetDisease() {
        return personsCanGetDisease;
    }

//    public static String getTotalPeople(){
//
//        return "D: " + counterDeadPeople + "   H: " + counterHealthyPeople + "   I: " + counterInfectedPeople + "   R: " + counterRecoveredPeople + "   T: " +
//                (counterRecoveredPeople+counterInfectedPeople+counterHealthyPeople+counterDeadPeople);
//    }
}
