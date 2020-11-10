package com.dragonboat.game;
import java.util.ArrayList;

public class Opponent extends Boat {

    public String steering = "None";
    private ArrayList<Obstacle> sortedIncomingObstacles;

    public Opponent(int yPosition, int width, int height, Lane lane, String name) {
        super(yPosition, width, height, lane, name);
        sortedIncomingObstacles = new ArrayList<Obstacle>();
    }

    public void betterAI(int backgroundOffset) {

        int leftSide = Math.round(xPosition);
        int rightSide = Math.round(xPosition + width);

        int fov = 5; //Determine a good field of view for the Opponents to start reacting to incoming obstacles.
        int visionDistance = Math.round(yPosition + height) + fov;

        ArrayList<Obstacle> allIncomingObstacles = this.lane.obstacles;

        boolean noNewPath = true; //Set to false whenever the Opponent has decided on a new path.

        if(noNewPath) { //If still no new path, if there is one, skip all this code for speed's sake.

            //Insertion sort Obstacles in incomingObstacles from lowest to highest yPosition (proximity to the Boat, even)
            for(Obstacle obs : allIncomingObstacles) {
                if(obs.getY() + backgroundOffset > this.getY()) {
                    System.out.println("sees obstacle at " + obs.getY());
                    if(sortedIncomingObstacles.size() == 0) {
                        sortedIncomingObstacles.add(obs);
                    }
                    else {
                        boolean inserted = false;
                        int index = 0;
                        while(inserted == false) {
                            Obstacle thisObstacle = sortedIncomingObstacles.get(index);
                            if(index < sortedIncomingObstacles.size()) {
                                //If not reached end of sortedIncomingObstacles.
                                if(thisObstacle.getY() > obs.getY()) {
                                    //Keep looking for place to insert.
                                    index += 1; 
                                }
                                else {
                                    //Found place to insert!
                                    sortedIncomingObstacles.add(index, obs); 
                                    inserted = true;
                                }
                            }
                            else {
                                //End of ArrayList reached.
                                sortedIncomingObstacles.add(obs);
                                inserted = true;
                            }  
                        }
                    }
                }
            }
            if(sortedIncomingObstacles.size() > 0) {
                Obstacle incoming = sortedIncomingObstacles.get(0);
                if (incoming.getY() <= visionDistance) {
                    if (incoming.getX() + incoming.width < leftSide) {

                    } else if (incoming.getX() + incoming.width < rightSide) {

                    } else {
                        if (incoming.getX() < leftSide) {
                            this.SteerRight();
                        } else {
                            this.SteerLeft();
                        }
                        noNewPath = false;
                    }
                }
            }
            
        }
    }

