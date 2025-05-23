import java.util.LinkedList;
import java.util.Queue;

public class PUYOPUYO {

	public static void main(String[] args) {
		//ゲームソフト「ぷよぷよ」の、
		//ブロック消去システムを再現するのが目的である。
		//ここではユーザーによる自由な配置まではフォローしない
		
		//ルールとしては2次元フィールド上に
		//複数の種類のブロックを配置し、
		//同じ種類のブロックが２次元フィールド上で４つ以上つながっていると
		//それらのブロックが全て消去され、
		//残ったブロックは「下」に落下する
		//落下後には再び消去されるかどうかをチェックし
		//すべてが終わったら処理完了である。

		//本プログラムではブロック配置は
		//2次元配列で制御・管理する
		//配列の要素[0][0]は左上、要素[0][y]は右上、
		//要素[x][0]は左下、要素[x][y]は右下、という形である
		
		//「下に落ちる」とは、プログラム制御に即して換言すれば
		//配列の添え字が一つシフトする制御を指すものである
		//配列map[2][2]にあるブロックが下に落ちるということは
		//map[2][2]のものがmap[3][2]にコピーされ、
		//map[2][2]に空白を意味する要素が代入されることを意味する
		
		//ブロックの種類は「〇×△□☆」の５種類とし
		//ブロックが何も配置されていない空間は「・」とする、
		//これに加えてお邪魔ブロック「邪」も配置する
		//「邪」自身はいくつつながっていても消去されず、
		//隣接するブロックが消去されたら自分も消える
		//特殊なブロックである

		//テスト上の配列空間は縦10、横10の広さとし
		//配置と想定される消去機序は以下の通りとする
		//邪・・・・・・・邪・
		//邪・・・邪・・・×・
		//邪・・〇△・・・□・
		//邪□・〇〇△×・□×
		//〇□×〇□△☆×△□
		//〇□×△邪☆☆☆△□
		//邪□××□△△邪〇☆
		//〇〇〇〇☆×△〇〇☆
		//□□×〇☆△△☆〇△
		//□××☆☆□□××△
		//		
		//　　　　　↓
		//
		//邪・・・・・・・邪・
		//邪・・・邪・・・×・
		//邪・・・△・・・□・
		//・・・・・△×・□×
		//〇・・・□△・×△□
		//〇・・△・・・・△□
		//・・・・□・・・・☆
		//・・・・・×・・・☆
		//□□×・・・・☆・△
		//□××・・□□××△
		//		
		//　　　　　↓
		//
		//・・・・・・・・・・
		//・・・・・・・・・・
		//・・・・・・・・・・
		//邪・・・・・・・邪×
		//邪・・・・・・・×□
		//邪・・・・・・・□□
		//〇・・・邪△・・□☆
		//〇・・・△△・×△☆
		//□□×△□××☆△△
		//□××〇□□□××△
		//		
		//　　　　　↓
		//・・・・・・・・・・
		//・・・・・・・・・・
		//・・・・・・・・・・
		//邪・・・・・・・邪×
		//邪・・・・・・・×・
		//邪・・・・・・・・・
		//〇・・・邪△・・・☆
		//〇・・・△△・×・☆
		//□□×△・××☆・・
		//□××〇・・・××・
		//		
		//　　　　　↓
		//		
		//・・・・・・・・・・
		//・・・・・・・・・・
		//・・・・・・・・・・
		//邪・・・・・・・・・
		//邪・・・・・・・・・
		//邪・・・・・・・・・
		//〇・・・・・・・・・
		//〇・・・・△・×邪×
		//□□×△邪△・☆×☆
		//□××〇△××××☆
		//		
		//　　　　　↓
		//
		//・・・・・・・・・・
		//・・・・・・・・・・
		//・・・・・・・・・・
		//邪・・・・・・・・・
		//邪・・・・・・・・・
		//邪・・・・・・・・・
		//〇・・・・・・・・・
		//〇・・・・△・×・×
		//□□×△邪△・☆・☆
		//□××〇△・・・・☆
		//		
		//　　　　　↓
		//
		//・・・・・・・・・・
		//・・・・・・・・・・
		//・・・・・・・・・・
		//邪・・・・・・・・・
		//邪・・・・・・・・・
		//邪・・・・・・・・・
		//〇・・・・・・・・・
		//〇・・・・・・・・×
		//□□×△邪△・×・☆
		//□××〇△△・☆・☆
		//		

		String[][] map = {//map[10][10]
				{"邪","・","・","・","・","・","・","・","邪","・"},
				{"邪","・","・","・","邪","・","・","・","×","・"},
				{"邪","・","・","〇","△","・","・","・","□","・"},
				{"邪","□","・","〇","〇","△","×","・","□","×"},
				{"〇","□","×","〇","□","△","☆","×","△","□"},
				{"〇","□","×","△","邪","☆","☆","☆","△","□"},
				{"邪","□","×","×","□","△","△","邪","〇","☆"},
				{"〇","〇","〇","〇","☆","×","△","〇","〇","☆"},
				{"□","□","×","〇","☆","△","△","☆","〇","△"},
				{"□","×","×","☆","☆","□","□","×","×","△"}
		};
		
		boolean[][] checked ;//消去できるかどうかの調査が済んだエリアを管理する
		
		boolean goCheck = true;//落下したあとに連鎖が繋がるかどうかの判断
		Queue<int[]> queue;//消去候補ブロックのある座標
		int[][] vector = {{0,1},{0,-1},{1,0},{-1,0}};//四方の走査に関して、上下左右に対応するx軸y軸の移動量である
		
		
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
		System.out.println("初期状態");
		System.out.println("--------------------------------------------");
		bk:
		try{
			while(goCheck) {
				//----以下はブロック削除の処理
				goCheck = false;
				checked = new boolean[10][10];
				queue = new LinkedList<>();
				for(int i = 0; i < map.length; i++) {
					for(int j = 0; j < map[i].length; j++) {
						if(!checked[i][j]) {//消去可能かどうか走査している
							checked[i][j]=true;
							if(!map[i][j].equals("邪") && !map[i][j].equals("・")) {
								queue.add(new int[] {i,j});//消去候補座標を取り入れる
	
								//四方が消去対象か確認。もしあったらqueueにaddする
								QueueAdd(map, checked, i, j, vector, queue);
								if(queue.size()>=4) {//現在のポイントと同じ形のブロックが充分な数繋がっているかを確認
									goCheck = true;
									while(!queue.isEmpty()) {
										//queue内の座標に対応するmap座標を"・"にし、周囲の"邪"を消去する処理
										queue = MapUpdate(queue, map, vector);
									}
								}
								queue = new LinkedList<>();
							}
						}
					}
				}
				
				if(!goCheck)break bk;
				
				for(int i = 0; i < map.length; i++) {
					for(int j = 0; j < map[0].length; j++) {
						System.out.print(map[i][j]);
					}
					System.out.println();
				}
				
				System.out.println("消去処理直後");
				System.out.println("--------------------------------------------");
				
				Thread.sleep(1000);//落下の前に消去直後のブロックを見せる
				
				//----以下は消去後に浮いているブロックを落下させる処理
				for(int i = map.length - 2; i >= 0; i--) {
					for(int j = 0; j < map[0].length; j++) {
						if(!map[i][j].equals("・")) {
							int k = 0;
							while(i + k < map.length - 1 && map[i+k+1][j].equals("・")) {
								k++;
							}
							map[i + k][j] = map[i][j];
							if(k > 0)map[i][j]="・";
						}
					}
				}
				for(int i = 0; i < map.length; i++) {
					for(int j = 0; j < map[0].length; j++) {
						System.out.print(map[i][j]);
					}
					System.out.println();
				}
				
				System.out.println("落下処理後");
				System.out.println("--------------------------------------------");
				Thread.sleep(3000);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}

		System.out.println("処理終了");
	}
	
	//与えられた座標の四方を探り、自分と同じなら消去候補に加える処理
	static void QueueAdd(String[][] map, boolean[][] checked, int i, int j, int[][] v,Queue<int[]> queue) {
		
		for(int k = 0; k < 4; k++) {
			int iNEW = i + v[k][0]; int jNEW = j + v[k][1];
			if( (iNEW >= 0 && iNEW <= map.length -1 && jNEW >= 0 && jNEW <= map[0].length -1)//調査先が枠から出ていないか
				&& !checked[iNEW][jNEW]
				&& map[i][j].equals(map[iNEW][jNEW])) {//既に走査済か
				//現在調査しているポイントと、その四方が同じかどうかをチェック
				//同じなら消去候補に加える
				//その上で、消去候補に加えた先の四方を再帰的に処理する
				checked[iNEW][jNEW] = true;
				queue.add(new int[] {iNEW, jNEW});
				QueueAdd(map, checked, iNEW, jNEW, v, queue);
			}	
		}
	}
	
	//
	static Queue<int[]> MapUpdate(Queue<int[]> queue, String[][] map, int[][] vector) {
		int[] a = (queue.poll());
		map[a[0]][a[1]] ="・";
		for(int k = 0; k < 4; k++) {
			int JA_x = a[0] + vector[k][0];
			int JA_y = a[1] + vector[k][1];
			if(JA_x >= 0 && JA_x < map.length && JA_y>= 0 && JA_y < map[0].length
					&& map[JA_x][JA_y].equals("邪")) {
				map[JA_x][JA_y]="・";
			}
		}
		return queue;
	}
}
