package simulation.group;

import simulation.Disco;
import simulation.bean.Neighbor;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月22日 下午1:31:06
 */
public class GroupBaseDisco extends Disco{
	
	//邻居概率阈值
	static double NBP = 0.8;
	
	public GroupBaseDisco(double dc, int runTime) {
		super(dc, runTime);
	}
	
	public GroupBaseDisco(double dc, int runTime, int offset) {
		super(dc, runTime, offset);
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
					//设置主动苏醒时隙
					double l = getCommonNeighborRate(node);
					for(Neighbor neighbor : node.neighbors.values()) {
						if(!neighbors.containsKey(neighbor.node.id)) {
							double ln = node.getCommonNeighborRate(neighbor.node);
							if(getNeighborProbability(l, ln) <= NBP) {
								int dt = getNextOverlapSlot(neighbor.node, slot);
								int nt = getNextActiveSlot(slot);
								if(nt > 0) {	
									int at = neighbor.node.getNextActiveSlot(nt);
									if(at > 0 && at < dt && !schedule[at]) {
										schedule[at] = true;
										if(activeSlot.containsKey(at)) {
											activeSlot.put(at, activeSlot.get(at) + 1);
										}else {
											activeSlot.put(at, 1);
										}
										INC_SLOT++;
										TOTAL_INC_SLOT++;
									}
								}
							}	
						}
					}
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
	
	//计算是邻居概率
	double getNeighborProbability(double l1, double l2) {
		return Math.acos((l1 * l1 + l2 * l2 - 1) / (2 * l1 * l2)) / Math.PI;
	}
	
	public static void main(String[] args) {
		Square square = new Square(500, 500, 10000);
		Square.dynamic = true;
		square.initNodes(Disco.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		square.initNodes(GroupBaseDisco.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\groupBaseDisco.txt");
		System.out.println("发现完成");
	}
}
