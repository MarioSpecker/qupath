package qupath.lib.gui.commands.Mario.interfa;

public interface Filter {
	
	public void filterOperation(int width, int height, int sizeBorder, int gridSize, int[] argb, int[] updArray);

}
