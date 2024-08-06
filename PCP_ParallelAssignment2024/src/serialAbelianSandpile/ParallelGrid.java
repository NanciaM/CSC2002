package serialAbelianSandpile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ParallelGrid implements Grid {
    private int rows, columns, offset;
	private int [][] grid; //grid 
	private int [][] updateGrid;

	  ParallelGrid(int r,int c, int start) { 
	    rows=r;
        columns =c;
	    offset=start;
	  }
      public ParallelGrid(int[][] newGrid) {
		this(newGrid.length,newGrid[0].length); //call constructor above
		//don't copy over sink border
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.grid[i][j]=newGrid[i-1][j-1];
			}
		}
	}

	  protected void compute(){
		  if((rows) <=65) { //only one task left, do it. This cutoff would be bigger for proper programs
            for(int i=1; i<rows-1; i++ ) {
                for( int j=1; j<columns-1; j++ ) {
                    this.grid[i][j]= newGrid[i-1][j-1];
                }
            }
		  }
		    else {
		    		int split=(int) (rows/2.0);
		    		//split work into two
		    		HelloForkJoin left = new HelloForkJoin(split,offset);  //first half
		    		HelloForkJoin right= new HelloForkJoin(rows-split,offset+split ); //second half
		    		left.fork(); //give first half to new threas

		    	    right.compute(); //do second half in this thread	
		    	    left.join();
            }
        }

    

    
}
