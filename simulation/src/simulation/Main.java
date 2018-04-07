package simulation;

import simulation.bean.Square;

/**
 * @autor sunweijie
 * @since 2018年3月22日 上午9:56:32
 */
public class Main {

	public static void main(String[] args) {
		Square square = new Square(500, 500, 20000);
//		square.initNodes(Birthday.class, 200, 0.02);
//		System.out.println("Birthday");
//		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\birthday.txt");
		square.initNodes(Disco.class, 200, 0.02);
		System.out.println("Disco");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\disco.txt");
//		square.initNodes(Uconnect.class, 200, 0.02);
//		System.out.println("Uconnect");
//		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\uconnect.txt");
		square.initNodes(SearchLight.class, 200, 0.02);
		System.out.println("SearchLight");
		square.run("C:\\Users\\Administrator\\Desktop\\论文\\数据\\searchLight.txt");
		
	}

}
