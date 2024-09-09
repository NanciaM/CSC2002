//M. M. Kuttel 2024 mkuttel@gmail.com
//Class to represent a swim team - which has four swimmers
package medleySimulation;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;

import medleySimulation.Swimmer.SwimStroke;


public class SwimTeam extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private Swimmer [] swimmers;
	private int teamNo; //team number 
	private final CountDownLatch[] latches;
	private final CountDownLatch[] entranceLatches;

	
	public static final int sizeOfTeam=4;
	
	SwimTeam( int ID, FinishCounter finish,PeopleLocation [] locArr ) {
		this.teamNo=ID;
		this.latches = new CountDownLatch[sizeOfTeam];
		this.entranceLatches = new CountDownLatch[sizeOfTeam];

		swimmers= new Swimmer[sizeOfTeam];
	    SwimStroke[] strokes = SwimStroke.values();  // Get all enum constants
		stadium.returnStartingBlock(ID);

		 // Initialize latches
		 for (int i = 1; i < sizeOfTeam; i++) {
            latches[i] = new CountDownLatch(1); // Swimmers 2, 3, 4 wait for the previous swimmer
        }

		for(int i=teamNo*sizeOfTeam,s=0;i<((teamNo+1)*sizeOfTeam); i++,s++) { //initialise swimmers in team
			locArr[i]= new PeopleLocation(i,strokes[s].getColour());
	      	int speed=(int)(Math.random() * (3)+30); //range of speeds 
			swimmers[s] = new Swimmer(i,teamNo,locArr[i],finish,speed,strokes[s]); //hardcoded speed for now
		}
		//Initialize latches to sort swimmers in order of their events 
		for (int i = 0; i < sizeOfTeam; i++) {
            entranceLatches[i] = new CountDownLatch(1); // Each swimmer will wait for the previous swimmer
        }
		 
		//Set Latches to notify swimmers of previous and next swimmer
		for (int s = 0; s < sizeOfTeam; s++) {	
			CountDownLatch prevLatch = (s == 0) ? null : latches[s]; // First swimmer doesn't wait
			CountDownLatch nextLatch = (s == sizeOfTeam - 1) ? null : latches[s + 1]; // Last swimmer doesn't need to signal anyone
			swimmers[s].setLatches(prevLatch, nextLatch);
			 
		}
		
	}
	
	
	public void run() {
		try {
			// Set latches for each swimmer
			for (int s = 0; s < sizeOfTeam; s++) {
                CountDownLatch entryPrevLatch = (s == 0) ? null : entranceLatches[s - 1];
                CountDownLatch entryNextLatch = (s == sizeOfTeam - 1) ? null : entranceLatches[s];
                swimmers[s].setEntryLatches(entryPrevLatch, entryNextLatch);
            }

				
			// Start swimmer threads
				for (int s=0;s<sizeOfTeam; s++) {
					swimmers[s].start();
					
				}
	
				// Ensure all swimmer threads are completed
				for (int s=0;s<sizeOfTeam; s++) {
					swimmers[s].join();
				}
		} catch (InterruptedException e) {
            e.printStackTrace();
        }
        
	}
}

	

