//M. M. Kuttel 2024 mkuttel@gmail.com
// GridBlock class to represent a block in the grid.
// only one thread at a time "owns" a GridBlock - this must be enforced

package medleySimulation;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;


public class GridBlock {
	
	private int isOccupied = -1; // -1 means not occupied
    private final boolean isStart; // is this a starting block?
	private final Object lock = new Object();
   // private final Lock lock = new ReentrantLock();
    //private final Condition blockFree = lock.newCondition(); // Condition for checking if block is free

	private int [] coords; // the coordinate of the block.
	
	GridBlock(boolean startBlock) throws InterruptedException {
		this.isStart = startBlock;
	}
	
	GridBlock(int x, int y, boolean startBlock) throws InterruptedException {
		this(startBlock);
		coords = new int [] {x,y};
	}
	
	public   int getX() {return coords[0];}  
	
	public   int getY() {return coords[1];}
	
	
	
	//Get a block
	public void get(int threadID) throws InterruptedException {
		synchronized (lock) {  // Synchronize on the lock object (Java monitor lock)
            while (isOccupied != -1 && isOccupied != threadID) {
                lock.wait();  // Wait if the block is occupied
            }
            isOccupied = threadID;  // Occupy the block
        }
		/*if (isOccupied.get()==threadID) return true; //thread Already in this block
		if (isOccupied.compareAndSet(-1, threadID)) return true; //set ID to thread that had block		  
		return false;//space is occupied*/
	}
		
	
	//release a block
	public  void release() {
		synchronized (lock) {
            isOccupied = -1;  // Mark as not occupied
            lock.notifyAll();  // Notify all waiting threads that the block is free
        }
		//isOccupied= new AtomicInteger(-1);
	}
	

	//is a bloc already occupied?
	public  boolean occupied() {
		synchronized (lock) {
            return isOccupied != -1;
        }
	}
	
	
	//is a start block
	public  boolean isStart() {
		return isStart;
	}

}
