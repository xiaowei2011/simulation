package simulation.group;

import simulation.SearchLight;
import simulation.bean.Neighbor;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月22日 下午5:03:16
 */
public class GBFASearchLight extends SearchLight {

	// 邻居概率阈值
	static double NBP = 0.45;

	public GBFASearchLight(double dc, int runTime) {
		super(dc, runTime);
	}

	@Override
	protected boolean discovery(Node node, int slot) {
		boolean dis = super.discovery(node, slot);
		if(!dis) {
			return false;
		}
		for(Neighbor neighbor : node.neighbors.values()) {
			if(!neighbors.containsKey(neighbor.node.id)) {
				double ln = getCommonNeighborRate(neighbor.node);
				if (ln >= NBP) {
					int dt = getNextOverlapSlot(neighbor.node, slot);
					int at = neighbor.node.getNextActiveSlot(slot);
					if (at > 0 && at < dt && !schedule[at]) {
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
		return true;
	}

	public static void main(String[] args) {
		Square square = new Square(500, 500, 10000);
		square.initNodes(SearchLight.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		square.initNodes(GBFASearchLight.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
		System.out.println("发现完成");
	}
}
