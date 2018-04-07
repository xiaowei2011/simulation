package simulation.group;

import simulation.Disco;
import simulation.SearchLight;
import simulation.bean.Neighbor;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月22日 下午1:31:06
 */
public class GroupBaseSearchLight extends SearchLight{
	
	//邻居概率阈值
	static double NBP = 1.8;
	
	public GroupBaseSearchLight(double dc, int runTime) {
		super(dc, runTime);
	}
	
	protected boolean discovery(Node node, int slot) {
		boolean dis = super.discovery(node, slot);
		if(!dis) {
			return false;
		}
		double l = getCommonNeighborRate(node);
		for(Neighbor neighbor : node.neighbors.values()) {
			if(!neighbors.containsKey(neighbor.node.id)) {
				double ln = node.getCommonNeighborRate(neighbor.node);
				if(getNeighborProbability(l, ln) <= NBP) {
					int dt = getNextOverlapSlot(neighbor.node, slot);
					int nt = getNextActiveSlot(slot);
					if(nt != -1) {	
						int at = neighbor.node.getNextActiveSlot(nt);
						if(at != -1 && at < dt && !schedule[at]) {
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
		return true;	
	}
	
	//计算是邻居概率
	double getNeighborProbability(double l1, double l2) {
		return Math.acos((l1 * l1 + l2 * l2 - 1) / (2 * l1 * l2)) / Math.PI;
	}
	
	public static void main(String[] args) {
		Square square = new Square(500, 500, 10000);
		square.initNodes(Disco.class, 150, 0.05);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		square.initNodes(GroupBaseSearchLight.class, 150, 0.05);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\groupBaseDisco.txt");
		System.out.println("发现完成");
	}
}
