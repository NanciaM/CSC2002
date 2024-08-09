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
public class ParallelGrid extends RecursiveTask<Boolean> {
	public int rows, columns, startRow;
	public Grid grid;
	public int [][] pGrid; //ParallelGrid 
	private int [][] updatePGrid;//ParallelGrid for next time step

    public ParallelGrid(Grid grid, int startR, int endR) {
        this.grid = grid;
        
        this.rows = endR+2;
    }
    
	public ParallelGrid(int w, int h) {
		rows = w+2; //for the "sink" border
		columns = h+2; //for the "sink" border
		pGrid = new int[this.rows][this.columns];
		updatePGrid=new int[this.rows][this.columns];
		/* ParallelGrid  initialization */
		for(int i=0; i<this.rows; i++ ) {
			for( int j=0; j<this.columns; j++ ) {
				pGrid[i][j]=0;
				updatePGrid[i][j]=0;
			}
		}
	}

	public ParallelGrid(int[][] newGrid) {
		this(newGrid.length,newGrid[0].length); //call constructor above
		//don't copy over sink border
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.pGrid[i][j]=newGrid[i-1][j-1];
			}
		}
		
	}
	public ParallelGrid(ParallelGrid copyGrid) {
		this(copyGrid.rows,copyGrid.columns); //call constructor above
		/* ParallelGrid  initialization */
		for(int i=0; i<rows; i++ ) {
			for( int j=0; j<columns; j++ ) {
				this.pGrid[i][j]=copyGrid.get(i,j);
			}
		}
	}
	
	public int getRows() {
		return rows-2; //less the sink
	}

	public int getColumns() {
		return columns-2;//less the sink
	}


	int get(int i, int j) {
		return this.pGrid[i][j];
	}

	void setAll(int value) {
		//borders are always 0
		for( int i = 1; i<rows-1; i++ ) {
			for( int j = 1; j<columns-1; j++ ) 			
				pGrid[i][j]=value;
			}
	}
	

	//for the next timestep - copy updatePGrid into ParallelGrid
	public void nextTimeStep() {
		for(int i=1; i<rows-1; i++ ) {
			for( int j=1; j<columns-1; j++ ) {
				this.pGrid[i][j]=updatePGrid[i][j];
			}
		}
	}
	
	//key method to calculate the next update grod
	protected Boolean compute() {
		boolean change=false;
		//do not update border
        if((rows) <=30 && (columns) <=30) { // 30 is Small enough cutoff task to compute directly

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
		 
	return change;
	}
    public boolean update() {
        ForkJoinPool pool = new ForkJoinPool();
        ParallelGrid task = new ParallelGrid(grid, 1, rows - 1);
        boolean changed = pool.invoke(task);
        nextTimeStep();
        return changed;
    }
	
	
	
	//display the ParallelGrid in text format
	void printGrid( ) {
		int i,j;
		//not border is not printed
		System.out.printf("pGrid:\n");
		System.out.printf("+");
		for( j=1; j<columns-1; j++ ) System.out.printf("  --");
		System.out.printf("+\n");
		for( i=1; i<rows-1; i++ ) {
			System.out.printf("|");
			for( j=1; j<columns-1; j++ ) {
				if ( pGrid[i][j] > 0) 
					System.out.printf("%4d", pGrid[i][j] );
				else
					System.out.printf("    ");
			}
			System.out.printf("|\n");
		}
		System.out.printf("+");
		for( j=1; j<columns-1; j++ ) System.out.printf("  --");
		System.out.printf("+\n\n");
	}
	
	//write ParallelGrid out as an image
	void gridToImage(String fileName) throws IOException {
        BufferedImage dstImage =
                new BufferedImage(rows, columns, BufferedImage.TYPE_INT_ARGB);
        //integer values from 0 to 255.
        int a=0;
        int g=0;//green
        int b=0;//blue
        int r=0;//red

		for( int i=0; i<rows; i++ ) {
			for( int j=0; j<columns; j++ ) {
			     g=0;//green
			     b=0;//blue
			     r=0;//red

				switch (pGrid[i][j]) {
					case 0:
		                break;
		            case 1:
		            	g=255;
		                break;
		            case 2:
		                b=255;
		                break;
		            case 3:
		                r = 255;
		                break;
		            default:
		                break;
				
				}
		                // Set destination pixel to mean
		                // Re-assemble destination pixel.
		              int dpixel = (0xff000000)
		                		| (a << 24)
		                        | (r << 16)
		                        | (g<< 8)
		                        | b; 
		              dstImage.setRGB(i, j, dpixel); //write it out

			
			}}
		
        File dstFile = new File(fileName);
        ImageIO.write(dstImage, "png", dstFile);
	}
	
	


}