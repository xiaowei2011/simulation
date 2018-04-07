package simulation.group;

import java.util.Set;
import java.util.TreeSet;

import simulation.Disco;
import simulation.bean.Neighbor;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月27日 下午4:08:15
 */
public class OneHopDisco extends Disco {
	
	//gbfa还是group-base
	static boolean gbfa;
	
	Set<Integer> as = new TreeSet<>();

	public OneHopDisco(double dc, int runTime) {
		super(dc, runTime);
	}

	@Override
	protected boolean discovery(Node node, int slot) {
		if(!this.logicNeighbors.containsKey(node.id)) {
			Neighbor from = new Neighbor(node, slot);
			Neighbor to = new Neighbor(this, slot);
			this.logicNeighbors.put(node.id, from);
			node.logicNeighbors.put(this.id,to);
			LOGIC_NEIGHBOR_NUM += 2;
		}
		if(this.isWake(slot) && node.isWake(slot)) {
			if(!this.neighbors.containsKey(node.id)) {
				Neighbor from = this.logicNeighbors.get(node.id);
				from.discoveryTime = slot;
				this.neighbors.put(node.id, from);
				int st = node.getNextActiveSlot(slot);
				if(!gbfa && st > 0 && !this.schedule[st]) {
					TOTAL_INC_SLOT ++;
					this.as.add(st);
				}
				TOTAL_DISCOVERY_TIME += from.discoveryTime;
				NEIGHBOR_NUM ++;
			}
			OneHopDisco ond = (OneHopDisco)node;
			if(!node.neighbors.containsKey(this.id)) {
				Neighbor to = node.logicNeighbors.get(this.id);
				to.discoveryTime = slot;
				node.neighbors.put(this.id, to);
				int st = this.getNextActiveSlot(slot);
				if(!gbfa && st > 0 && !node.schedule[st]) {
					TOTAL_INC_SLOT ++;
					ond.as.add(st);
				}
				TOTAL_DISCOVERY_TIME +=  to.discoveryTime;
				NEIGHBOR_NUM ++;
			}
			if(gbfa) {
				for (Neighbor neighbor : node.neighbors.values()) {
					if (this.id != neighbor.node.id && !this.neighbors.containsKey(neighbor.node.id)) {
						Neighbor nbor = new Neighbor(neighbor.node, -1);
						nbor.discoveryTime = slot;
						this.neighbors.put(neighbor.node.id, nbor);
						TOTAL_DISCOVERY_TIME += nbor.discoveryTime;
						NEIGHBOR_NUM++;
					}
				}
				for (Neighbor neighbor : this.neighbors.values()) {
					if (node.id != neighbor.node.id && !node.neighbors.containsKey(neighbor.node.id)) {
						Neighbor nbor = new Neighbor(neighbor.node, -1);
						nbor.discoveryTime = slot;
						node.neighbors.put(neighbor.node.id, nbor);
						TOTAL_DISCOVERY_TIME += nbor.discoveryTime;
						NEIGHBOR_NUM++;
					}
				}	
			}
			AVG_DISCOVERY_TIME = TOTAL_DISCOVERY_TIME / NEIGHBOR_NUM;
//			System.out.printf("cur:%d, NEIGHBOR_NUM:%d\n",slot, NEIGHBOR_NUM);
			return true;
		}
		if(gbfa) {
			return false;
		}
		if(this.as.contains(slot) && node.isWake(slot)) {
			for (Neighbor neighbor : this.neighbors.values()) {
				if (node.id != neighbor.node.id && !node.neighbors.containsKey(neighbor.node.id)) {
					Neighbor nbor = new Neighbor(neighbor.node, -1);
					nbor.discoveryTime = slot;
					node.neighbors.put(neighbor.node.id, nbor);
					AVG_INC_SLOT_RATE++;
					TOTAL_DISCOVERY_TIME += nbor.discoveryTime;
					NEIGHBOR_NUM++;
				}
			}	
		}
		return false;
	}

	public static void main(String[] args) {
		Square square = new Square(100, 100, 1000000);
		Square.sampleInterval = 10;
		Node.radioRange = 150;
		square.initNodes(Disco.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
		
		square.initNodes(OneHopDisco.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
		System.out.printf("TOTAL_INC_SLOT:%s, AVG_INC_SLOT_RATE:%s\n", TOTAL_INC_SLOT, AVG_INC_SLOT_RATE);
		OneHopDisco.gbfa = true;
		square.initNodes(OneHopDisco.class, 200, 0.02);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBFADisco.txt");
		
		System.out.println("发现完成");
	}

}
