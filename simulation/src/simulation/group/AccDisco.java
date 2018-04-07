package simulation.group;

import simulation.Disco;
import simulation.bean.Neighbor;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月27日 下午4:08:15
 */
public class AccDisco extends Disco {
	
	//主动苏醒间隔
	int interval;
	
	public int[] active;

	public AccDisco(double dc, int runTime) {
		super(dc, runTime);
		interval = (int)(1 / dc);
		active = new int[runTime / interval + 1];
		int first = interval - 1;
		while(schedule[first] && first > 0) {
			first--;
		}
		schedule[first] = true;
		active[0] = first;
	}

	@Override
	protected boolean discovery(Node node, int slot) {
		if (!this.logicNeighbors.containsKey(node.id)) {
			Neighbor from = new Neighbor(node, slot);
			Neighbor to = new Neighbor(this, slot);
			this.logicNeighbors.put(node.id, from);
			node.logicNeighbors.put(this.id, to);
			LOGIC_NEIGHBOR_NUM += 2;
		}
		if (this.isWake(slot) && node.isWake(slot)) {
			addNeighbor(this, node, slot);
			addNeighbor(node, this, slot);
			//合并邻居表
			mergeNeighbor(this, node, slot);
			mergeNeighbor(node, this, slot);
			
			insertSlot(this, slot);
			insertSlot((AccDisco)node, slot);
			AVG_DISCOVERY_TIME = TOTAL_DISCOVERY_TIME / NEIGHBOR_NUM;
			return true;
		}
		return false;
	}
	
	void insertSlot(AccDisco node, int slot) {
		int st = calculateNextActiveSlot(node, slot);
		int id = slot / interval + 1;
		if(node.active[id] != 0 && node.active[id] != st) {
			node.schedule[id] = false;
			node.active[id] = st;
			node.schedule[id] = true;
		}
	}
	
	//计算下一个主动苏醒位置
	int calculateNextActiveSlot(AccDisco node, int slot) {
		int start = (slot / interval + 1) * interval;
		double[] v = new double[interval];
		int mi = 0;
		for(int i = start; i < start + interval && i < schedule.length; i++) {
			if(node.isWake(i)) {
				continue;
			}
			for(Neighbor neighbor : node.neighbors.values()) {
				if(neighbor.node.isWake(i)) {
					double a = node.getCommonNeighborRate(neighbor.node);
					double b = node.getTemporalDiversity(this, node, start - interval, i);
					v[i - start] = a + b;
				}
			}
			if(v[i - start] > v[mi]) {
				mi = i - start;
			}
		}
		return mi;
	}
	
	//获取时间差异性
	double getTemporalDiversity(Node node1, Node node2, int start, int end) {
		double count = 0;
		for(int i = start; i < end && i < node1.schedule.length; i++) {
			if(!node1.isWake(i) && node2.isWake(i)) {
				count++;
			}
		}
		return count / interval;
	}
	
	void addNeighbor(Node node1, Node node2, int slot) {
		if (!node1.neighbors.containsKey(node2.id)) {
			Neighbor nbor = node1.logicNeighbors.get(node2.id);
			nbor.discoveryTime = slot;
			node1.neighbors.put(node2.id, nbor);
			TOTAL_DISCOVERY_TIME += nbor.discoveryTime;
			NEIGHBOR_NUM++;
		}
	}
	
	void mergeNeighbor(Node node1, Node node2, int slot) {
		for (Neighbor neighbor : node2.neighbors.values()) {
			if (node1.id != neighbor.node.id && !node1.neighbors.containsKey(neighbor.node.id)) {
				Neighbor nbor = new Neighbor(neighbor.node, -1);
				nbor.discoveryTime = slot;
				node1.neighbors.put(neighbor.node.id, nbor);
				TOTAL_DISCOVERY_TIME += nbor.discoveryTime;
				NEIGHBOR_NUM++;
			}
		}
	}

	public static void main(String[] args) {
		Square square = new Square(100, 100, 1000000);
		Square.sampleInterval = 10;
		Node.radioRange = 150;
		square.initNodes(Disco.class, 200, 0.01);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		square.initNodes(AccDisco.class, 200, 0.005);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
		System.out.println("发现完成");
	}

}
