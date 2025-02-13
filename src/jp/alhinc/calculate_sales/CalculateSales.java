package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		//if()内（readFileメソッド）がtrueだったら｛｝の処理を行う
		if(!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			//falseだったらreturnで処理を終了させる（次へ進む）
			return;
		}

		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)

		//listFilesを使⽤してfilesという配列に、
		//指定したパスに存在する全てのファイル(または、ディレクトリ)の情報を格納します。
		File[] files = new File("C:\\Users\\trainee1197\\プログラミング言語基礎課題「売上集計システム」").listFiles();

		//先にファイルの情報を格納する List(ArrayList) を宣⾔します。
		List<File> rcdFiles = new ArrayList<>();

		for(int i = 0; i < files.length ; i++) {
		//getName()で取り出したものをString型のfilenameとして定義する（格納先を用意してあげる）
		String filename = files[i].getName();

			//matches を使⽤してファイル名が「数字8桁.rcd」なのか判定します。
		    //「数字8桁.rcd」に該当する正規表現構文を参考表から探す
				if(filename.matches("^[0-9]{8}.rcd$")) {
					//trueの場合の処理
					rcdFiles.add(files[i]);
				}
		}

		//処理内容 2-2
		//rcdFilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返します。
				for(int j = 0; j < rcdFiles.size(); j++) {

				//ファイルの読み込みは、処理内容1-1を参考にFileReaderやBufferedReaderを使う。
				//rcdFilesには売上ファイルの情報(ファイル名やパス等)が格納されているため、rcdFilesからファイルの情報を取得してください。

					BufferedReader br = null;

					try {
						//rcdFilesというArrayListの(j)番目を.getする、それのfilenameを.getNameする。.get(j)でｊ番目と指定しているから.getNameは空欄でOK)
						File files1 = new File("C:\\Users\\trainee1197\\プログラミング言語基礎課題「売上集計システム」", rcdFiles.get(j).getName());
						FileReader fr = new FileReader(files1);
						br = new BufferedReader(fr);

						//lineメソッドで読み込むものはString型と定義する（テキストファイルで保存したものは全てString型（支店コードも売上金額もString型になる））
						String line;
						//売上ファイルは複数存在している。売上ファイルの中身は新しいList（SalesFiles）を作成して保持。
						//売上ファイルの1行目には支店コード、2行目には売上金額が入っている。1行ずつ読み込んで作成したリストに追加。
						List<String> SalesFiles = new ArrayList<>();

						while((line = br.readLine()) != null) {

							SalesFiles.add(line);
						}
							//売上ファイルから読み込んだ支店コードと売上金額を新たなMapを使用して保持。
							//売上ファイルから読み込んだ売上金額（SalesFiles.get(1)）を既存Map（branchSales）に加算していくために、parseLongメソッドで型の変換を行う
							long fileSale = Long.parseLong(SalesFiles.get(1));
							//読み込んだ売上⾦額を加算(合計はsaleAmount）
							Long saleAmount = branchSales.get(SalesFiles.get(0))+ fileSale;

							//加算した売上⾦額を既存Mapに追加
							branchSales.put(SalesFiles.get(0),saleAmount);


					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;

					}finally {
				}
	}
		// 支店別集計ファイル書き込み処理
		if(!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
			return;
		}

	}

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		BufferedReader br = null;

		try {
			File file = new File("C:\\Users\\trainee1197\\プログラミング言語基礎課題「売上集計システム」","branch.lst");
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

			String line;
			// 一行ずつ読み込む
			while((line = br.readLine()) != null) {
				// ※ここの読み込み処理を変更してください。(処理内容1-2)
				String[] items = line.split(",");

			    //Mapに追加する2つの情報を putの引数として指定します。
			    branchNames.put(items[0],items[1]);

			    branchSales.put(items[0],0L);

			}

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(br != null) {
				try {
					// ファイルを閉じる
					br.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */
	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		// ※ここに書き込み処理を作成してください。(処理内容3-1)

		BufferedWriter bw = null;

		try {
			File file = new File(path,"branch.out" );
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

            //支店コードを入れたMapからKeyの一覧を取得してKeyの数だけ繰り返す
			for (String key : branchSales.keySet()) {
				//write(書き込む文字列)
				bw.write(key + "," + branchNames.get(key) + "," + branchSales.get(key));
				bw.newLine();
		    }

		} catch(IOException e) {
			System.out.println(UNKNOWN_ERROR);
			return false;
		} finally {
			// ファイルを開いている場合
			if(bw != null) {
				try {
					// ファイルを閉じる
					bw.close();
				} catch(IOException e) {
					System.out.println(UNKNOWN_ERROR);
					return false;
				}
			}
		}
		return true;
	}

}
