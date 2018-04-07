package simulation;

import java.util.Arrays;

import simulation.bean.Const;
import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月21日 下午10:29:02
 */
public class Uconnect extends Node {

	// 周期
	int p;
	// 偏移量
	int offset;

	static boolean[] sche;

	public Uconnect(double dc, int runTime) {
		p = findPrime(dc);
		int T = p * p;
		offset = random.nextInt(T);
		if (sche == null) {
			sche = new boolean[runTime + T];
			for (int i = 0; i < sche.length; i++) {
				if (i % p == 0 || i % T <= (p + 1) / 2) {
					sche[i] = true;
				}
			}
		}
		schedule = Arrays.copyOfRange(sche, offset, runTime + offset);
	}
	
	protected int findPrime(double dc){
		double diff = 0;
		for (int i=0; i< Const.PRIME.length; i++)
		{
			double dctmp = (3*Const.PRIME[i] - 1) / 2.0 / Const.PRIME[i] / Const.PRIME[i];
			if (dctmp == dc)
				return Const.PRIME[i];
			else if (dctmp > dc)
				diff = dctmp - dc;
			else
			{
				if (i == 0)
					return Const.PRIME[0];
				else if	(dc - dctmp > diff)
					return Const.PRIME[i-1];
				else
					return Const.PRIME[i];
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		Square square = new Square(500, 500, 10000);
		square.initNodes(Uconnect.class, 150, 0.05);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\uconnect.txt");
		System.out.println("发现完成");
	}

}
