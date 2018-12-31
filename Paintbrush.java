import java.util.*;
class Paintbrush{
	private tileType[][] grid = Advent17.grid;
	private int x;
	private int y;
	private int direction = 1;
	private Paintbrush mother;
	private Paintbrush[] daughters = new Paintbrush[2];
	private boolean sisterBounced = false,ready = false;
	private Paintbrush sister;
	private orientations H = orientations.H;
	private orientations V = orientations.V;
	private orientations orientation;
	private boolean terminated = false;
	private enum orientations { H,V };
	public Paintbrush(int x, int y, int direction, Paintbrush mother){
		grid[y][x] = tileType.WET_SAND;
		this.mother = mother;
		this.x = x;
		this.y = y;
		if(direction % 2 == 0){
			orientation = V;
			if(direction == 0){
				this.direction = -1;
			}
		}else{
			orientation = H;
			if(direction == 3){
				this.direction = -1;
			}
		}
	}

	public void start(){
		if(terminated) return;
		continueUntilStopped();
	}

	private static void bond(Paintbrush[] daughters){
		if(daughters[0] != null){
			daughters[0].sister = daughters[1];
		}else{
			daughters[1].sisterBounced = true;
		}
		if(daughters[1] != null){
			daughters[1].sister = daughters[0];
		}else{
			daughters[0].sisterBounced = true;
		}
	}

	private void continueUntilStopped(){
		if(terminated) return;
		if(orientation == H){
			moveHorizontallyUntilStopped();
		}else{
			fallUntilStopped();
		}
	}

	private void moveHorizontallyUntilStopped(){
		if(terminated) return;
		outFlow();
	}

	private void outFlow(){				// moving away from mother
		if(terminated) return;
		for(; y + 1 < grid.length && (grid[y + 1][x] != tileType.DRY_SAND && grid[y + 1][x] != tileType.WET_SAND) && (x + direction >= 0 && x + direction < grid[y].length && (grid[y][x + direction] == tileType.DRY_SAND)); x += direction){
			if(x < 0 || x > grid[y].length) break;
			grid[y][x + direction] = tileType.WET_SAND;
		}
		if(y + 1 < grid.length && grid[y + 1][x] == tileType.DRY_SAND){
			turn();
			fallUntilStopped();
		}else if((x + direction >= 0 && x + direction < grid[y].length) && grid[y][x + direction] == tileType.CLAY){
			if(sister != null) sister.didBounce();
			ready = true;
			if(sisterBounced) inFlow();
		}
	}

	private void didBounce(){
		if(terminated) return;
		sisterBounced = true;
		if(ready) inFlow();
	}

	private void inFlow(){				// moving towards mother
		if(terminated || !sisterBounced) return;
		reverse();
		for(; x != mother.x; x += direction){
			grid[y][x + direction] = tileType.WATER;
		}
	}

	private void fallUntilStopped(){
		if(terminated) return;
		for(; y + direction < grid.length && grid[y + direction][x] == tileType.DRY_SAND; y += direction){
			grid[y + direction][x] = tileType.WET_SAND;
		}
		if(y + direction < grid.length){
			if((y + direction < grid.length) && grid[y + direction][x] != tileType.WET_SAND) spawn();
			for(Paintbrush p : daughters) if(p != null) p.start();
			if(checkClimb()) climbOneTile();
		}
	}

	private boolean checkClimb(){
		for(int i = x - 1; i < x + 2; i+= 2){
			if(!(grid[y][i] == tileType.CLAY || grid[y][i] == tileType.WATER)) return false;
		}
		return true;
	}

	private void climbOneTile(){
		if(terminated) return;
		direction = -1;
		for(int i = 0; i < 2; i++) daughters[i] = null;
		if(y + direction >= 0 && y + direction < grid.length){
			y += direction;
			spawn();
			if(mother != null && y == mother.y){
				int i = 0;
				for(; i < 2; i++){
					if(daughters[i] != null) break;
				}
				sister.sister = daughters[i];
				daughters[i].sister = sister;
				daughters[i].mother = mother;
				if(sister.ready) daughters[i].didBounce();
				terminated = true;
			}
			for(Paintbrush p : daughters) if(p != null) p.start();
		}
		if(checkClimb()) climbOneTile();
	}

	private void spawn(){
		if(terminated) return;
		int index = 0;
		for(int i = x - 1; i < x + 2; i += 2){
			if(i < 0 || i > grid[y].length || grid[y][i] != tileType.DRY_SAND) continue;
			if(print) Advent17.println("I am giving birth!");
			daughters[index] = new Paintbrush(i,y,((i < x) ? 3 : 1),this);
			index++;
		}
		bond(daughters);
	}

	private void reverse(){
		if(terminated) return;
		direction *= -1;
		grid[y][x] = (grid[y][x] == tileType.DRY_SAND) ? tileType.WET_SAND : tileType.WATER;
	}

	private void turn(){
		if(terminated) return;
		orientation = V;
		direction = 1;
	}
}
