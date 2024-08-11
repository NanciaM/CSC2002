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
	private int rows, columns;
	public int [][] grid; //grid 
	public int [][] updateGrid;//grid for next time step

	public ParallelUpdate(int[][] grid, int startRow, int endRow) {
		
		this.rows = (endRow - startRow) + 2; // +2 for the sink borders
		this.columns = grid[0].length + 2; // +2 for the sink borders
	
		this.grid = new int[this.rows][this.columns];
		this.updateGrid = new int[this.rows][this.columns];
		for (int i = 1; i < this.rows - 1; i++) {
			for (int j = 1; j < this.columns - 1; j++) {
				this.grid[i][j] = grid[startRow + i - 1][j - 1];
			}
		}
	}
  
	
	protected Boolean compute() {
		boolean change=false;
		//do not update border
        if((rows) <=30 ) { // 30 is Small enough cutoff task to compute directly

            for( int i = 1; i<rows-1; i++ ) {
                for( int j = 1; j<columns-1; j++ ) {
                    updateGrid[i][j] = (grid[i][j] % 4) + 
                            (grid[i-1][j] / 4) +
                            grid[i+1][j] / 4 +
                            grid[i][j-1] / 4 + 
                            grid[i][j+1] / 4;
                    if (grid[i][j]!=updateGrid[i][j]) {  
                        change=true;
                    }
            }}
            if (change) { nextTimeStep();}
        }
        else{
            int rowSplit=(int) (rows/2.0);
             
		    		//split work into two
		    		ParallelUpdate left = new ParallelUpdate(grid, 1, rowSplit);  //first half
		    		ParallelUpdate right= new ParallelUpdate(grid, rowSplit, rows - 1); //second half
		    		left.fork(); //give first half to new threas

		    	    right.compute(); //do second half in this thread	
		    	    left.join();
        }
		 
	return change;
	}
    public boolean parallelUpdate() {
        ForkJoinPool pool = new ForkJoinPool();
        Grid task = new Grid(grid);
        boolean changed = pool.invoke(task);
        nextTimeStep();
        return changed;
    }
	
}	