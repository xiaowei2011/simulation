package simulation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月20日 下午7:28:27
 */
public class Disco extends Node{
	//周期限素数
	int p1;
	int p2;
	//偏移量
	int offset;
	
	//初始时刻表（偏移量为0）
	static Map<Double, boolean[]> sches = new HashMap<>();
	
	public Disco() {}
	
	public Disco(double dc, int runTime) {
		int[] p = findPrimePair(dc);
		p1 = p[0];
		p2 = p[1];
		offset = random.nextInt(p1 * p2);
		boolean[] sche = sches.get(dc);
		if(sche == null) {
			sche = new boolean[runTime + p1 * p2];
			for(int i = 0; i < sche.length; i++) {
				if(i % p1 == 0 || i % p2 == 0) {
					sche[i] = true;
				}
			}
			sches.put(dc, sche);
		}
		schedule = Arrays.copyOfRange(sche, offset, runTime + offset);
	}
	
	public Disco(double dc, int runTime, int offset) {
		int[] p = findPrimePair(dc);
		p1 = p[0];
		p2 = p[1];
		this.offset = offset;
		boolean[] sche = sches.get(dc);
		if(sche == null) {
			sche = new boolean[runTime + p1 * p2];
			for(int i = 0; i < sche.length; i++) {
				if(i % p1 == 0 || i % p2 == 0) {
					sche[i] = true;
				}
			}
			sches.put(dc, sche);
		}
		schedule = Arrays.copyOfRange(sche, offset, runTime + offset);
	}
	
	public static int[] findPrimePair(double dc) {
		int[] p = new int[2];
		if(dc == 0.001) {
			p[0] = 1997;
			p[1] = 2003;
		}else if(dc == 0.002) {
			p[0] = 991;
			p[1] = 1009;
		}else if(dc == 0.004) {
			p[0] = 491;
			p[1] = 509;
		}else if(dc == 0.005) {
			p[0] = 397;
			p[1] = 401;
		}else if(dc == 0.01) {
			p[0] = 191;
			p[1] = 211;
		}else if(dc == 0.02) {
			p[0] = 97;
			p[1] = 103;
		}else if(dc == 0.03) {
			p[0] = 61;
			p[1] = 73;
		}else if(dc == 0.04) {
			p[0] = 47;
			p[1] = 53;
		}else if(dc == 0.05) {
			p[0] = 37;
			p[1] = 43;
		}else if(dc == 0.06) {
			p[0] = 29;
			p[1] = 37;
		}else if(dc == 0.08) {
			p[0] = 23;
			p[1] = 39;
		}else if(dc == 0.1) {
			p[0] = 17;
			p[1] = 23;
		}else if(dc == 0.2) {
			p[0] = 7;
			p[1] = 13;
		}else if(dc == 0.5) {
			p[0] = 3;
			p[1] = 5;
		}
		return p;
	}
	
	public static void main(String[] args) {
		Square square = new Square(500, 500, 10000);
		Square.dynamic = true;
		square.initNodes(Disco.class, 200, 0.05);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		DOI = 1;
		square.initNodes(Disco.class, 200, 0.05);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		System.out.println("发现完成");
	}
}
