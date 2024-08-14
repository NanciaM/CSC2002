package parallelVersion;

import java.util.concurrent.RecursiveTask;

class ParallelUpdate extends RecursiveTask<Boolean> {
    private static final int CUTOFF_VALUE = 300 ; // Adjust based on grid size
    private Grid newGrid;
    private int startRow,endRow;
    private int endColumn;

    public ParallelUpdate(Grid grid, int t, int b) {
        this.newGrid = grid;
        this.endRow=t;
        this.startRow=b;
        this.endColumn = newGrid.grid[0].length - 1;
    }

    @Override
    protected Boolean compute() {
        if ((startRow-endRow) <= CUTOFF_VALUE) {
            boolean change = false;
            // we only seperate at the rows
            for (int i = endRow; i < startRow; i++) {
                for (int j =0; j < endColumn; j++) {
                    // Ensure we don't go out of bounds
                    if (i > 0 && i < newGrid.grid.length - 1 && j > 0 && j < newGrid.grid[0].length - 1) {
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
            return change;
        } else {

            ParallelUpdate top = new ParallelUpdate(newGrid, endRow, (startRow+endRow)/2);
            ParallelUpdate bottom = new ParallelUpdate(newGrid, (startRow+endRow)/2, startRow);

            top.fork();
            Boolean bottomhalf = bottom.compute(); // Process the bottom-right quadrant in this thread
            Boolean tophalf = top.join();

            return bottomhalf || tophalf;
        }
    }
}
