import java.io.*;
import java.util.*;
class Advent17 {
	static SortList sortXs = new SortList(0);
	static SortList sortYs = new SortList(1);
	public static tileType[][] grid;
	static boolean print = false;

	static tileType[][] makeGrid(ArrayList<int[]> claySquares){
		Collections.sort(claySquares,sortYs);
		int lowestY = claySquares.get(0)[1];
		Collections.reverse(claySquares);
		int highestY = claySquares.get(0)[1];
		Collections.sort(claySquares,sortXs);
		int lowestX = claySquares.get(0)[0];
		Collections.reverse(claySquares);
		int highestX = claySquares.get(0)[0];
		Collections.sort(claySquares,sortYs);
		tileType[][] grid = new tileType[(highestY - lowestY) + 1][1 + (highestX - lowestX)];
		for(int y = 0; y <= (highestY - lowestY); y++){
			HashSet<Integer> zz = new HashSet<>();
			for(int i = 0; i < claySquares.size(); i++){
				if(claySquares.get(i)[1] < (y + lowestY)) continue;
				if(claySquares.get(i)[1] > (y + lowestY)) break;
				zz.add(claySquares.get(i)[0]);
			}
			if(!zz.isEmpty()){
				ArrayList<Integer> xs = new ArrayList<>(zz);
				Collections.sort(xs);
				int next = xs.remove(0) - lowestX;
				for(int x = 0; x <= (highestX - lowestX); x++){
					if(y == 0 && x == 500 - lowestX){
						grid[y][x] = tileType.SPRING;
					}else{
						if(next < x || next > x){
							grid[y][x] = tileType.DRY_SAND;
						}else if(next == x){
							grid[y][x] = tileType.CLAY;
							if(!xs.isEmpty()) next = xs.remove(0) - lowestX;
						}
					}
				}
			}else{
				for(int x = 0; x <= (highestX - lowestX); x++){
					if(y == 0 && x == 500 - lowestX){
						grid[y][x] = tileType.SPRING;
					}else{
						grid[y][x] = tileType.DRY_SAND;
					}
				}
			}
		}
		tileType[][] biggerGrid = new tileType[grid.length][grid[0].length + 2];
		for(int y = 0; y < grid.length; y++){
			for(int x = 0; x < biggerGrid[y].length; x++){
				if(x == 0 || x == biggerGrid[y].length - 1){
					biggerGrid[y][x] = tileType.DRY_SAND;
					continue;
				}
				biggerGrid[y][x] = grid[y][x - 1];
			}
		}
		return biggerGrid;
	}

	public static void main(String[] args) {
		ArrayList<Map.Entry<Integer,ArrayList<Integer>>> ysFirst = new ArrayList<>();
		ArrayList<Map.Entry<Integer,ArrayList<Integer>>> xsFirst = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new FileReader("input.txt"));
			String line;
			while((line = br.readLine()) != null){
				HashMap<Integer,ArrayList<Integer>> bucket = new HashMap<>();
				String[] chunks = line.split(", ");
				int first = Integer.parseInt(chunks[0].substring(2));
				bucket.put(first,new ArrayList<Integer>());
				int lowSecond = Integer.parseInt(chunks[1].substring(2,chunks[1].indexOf(".")));
				int highSecond = Integer.parseInt(chunks[1].substring(chunks[1].lastIndexOf(".") + 1));
				for(int i = lowSecond; i <= highSecond; i++){
					bucket.get(first).add(i);
				}
				for(Map.Entry<Integer,ArrayList<Integer>> me : bucket.entrySet()){
					if(line.charAt(0) == 'y'){
						ysFirst.add(me);
					}else{
						xsFirst.add(me);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace(System.out);
		}
		ArrayList<int[]> claySquares = new ArrayList<>();
		for(Map.Entry<Integer,ArrayList<Integer>> me : xsFirst){
			for(int i = 0; i < me.getValue().size(); i++){
				claySquares.add(new int[]{ me.getKey(),me.getValue().get(i) });
			}
		}
		for(Map.Entry<Integer,ArrayList<Integer>> me : ysFirst){
			for(int i = 0; i < me.getValue().size(); i++){
				claySquares.add(new int[]{ me.getValue().get(i),me.getKey() });
			}
		}
		int j = -1;
		grid = makeGrid(claySquares);
		for(int i = 0; i < grid[0].length; i++){
			if(grid[0][i] == tileType.SPRING){
				j = i;
				break;
			}
		}
		new Paintbrush(j,1,2,null,0).start();
		int wetTiles = 0;
		for(tileType[] T : grid){
			for(tileType t : T){
				if(t == tileType.WET_SAND || t == tileType.WATER) wetTiles++;
			}
		}
		System.out.println(wetTiles); // 29801
	}
}
