package simulation;

import java.util.Arrays;

import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月21日 下午6:44:57
 */
public class SearchLight extends Node{
	
	//周期
	int t;
	//偏移量
	int offset;
	
	static boolean[] sche;
	
	public SearchLight(double dc, int runTime) {
		t = (int) Math.round(1 / dc) * 2;
		int T = t * (t / 2);
		offset = random.nextInt(T);
		if(sche == null) {
			sche = new boolean[runTime + T];
			for(int i = 0; i < sche.length; i++) {
				if(i % t == 0 || (i % T) / t + 1 == (i % T) % t) {
					sche[i] = true;
				}
			}
		}
		schedule = Arrays.copyOfRange(sche, offset, runTime + offset);
	}
	
	public static void main(String[] args) {
		Square square = new Square(500, 500, 10000);
		square.initNodes(SearchLight.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\searchLight.txt");
		System.out.println("发现完成");
	}

}
