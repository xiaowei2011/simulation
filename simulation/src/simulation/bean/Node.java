package simulation.bean;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * @autor sunweijie
 * @since 2018年3月20日 下午5:33:18
 */
public class Node implements Comparable<Node>{
	
	public int id;
	//占空比
	public double dutyCycle;
	//x坐标
	public double posX;
	//y坐标
	public double posY;
	//移动速度
	public double speed;
	public double speedX;
	public double speedY;
	//移动目标
	public double targetPosX;
	public double targetPosY;
	//当前时间
	public int cur;
	public boolean[] schedule;
	//邻居表
	public Map<Integer, Neighbor> neighbors = new HashMap<>();
	//逻辑邻居（在自己的通信范围内）
	public Map<Integer, Neighbor> logicNeighbors = new HashMap<>();
	//全局变量
	//各方向上的通信距离
	public Map<Double, Double> randioDistance = new HashMap<>();
	//广播距离
	public static double radioRange = 50;
	//通信范围不规则度
	public static double DOI;
	//统计变量
	//累计发现率
	public double CDF;
	
	//主动苏醒时隙数
	public long INC_SLOT;
	
	public static Map<Integer, Integer> activeSlot = new TreeMap<>();
	
	public static int count = 0;
	//平均累计发现率
	public static double AVG_CDF;
	//逻辑邻数
	public static long LOGIC_NEIGHBOR_NUM;
	//邻居数
	public static long NEIGHBOR_NUM;
	//总的邻居发现时间
	public static double TOTAL_DISCOVERY_TIME;
	//平均发现时间
	public static double AVG_DISCOVERY_TIME;
	
	public static double TOTAL_INC_SLOT;
	
	public static double AVG_INC_SLOT;
	
	public static double AVG_INC_SLOT_RATE;
	
	public static Random random = new Random();
	
	public Node() {
		count++;
		id = count;
		for(double i = 1; i <= 4.5; i += 0.5) {
			double x = DOI * radioRange / (2 - DOI);
			double l = radioRange - x  +  2 * x * random.nextDouble();
			randioDistance.put(i, l * l);
		}
	}
	
	public boolean isWake() {
		return isWake(cur);
	}
	
	public boolean isWake(int slot) {
		return schedule[slot];
	} 
	
	protected boolean discovery(Node node, int slot) {
		if(this.isWithinRadioRange(node) && node.isWithinRadioRange(this)) {
			if(!this.logicNeighbors.containsKey(node.id)) {
				Neighbor from = new Neighbor(node, slot);
				Neighbor to = new Neighbor(this, slot);
				this.logicNeighbors.put(node.id, from);
				node.logicNeighbors.put(this.id,to);
				LOGIC_NEIGHBOR_NUM++;
			}
			if(this.isWake(slot) && node.isWake(slot)) {
				if(!this.neighbors.containsKey(node.id)) {
					Neighbor from = this.logicNeighbors.get(node.id);
					Neighbor to = node.logicNeighbors.get(this.id);
					from.discoveryTime = to.discoveryTime = slot - from.meetTime;
					this.neighbors.put(node.id, from);
					node.neighbors.put(this.id, to);
					TOTAL_DISCOVERY_TIME += from.discoveryTime;
					NEIGHBOR_NUM++;
					AVG_DISCOVERY_TIME = TOTAL_DISCOVERY_TIME / NEIGHBOR_NUM;
				}
				return true;
			}
		}else {
			if(this.logicNeighbors.containsKey(node.id)) {
				this.logicNeighbors.remove(node.id);
				node.logicNeighbors.remove(this.id);
			}
			if(this.neighbors.containsKey(node.id)) {
				this.neighbors.remove(node.id);
				node.neighbors.remove(this.id);
			}
		}
		return false;
	}
	//初始化统计数据
	public static void init() {
		count = 0;
		AVG_CDF = 0;
		LOGIC_NEIGHBOR_NUM = 0;
		NEIGHBOR_NUM = 0;
		TOTAL_DISCOVERY_TIME = 0;
		AVG_DISCOVERY_TIME = 0;
		TOTAL_INC_SLOT = 0;
		AVG_INC_SLOT = 0;
		AVG_INC_SLOT_RATE = 0;
		activeSlot.clear();
	}
	
	//计算两节点是否在各自通信范围内
	boolean isWithinRadioDistance(Node node) {
		return Math.pow(node.posX - this.posX, 2) + Math.pow(node.posY - this.posY, 2) <= radioRange * radioRange;
	}
	
	//计算两节点是否在各自通信范围内
	public boolean isWithinRadioRange(Node node) {
		double dx = node.posX - this.posX;
		double dy = node.posY - this.posY;
		double d = dx * dx + dy * dy;
		double orientation;
		if(dx == 0) {
			orientation = dy >= 0 ? 1.5 : 3.5;
		}else if(dx > 0){
			if(dy > 0) {
				orientation = dy / dx > 1 ? 1.5 : 1;
			}else {
				orientation = dy / dx > -1 ? 4 : 4.5;
			}
		}else{
			if(dy > 0) {
				orientation = dy / dx > -1 ? 2.5 : 2;
			}else {
				orientation = dy / dx > 1 ? 3.5 : 3;
			}
		}
		return d <= randioDistance.get(orientation);
	}
	//计算公共邻居概率
	public double getCommonNeighborRate(Node node) {
		if(this.neighbors.size() == 0 || node.neighbors.size() == 0) {
			return 0;
		}
		int count = 1;
		for(Neighbor neighbor : node.neighbors.values()) {
			count += this.neighbors.containsKey(neighbor.node.id) ? 1 : 0;
		}
		return (double)count / this.neighbors.size(); 
	}
	
	//计算下一个活跃时隙
	public int getNextActiveSlot(int slot) {
		slot++;
		while(slot < schedule.length) {
			if(schedule[slot]) {
				return slot;
			}
			slot++;
		}
		return -1;
	}
	//计算一个重叠时隙
	public int getNextOverlapSlot(Node node, int slot) {
		slot++;
		while(slot < schedule.length) {
			if(schedule[slot] && node.schedule[slot]) {
				return slot;
			}
			slot++;
		}
		return -1;
	}
	
	protected static int findPrime(double dc) {
		int d = (int) Math.round(1 / dc);
		if(isPrime(d)) {
			return d;
		}
		int i = 1;
		while(d > i) {
			int l = d - i;
			int r = d + i;
			boolean b1 = isPrime(l);
			boolean b2 = isPrime(r);
			if(b1 && b2) {
				return dc - l < r - dc ? l : r;
			}else if(b1) {
				return l;
			}else if(b2) {
				return r;
			}
			i++;
		}
		return 0;
	}
	
	public static boolean isPrime(int n) {
		if (n < 2) {
			return false;
		}
		if (n == 2) {
			return true;
		}
		if (n % 2 == 0) {
			return false;
		}
		for (int i = 3; i * i <= n; i += 2) {
			if (n % i == 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public int compareTo(Node node) {
		return Integer.compare(this.id, node.id);
	}
	
}
