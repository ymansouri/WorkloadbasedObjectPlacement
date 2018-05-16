package workload;




// This class calculates delay between data centers
public class delayCalculation {
	/*
	 CITY:"1: Ireland, 2:Netherlands, 3:Singapore, 4:Sao palo, 5:HongKong, 6:Tokyo, 7.Sydney, 8.Oregan, 9:California", 10: Chile, 11:Taiwan, 12:Virginia, 13: Frankfort, 14. Finland
	 */
	static int [][] distance ={  
		             {0,    755,    9391,   11206,    9835,  9587,   17214,  7500,  8314,  11465,   6257,  5529,  1086,   2030},
			         {755,  0,      10499,  9804,     9262,  9290,   16641,  8057,  8927,  7464 ,   9545,  6280,  363,    1510 },
			         {9391, 10499,  0,      15985,    2585,  5321,   6298,   13114, 14166, 10186,   3007,  15771, 10625,  9267},
			         {11206, 9804,  15985,  0    ,    18042, 18531,  13356,  10820, 9947,  1606,    18669, 7389,  9829,   11315},
			         {9835,  9262,  2585,   18042,    0    , 2888,   7372,   10565, 8855,  11610,   650,   8296,  9141,   7809},
			         {9587,  9290,  5321,   18531,    2888,  0,      7824,   7808,  8755,  10705,   1454,  11142, 9333,   7810},
			         {17214, 16641, 6298,   13326,    7372,  7824,   0,      12334, 12084, 7050,    7144,  15762, 16481,  15191},
			         {7500,  8057,  13114,  10820,    10565, 7808,   12334,    0,    1193, 6349,    10107, 3956,  8413,   7918},
			         {8314,  8927,  14166,  9947  ,   8855,  8755,   12084,  1193,  0,     8999,    11148, 3794,  9290,   8994},
			         {11465, 7464,  10186,  1606,     11610, 10705,  7050,   6349,  8999,    0,     18458, 7838,  12109,  13489},
			         {6257,  9545,  3007,   18669,    650,   1454,   7144,   10107, 11148, 18458,     0,   13121, 9456,   8075 },
			         {5529,  6280,  15771,  7389,     8296,  11142,  15762,  3956,  3794,  7838,    13121,  0,    6625,   7071},
			         {1086,   363,  10625,  9829,     9141,  9333,   16481,  8413,  9290,  12109,    9456, 6625,   0,     1524},
			         {2030,  1510,  9267,   11315,    7806,  7810,   15191,  7918,  8994,  13489,    8057, 7071,  6083,    0 },
	};
	static int [][] delay=new int[14][14];
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	   calculatedelay();
	   System.out.println("delay between each pair of data center");
	   print();
	}

	
	public  static void calculatedelay() {
		
		for (int i = 0; i < distance.length ; i++) {
			for (int j = 0; j < distance[i].length; j++) {
				delay[i][j]=(int)(0.02*distance[i][j]+5);
			}
		}
		
	}
	public static void print() {
		//System.out.print("Ireland"+" "+"Netherlands"+" "+"Singapore"+"  "+"Sao palo"+"  "+"HongKong"+"  "+"Sydney"+"  "+"Oregan"+"  "+"California"+"  ");
		System.out.println();
		for (int i = 0; i <delay.length ; i++) {
			for (int j = 0; j < delay[i].length; j++) {
				System.out.print(delay[i][j]+"  ");
			}
			System.out.println();
		}
		
	}
	
	

}
