 
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

import parallelAbelianSandpile.Grid;

//This class is for the ParallelGrid for the Abelian Sandpile cellular automaton
public class ParallelUpdate extends RecursiveTask<Boolean> {
    private static final int CUTOFF_VALUE = 100; // Adjust based on grid size
    private Grid newGrid;
    private int startRow,endRow;
    private int endColumn;

    ParallelUpdate(Grid grid, int l, int h) {
        this.newGrid = grid;
        this.endRow=l;
        this.startRow=h;
        this.endColumn = newGrid.grid[0].length - 1;
    }
	
	//key method to calculate the next update grod
	@Override
    protected Boolean compute() {
        if ((startRow-endRow) <= CUTOFF_VALUE) {
            boolean change = false;
            // we only seperate at the rows
            for (int i = endRow; i < startRow; i++) {
                for (int j =0; j < endCol; j++) {
                    // Ensure we don't go out of bounds
                    if (i > 0 && i < newGrid.grid.length - 1 && j > 0 && j < grid.grid[0].length - 1) {
                        if (newGrid.grid[i][j] >= 4) {
                            newGrid.updateGrid[i][j] = (newGrid.grid[i][j] % 4)
                                + (newGrid.grid[i - 1][j] / 4)
                                + (newGrid.grid[i + 1][j] / 4)
                                + (newGrid.grid[i][j - 1] / 4)
                                + (newGrid.grid[i][j + 1] / 4);
                        if (newGrid.grid[i][j] != newGrid.updateGrid[i][j]) {
                            change = true;
                        }
                    }
                }
                }
            }
            return change;
        } else {

            ParallelUpdate top = new ParallelUpdate(grid, endRow, (startRow+endRow)/2);
            ParallelUpdate bottom = new ParallelUpdate(grid,  (startRow+endRow)/2, startRow);
            top.fork();
            Boolean bottomChangeBoolean = bottom.compute(); // Process the bottom-right quadrant in this thread
            Boolean topChangebBoolean = top.join();

            return bottomChangeBoolean || topChangebBoolean;
        }
    }
}