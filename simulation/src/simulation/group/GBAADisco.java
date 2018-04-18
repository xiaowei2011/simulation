package simulation.group;

import simulation.Disco;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年4月8日 下午10:58:01
 */
public class GBAADisco extends Disco{
	
	//组中心节点
	GBAADisco groupCenter;
	//预加入组中心
	GBAADisco preGroupCenter;
	//主动属性时隙偏移
	int activeOffset;
	//主动苏醒时隙周期
	int activePrime;
	//额外能量预算率
	//组外节点0，预入组1，加入组2
	int status = 0;
	static double ENBR = 1;
	
	public GBAADisco(double dc, int runTime) {
		this(dc,runTime, findPrime(dc * ENBR));
	}
	
	public GBAADisco(double dc, int runTime, int activePrime) {
		super(dc, runTime);
		this.activePrime = activePrime;
//		activeOffset =  getNextActiveSlot(-1) % activePrime;
		activeOffset =  random.nextInt(activePrime);
	}
	
	@Override
	public boolean isWake(int slot) {
		return schedule[slot] || slot % activePrime == activeOffset;
	}
	
	@Override
	protected boolean discovery(Node node, int slot) {
		boolean dis = super.discovery(node, slot);
		GBAADisco gbaaNode = (GBAADisco) node;
		if(!dis) {
			if(slot % activePrime == activeOffset && status == 2 && groupCenter == gbaaNode) {
				 departGroup();
			}
			if(slot % gbaaNode.activePrime == gbaaNode.activeOffset && gbaaNode.status == 2 && gbaaNode.groupCenter == this) {
				gbaaNode.departGroup();
			}
			return false;
		}
		if(status == 0) {
			if(gbaaNode.status == 0) {
				GBAADisco center = neighbors.size() >= gbaaNode.neighbors.size() ? this : gbaaNode;
				 adjustActiveSlot(center);
				 joinGroup(center);
				 gbaaNode.adjustActiveSlot(center);
				 gbaaNode.joinGroup(center);
			}else if(gbaaNode.status == 2){
				adjustActiveSlot(gbaaNode.groupCenter);
				if(gbaaNode.groupCenter == gbaaNode) {
					joinGroup(gbaaNode.groupCenter);
				}
			}
		}else if(status == 1){
			if(gbaaNode.status == 2) {
				if(groupCenter == gbaaNode) {
					joinGroup(gbaaNode);			
				}else if(preGroupCenter.neighbors.size() < gbaaNode.groupCenter.neighbors.size()){
					adjustActiveSlot(gbaaNode.groupCenter);
				}
			}
		}else {
			if(gbaaNode.status == 0) {
				gbaaNode.adjustActiveSlot(groupCenter);
				if(groupCenter == gbaaNode) {
					joinGroup(gbaaNode.groupCenter);
				}
			}else if(gbaaNode.status == 1 && gbaaNode.preGroupCenter == this) {
				gbaaNode.joinGroup(this);
			}else if(gbaaNode.status == 2 && groupCenter != gbaaNode.groupCenter) {
				if(groupCenter.neighbors.size() < gbaaNode.groupCenter.neighbors.size()) {
					adjustActiveSlot(gbaaNode.groupCenter);
				}else {
					gbaaNode.adjustActiveSlot(groupCenter);
				}
			}
		}
		return true;
	}
	
	void departGroup() {
		preGroupCenter = groupCenter = null;
//		activeOffset =  getNextActiveSlot(-1) % activePrime;
		activeOffset =  random.nextInt(activePrime);
		status = 0;
	}
	
	void joinGroup(GBAADisco groupCenter) {
		this.groupCenter = groupCenter;
		status = 2;
		preGroupCenter = null;
	}
	
	//根据中心节点调整主动苏醒时隙
	void adjustActiveSlot(GBAADisco preGroupCenter){
		this.preGroupCenter = preGroupCenter;
		activeOffset = preGroupCenter.activeOffset;
		activePrime = preGroupCenter.activePrime;
		status = 1;
	}
	public static void main(String[] args) {
		Square square = new Square(500, 500, 50000);
		int nodeNum = 200;
		double dc = 0.01;
		Square.dynamic = true;
		Square.maxSpeed = Square.minSpeed = 0.02;
		System.out.println("Disco");
		square.initNodes(Disco.class, nodeNum, 2 * dc); 
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
//		System.out.println("GroupBase");
//		GroupBaseDisco.NBP = 0.6;
//		square.initNodes(GroupBaseDisco.class, nodeNum, dc);
//		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GroupBaseDisco.txt");
		System.out.println("GBAA");
//		GBAADisco.ENBR = 0.1;
		square.initNodes(GBAADisco.class, nodeNum, dc);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\GBAADisco.txt");
		System.out.println("...............................................");
		System.out.println("发现完成");
	}

}
