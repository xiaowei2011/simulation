package simulation;

import simulation.bean.Node;
import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月21日 下午5:07:22
 */
public class Birthday extends Node{
	
	public Birthday(double dc, int runTime) {
		dutyCycle = dc;
		schedule = new boolean[runTime];
		for(int i = 0; i < runTime; i++) {
			schedule[i] = random.nextDouble() < dutyCycle;
		}
	}
	
	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Square square = new Square(500, 500, 10000);
		square.initNodes(Birthday.class, 150, 0.05);
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\birthday.txt");
		System.out.println("发现完成");
	}

}
