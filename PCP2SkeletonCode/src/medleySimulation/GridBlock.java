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
    private final Lock lock = new ReentrantLock();
    private final Condition blockFree = lock.newCondition(); // Condition for checking if block is free
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
		lock.lock();
        try {
            while (isOccupied != -1 && isOccupied != threadID) {
                blockFree.await(); // Wait until the block is free
            }
            isOccupied = threadID; // Occupy the block
        } finally {
            lock.unlock();
        }
		
		/*if (isOccupied.get()==threadID) return true; //thread Already in this block
		if (isOccupied.compareAndSet(-1, threadID)) return true; //set ID to thread that had block		  
		return false;//space is occupied*/
	}
		
	
	//release a block
	public  void release() {
		lock.lock();
        try {
            isOccupied = -1; // Mark as not occupied
            blockFree.signalAll(); // Notify all waiting threads that block is free
        } finally {
            lock.unlock();
        }
		//isOccupied= new AtomicInteger(-1);
	}
	

	//is a bloc already occupied?
	public  boolean occupied() {
		lock.lock();
        try {
            return isOccupied != -1;
        } finally {
            lock.unlock();
        }
	}
	
	
	//is a start block
	public  boolean isStart() {
		return isStart;
	}

}