    public void ai(int backgroundOffset) {
        /*
        One method to control the AI behaviour of one boat.
        Every so often in the game loop, Opponent.ai() gets called and the movement path of the Opponent boats will change.

        AI new path selection:
            1) If not in lane, go back to lane.
            2) If obstacle ahead, avoid the obstacle. If dead ahead, slow down.
            3) If nothing, speed up.
        */

        int leftSide = Math.round(xPosition);
        int rightSide = Math.round(xPosition + width);

        int fov = 10; //Determine a good field of view for the Opponents to start reacting to incoming obstacles.
        int visionDistance = Math.round(yPosition + height) + fov;

        ArrayList<Obstacle> allIncomingObstacles = this.lane.obstacles;

        boolean noNewPath = true; //Set to false whenever the Opponent has decided on a new path.

        if(this.steering == "Left") {
            this.SteerLeft();
            this.steering = "None";
            noNewPath = false;
        }
        else if(this.steering == "Right") {
            this.SteerRight();
            this.steering = "None";
            noNewPath = false;
        }
        else {
            noNewPath = true;
        }

        /*
        1) If not in lane, go back to lane.
        */

        if(this.CheckIfInLane() == false || !noNewPath) {
            //Commence route back into lane.
            if(leftSide - this.lane.GetLeftBoundary() <= 0) {
                //Will only be negative if the boat is further left than the leftBoundary.
                this.SteerRight();
                this.steering = "Right";
            }
            else if(rightSide - this.lane.GetRightBoundary() >= 0) {
                //Will only be positive if the boat is further right than the rightBoundary.
                this.SteerLeft();
                this.steering = "Left";
            }
            noNewPath = false;
        }

        /*
        2) If obstacle ahead, avoid the obstacle. If dead ahead, slow down.
        */
        if(noNewPath) { //If still no new path, if there is one, skip all this code for speed's sake.

            //Insertion sort Obstacles in incomingObstacles from lowest to highest yPosition (proximity to the Boat, even)
            for(Obstacle obs : allIncomingObstacles) {
                if(sortedIncomingObstacles.size() == 0) {
                    sortedIncomingObstacles.add(obs);
                }
                else {
                    boolean inserted = false;
                    int index = 0;
                    while(inserted == false) {
                        Obstacle thisObstacle = sortedIncomingObstacles.get(index);
                        if(index < sortedIncomingObstacles.size()) {
                            //If not reached end of sortedIncomingObstacles.
                            if(thisObstacle.getY() > obs.getY()) {
                                //Keep looking for place to insert.
                                index += 1; 
                            }
                            else {
                                //Found place to insert!
                                sortedIncomingObstacles.add(index, obs); 
                                inserted = true;
                            }
                        }
                        else {
                            //End of ArrayList reached.
                            sortedIncomingObstacles.add(obs);
                            inserted = true;
                        }  
                    }
                }
            }

            for(Obstacle obs : sortedIncomingObstacles) {

                if(obs.getY() + backgroundOffset <= visionDistance && obs.getY() + backgroundOffset > this.yPosition) {
                    //The obstacle is visible from the boat.
                    System.out.println("Sees " + obs.getClass() + " at " + obs.getY());
                    if(obs.getX() + obs.width < leftSide) {
                        /*
                        The obstacle is far left of the boat.

                        Do nothing? A moving Obstacle (Goose) might currently be far left but heading right on a collision course.
                        Maybe check type of Obstacle then; if it's a Goose, check the direction. If it's headed your way, do something about that.
                        */
                        
                        if((obs.getClass() == Goose.class) && ((Goose) obs).direction == "East") {
                            this.SteerRight();
                            this.steering = "Right";
                        }
                        
                    }
                    else if(obs.getX() > rightSide) {
                        /*
                        The obstacle is far right of the boat.

                        Do nothing? A moving Obstacle (Goose) might currently be far right but heading left on a collision course.
                        Maybe check type of Obstacle then; if it's a Goose, check the direction. If it's headed your way, do something about that.
                        */

                        if((obs.getClass() == Goose.class) && ((Goose) obs).direction == "West") {
                            this.SteerLeft();
                            this.steering = "Left";
                        }
                    }
                    else {
                        /*
                        Part or all of the obstacle is on a collision course with the boat.
                        Move in the appropriate direction. Depends on whether the Obstacle is to the left or right, on whether you are close to the Lane boundary, etc.
                        If the obstacle is dead ahead, slow down also.
                        If there is a goose ahead and it is moving horizontally, steer around it and opposite it.
                        */

                        if((obs.getClass() == Goose.class) && ((Goose) obs).direction == "East") {
                            this.SteerLeft();
                            this.steering = "Left";
                        }
                        
                        else if((obs.getClass() == Goose.class) && ((Goose) obs).direction == "West") {
                            this.SteerRight();
                            this.steering = "Right";
                        }
                        
                        else {
                            //For objects moving vertically, calculate whether steering left of it or right of it is the easiest course. 
                            int leftMargin = Math.round(leftSide - obs.getX());
                            int rightMargin = Math.round(obs.getX()) + obs.width - rightSide;
                            
                            //Check to slow down.
                            if((leftMargin <= 0 && rightMargin <= 0) || (leftMargin >= 0 && rightMargin >= 0)){
                                //Obstacle is dead ahead. Slow down.
                                this.DecreaseSpeed();
                            }
        
                            //Check to go left or right.
                            if(leftMargin <= rightMargin) {
                                //If easier to dodge to the left of the obstacle, steer to the left of the obstacle.
                                this.SteerLeft();
                                this.steering = "Left";
                            }
                            else {
                                //If not, steer to the right of the obstacle.
                                this.SteerRight();
                                this.steering = "Right";
                            }
                        }

                        noNewPath = false;
                        break;
                    }
                }
            }
        }

        /*
        2.5) Move to middle.
        */
        if(noNewPath) {
            int middle = lane.GetRightBoundary() - (lane.GetRightBoundary() - lane.GetLeftBoundary()) / 2 - this.width / 2;
            if (leftSide == middle) {

            } else if (leftSide < middle) {
                this.SteerRight();
                steering = "Right";
            } else {
                this.SteerLeft();
                steering = "Left";
            }
        }
        /*
        3) If nothing, speed up.
        */
        /*
        if(noNewPath) {
            //Still no new path? Just speed up in the forwards direction!
            this.IncreaseSpeed();
        }
        */
    }
}
