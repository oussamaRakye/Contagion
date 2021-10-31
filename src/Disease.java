import java.util.Random;

/**
 * Creates the disease according to some fields
 *
 * @author Oussama Rakye
 * @version 1.1
 */

class Disease {
    //The steps left for this disease
    private int infectionRemaining;

    private Random random;

    /**
     * Sets the remaining infection time of the disease between the value set and 9 days less
     */
    Disease(){
        random = new Random();
        infectionRemaining = random.nextInt((Constants.INFECTION_LEGNTH - (Constants.INFECTION_LEGNTH -9)) + 1) + (Constants.INFECTION_LEGNTH -9)*20;    //There are 20 steps in one day
    }

//    /**
//     * @return the duration left for the disease
//     */
//    public int step(){
//        return infectionRemaining;
//    }

    int getInfectionRemaining(){
        return infectionRemaining;
    }

    /**
     * @return true if the disease kills the person, false otherwise
     */
    boolean getIsDead(double multiplier) {
        --infectionRemaining;
        if (infectionRemaining / 20 < 7) {
            return random.nextDouble() < multiplier * Constants.MORTALITY_RATE / (7 * 20);
        }
        return false;
    }

}
