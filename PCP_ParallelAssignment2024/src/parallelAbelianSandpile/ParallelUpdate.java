//Copyright M.M.Kuttel 2024 CSC2002S, UCT
package serialAbelianSandpile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

//This class is for the ParallelGrid for the Abelian Sandpile cellular automaton
public class ParallelUpdate extends RecursiveTask<Boolean> {
	public int rows, columns, startRow;
	public Grid grid; //ParallelGrid 
	 

    public ParallelUpdate(Grid grid, int startR, int endR) {
        this.grid = grid;
        this.startRow = startR;
        this.rows = endR+2 ;
    }
  
	
	//key method to calculate the next update grod
	protected Boolean compute() {
		
		//do not update border
        if((rows) <=65) { // 65 is Small enough cutoff task to compute directly
            boolean change=false;

            for( int i = 1; i<rows-1; i++ ) {
                for( int j = 1; j<columns-1; j++ ) {
                    updatePGrid[i][j] = (pGrid[i][j] % 4) + 
                            (pGrid[i-1][j] / 4) +
                            pGrid[i+1][j] / 4 +
                            pGrid[i][j-1] / 4 + 
                            pGrid[i][j+1] / 4;
                    if (pGrid[i][j]!=updatePGrid[i][j]) {  
                        change=true;
                    }
            }}
            if (change) { nextTimeStep();}
            return change;
        }
        else{
            int rowSplit=(int) (rows/2.0);
            int offset = 0;
		    		//split work into two
		    		ParallelGrid left = new ParallelGrid(rowSplit, offset);  //first half
		    		ParallelGrid right= new ParallelGrid(rows-rowSplit,rowSplit+offset); //second half
		    		left.fork(); //give first half to new threas

		    	    right.compute(); //do second half in this thread	
		    	    left.join();
        }
	}
    public boolean update() {
        ForkJoinPool pool = new ForkJoinPool();
        ParallelGrid task = new ParallelGrid(this, 1, rows - 1);
        boolean changed = pool.invoke(task);
        nextTimeStep();
        return changed;
    }
}