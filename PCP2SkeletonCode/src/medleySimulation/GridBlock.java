//M. M. Kuttel 2024 mkuttel@gmail.com
// GridBlock class to represent a block in the grid.
// only one thread at a time "owns" a GridBlock - this must be enforced

package medleySimulation;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class GridBlock {
	
	private AtomicInteger isOccupied; 
	
	private final boolean isStart;  //is this a starting block?
	private int [] coords; // the coordinate of the block.
	//private final Object lock = new Object();
	
	GridBlock(boolean startBlock) throws InterruptedException {
		isStart=startBlock;
		isOccupied= new AtomicInteger (-1);
	}
	
	GridBlock(int x, int y, boolean startBlock) throws InterruptedException {
		this(startBlock);
		coords = new int [] {x,y};
	}
	
	public   int getX() {return coords[0];}  
	
	public   int getY() {return coords[1];}
	
	
	
	//Get a block
	public  boolean get(int threadID) throws InterruptedException {
		while (isOccupied.get() >= 0 && isOccupied.get() != threadID) {
			wait();
		}

		if (isOccupied.get() == threadID) {
			return true;  // Thread is already in the block
		}

		isOccupied = new AtomicInteger (threadID);
    	return true;  // Block is now occupied by this thread
	}
		
	
	//release a block
	public synchronized void release() {
			isOccupied.set(-1);
			notifyAll();
	}
	

	//is a bloc already occupied?
	public  boolean occupied() {
		return isOccupied.get() != -1;
	}
	
	
	//is a start block
	public  boolean isStart() {
		return isStart;	
	}

}
