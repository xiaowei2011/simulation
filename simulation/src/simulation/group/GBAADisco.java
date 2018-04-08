package simulation.group;

import simulation.Disco;
import simulation.bean.Node;

/**
 * @autor sunweijie
 * @since 2018年4月8日 下午10:58:01
 */
public class GBAADisco extends Disco{
	
	//组中心节点
	GBAADisco groupCenter;
	//主动属性时隙偏移
	int activeOffset;
	//主动苏醒时隙周期
	int activePrime;
	
	public GBAADisco(double dc, int runTime) {
		super(dc, runTime);
	}
	
	public GBAADisco(double dc, int runTime, int activePrime) {
		this(dc, runTime);
		this.activePrime = activePrime;
	}
	
	@Override
	protected boolean discovery(Node node, int slot) {
		boolean dis = super.discovery(node, slot);
		if(!dis) {
			return false;
		}
		
		return true;
	}

	public static void main(String[] args) {

	}

}
