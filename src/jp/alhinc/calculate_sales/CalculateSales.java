package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
	private static final String FILE_NOT_CONSECUTIVE = "売上ファイル名が連番になっていません";
	private static final String SALEAMOUNT_OVER_TEN_DIGIT = "合計金額が10桁を超えました";
	private static final String FILE_BRANCH_CODE_INVALID_FORMAT ="の支店コードが不正です";
	private static final String FILE_BRANCH_NAME_INVALID_FORMAT ="のフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {

		//コマンドライン引数が１つ設定されていなかったら、エラーメッセージをコンソールに表示する。
	    if(args.length != 1){
	    	System.out.println(UNKNOWN_ERROR);
			return;
	    }
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
		// ※集計処理の作成
		//listFilesを使用してfilesという配列に、指定したパスに存在する全てのファイル(または、ディレクトリ)の情報を格納します。
		File[] files = new File(args[0]).listFiles();

		//先にファイルの情報を格納する List(ArrayList) を宣言します。
		List<File> rcdFiles = new ArrayList<>();
		for(int i = 0; i < files.length ; i++) {
		//getName()で取り出したものをString型のfilenameとして定義する（格納先を用意してあげる）
			String filename = files[i].getName();

			//matches を使用してファイル名が「数字8桁.rcd」なのか判定します。
		    //「数字8桁.rcd」に該当する正規表現構文を参考表から探す
			//対象がファイルであるかどうかも確認する。
			if(files[i].isFile() && filename.matches("^[0-9]{8}.rcd$")) {
				rcdFiles.add(files[i]);
			}
		}
		//昇順にソートされた状態で読み込ませるために売上ファイルを保持しているListをソートする
		Collections.sort(rcdFiles);

		//売上ファイルのファイル名が連番になっているか確認し、連番になっていない場合は「売上ファイル名が連番になっていません」を表示し、処理を終了する。
		//2個ずつ確認するから回数は売上ファイルの数よりも1回少ない。
		//比較する2つのファイル名の先頭から数字の8文字を切り出し、int型に変換します。
		for(int i = 0; i <rcdFiles.size() - 1; i++) {
			int former = Integer.parseInt(rcdFiles.get(i).getName().substring(0, 8));
			int latter = Integer.parseInt(rcdFiles.get(i + 1).getName().substring(0, 8));

			if((latter - former) != 1) {
				System.out.println(FILE_NOT_CONSECUTIVE);
				return;
			}
		}
		//rcdFilesに複数の売上ファイルの情報を格納しているので、その数だけ繰り返します。
		for(int i = 0; i < rcdFiles.size(); i++) {
		//ファイルの読み込みは、処理内容1-1を参考にFileReaderやBufferedReaderを使う。
		//rcdFilesには売上ファイルの情報(ファイル名やパス等)が格納されているため、rcdFilesからファイルの情報を取得してください。
			BufferedReader br = null;

			try {
				//rcdFilesというArrayListの(i)番目を.getする、それのfilenameを.getNameする。(.get(i)でi番目と指定しているから.getNameは空欄でOK)
				File rcdFile = new File(args[0], rcdFiles.get(i).getName());
				FileReader fr = new FileReader(rcdFile);
				br = new BufferedReader(fr);

				//lineメソッドで読み込むものはString型と定義する（テキストファイルで保存したものは全てString型（支店コードも売上金額もString型になる））
				String line;
				//売上ファイルは複数存在している。売上ファイルの中身は新しいList（salesFiles）を作成して保持。
				//売上ファイルの1行目には支店コード、2行目には売上金額が入っている。1行ずつ読み込んで作成したリストに追加。
				List<String> salesFiles = new ArrayList<>();
				while((line = br.readLine()) != null) {
					salesFiles.add(line);
				}
				//売上ファイルの中身が2桁じゃない場合は、エラーメッセージ「<該当ファイル名>のフォーマットが不正です」と表示し処理を終了する。
				if(salesFiles.size() != 2){
					System.out.println(rcdFiles.get(i).getName() + FILE_BRANCH_NAME_INVALID_FORMAT);
					return;
				}
				//支店定義ファイルの支店コードに該当がなかった場合は、エラーメッセージ「<該当ファイル名>の支店コードが不正です」と表示し、処理を終了する。
				if(!branchNames.containsKey(salesFiles.get(0))){
					System.out.println(rcdFiles.get(i).getName() + FILE_BRANCH_CODE_INVALID_FORMAT);
					return;
				}
				//売上金額が数字なのか確認し、数字でなかったらエラーメッセージをコンソールに表示する。
				if(!salesFiles.get(1).matches("^[0-9]+$")){
					System.out.println(UNKNOWN_ERROR);
					return;
				}
				//売上ファイルから読み込んだ支店コードと売上金額(fileSale)を新たなMapを使用して保持。
				//売上ファイルから読み込んだ売上金額（salesFiles.get(1)）を既存Map（branchSales）に加算していくために、parseLongメソッドで型の変換を行う
				long fileSale = Long.parseLong(salesFiles.get(1));

				//読み込んだ売上金額を加算(合計はsaleAmount）
				Long saleAmount = branchSales.get(salesFiles.get(0)) + fileSale;

				//合計金額が10桁を超えた場合（売上金額が11桁以上の場合）、エラーメッセージをコンソールに表示し、処理を終了する。
				if(saleAmount >= 10000000000L){
					System.out.println(SALEAMOUNT_OVER_TEN_DIGIT);
					return;
				}
				//加算した売上金額を既存Mapに追加
				branchSales.put(salesFiles.get(0), saleAmount);

			} catch(IOException e) {
				System.out.println(UNKNOWN_ERROR);
				return;
			}finally {
				if(br != null) {
					try {
						br.close();
					} catch(IOException e) {
						System.out.println(UNKNOWN_ERROR);
						return;
					}
				}
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
			File file = new File(path, fileName);
			//支店定義ファイルが存在しない場合は、エラーメッセージ「支店定義ファイルが存在しません」を表示し、処理を終了する。
			if(!file.exists()){
				System.out.println(FILE_NOT_EXIST);
				return false;
			}
			//File型fileを引数にFileReaderを生成、frを引数にBufferedReaderを生成、という流れでデータを渡していく。
			FileReader fr = new FileReader(file);
			br = new BufferedReader(fr);

            //Stirng型の変数lineを用意
			String line;
			//BufferedReaderのreadLineメソッド（1行ずつ読み込む）で読み込んだものをlineに代入。nullじゃない場合は繰り返す。
			while((line = br.readLine()) != null) {

				// ※読み込み処理を変更
				String[] items = line.split(",");
                //支店定義ファイルの仕様が満たされていない場合、エラーメッセージをコンソールに表示させる。
				//（支店コードと支店名が「,」(カンマ)で区切られて文字数２にならない、または支店コードが数字3桁でない場合）
				if((items.length != 2) || (!items[0].matches("^[0-9]{3}$"))){
					System.out.println(FILE_INVALID_FORMAT);
					return false;
				}
			    //Mapに追加する2つの情報を putの引数として指定します。
			    branchNames.put(items[0], items[1]);
			    branchSales.put(items[0], 0L);
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
		// ※書き込み処理

		BufferedWriter bw = null;

		try {
			File file = new File(path, fileName);
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
